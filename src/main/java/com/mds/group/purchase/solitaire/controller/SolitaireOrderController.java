/*
 * Copyright Ningbo Qishan Technology Co., Ltd
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.mds.group.purchase.solitaire.controller;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import com.github.binarywang.wxpay.bean.notify.WxPayNotifyResponse;
import com.github.binarywang.wxpay.bean.notify.WxPayOrderNotifyResult;
import com.google.common.collect.Maps;
import com.mds.group.purchase.activity.result.ActGoodsInfoResult;
import com.mds.group.purchase.activity.service.ActivityGoodsService;
import com.mds.group.purchase.configurer.GroupMallProperties;
import com.mds.group.purchase.constant.ActivityConstant;
import com.mds.group.purchase.constant.RedisPrefix;
import com.mds.group.purchase.core.CodeMsg;
import com.mds.group.purchase.core.Result;
import com.mds.group.purchase.order.model.OrderDetail;
import com.mds.group.purchase.order.vo.SolitaireOrderVo;
import com.mds.group.purchase.solitaire.service.SolitaireGoodsService;
import com.mds.group.purchase.solitaire.service.SolitaireOrderService;
import com.mds.group.purchase.utils.ActivityGoodsUtil;
import com.mds.group.purchase.utils.OrderUtil;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * The type Solitaire order controller.
 *
 * @author pavawi
 */
@RestController
@RequestMapping("/solitaire/order")
public class SolitaireOrderController {

    @Resource
    private OrderUtil orderUtil;
    @Resource
    private ActivityGoodsUtil activityGoodsUtil;
    @Resource
    private RedisTemplate<String, Integer> redisTemplate4Int;
    @Resource
    private ActivityGoodsService activityGoodsService;
    @Resource
    private SolitaireOrderService solitaireOrderService;
    @Resource
    private SolitaireGoodsService solitaireGoodsService;

    private Logger log = LoggerFactory.getLogger(SolitaireOrderController.class);

    /**
     * 接龙订单-》包含多个商品的订单
     *
     * @param appmodelId the appmodel id
     * @param vo         the vo
     * @return result result
     * @since v1.2.3
     */
    @PostMapping("/create")
    public Result solitaireOrder(@RequestHeader String appmodelId, @RequestBody SolitaireOrderVo vo) {
        vo.setAppmodelId(appmodelId);
        //判断商品是否在售卖
        List<OrderDetail> details = vo.vo2OrderDetail();
        Map<Long, Long> surplusActStockMap = Maps.newHashMapWithExpectedSize(details.size());
        for (OrderDetail detail : details) {
            Long actGoodsId = detail.getActGoodsId();
            long goodsNum = detail.getGoodsNum().longValue();
            boolean goodsOnSale = activityGoodsUtil.thisGoodsOnSale(actGoodsId, appmodelId);
            if (!goodsOnSale) {
                return Result.error(CodeMsg.FAIL.fillArgs("商品" + actGoodsId + "已售罄"));
            }
            //读取内存标记，判断活动商品是否已经售罄
            String redisKeyPrefix = GroupMallProperties.getRedisPrefix().concat(appmodelId);
            //在redis中 预减库存
            String actGoodsStockRedisKey = GroupMallProperties.getRedisPrefix().concat(appmodelId)
                    .concat(RedisPrefix.ACT_GOODS_STOCK).concat(actGoodsId.toString());
            String redisKeySaleVolum = redisKeyPrefix.concat(RedisPrefix.ACT_GOODS_SALES_VOLUME)
                    .concat(actGoodsId.toString());
            Long surplusActStock;
            Integer actStock = redisTemplate4Int.opsForValue().get(actGoodsStockRedisKey);
            //redis缓存过期后从数据库获取数据，并重新存入redis
            if (actStock == null) {
                ActGoodsInfoResult actGoods = activityGoodsService.getActGoodsById(actGoodsId, appmodelId);
                actStock = actGoods.getActivityStock();
                redisTemplate4Int.opsForValue().set(actGoodsStockRedisKey, actStock, 3, TimeUnit.DAYS);
            }
            if (actStock <= 0) {
                return Result.error(CodeMsg.GOODS_UNDERSTOCK.fillArgs("商品库存不足，下单失败"));
            }
            surplusActStock = redisTemplate4Int.opsForValue().decrement(actGoodsStockRedisKey, goodsNum);
            if (surplusActStock != null && surplusActStock < 0) {
                //库存减少失败，不能下单,将减少的库存还回redis
                redisTemplate4Int.opsForValue().increment(actGoodsStockRedisKey, goodsNum);
                return Result.error(CodeMsg.GOODS_UNDERSTOCK.fillArgs("商品库存不足，下单失败"));
            }
            surplusActStockMap.put(actGoodsId, surplusActStock);
            //更新活动商品销量
            //将活动销量在redis增加
            redisTemplate4Int.opsForValue().increment(redisKeySaleVolum, goodsNum);
            //增加redis中用户购买数量
            String buyAmountKey = redisKeyPrefix.concat(RedisPrefix.ACTIVITY_BUY_AMOUNT)
                    .concat(detail.getWxuserId().toString()).concat(detail.getActGoodsId().toString());
            redisTemplate4Int.opsForValue().increment(buyAmountKey, detail.getGoodsNum().longValue());
        }
        try {
            //更新活动商品销量
            String orderNo = solitaireOrderService.saveSolitaireOrder(vo);
            surplusActStockMap.keySet().forEach(o -> {
                if (surplusActStockMap.get(o) <= 0) {
                    //库存减少后为0，标识活动商品已售罄，更改活动商品状态
                    activityGoodsService.updateActGoodsPreheatStatusById(o, ActivityConstant.ACTIVITY_GOODS_OVER);
                }
            });
            return Result.success(orderNo);
        } catch (Exception e) {
            //发生异常，不能下单,将减少的库存还回redis
            details.forEach(o -> {
                String actGoodsStockRedisKey = GroupMallProperties.getRedisPrefix().concat(appmodelId)
                        .concat(RedisPrefix.ACT_GOODS_STOCK).concat(o.getActGoodsId().toString());
                String redisKeySaleVolum = GroupMallProperties.getRedisPrefix().concat(appmodelId)
                        .concat(RedisPrefix.ACT_GOODS_SALES_VOLUME).concat(o.getActGoodsId().toString());
                redisTemplate4Int.opsForValue().increment(actGoodsStockRedisKey, o.getGoodsNum().longValue());
                //销量减少
                redisTemplate4Int.opsForValue().decrement(redisKeySaleVolum, o.getGoodsNum().longValue());
            });
            log.error(e.getMessage());
            log.error(e.getCause().toString());
            return Result.error(CodeMsg.FAIL.fillArgs("下单失败"));
        }
    }

    /**
     * h5页面获取用户的已购买的活动商品数量
     *
     * @param appmodelId the appmodel id
     * @param wxuserId   the wxuser id
     * @return the user buy count
     */
    @GetMapping("/userBuyCount")
    public Result getUserBuyCount(@RequestHeader String appmodelId, @RequestParam Long wxuserId) {
        List<ActGoodsInfoResult> solitaireGoodsList = solitaireGoodsService.getSolitaireGoodsList(appmodelId, wxuserId);
        if (CollectionUtil.isNotEmpty(solitaireGoodsList)) {
            List<String> actGoodsIds = solitaireGoodsList.stream().map(o -> o.getActivityGoodsId().toString())
                    .collect(Collectors.toList());
            String redisKeyPrefix = GroupMallProperties.getRedisPrefix().concat(appmodelId);
            Map<String, Integer> map = Maps.newHashMapWithExpectedSize(actGoodsIds.size());
            for (String id : actGoodsIds) {
                String buyAmountKey = redisKeyPrefix.concat(RedisPrefix.ACTIVITY_BUY_AMOUNT).concat(wxuserId.toString())
                        .concat(id);
                Integer userBuyCount = redisTemplate4Int.opsForValue().get(buyAmountKey);
                map.put(id, userBuyCount == null ? 0 : userBuyCount);
            }
            return Result.success(map);
        }
        return Result.success(new HashMap<>());
    }


    /**
     * 支付接口
     *
     * @param orderNo 订单no
     * @param request the request
     * @return the result
     */
    @ApiOperation(value = "支付", tags = "更新接口")
    @GetMapping("/h5/v1/pay")
    public Result<Map<String, String>> h5Pay(@RequestParam String orderNo, HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.length() == 0) {
            ip = request.getRemoteAddr();
        }
        log.info("ip:" + ip + "请求支付接口！订单no：" + orderNo + ",请求时间：" + DateUtil.date());
        Map<String, String> map = solitaireOrderService.h5Pay(orderNo, request, ip);
        return Result.success(map);
    }

    /**
     * h5接龙活动支付回调
     *
     * @param request request
     * @return the string
     * @throws Exception 异常
     * @since v1.2.3
     */
    @PostMapping("/h5/v1/notify")
    @ApiOperation(value = "订单支付回调V2版本，此版本支付成功后会生成对应该订单的订单发货单关系映射数据", tags = "v1.2版本接口")
    protected String notifyH5(HttpServletRequest request) throws Exception {
        String xmlResult = IOUtils.toString(request.getInputStream(), request.getCharacterEncoding());
        WxPayOrderNotifyResult payOrderNotifyResult = WxPayOrderNotifyResult.fromXML(xmlResult);
        try {
            return solitaireOrderService.notifyH5(payOrderNotifyResult);
        } catch (Exception e) {
            orderUtil.notifyFailHandle(e, payOrderNotifyResult);
            return WxPayNotifyResponse.fail("订单回调处理失败");
        }
    }
}
