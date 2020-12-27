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

package com.mds.group.purchase.listener;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.mds.group.purchase.activity.model.Activity;
import com.mds.group.purchase.activity.service.ActivityService;
import com.mds.group.purchase.configurer.GroupMallProperties;
import com.mds.group.purchase.constant.ActiviMqQueueName;
import com.mds.group.purchase.constant.ActivityConstant;
import com.mds.group.purchase.constant.RedisPrefix;
import com.mds.group.purchase.goods.model.GoodsDetail;
import com.mds.group.purchase.goods.service.GoodsDetailService;
import com.mds.group.purchase.jobhandler.ActiveDelaySendJobHandler;
import com.mds.group.purchase.order.model.Order;
import com.mds.group.purchase.order.model.OrderDetail;
import com.mds.group.purchase.order.service.OrderDetailService;
import com.mds.group.purchase.order.service.OrderService;
import com.mds.group.purchase.utils.GoodsUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


/**
 * 订单队列消息接收类
 *
 * @author shuke
 * @date 2018 -12-14
 */
@Component
public class OrderListener {

    @Resource
    private GoodsUtil goodsUtil;
    @Resource
    private OrderService orderService;
    @Resource
    private RedisTemplate<String,Integer> redisTemplate;
    @Resource
    private ActivityService activityService;
    @Resource
    private OrderDetailService orderDetailService;
    @Resource
    private GoodsDetailService goodsDetailService;
    @Resource
    private ActiveDelaySendJobHandler activeDelaySendJobHandler;

    private Logger logger = LoggerFactory.getLogger(OrderListener.class);

    /**
     * 关闭订单
     *
     * @param jsonData json字符串
     */
    @JmsListener(destination = ActiviMqQueueName.ORDER_CLOSE)
    @Transactional(rollbackFor = Exception.class)
    public void closelOrderV1(String jsonData) {
        logger.info("订单关闭监听-------start");
        JSONObject jsonObject = JSON.parseObject(jsonData);
        String orderIds1 = jsonObject.getString("orderIds");
        List<String> stringList = Arrays.asList(orderIds1.split(","));
        List<Long> collect = stringList.stream().map(Long::valueOf).collect(Collectors.toList());
        List<Order> orderList = orderService.findWaitPayOrderByIds(collect);
        if (CollectionUtil.isNotEmpty(orderList)) {
            DateTime currentTime = DateUtil.date();
            Integer payStatu = jsonObject.getInteger("closeType");
            List<Long> orderIdList = orderList.stream().map(Order::getOrderId).collect(Collectors.toList());
            Map<String, Object> map = new HashMap<>(8);
            map.put("orderIdList", orderIdList);
            map.put("closeTime", currentTime);
            map.put("payStatu", payStatu);
            orderService.updateOrderStatus(map);
            String orderIds = orderIdList.stream().map(Object::toString).collect(Collectors.joining(","));
            logger.info("关闭单成功,订单号:  " + orderIds);
            List<OrderDetail> orderDetailList = orderDetailService.findByOrderIds(orderIdList);
            //获取商品
            String goodsIds = orderDetailList.stream().map(obj -> obj.getGoodsId().toString()).distinct()
                    .collect(Collectors.joining(","));
            Map<String, GoodsDetail> goodsDetailMap = goodsDetailService.findByGoodsIds(goodsIds).stream()
                    .collect(Collectors.toMap(o->o.getGoodsId().toString(), v -> v));
            //获取活动
            String activityIds = orderDetailList.stream().map(obj -> obj.getActivityId().toString()).distinct()
                    .collect(Collectors.joining(","));
            Map<String, Activity> activityMap = activityService.findByIds(activityIds).stream()
                    .collect(Collectors.toMap(o->o.getActivityId().toString(), v -> v));
            String appmodelId = orderList.get(0).getAppmodelId();
            String redisKeyPrefix = GroupMallProperties.getRedisPrefix().concat(appmodelId);
            for (OrderDetail orderDetail : orderDetailList) {
                Activity activity = activityMap.get(orderDetail.getActivityId().toString());
                GoodsDetail goodsDetail = goodsDetailMap.get(orderDetail.getGoodsId().toString());
                if (activity.getStatus().equals(ActivityConstant.ACTIVITY_STATUS_START)) {
                    //1如果活动未结束，将活动库存自接归还到redis
                    String redisKey = redisKeyPrefix.concat(RedisPrefix.ACT_GOODS_STOCK)
                            .concat(orderDetail.getActGoodsId().toString());
                    redisTemplate.opsForValue().increment(redisKey, orderDetail.getGoodsNum());
                    //活动销量减去
                    String redisKeySale = redisKeyPrefix.concat(RedisPrefix.ACT_GOODS_SALES_VOLUME)
                            .concat(orderDetail.getActGoodsId().toString());
                    Long decrement = redisTemplate.opsForValue().decrement(redisKeySale, orderDetail.getGoodsNum());
                    if (decrement != null && decrement < 0) {
                        redisTemplate.opsForValue().increment(redisKeySale);
                    }
                    String buyAmountKey = redisKeyPrefix.concat(RedisPrefix.ACTIVITY_BUY_AMOUNT)
                            .concat(orderDetail.getWxuserId() + "" + orderDetail.getActGoodsId());
                    Object o = redisTemplate.opsForValue().get(buyAmountKey);
                    if (o != null) {
                        redisTemplate.opsForValue().decrement(buyAmountKey, orderDetail.getGoodsNum());
                    }
                } else {
                    if (goodsDetail.getShamVolume() != null
                            && (goodsDetail.getShamVolume() - orderDetail.getGoodsNum()) >= 0) {
                        goodsDetail.setSalesVolume(goodsDetail.getSalesVolume() - orderDetail.getGoodsNum());
                    }
                    //2如果活动已结束，库存归还到普通商品
                    goodsDetail.setStock(goodsDetail.getStock() + orderDetail.getGoodsNum());
                    //商品销量回退,如果活动已结束库存归还
                    goodsDetailService.update(goodsDetail);
                    goodsUtil.flushGoodsCache(appmodelId);
                }
                String redisKey = GroupMallProperties.getRedisPrefix() + appmodelId + RedisPrefix.CANCLE_ORDER_ID + orderDetail.getOrderId();
                redisTemplate.delete(redisKey);
            }
        }
        activeDelaySendJobHandler.updateState(jsonObject.getString("activeMqTaskTOId"));
        logger.info("订单关闭监听-------end");
    }

}
