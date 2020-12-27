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

package com.mds.group.purchase.utils;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.NumberUtil;
import com.mds.group.purchase.activity.dao.ActivityGoodsMapper;
import com.mds.group.purchase.activity.model.Activity;
import com.mds.group.purchase.activity.model.ActivityGoods;
import com.mds.group.purchase.activity.result.ActGoodsInfoResult;
import com.mds.group.purchase.activity.service.ActivityGoodsService;
import com.mds.group.purchase.configurer.GroupMallProperties;
import com.mds.group.purchase.constant.ActivityConstant;
import com.mds.group.purchase.constant.GoodsConstant;
import com.mds.group.purchase.constant.OrderConstant;
import com.mds.group.purchase.constant.RedisPrefix;
import com.mds.group.purchase.exception.ServiceException;
import com.mds.group.purchase.goods.model.Goods;
import com.mds.group.purchase.goods.result.GoodsFuzzyResult;
import com.mds.group.purchase.logistics.model.GoodsAreaMapping;
import com.mds.group.purchase.logistics.service.GoodsAreaMappingService;
import com.mds.group.purchase.user.model.Wxuser;
import com.mds.group.purchase.user.service.WxuserService;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 活动商品工具类
 *
 * @author shuke
 * @since v1.2
 */
@Component
public class ActivityGoodsUtil {

    @Resource
    private ActivityUtil activityUtil;
    @Resource
    private GoodsUtil goodsUtil;
    @Resource
    private WxuserService wxuserService;
    @Resource
    private GoodsAreaMappingService goodsAreaMappingService;
    @Resource
    private RedisTemplate<String, Map<String, ActivityGoods>> redisTemplate;
    @Resource
    private RedisTemplate<String, Map<String, ActivityGoods>> redisTemplate2;
    @Resource
    private RedisTemplate<String, Map<String, ActGoodsInfoResult>> redisTemplate3;
    @Resource
    private RedisTemplate<String, Map<String, GoodsFuzzyResult>> redisTemplate4GFR;
    @Resource
    private RedisTemplate<String, Map<String, Goods>> redisTemplate4Goods;
    @Resource
    private RedisTemplate<String, Map<String, Activity>> redisTemplate4Act;
    @Resource
    private ActivityGoodsMapper activityGoodsMapper;
    @Resource
    private ActivityGoodsService activityGoodsService;
    @Resource
    private RedisTemplate<String, Map<String, ActGoodsInfoResult>> redisTemplate4AGR;


    /**
     * 只能对预热中和已开始活动的活动商品排序
     *
     * @param actGoodsInfoResultList the act goods info result list
     * @param appmodelId             the appmodel id
     * @return the list
     * @since v1.2
     */
    public List<ActGoodsInfoResult> sort(List<ActGoodsInfoResult> actGoodsInfoResultList, String appmodelId) {
        List<ActGoodsInfoResult> resultList = new ArrayList<>();
        if (actGoodsInfoResultList != null && !actGoodsInfoResultList.isEmpty()) {
            //获取缓存中的活动
            Map<String, Activity> allCacheIndexAct = activityUtil.getAllCacheIndexAct(appmodelId);

            //将活动按照活动开始时间排序
            List<Activity> activities = new ArrayList<>(allCacheIndexAct.values());
            activities.sort(Comparator.comparing(Activity::getStartTime));

            //将活动商品对象按照活动id分组
            Map<String, List<ActGoodsInfoResult>> groupByActId = actGoodsInfoResultList.stream()
                    .collect(Collectors.groupingBy(o -> o.getActivityId().toString()));

            //将不同场活动的商品分开排序，已开始的活动排序靠前
            for (Activity activity : activities) {
                String actId = activity.getActivityId().toString();
                if (groupByActId.containsKey(actId)) {
                    List<ActGoodsInfoResult> goodsInfoResults = groupByActId.get(actId);
                    //兼容之前版本，默认按照销量排序
                    boolean actGoodsAutoSort =
                            activity.getActGoodsAutoSort() == null ? true : activity.getActGoodsAutoSort();
                    if (actGoodsAutoSort) {
                        //按照销量排序
                        goodsInfoResults.sort((a1, a2) -> {
                            int x = (a2.getSalesVolume() + a2.getActivitySalesVolume()) - (a1.getSalesVolume() + a1
                                    .getActivitySalesVolume());
                            int y = (int) (a1.getGoodsId() - a2.getGoodsId());
                            return x == 0 ? y : x;
                        });
                    } else {
                        //按照手动设置排序
                        Collections.sort(goodsInfoResults);
                    }
                    resultList.addAll(goodsInfoResults);
                }
            }
        }
        return resultList;
    }


    /**
     * 兼容1.2版本之前的活动价格
     *
     * @param activityGoodsList the activity goods list
     */
    public void compatibleActPrice(List<ActivityGoods> activityGoodsList) {
        if (CollectionUtil.isNotEmpty(activityGoodsList)) {
            Map<String, GoodsFuzzyResult> allGoods = redisTemplate4GFR.opsForValue()
                    .get(GroupMallProperties.getRedisPrefix().concat(activityGoodsList.get(0).getAppmodelId()).concat(RedisPrefix.ALL_GOODS));
            if (allGoods != null) {
                activityGoodsList.forEach(o -> {
                    GoodsFuzzyResult goods = allGoods.get(o.getGoodsId().toString());
                    if (goods != null) {
                        if (o.getActivityPrice() == null || o.getActivityPrice().doubleValue() == 0) {
                            BigDecimal actPrice;
                            Double activityDiscount = o.getActivityDiscount();
                            actPrice = goods.getPrice().multiply(new BigDecimal(activityDiscount))
                                    .divide(BigDecimal.valueOf(10), 2, RoundingMode.HALF_UP);
                            o.setActivityPrice(actPrice);
                        }
                    }
                });
            }
        }
    }

    /**
     * 兼容1.2版本之前的活动价格
     *
     * @param activityGoodsList the activity goods list
     */
    void compatibleActPriceForActGoodsInfoResult(List<ActGoodsInfoResult> activityGoodsList) {
        if (activityGoodsList.isEmpty()) {
            return;
        }
        Map<String, GoodsFuzzyResult> allGoods = redisTemplate4GFR.opsForValue()
                .get(GroupMallProperties.getRedisPrefix().concat(activityGoodsList.get(0).getAppmodelId()).concat(RedisPrefix.ALL_GOODS));
        if (allGoods == null) {
            return;
        }
        activityGoodsList.forEach(o -> {
            GoodsFuzzyResult goods = allGoods.get(o.getGoodsId().toString());
            if (goods != null) {
                if (o.getActPrice() == null || o.getActPrice().doubleValue() == 0) {
                    BigDecimal actPrice;
                    Double activityDiscount = o.getActivityDiscount();
                    actPrice = goods.getPrice().multiply(new BigDecimal(activityDiscount))
                            .divide(new BigDecimal(10), 2, RoundingMode.HALF_DOWN);
                    o.setActPrice(actPrice);
                }
            }
        });
    }

    /**
     * 从缓存中获取首页活动商品
     *
     * @param appmodelId the appmodel id
     * @param type       the type
     * @param actId      the act id
     * @return the index act goods cache
     */
    public Map<String, ActivityGoods> getIndexActGoodsCache(String appmodelId, Integer type, Long actId) {
        String redisKey;
        if (ActivityConstant.ACTIVITY_SECKILL.equals(type)) {
            redisKey = GroupMallProperties.getRedisPrefix() + appmodelId + RedisPrefix.INDEX_GROUP_ACTGOODS + actId;
        } else if (ActivityConstant.ACTIVITY_GROUP.equals(type)) {
            redisKey = GroupMallProperties.getRedisPrefix() + appmodelId + RedisPrefix.INDEX_GROUP_ACTGOODS + actId;
        } else {
            throw new ServiceException("活动类型错误");
        }
        Map<String, ActivityGoods> actGoodsMap = redisTemplate.opsForValue().get(redisKey);
        if (actGoodsMap == null) {
            activityUtil.flushIndexActGoodsCache(appmodelId);
            List<ActivityGoods> actGoodsList = activityGoodsMapper.selectActGoodsByActId(actId);
            actGoodsMap = actGoodsList.stream().filter(o -> o.getIndexDisplay() != null && o.getIndexDisplay())
                    .collect(Collectors.toMap(o -> o.getActivityGoodsId().toString(), v -> v));
        }
        return actGoodsMap;
    }

    /**
     * 获取所有活动商品缓存
     *
     * @param appmodelId the appmodel id
     * @return ActivityGoods all act goods cache
     */
    public Map<String, ActivityGoods> getAllActGoodsCache(String appmodelId) {
        String key = GroupMallProperties.getRedisPrefix() + appmodelId + RedisPrefix.ALL_ACTGOODS_1;
        Map<String, ActivityGoods> allActGoodsMap = redisTemplate2.opsForValue().get(key);
        if (allActGoodsMap == null || allActGoodsMap.isEmpty()) {
            activityUtil.flushAllActGoodsCache(appmodelId);
            allActGoodsMap = redisTemplate2.opsForValue().get(key);
        }
        return allActGoodsMap;
    }

    /**
     * 获取所有活动商品缓存
     *
     * @param appmodelId the appmodel id
     * @return ActGoodsInfoResult all act goods result cache
     */
    public Map<String, ActGoodsInfoResult> getAllActGoodsResultCache(String appmodelId) {
        String key = GroupMallProperties.getRedisPrefix() + appmodelId + RedisPrefix.ALL_ACTGOODS;
        Map<String, ActGoodsInfoResult> allActGoodsMap = redisTemplate3.opsForValue().get(key);
        if (allActGoodsMap == null || allActGoodsMap.isEmpty()) {
            activityUtil.flushAllActGoodsCache(appmodelId);
            allActGoodsMap = redisTemplate3.opsForValue().get(key);
        }
        return allActGoodsMap;
    }

    /**
     * This goods on sale boolean.
     *
     * @param actGoodsId the act goods id
     * @param appmodelId the appmodel id
     * @return the boolean
     */
    public boolean thisGoodsOnSale(Long actGoodsId, String appmodelId) {
        String actGoodsKey = GroupMallProperties.getRedisPrefix() + appmodelId + RedisPrefix.ALL_ACTGOODS;
        Map<String, ActGoodsInfoResult> actGoodsMap = redisTemplate4AGR.opsForValue().get(actGoodsKey);
        ActGoodsInfoResult actGoodsInfoResult;
        if (actGoodsMap == null || actGoodsMap.isEmpty()) {
            actGoodsInfoResult = activityGoodsService.getActGoodsById(actGoodsId, appmodelId);
        } else {
            actGoodsInfoResult = actGoodsMap.get(actGoodsId.toString());
            if (actGoodsInfoResult == null) {
                return false;
            }
        }
        Long goodsId = actGoodsInfoResult.getGoodsId();
        String goodsMapKey = GroupMallProperties.getRedisPrefix() + appmodelId + RedisPrefix.ALL_GOODS;
        Map<String, GoodsFuzzyResult> goodsMap = redisTemplate4GFR.opsForValue().get(goodsMapKey);
        GoodsFuzzyResult goodsFuzzyResult;
        if (goodsMap != null) {
            goodsFuzzyResult = goodsMap.get(goodsId.toString());
        } else {
            goodsFuzzyResult = new GoodsFuzzyResult();
        }
        return GoodsConstant.ON_SALE == goodsFuzzyResult.getGoodsStatus();
    }

    /**
     * 获取缓存中的商品信息
     * 用于小程序端的活动商品获取的接口
     *
     * @param activityGoodsList 活动商品列表
     * @param wxuserId          用户id
     * @param appmodelId        小程序模板id
     * @return the result by act goods list 4 wx
     */
    public List<ActGoodsInfoResult> getResultByActGoodsList4Wx(List<ActivityGoods> activityGoodsList, Long wxuserId,
                                                               String appmodelId) {
        List<ActGoodsInfoResult> actGoodsInfoResults = new ArrayList<>();
        if (activityGoodsList != null && activityGoodsList.size() > 0) {
            Map<String, Goods> goodsMap = redisTemplate4Goods.opsForValue()
                    .get(GroupMallProperties.getRedisPrefix().concat(appmodelId).concat(RedisPrefix.ALL_GOODS));
            Map<String, Activity> seckill = redisTemplate4Act.opsForValue()
                    .get(GroupMallProperties.getRedisPrefix().concat(appmodelId).concat(RedisPrefix.INDEX_SECKILL_ACT));
            Map<String, Activity> group = redisTemplate4Act.opsForValue()
                    .get(GroupMallProperties.getRedisPrefix().concat(appmodelId).concat(RedisPrefix.INDEX_GROUP_ACT));
            Map<String, Activity> allMap = new HashMap<>(16);
            if (seckill != null) {
                allMap.putAll(seckill);
            }
            if (group != null) {
                allMap.putAll(group);
            }
            actGoodsInfoResults = this.packagingActGoodsInfoResults(activityGoodsList, goodsMap, allMap, wxuserId);
            //v1.2版本新需求，可以根据销量和手动排序
            actGoodsInfoResults = this.sort(actGoodsInfoResults, appmodelId);
        }
        return actGoodsInfoResults;
    }

    /**
     * 封装活动商品信息
     * 判断用户所在的小区是否排除指定商品
     *
     * @param activityGoodsList 活动商品列表
     * @param goodsMap          商品map
     * @param allMap            所有活动的map
     * @param wxuserId          the wxuser id
     * @return the list
     */
    public List<ActGoodsInfoResult> packagingActGoodsInfoResults(List<ActivityGoods> activityGoodsList, Map goodsMap,
                                                                 Map allMap, Long wxuserId) {
        List<ActGoodsInfoResult> actGoodsInfoResults = new ArrayList<>();
        activityGoodsList.parallelStream().forEach(activityGoods -> {
            GoodsFuzzyResult goods = (GoodsFuzzyResult) goodsMap.get(activityGoods.getGoodsId().toString());
            //在投放区域中
            Activity act = (Activity) allMap.get(activityGoods.getActivityId().toString());
            if (act != null) {
                ActGoodsInfoResult actGoodsInfoResult = this.getActGoodsInfoResultHaveCache(act, activityGoods, goods);
                if (actGoodsInfoResult != null) {
                    actGoodsInfoResult.setActEndDate(act.getEndTime());
                    actGoodsInfoResult.setActivityStatus(act.getStatus());
                    actGoodsInfoResult.setActStartTDate(act.getStartTime());
                    actGoodsInfoResult.setActivityName(act.getActivityName());
                    actGoodsInfoResult.setActivityType(act.getActivityType());
                    actGoodsInfoResult.setActivityImg(act.getActivityPoster());
                    actGoodsInfoResult.setActivitySalesVolume(activityGoods.getActivitySalesVolume());
                    actGoodsInfoResult.setForecastReceiveTime(act.getForecastReceiveTime());
                    actGoodsInfoResult.setGoodsVideoUrl(goods.getGoodsVideoUrl());
                    actGoodsInfoResult.setProviderName(goods.getProviderName());
                    actGoodsInfoResult.setSalesVolume(goods.getSalesVolume());
                    if (ActivityConstant.ACTIVITY_STATUS_START.equals(act.getStatus())
                            || ActivityConstant.ACTIVITY_STATUS_READY.equals(act.getStatus())) {
                        actGoodsInfoResult.setShamSalesVolume(goods.getShamVolume());
                        if (goods.getGoodsStatus().equals(GoodsConstant.SOLD_OUT) || goods.getGoodsDelFlag()) {
                            //正在活动中的商品被强制下架或删除后后，活动商品显示已售罄
                            actGoodsInfoResult.setSoldOutStatus(1);
                        }
                    }
                    if (activityGoods.getSoldOutFlag() != null && activityGoods.getSoldOutFlag() == 1) {
                        actGoodsInfoResult.setSoldOutStatus(1);
                    }
                    //获取实时库存和销量
                    goodsUtil.realTimeStockAndSaleVolume(activityGoods.getAppmodelId(), actGoodsInfoResult);
                    actGoodsInfoResults.add(actGoodsInfoResult);
                }
            }
        });
        //根据用户id筛选出用户能买到的商品
        if (wxuserId != null) {
            Wxuser wxuser = wxuserService.findById(wxuserId);
            Long communityId = wxuser.getCommunityId();
            //当新用户进入时，因为还没有选择小区，则不进行筛选
            if (communityId != null) {
                //根据投放区域筛选
                List<GoodsAreaMapping> communityId1 = goodsAreaMappingService.findByList("communityId", communityId);
                List<Long> collect = communityId1.parallelStream().map(GoodsAreaMapping::getGoodsId).collect(Collectors.toList());
                return actGoodsInfoResults.parallelStream()
                        .filter(actGoodsInfoResult -> collect.contains(actGoodsInfoResult.getGoodsId()))
                        .collect(Collectors.toList());
            }
        }
        return actGoodsInfoResults;
    }

    private ActGoodsInfoResult getActGoodsInfoResultHaveCache(Activity activity, ActivityGoods activityGoods,
                                                              GoodsFuzzyResult goodsFuzzyResult) {
        ActGoodsInfoResult actGoodsInfoResult = null;
        if (goodsFuzzyResult != null) {
            actGoodsInfoResult = new ActGoodsInfoResult();
            actGoodsInfoResult.setActivityGoodsId(activityGoods.getActivityGoodsId());
            actGoodsInfoResult.setActivityDiscount(activityGoods.getActivityDiscount());
            actGoodsInfoResult.setActivityId(activityGoods.getActivityId());
            actGoodsInfoResult.setActivitySalesVolume(activityGoods.getActivitySalesVolume());
            actGoodsInfoResult.setActivityStock(activityGoods.getActivityStock());
            actGoodsInfoResult.setGoodsId(activityGoods.getGoodsId());
            actGoodsInfoResult.setGoodsImg(goodsFuzzyResult.getGoodsImg());
            actGoodsInfoResult.setGoodsName(goodsFuzzyResult.getGoodsName());
            actGoodsInfoResult.setGoodsProperty(goodsFuzzyResult.getGoodsProperty());
            actGoodsInfoResult.setGoodsTitle(goodsFuzzyResult.getGoodsTitle());
            actGoodsInfoResult.setDesc(goodsFuzzyResult.getGoodsDesc());
            actGoodsInfoResult.setIndexDisplay(activityGoods.getIndexDisplay());
            actGoodsInfoResult.setMaxQuantity(activityGoods.getMaxQuantity());
            actGoodsInfoResult.setPreheatStatus(activityGoods.getPreheatStatus());
            actGoodsInfoResult.setPrice(goodsFuzzyResult.getPrice());
            actGoodsInfoResult.setSortPosition(activityGoods.getSortPosition());
            actGoodsInfoResult.setStock(goodsFuzzyResult.getStock());
            actGoodsInfoResult.setText(goodsFuzzyResult.getText());
            actGoodsInfoResult.setAppmodelId(activityGoods.getAppmodelId());
            if (ActivityConstant.ACTIVITY_STATUS_START.equals(activity.getStatus())
                    || ActivityConstant.ACTIVITY_STATUS_READY.equals(activity.getStatus())) {
                actGoodsInfoResult.setShamSalesVolume(goodsFuzzyResult.getShamVolume());
                if (goodsFuzzyResult.getGoodsStatus().equals(GoodsConstant.SOLD_OUT) || goodsFuzzyResult
                        .getGoodsDelFlag()) {
                    //正在活动中的商品被强制下架或删除后后，活动商品显示已售罄
                    actGoodsInfoResult.setSoldOutStatus(1);
                }
            }
            if (activityGoods.getSoldOutFlag() != null && activityGoods.getSoldOutFlag() == 1) {
                actGoodsInfoResult.setSoldOutStatus(1);
            }
            BigDecimal discount = new BigDecimal(0);
            if (activityGoods.getActivityDiscount() != null) {
                discount = BigDecimal.valueOf(activityGoods.getActivityDiscount())
                        .divide(BigDecimal.valueOf(10), 2, BigDecimal.ROUND_HALF_UP);
            }
            BigDecimal actPrice = activityGoods.getActivityPrice();
            if (actPrice == null) {
                actPrice = NumberUtil.mul(goodsFuzzyResult.getPrice(), discount);
            }
            if (actPrice.doubleValue() < OrderConstant.MIN_PAYFEE) {
                actGoodsInfoResult.setActPrice(BigDecimal.valueOf(OrderConstant.MIN_PAYFEE));
            } else {
                actGoodsInfoResult.setActPrice(actPrice);
            }
        }
        return actGoodsInfoResult;
    }

}
