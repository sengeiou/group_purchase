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
import cn.hutool.core.date.DateUtil;
import com.mds.group.purchase.activity.dao.ActivityGoodsMapper;
import com.mds.group.purchase.activity.model.Activity;
import com.mds.group.purchase.activity.model.ActivityGoods;
import com.mds.group.purchase.activity.result.ActGoodsInfoResult;
import com.mds.group.purchase.activity.service.ActivityGoodsService;
import com.mds.group.purchase.activity.service.ActivityService;
import com.mds.group.purchase.activity.vo.ActivityVo;
import com.mds.group.purchase.configurer.GroupMallProperties;
import com.mds.group.purchase.constant.ActiviMqQueueName;
import com.mds.group.purchase.constant.ActivityConstant;
import com.mds.group.purchase.constant.RedisPrefix;
import com.mds.group.purchase.core.CodeMsg;
import com.mds.group.purchase.exception.GlobalException;
import com.mds.group.purchase.exception.ServiceException;
import com.mds.group.purchase.goods.model.GoodsDetail;
import com.mds.group.purchase.goods.result.GoodsFuzzyResult;
import com.mds.group.purchase.goods.service.GoodsDetailService;
import com.mds.group.purchase.jobhandler.ActiveDelaySendJobHandler;
import com.mds.group.purchase.jobhandler.to.ActiveMqTaskTO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 活动相关公共方法
 *
 * @author shuke
 */
@Component
public class ActivityUtil {

    @Resource
    private GoodsUtil goodsUtil;
    @Resource
    private MongoTemplate mongoTemplate;
    @Resource
    private RedisTemplate<String, Map<String, ActGoodsInfoResult>> redisTemplate;
    @Resource
    private RedisTemplate<String, Boolean> redisTemplate4Boolean;
    @Resource
    private RedisTemplate<String, Map<String, GoodsFuzzyResult>> redisTemplate4GFR;
    @Resource
    private RedisTemplate<String, Integer> redisTemplate4Int;
    @Resource
    private RedisTemplate<String, Map<String, ActivityGoods>> redisTemplate4ActG;
    @Resource
    private RedisTemplate<String, Map<String, Activity>> redisTemplate4Act;
    @Resource
    private ActivityService activityService;
    @Resource
    private ActivityGoodsUtil activityGoodsUtil;
    @Resource
    private GoodsDetailService goodsDetailService;
    @Resource
    private ActivityGoodsMapper activityGoodsMapper;
    @Resource
    private ActivityGoodsService activityGoodsService;
    @Resource
    private ActiveDelaySendJobHandler activeDelaySendJobHandler;


    /**
     * 刷新所有活动商品缓存
     *
     * @param appmodelId the appmodel id
     */
    public void flushAllActGoodsCache(String appmodelId) {
        //刷新所有活动商品缓存
        List<Activity> acts = activityService.findActs(appmodelId);
        if (CollectionUtil.isNotEmpty(acts)) {
            String redisKeyAllActGoods =
                    GroupMallProperties.getRedisPrefix().concat(appmodelId).concat(RedisPrefix.ALL_ACTGOODS);
            List<ActGoodsInfoResult> activityGoodsList = activityGoodsService
                    .getActGoodsByActIds(acts.stream().map(Activity::getActivityId).collect(Collectors.toList()));
            //兼容v1.2版本之前的活动价格
            activityGoodsUtil.compatibleActPriceForActGoodsInfoResult(activityGoodsList);
            Map<String, ActGoodsInfoResult> collect = activityGoodsList.stream()
                    .collect(Collectors.toMap(o -> o.getActivityGoodsId().toString(), v -> v));
            redisTemplate.opsForValue().set(redisKeyAllActGoods, collect, RedisPrefix.EXPIRATION_TIME, TimeUnit.HOURS);

            List<ActivityGoods> actGoodsList = activityGoodsService
                    .getActGoodsByActIdList(acts.stream().map(Activity::getActivityId).collect(Collectors.toList()));
            //兼容v1.2版本之前的活动价格
            activityGoodsUtil.compatibleActPrice(actGoodsList);
            String redisKeyAllActGoods1 =
                    GroupMallProperties.getRedisPrefix().concat(appmodelId).concat(RedisPrefix.ALL_ACTGOODS_1);
            Map<String, ActivityGoods> collect2 = actGoodsList.stream()
                    .collect(Collectors.toMap(o -> o.getActivityGoodsId().toString(), v -> v));
            redisTemplate4ActG.opsForValue()
                    .set(redisKeyAllActGoods1, collect2, RedisPrefix.EXPIRATION_TIME, TimeUnit.HOURS);
        }

    }

    /**
     * 刷新首页活动商品缓存
     *
     * @param appmodelId the appmodel id
     */
    public void flushIndexActGoodsCache(String appmodelId) {
        StringBuilder redisKey1 = new StringBuilder();
        redisKey1.append(GroupMallProperties.getRedisPrefix()).append(appmodelId).append(RedisPrefix.INDEX_SECKILL_ACTGOODS);
        StringBuilder redisKey2 = new StringBuilder();
        redisKey2.append(GroupMallProperties.getRedisPrefix()).append(appmodelId).append(RedisPrefix.INDEX_GROUP_ACTGOODS);
        List<Activity> acts = activityService.findActs(appmodelId);
        List<Activity> groupAct = new ArrayList<>();
        List<Activity> skillAct = new ArrayList<>();
        acts.forEach(o -> {
            if (o.getActivityType() == 1) {
                skillAct.add(o);
            } else {
                groupAct.add(o);
            }
        });
        this.sada(redisKey1, skillAct);
        this.sada(redisKey2, groupAct);
    }

    /**
     * 将活动商品存入redis
     */
    private void sada(StringBuilder redisKey, List<Activity> act) {
        if (!act.isEmpty()) {
            List<Long> actIds = act.stream().map(Activity::getActivityId).collect(Collectors.toList());
            List<ActivityGoods> activityGoodsList = activityGoodsMapper.selectActGoodsByActIds(actIds);
            activityGoodsList = activityGoodsList.stream().filter(ActivityGoods::getIndexDisplay)
                    .collect(Collectors.toList());
            //兼容v1.2版本之前的活动价格
            activityGoodsUtil.compatibleActPrice(activityGoodsList);
            Map<String, ActivityGoods> activityGoodsMap2 = activityGoodsList.stream()
                    .filter(ActivityGoods::getIndexDisplay)
                    .collect(Collectors.toMap(o -> o.getActivityGoodsId().toString(), v -> v));
            redisTemplate4ActG.opsForValue()
                    .set(redisKey.toString(), activityGoodsMap2, RedisPrefix.EXPIRATION_TIME, TimeUnit.HOURS);
        }
    }

    /**
     * //刷新所有活动列表中缓存
     *
     * @param appmodelId the appmodel id
     */
    public void flushActivityCache(String appmodelId) {
        //刷新所有活动列表中缓存
        Map<String, Activity> allActivitiesMap = activityService.findActs(appmodelId).stream()
                .collect(Collectors.toMap(o -> o.getActivityId().toString(), v -> v));
        Iterator<Map.Entry<String, Activity>> iterator = allActivitiesMap.entrySet().iterator();
        Map<String, Activity> groupActMap = new HashMap<>(8);
        Map<String, Activity> skillActMap = new HashMap<>(8);
        while (iterator.hasNext()) {
            Map.Entry<String, Activity> next = iterator.next();
            Activity activity = next.getValue();
            if (activity.getActivityType() == 1) {
                skillActMap.put(activity.getActivityId().toString(), activity);
            } else {
                groupActMap.put(activity.getActivityId().toString(), activity);
            }
        }
        redisTemplate4Act.opsForValue().set(GroupMallProperties.getRedisPrefix() + appmodelId + RedisPrefix.INDEX_SECKILL_ACT, skillActMap,
                RedisPrefix.EXPIRATION_TIME, TimeUnit.HOURS);
        redisTemplate4Act.opsForValue()
                .set(GroupMallProperties.getRedisPrefix() + appmodelId + RedisPrefix.INDEX_GROUP_ACT, groupActMap,
                        RedisPrefix.EXPIRATION_TIME,
                        TimeUnit.HOURS);
    }

    /**
     * 将活动中的所有商品库存还给普通商品
     *
     * @param activityId the activity id
     * @param appmodelId the appmodel id
     */
    public void returnGoodsStock(Long activityId, String appmodelId) {
        //删除活动时将原来的活动商品库存还给普通商品
        List<ActivityGoods> actGoods = activityGoodsMapper.selectActGoodsByActId(activityId);
        activityGoodsMapper.updatePreheatByActId(activityId, ActivityConstant.ACTIVITY_GOODS_DONT_SHOW);
        returnStock(actGoods);
        //刷新所有缓存
        goodsUtil.flushGoodsCache(appmodelId);
    }

    /**
     * 指定活动商品归还库存
     *
     * @param actGoods the act goods
     */
    public void returnGoodsStock(List<ActivityGoods> actGoods) {
        returnStock(actGoods);
    }

    private void returnStock(List<ActivityGoods> actGoods) {
        String goodsIds = actGoods.parallelStream().map(obj -> obj.getGoodsId().toString())
                .collect(Collectors.joining(","));
        List<GoodsDetail> byGoodsIds = goodsDetailService.findByGoodsIds(goodsIds);
        Map<Long, GoodsDetail> collect = byGoodsIds.parallelStream()
                .collect(Collectors.toMap(GoodsDetail::getGoodsId, v -> v));
        List<GoodsDetail> goodsDetailList = new ArrayList<>();
        actGoods.forEach(actGood -> {
            GoodsDetail goodsDetail = collect.get(actGood.getGoodsId());
            //将剩余的活动商品库存还回到实际库存
            goodsDetail.setStock(goodsDetail.getStock() + actGood.getActivityStock());
            goodsDetailList.add(goodsDetail);
        });
        goodsDetailService.updateList(goodsDetailList);

    }

    /**
     * 1、更新拼团活动列表
     * 2、刷新首页活动商品缓存
     * 3、刷新所有活动商品缓存
     *
     * @param appmodelId the appmodel id
     */
    public void flushMethod(String appmodelId) {
        //更新拼团活动列表
        flushActivityCache(appmodelId);
        //刷新首页活动商品缓存
        flushIndexActGoodsCache(appmodelId);
        //刷新所有活动商品缓存
        flushAllActGoodsCache(appmodelId);
    }

    /**
     * 判断是否在预热时间之内，不在内则更新活动预热状态
     *
     * @param activityVo the activity vo
     */
    public void startActivityMq(ActivityVo activityVo) {
        //mili为活动开始时间减去活动预热时间再减去当前时间得到的毫秒数
        Long msec = null;
        if (StringUtils.isNotBlank(activityVo.getReadyTime())) {
            String[] time = activityVo.getReadyTime().split(":");
            String hour = time[0], minute = time[1];
            //计算时间
            msec = (Long.parseLong(hour) * 60 * 60 * 1000);
            msec += (Long.parseLong(minute) * 60 * 1000);
        }
        //如果没有计算预热所需时间则无预热
        if (msec == null) {
            msec = DateUtil.parse(activityVo.getStartTime()).getTime();
        } else {
            msec = DateUtil.parse(activityVo.getStartTime()).getTime() - msec;
        }
        activeDelaySendJobHandler.savaTask(activityVo.getActivityId().toString(), ActiviMqQueueName.READY_ACTIVITY_V1,
                msec - System.currentTimeMillis(), activityVo.getAppmodelId(), true);
        //判断当前时间是否在预热时间段内
        //不再预热时间段，更新活动为未开始状态(防止修改时原本在预热时间段之内,修改之后没有在预热时间之内,出现商品库存为0)
        if (System.currentTimeMillis() < msec) {
            Activity now = new Activity();
            now.setActivityId(activityVo.getActivityId());
            now.setStatus(ActivityConstant.ACTIVITY_STATUS_DNS);
            activityService.update(now);
        }
    }

    /**
     * 删除活动时将mongodb里面的该活动对应的队列记录删除
     *
     * @param activityId the activity id
     */
    public void removeMongodbValueById(Long activityId) {
        //删除活动时将mongodb里面的该活动对应的队列记录删除
        Query query = new Query();
        List<String> list = new ArrayList<>();
        list.add(ActiviMqQueueName.START_ACTIVITY_V1);
        list.add(ActiviMqQueueName.READY_ACTIVITY_V1);
        list.add(ActiviMqQueueName.END_ACTIVITY_V1);
        String pattern = ".*?".concat(activityId.toString()).concat(".*");
        query.addCriteria(Criteria.where("queueName").in(list).and("jsonData").regex(pattern));
        List<ActiveMqTaskTO> activeMqTaskTOS = mongoTemplate.find(query, ActiveMqTaskTO.class);
        if (activeMqTaskTOS != null) {
            for (ActiveMqTaskTO activeMqTaskTO : activeMqTaskTOS) {
                mongoTemplate.remove(activeMqTaskTO);
            }
        }
    }

    /**
     * Remove mongodb colse order info.
     *
     * @param orderId    the order id
     * @param appmodelId the appmodel id
     */
    public void removeMongodbColseOrderInfo(List<Long> orderId, String appmodelId) {
        //删除活动时将mongodb里面的该活动对应的队列记录删除
        Query query = new Query();

        query.addCriteria(
                Criteria.where("queueName").in("GroupMall_order_close").and("appmodelId").is(appmodelId).and(
                        "jsonData")
                        .and("orderIds").in(orderId));
        List<ActiveMqTaskTO> activeMqTaskTOS = mongoTemplate.find(query, ActiveMqTaskTO.class);
        if (activeMqTaskTOS != null) {
            for (ActiveMqTaskTO activeMqTaskTO : activeMqTaskTOS) {
                mongoTemplate.remove(activeMqTaskTO);
            }
        }
    }


    /**
     * 活动开始时将活动商品的库存标记进行初始化
     *
     * @param actId the act id
     */
    @SuppressWarnings("unchecked")
    public void afterPropertiesSet(Long actId) {
        List<ActivityGoods> goodsList = activityGoodsService.findByActId(actId);
        if (CollectionUtil.isNotEmpty(goodsList)) {
            String redisKeyPrefix = GroupMallProperties.getRedisPrefix().concat(goodsList.get(0).getAppmodelId());
            String redisKeyActGoodsStock = redisKeyPrefix.concat(RedisPrefix.ACT_GOODS_STOCK);
            String redisKeyActGoodsSalesVolume = redisKeyPrefix.concat(RedisPrefix.ACT_GOODS_SALES_VOLUME);
            String maxQuantityKey = redisKeyPrefix.concat(RedisPrefix.MAX_QUANTITY);
            for (ActivityGoods activityGoods : goodsList) {
                //将活动商品的库存存入redis
                String redisKey = redisKeyActGoodsStock.concat(activityGoods.getActivityGoodsId().toString());
                redisTemplate4Int.opsForValue().set(redisKey, activityGoods.getActivityStock(), 3, TimeUnit.DAYS);
                //将活动销量在redis初始化
                String redisKeySaleVolum = redisKeyActGoodsSalesVolume
                        .concat(activityGoods.getActivityGoodsId().toString());
                redisTemplate4Int.opsForValue().set(redisKeySaleVolum, 0, 3, TimeUnit.DAYS);
                //如果活动商品有限购数量,则将限售量存入redis
                if (activityGoods.getMaxQuantity() != null) {
                    String redisKey4limit = maxQuantityKey.concat(activityGoods.getActivityGoodsId().toString());
                    redisTemplate4Int.opsForValue()
                            .set(redisKey4limit, activityGoods.getMaxQuantity(), 3, TimeUnit.DAYS);
                    //一级缓存存入活动商品限购数量
                }
            }
        }
    }


    /**
     * Act price assert.
     *
     * @param activityVo the activity vo
     */
    @Deprecated
    public void actPriceAssert(ActivityVo activityVo) {
        //判断设置的活动价是否小于团长佣金
        List<ActivityGoods> actGoodsList = activityVo.getActGoodsList();
        if (actGoodsList != null) {
            actGoodsList.forEach(activityGoods -> {
                Double dis = activityGoods.getActivityDiscount();
                Long goodsId = activityGoods.getGoodsId();
                Map<String, GoodsFuzzyResult> goodsMap = redisTemplate4GFR.opsForValue()
                        .get(GroupMallProperties.getRedisPrefix().concat(activityVo.getAppmodelId()).concat(RedisPrefix.ALL_GOODS));
                if (goodsMap != null) {
                    GoodsFuzzyResult goods = goodsMap.get(goodsId.toString());
                    if (goods != null) {
                        double price = goods.getPrice().doubleValue();
                        double commission = goods.getGroupLeaderCommission().doubleValue();

                        if (price * dis / 10 < commission && goods.getCommissionType() == GoodsFuzzyResult.CommissionType.FIXED_AMOUNT) {
                            throw new GlobalException(
                                    CodeMsg.FAIL.fillArgs("商品《" + goods.getGoodsName() + " 》的活动价格小于团长佣金，请重新设置"));
                        }
                    }
                }
            });
        }
    }

    /**
     * 判断活动价是否小于团长佣金
     *
     * @param activityVo the activity vo
     * @since v1.2
     */
    public void actPriceAssertV12(ActivityVo activityVo) {
        //判断设置的活动价是否小于团长佣金
        List<ActivityGoods> actGoodsList = activityVo.getActGoodsList();
        //从缓存中获取所有商品
        Map<String, GoodsFuzzyResult> goodsMap = redisTemplate4GFR.opsForValue()
                .get(GroupMallProperties.getRedisPrefix().concat(activityVo.getAppmodelId()).concat(RedisPrefix.ALL_GOODS));
        if (actGoodsList != null && goodsMap != null) {
            actGoodsList.forEach(activityGoods -> {
                Long goodsId = activityGoods.getGoodsId();
                GoodsFuzzyResult goods = goodsMap.get(goodsId.toString());
                Double price;
                double commission;
                if (goods != null) {
                    price = goods.getPrice().doubleValue();
                    commission = goods.getGroupLeaderCommission().doubleValue();
                    if (activityGoods.getActivityDiscount() != null && activityGoods.getActivityDiscount() != 0) {
                        //兼容老版本不能直接设置活动价格
                        Double dis = activityGoods.getActivityDiscount();
                        if (price * dis / 10 < commission && goods.getCommissionType() == GoodsFuzzyResult.CommissionType.FIXED_AMOUNT) {
                            throw new GlobalException(
                                    CodeMsg.FAIL.fillArgs("商品《" + goods.getGoodsName() + " 》的活动价格小于团长佣金，请重新设置"));
                        }
                    } else {
                        //v1.2版本判断佣金是否大于价格
                        if (activityGoods.getActivityPrice().doubleValue() < commission
                                && goods.getCommissionType() == 2) {
                            throw new GlobalException(
                                    CodeMsg.FAIL.fillArgs("商品《" + goods.getGoodsName() + " 》的活动价格小于团长佣金，请重新设置"));
                        }
                    }
                    //判断活动价是否小于原价
                    if (goods.getPrice().compareTo(activityGoods.getActivityPrice()) < 0) {
                        throw new GlobalException(
                                CodeMsg.FAIL.fillArgs("商品《" + goods.getGoodsName() + " 》的活动价格大于商品原价，请重新设置"));
                    }
                }
            });
        }
    }

    /**
     * 获取所有缓存中的预热和开始的活动
     *
     * @param appmodelId the appmodel id
     * @return the all cache index act
     * @since v1.2
     */
    Map<String, Activity> getAllCacheIndexAct(String appmodelId) {
        //获取缓存中的活动
        Map<String, Activity> indexSecAct = redisTemplate4Act.opsForValue()
                .get(GroupMallProperties.getRedisPrefix() + appmodelId + RedisPrefix.INDEX_SECKILL_ACT);
        Map<String, Activity> indexGroupAct = redisTemplate4Act.opsForValue()
                .get(GroupMallProperties.getRedisPrefix() + appmodelId + RedisPrefix.INDEX_GROUP_ACT);
        Map<String, Activity> allIndexAct = new HashMap<>(4);
        if (indexGroupAct != null) {
            allIndexAct.putAll(indexGroupAct);
        }
        if (indexSecAct != null) {
            allIndexAct.putAll(indexSecAct);
        }
        return allIndexAct;
    }


    /**
     * 设置活动更新的状态为true
     *
     * @param appmodelId the appmodel id
     * @since v1.2
     */
    public void setActivityUpdate(String appmodelId) {
        String key1 = GroupMallProperties.getRedisPrefix() + appmodelId + RedisPrefix.ACT_UPDATE_STATUS + "index";
        String key2 = GroupMallProperties.getRedisPrefix() + appmodelId + RedisPrefix.ACT_UPDATE_STATUS + "class";
        redisTemplate4Boolean.opsForValue().set(key1, true);
        redisTemplate4Boolean.opsForValue().set(key2, true);
    }

    /**
     * 得到进行中和预热中的秒杀/拼团活动
     *
     * @param appmodelId the appmodel id
     * @param type       the type
     * @return the list
     */
    public List<Activity> actOnDoingAndPreheat(String appmodelId, Integer type) {
        //得到进行中和预热中的秒杀/拼团活动
        String redisKey;
        if (ActivityConstant.ACTIVITY_SECKILL.equals(type)) {
            redisKey = GroupMallProperties.getRedisPrefix() + appmodelId + RedisPrefix.INDEX_SECKILL_ACT;
        } else if (ActivityConstant.ACTIVITY_GROUP.equals(type)) {
            redisKey = GroupMallProperties.getRedisPrefix() + appmodelId + RedisPrefix.INDEX_GROUP_ACT;
        } else {
            throw new ServiceException("活动类型错误");
        }
        Map<String, Activity> activitiesSkillMap = redisTemplate4Act.opsForValue().get(redisKey);
        List<Activity> activities;
        if (activitiesSkillMap == null) {
            activities = activityService.findAct(appmodelId, type);
            activitiesSkillMap = activities.stream().collect(Collectors.toMap(o->o.getActivityId().toString(),
                    v -> v));
            redisTemplate4Act.opsForValue().set(redisKey, activitiesSkillMap, RedisPrefix.EXPIRATION_TIME, TimeUnit.HOURS);
        } else {
            activities = new ArrayList<>(activitiesSkillMap.values());
        }
        return activities;
    }
}
