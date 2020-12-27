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
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.HashUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.mds.group.purchase.activity.model.Activity;
import com.mds.group.purchase.activity.model.ActivityGoods;
import com.mds.group.purchase.activity.service.ActivityGoodsService;
import com.mds.group.purchase.activity.service.ActivityService;
import com.mds.group.purchase.configurer.GroupMallProperties;
import com.mds.group.purchase.constant.ActiviMqQueueName;
import com.mds.group.purchase.constant.ActivityConstant;
import com.mds.group.purchase.constant.OrderConstant;
import com.mds.group.purchase.constant.RedisPrefix;
import com.mds.group.purchase.goods.model.GoodsDetail;
import com.mds.group.purchase.goods.service.GoodsDetailService;
import com.mds.group.purchase.jobhandler.ActiveDelaySendJobHandler;
import com.mds.group.purchase.order.model.OrderDetail;
import com.mds.group.purchase.order.service.OrderDetailService;
import com.mds.group.purchase.utils.ActivityUtil;
import com.mds.group.purchase.utils.GoodsUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;


/**
 * 活动队列消息接收类
 *
 * @author shuke
 * @date 2018 -12-15
 */
@Component
public class ActivityListener {

    @Resource
    private GoodsUtil goodsUtil;
    @Resource
    private ActivityUtil activityUtil;
    @Resource
    private RedisTemplate<String, String> redisTemplate4Str;
    @Resource
    private RedisTemplate<String, Integer> redisTemplate4Int;
    @Resource
    private RedisTemplate<String, Long> redisTemplate4Long;
    @Resource
    private ActivityService activityService;
    @Resource
    private GoodsDetailService goodsDetailService;
    @Resource
    private OrderDetailService orderDetailService;
    @Resource
    private ActivityGoodsService activityGoodsService;
    @Resource
    private ActiveDelaySendJobHandler activeDelaySendJobHandler;


    private Logger logger = LoggerFactory.getLogger(ActivityListener.class);

    /**
     * 开始预热活动
     *
     * @param jsonData json字符串
     */
    @JmsListener(destination = ActiviMqQueueName.READY_ACTIVITY_V1)
    public void readyActivity(String jsonData) {
        //修改活动状态为预热状态
        JSONObject jsonObject = JSON.parseObject(jsonData);
        Activity activity = activityService.findByActId(jsonObject.getLong("id"));
        if (activity != null) {
            if (isToken(jsonObject, activity)) {
                Long milliSecond = readyActivity(activity);
                activeDelaySendJobHandler
                        .savaTask(activity.getActivityId().toString(), ActiviMqQueueName.START_ACTIVITY_V1, milliSecond,
                                activity.getAppmodelId(), true);
                activeDelaySendJobHandler.updateState(jsonObject.getString("activeMqTaskTOId"));
            }
        }
    }

    /**
     * 判断令牌是否正确,不正确则修改队列为无效队列
     * @param jsonObject json对象
     * @param activity 活动
     * @return true token正确，有效队列，false 无效队列
     */
    private boolean isToken(JSONObject jsonObject, Activity activity) {
        Integer token = jsonObject.getInteger("token");
        String key = GroupMallProperties.getRedisPrefix().concat(activity.getAppmodelId())
                .concat(RedisPrefix.ACTIVITY_TOKEN);
        String hashKey = String.valueOf(HashUtil.bkdrHash(key.concat(":").concat(jsonObject.getString("id"))));
        String redisToken = (String) redisTemplate4Str.opsForHash().get(key, hashKey);
        logger.info("==========================>活动id和token：" + activity.getActivityId() + "|||||" + token);
        logger.info("==========================>活动id和redistoken：" + activity.getActivityId() + "|||||" + redisToken);
        if (StringUtils.isNotBlank(redisToken)) {
            redisToken = redisToken.replace("\"", "");
            logger.info(
                    "==========================>活动id和redistokenNew：" + activity.getActivityId() + "|||||" + redisToken);
            //如果令牌值不正确,则是无效队列
            if (StringUtils.isNotBlank(redisToken) && !StringUtils.equals(token.toString(), redisToken)) {
                activeDelaySendJobHandler.updateState(jsonObject.getString("activeMqTaskTOId"));
                return false;
            }
        }
        //删除已经使用的令牌
        redisTemplate4Str.opsForHash().delete(key, hashKey);
        return true;
    }

    private Long readyActivity(Activity activity) {
        activity.setStatus(ActivityConstant.ACTIVITY_STATUS_READY);
        activityService.update(activity);
        //修改活动商品的状态
        activityGoodsService.updatePreheatStatus(activity.getActivityId(), ActivityConstant.ACTIVITY_GOODS_PREHEAT);
        //活动开始时将活动商品的库存标记进行初始化
        activityUtil.afterPropertiesSet(activity.getActivityId());
        //修改成功后将活动再次加入活动开始队列
        long milliSecond = DateUtil.parse(activity.getStartTime()).getTime() - System.currentTimeMillis();
        if (milliSecond < 0) {
            milliSecond = 0L;
        }
        //更新活动缓存
        activityUtil.flushMethod(activity.getAppmodelId());
        return milliSecond;
    }


    /**
     * 开始活动
     *
     * @param jsonData json字符串
     */
    @JmsListener(destination = ActiviMqQueueName.START_ACTIVITY_V1)
    public void startActivity(String jsonData) {
        //修改活动状态为开始状态
        JSONObject jsonObject = JSON.parseObject(jsonData);
        Activity activity = activityService.findByActId(jsonObject.getLong("id"));
        if (activity != null) {
            if (isToken(jsonObject, activity)) {
                Long milliSecond = startActivity(activity);
                //活动结束队列
                activeDelaySendJobHandler
                        .savaTask(activity.getActivityId().toString(), ActiviMqQueueName.END_ACTIVITY_V1, milliSecond,
                                activity.getAppmodelId(), true);
                //发送接龙活动开始信息到微信群
                activeDelaySendJobHandler.savaTask(activity.getAppmodelId(), ActiviMqQueueName.SOLITAIRE_ACT_START_MSG, 0L,
                        activity.getAppmodelId(), false);
                activeDelaySendJobHandler.updateState(jsonObject.getString("activeMqTaskTOId"));
            }
        }
    }

    private Long startActivity(Activity activity) {
        activity.setStatus(ActivityConstant.ACTIVITY_STATUS_START);
        activityService.update(activity);
        //活动开始时将活动商品的库存标记进行初始化
        activityUtil.afterPropertiesSet(activity.getActivityId());
        //修改活动商品的状态
        activityGoodsService.updatePreheatStatus(activity.getActivityId(), ActivityConstant.ACTIVITY_GOODS_SEAL);
        //修改成功后将活动再次加入活动结束队列
        long milliSecond = DateUtil.parse(activity.getEndTime()).getTime() - System.currentTimeMillis();
        if (milliSecond < 0) {
            milliSecond = 0L;
        }
        //更新拼团活动列表
        activityUtil.flushMethod(activity.getAppmodelId());
        return milliSecond;

    }


    /**
     * 结束活动
     *
     * @param jsonData json字符串
     */
    @JmsListener(destination = ActiviMqQueueName.END_ACTIVITY_V1)
    public void endActivity(String jsonData) {
        //修改活动状态为结束状态
        JSONObject jsonObject = JSON.parseObject(jsonData);
        Long actId = jsonObject.getLong("id");
        Activity activity = activityService.findByActId(actId);
        if (activity != null && !ActivityConstant.ACTIVITY_STATUS_END.equals(activity.getStatus())) {
            if (isToken(jsonObject, activity)) {
                this.endActivity(jsonObject, actId, activity);
            }
        }
    }

    private void endActivity(JSONObject jsonObject, Long actId, Activity activity) {
        activity.setStatus(ActivityConstant.ACTIVITY_STATUS_END);
        DateTime date = DateUtil.date();
        if (date.getTime() < DateUtil.parse(activity.getEndTime(), DatePattern.NORM_DATETIME_MINUTE_FORMAT).getTime()) {
            activity.setEndTime(DateUtil.formatDateTime(date));
        }
        activityService.update(activity);
        //更改活动商品状态
        activityGoodsService.updatePreheatStatus(activity.getActivityId(), ActivityConstant.ACTIVITY_GOODS_DONT_SHOW);
        //将剩余的活动商品库存还回到实际库存,以及增加商品销售量
        List<ActivityGoods> actGoods = activityGoodsService.findByActId(actId);
        String goodsIds = actGoods.parallelStream().map(obj -> obj.getGoodsId().toString())
                .collect(Collectors.joining(","));
        List<GoodsDetail> byGoodsIds = goodsDetailService.findByGoodsIds(goodsIds);
        Map<Long, GoodsDetail> collect = byGoodsIds.parallelStream()
                .collect(Collectors.toMap(GoodsDetail::getGoodsId, v -> v));
        List<GoodsDetail> goodsDetailList = new ArrayList<>();
        AtomicLong sumSaleVolume = new AtomicLong(0);
        actGoods.forEach(actGood -> {
            GoodsDetail goodsDetail = collect.get(actGood.getGoodsId());
            //剩余的库存和销量
            Integer actStock = goodsUtil
                    .getActStock(actGood.getAppmodelId(), actGood.getActivityGoodsId(), actGood.getActivityId());
            Integer actGoodsSaleVolume = goodsUtil
                    .getActGoodsSaleVolume(actGood.getAppmodelId(), actGood.getActivityGoodsId());
            sumSaleVolume.addAndGet(actGoodsSaleVolume);
            //将剩余的活动商品库存还回到实际库存
            goodsDetail.setStock(goodsDetail.getStock() + actStock);
            goodsDetail.setSalesVolume(goodsDetail.getSalesVolume() + actGoodsSaleVolume);
            goodsDetailList.add(goodsDetail);

        });
        goodsDetailService.updateList(goodsDetailList);
        //将内存标记删除
        this.afterPropertiesDel(actId);
        activeDelaySendJobHandler.updateState(jsonObject.getString("activeMqTaskTOId"));
        this.deleteActivityGoods(activity);
    }

    /**
     *活动结束时，将未付款订单都取消
     */
    @Deprecated
    private void cancleOrderOnActEnd(Long actId, String appmodelId) {
        //查询该活动的未付款订单
        List<OrderDetail> notPay = orderDetailService.findByActivityIdAndOrderNotPay(actId);
        //取消未付款订单
        if (notPay != null) {
            String orderIds = notPay.stream().map(obj -> obj.getOrderId().toString()).collect(Collectors.joining(","));
            List<String> stringList = Arrays.asList(orderIds.split(","));
            Iterator<String> iterator = stringList.iterator();
            while (iterator.hasNext()) {
                Long orderId = Long.valueOf(iterator.next());
                String redisKey =
                        GroupMallProperties.getRedisPrefix() + appmodelId + RedisPrefix.CANCLE_ORDER_ID + orderId;
                Integer integer = redisTemplate4Int.opsForValue().get(redisKey);
                if (integer == null) {
                    integer = -1;
                    redisTemplate4Long.opsForValue().set(redisKey, orderId, 60, TimeUnit.SECONDS);
                }
                if (orderId.equals(Long.valueOf(integer))) {
                    iterator.remove();
                }
            }
            String orderids = String.join(",", stringList);

            //订单修改状态为商家关闭订单
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("orderIds", orderids);
            jsonObject.put("closeType", OrderConstant.ORDER_CLOSE_BY_SHOP);
            activeDelaySendJobHandler.savaTask(jsonObject.toJSONString(), ActiviMqQueueName.ORDER_CLOSE, 0L,
                    notPay.get(0).getAppmodelId(), Boolean.FALSE);
        }
    }

    /**
     * 活动结束时删除活动商品的库存标记
     * */
    @SuppressWarnings("unchecked")
    private void afterPropertiesDel(Long actId) {
        List<ActivityGoods> goodsList = activityGoodsService.findByActId(actId);
        if (CollectionUtil.isNotEmpty(goodsList)) {
            String redisKeyPrefix = GroupMallProperties.getRedisPrefix().concat(goodsList.get(0).getAppmodelId());
            String redisKeyActGoodsStock = redisKeyPrefix.concat(RedisPrefix.ACT_GOODS_STOCK);
            String maxQuantityKey = redisKeyPrefix.concat(RedisPrefix.MAX_QUANTITY);
            for (ActivityGoods activityGoods : goodsList) {
                //将活动商品的库存从redis删除
                String redisKey = redisKeyActGoodsStock.concat(activityGoods.getActivityGoodsId().toString());
                redisTemplate4Str.delete(redisKey);
                //如果活动商品有限购数量,则将限售量从redis删除
                if (activityGoods.getMaxQuantity() != null) {
                    String redisKey4limit = maxQuantityKey.concat(activityGoods.getActivityGoodsId().toString());
                    redisTemplate4Str.delete(redisKey4limit);
                }
            }
        }
    }

    /**
     * 更新活动列表缓存
     *
     * @param jsonData the json data
     */
    @JmsListener(destination = ActiviMqQueueName.ACTIVITY_CACHE)
    public void flashActivityCache(String jsonData) {
        JSONObject jsonObject = JSON.parseObject(jsonData);
        Activity activity = JSON.toJavaObject(jsonObject, Activity.class);
        activityUtil.flushMethod(activity.getAppmodelId());
    }

    /**
     * 删除活动时将活动列表缓存中的该活动删除
     *
     * @param jsonData the json data
     */
    @JmsListener(destination = ActiviMqQueueName.DEL_ONE_ACTIVITY_CACHE)
    public void delOneActivityCache(String jsonData) {
        JSONObject jsonObject = JSON.parseObject(jsonData);
        Activity activity = activityService.findById(jsonObject.getString("id"));
        deleteActivityGoods(activity);
    }

    private void deleteActivityGoods(Activity activity) {
        List<ActivityGoods> byActId = activityGoodsService.findByActId(activity.getActivityId());
        byActId.forEach(activityGoods -> {
            //将活动商品的库存从redis删除
            String redisKey = GroupMallProperties.getRedisPrefix()
                    .concat(activityGoods.getAppmodelId().concat(RedisPrefix.ACT_GOODS_STOCK)
                            .concat(activityGoods.getActivityGoodsId().toString()));
            redisTemplate4Str.delete(redisKey);
            //如果活动商品有限购数量,则将限售量从redis删除
            if (activityGoods.getMaxQuantity() != null) {
                String redisKey4limit = GroupMallProperties.getRedisPrefix()
                        .concat(activityGoods.getAppmodelId().concat(RedisPrefix.MAX_QUANTITY)
                                .concat(activityGoods.getActivityGoodsId().toString()));
                redisTemplate4Str.delete(redisKey4limit);
            }
            String redisKeySaleVolum = GroupMallProperties.getRedisPrefix().concat(RedisPrefix.ACT_GOODS_SALES_VOLUME)
                    .concat(activityGoods.getActivityGoodsId().toString());
            redisTemplate4Str.delete(redisKeySaleVolum);
        });
        //删除活动时将mongodb里面的该活动对应的队列记录删除
        activityUtil.removeMongodbValueById(activity.getActivityId());
        goodsUtil.flushGoodsCache(activity.getAppmodelId());
    }

    /**
     * 更新商品时同时更新所有活动商品缓存
     *
     * @param appmodelId the appmodel id
     */
    @JmsListener(destination = ActiviMqQueueName.UPDATE_GOODS_FLASH_ACTGOODS_CACHE)
    public void flashActGoodsCacheByUpdateGoods(String appmodelId) {
        //刷新活动缓存
        activityUtil.flushActivityCache(appmodelId);
        //刷新所有活动商品缓存
        activityUtil.flushAllActGoodsCache(appmodelId);
        //刷新首页活动商品缓存
        activityUtil.flushIndexActGoodsCache(appmodelId);
    }

}
