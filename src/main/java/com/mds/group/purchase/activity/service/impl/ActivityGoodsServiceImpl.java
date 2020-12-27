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

package com.mds.group.purchase.activity.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.NumberUtil;
import com.mds.group.purchase.activity.dao.ActivityGoodsMapper;
import com.mds.group.purchase.activity.model.Activity;
import com.mds.group.purchase.activity.model.ActivityGoods;
import com.mds.group.purchase.activity.result.ActGoodsInfoResult;
import com.mds.group.purchase.activity.result.IndexSeckillResult;
import com.mds.group.purchase.activity.service.ActivityGoodsService;
import com.mds.group.purchase.activity.service.ActivityService;
import com.mds.group.purchase.configurer.GroupMallProperties;
import com.mds.group.purchase.constant.*;
import com.mds.group.purchase.core.AbstractService;
import com.mds.group.purchase.exception.ServiceException;
import com.mds.group.purchase.goods.model.Goods;
import com.mds.group.purchase.goods.model.GoodsClass;
import com.mds.group.purchase.goods.model.GoodsClassMapping;
import com.mds.group.purchase.goods.model.GoodsDetail;
import com.mds.group.purchase.goods.service.GoodsClassMappingService;
import com.mds.group.purchase.goods.service.GoodsClassService;
import com.mds.group.purchase.goods.service.GoodsDetailService;
import com.mds.group.purchase.goods.service.GoodsService;
import com.mds.group.purchase.utils.ActivityGoodsUtil;
import com.mds.group.purchase.utils.ActivityUtil;
import com.mds.group.purchase.utils.GoodsUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;


/**
 * 活动商品service实现类
 *
 * @author shuke
 * @date 2018 /12/03
 */
@Service
public class ActivityGoodsServiceImpl extends AbstractService<ActivityGoods> implements ActivityGoodsService {

    @Resource
    private GoodsUtil goodsUtil;
    @Resource
    private GoodsService goodsService;
    @Resource
    private ActivityUtil activityUtil;
    @Resource
    private RedisTemplate<String, Map<String, Goods>> redisTemplate4Goods;
    @Resource
    private RedisTemplate<String, Map<String, Activity>> redisTemplate4Act;
    @Resource
    private ActivityService activityService;
    @Resource
    private ActivityGoodsUtil activityGoodsUtil;
    @Resource
    private GoodsClassService goodsClassService;
    @Resource
    private GoodsDetailService goodsDetailService;
    @Resource
    private ActivityGoodsMapper tActivityGoodsMapper;
    @Resource
    private GoodsClassMappingService goodsClassMappingService;

    @Override
    public void deleteBatch(List<Long> delGoodsIdList) {
        if (delGoodsIdList != null && !delGoodsIdList.isEmpty()) {
            //批量删除活动商品
            tActivityGoodsMapper.deleteBatch(delGoodsIdList);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateBatch(List<ActivityGoods> goodsList) {
        for (ActivityGoods goods : goodsList) {
            tActivityGoodsMapper.updateByPrimaryKeySelective(goods);
        }
    }

    @Override
    public void reduceStockAndaddVolume(Long activityGoodsId, Integer goodsNum) {
        ActivityGoods activityGoods = tActivityGoodsMapper.selectByPrimaryKey(activityGoodsId);
        if (activityGoods != null) {
            tActivityGoodsMapper.reduceStockAndaddVolume(activityGoodsId, goodsNum);
        } else {
            throw new ServiceException("活动商品不存在或id错误");
        }
    }

    @Override
    public List<ActivityGoods> findByActId(Long activityId) {
        return tActivityGoodsMapper.selectActGoodsByActId(activityId);
    }

    @Override
    public List<ActivityGoods> findByActIds(List<Long> activityIds) {
        List<ActivityGoods> activityGoods = new ArrayList<>();
        if (CollectionUtil.isNotEmpty(activityIds)) {
            activityGoods = tActivityGoodsMapper.selectActGoodsByActIds(activityIds);
        }
        return activityGoods;
    }

    @Override
    public void updateActGoodsSortList(List<ActivityGoods> actGoodsList) {
        if (CollectionUtil.isNotEmpty(actGoodsList)) {
            List<ActivityGoods> actGoodsList1 = new ArrayList<>();
            actGoodsList.forEach(o -> {
                ActivityGoods activityGoods = new ActivityGoods();
                activityGoods.setActivityGoodsId(o.getActivityGoodsId());
                activityGoods.setSortPosition(o.getSortPosition());
                actGoodsList1.add(activityGoods);
            });
            actGoodsList1.forEach(o -> tActivityGoodsMapper.updateByPrimaryKeySelective(o));
        }
    }

    @Override
    public List<ActivityGoods> selectByActGoodsIds(List<Long> actGoodsIds) {
        return tActivityGoodsMapper.selectByActGoodsIds(actGoodsIds);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateActGoodsJoinSolitaire(List<ActivityGoods> actGoodsList) {
        if (CollectionUtil.isNotEmpty(actGoodsList)){
            //提取参加接龙的活动商品id
            String actGoodsIds1 = actGoodsList.stream().filter(ActivityGoods::getJoinSolitaire)
                    .map(o -> o.getActivityGoodsId().toString()).collect(Collectors.joining(Common.REGEX));
            //提取不参加接龙的活动商品id
            String actGoodsIds2 = actGoodsList.stream().filter(o -> !o.getJoinSolitaire())
                    .map(o -> o.getActivityGoodsId().toString()).collect(Collectors.joining(Common.REGEX));
            if (StringUtils.isNotBlank(actGoodsIds1)) {
                tActivityGoodsMapper.updateJoinSolitaireByIds(actGoodsIds1,true);
            }
            if (StringUtils.isNotBlank(actGoodsIds2)) {
                tActivityGoodsMapper.updateJoinSolitaireByIds(actGoodsIds1,false);
            }
        }
    }

    @Override
    public void updatePreheatStatus(Long activityId, Integer activityGoodsPreheat) {
        tActivityGoodsMapper.updatePreheatByActId(activityId, activityGoodsPreheat);
    }

    @Override
    public void updateActGoodsPreheatStatusById(Long actGoodsId, Integer activityGoodsPreheat) {
        ActivityGoods activityGoods = new ActivityGoods();
        activityGoods.setActivityGoodsId(actGoodsId);
        activityGoods.setPreheatStatus(activityGoodsPreheat);
        update(activityGoods);
    }

    @Override
    public ActGoodsInfoResult getActGoodsInfo(Long actGoodsId) {
        ActivityGoods activityGoods = tActivityGoodsMapper.selectByPrimaryKey(actGoodsId);
        if (activityGoods == null) {
            throw new ServiceException("活动商品不存在");
        }
        List<ActivityGoods> activityGoodsList = new ArrayList<>();
        activityGoodsList.add(activityGoods);
        return activityGoodsUtil.getResultByActGoodsList4Wx(activityGoodsList, null, activityGoods.getAppmodelId()).get(0);
    }

    @Override
    public List<ActGoodsInfoResult> getIndexGroupActGoods(Long wxuserId, String appmodelId) {
        List<ActGoodsInfoResult> actGoodsList4Wx = new ArrayList<>();
        //1、获取所有已开始和预热中拼团活动
        List<Activity> activities = activityUtil.actOnDoingAndPreheat(appmodelId, ActivityConstant.ACTIVITY_GROUP);
        if (CollectionUtil.isNotEmpty(activities)) {
            //按活动开始时间排序活动
            activities.sort(Comparator.comparing(Activity::getStartTime));
            Activity activity = activities.get(0);
            //获取当前拼团活动的商品缓
            Map<String, ActivityGoods> actGoodsMap = activityGoodsUtil
                    .getIndexActGoodsCache(appmodelId, ActivityConstant.ACTIVITY_GROUP, activity.getActivityId());
            //遍历activityGoodsMap获取首页商品
            Iterator<Map.Entry<String, ActivityGoods>> iterator = actGoodsMap.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, ActivityGoods> next = iterator.next();
                ActivityGoods activityGoods = next.getValue();
                //筛选首页显示的商品
                if (activityGoods.getIndexDisplay() == null || !activityGoods.getIndexDisplay()) {
                    iterator.remove();
                }
            }
            List<ActivityGoods> list = new ArrayList<>(actGoodsMap.values());
            actGoodsList4Wx = activityGoodsUtil.getResultByActGoodsList4Wx(list, wxuserId, appmodelId);
        }
        return activityGoodsUtil.sort(actGoodsList4Wx, appmodelId);
    }

    @Override
    public List<ActGoodsInfoResult> getActGoodsByClass(String appmodelId, Long goodsClassId, Long wxuserId) {
        List<ActivityGoods> activityGoodsList = new ArrayList<>();
        if (goodsClassId == -1) {
            //全部/分类：后台创建多场活动，小程序端只显示2场活动（进行中/即将开始），进行中活动已结束去掉，下一场活动显示小程序端（顶替已结束的活动）
            List<Activity> activities = activityUtil.actOnDoingAndPreheat(appmodelId, ActivityConstant.ACTIVITY_GROUP);
            if (CollectionUtil.isNotEmpty(activities)) {
                //2、按活动开始时间排序活动
                activities.sort(Comparator.comparing(Activity::getStartTime));
                //前两个活动
                List<Long> actIds = activities.stream().limit(2).map(Activity::getActivityId)
                        .collect(Collectors.toList());
                activityGoodsList = tActivityGoodsMapper.selectActGoodsByActIds(actIds);
            }
        } else {
            List<GoodsClass> goodsClassList = goodsClassService.getGoodsClassesAndUnderClass(goodsClassId);
            if (goodsClassList == null || goodsClassList.isEmpty()) {
                throw new ServiceException("商品分类不存在");
            }
            String goodsClassIds = goodsClassList.stream().map(o -> o.getGoodsClassId().toString())
                    .collect(Collectors.joining(","));
            List<GoodsClassMapping> goodsClassMappings = goodsClassMappingService
                    .findByList("goodsClassId", goodsClassIds);
            if (CollectionUtil.isNotEmpty(goodsClassMappings)) {
                //得到分类下的预热和进行中活动商品
                List<Long> goodsIdsList = goodsClassMappings.stream().map(GoodsClassMapping::getGoodsId).distinct()
                        .collect(Collectors.toList());
                Map<String, ActivityGoods> allActGoodsMap = activityGoodsUtil.getAllActGoodsCache(appmodelId);
                List<ActivityGoods> allActGoods = new ArrayList<>(allActGoodsMap.values());
                activityGoodsList = allActGoods.stream()
                        .filter(o -> goodsIdsList.contains(o.getGoodsId()) && ActivityConstant.ACTIVITY_GROUP
                                .equals(o.getActivityType()) && 0 != o.getPreheatStatus() && goodsIdsList
                                .contains(o.getGoodsId())).collect(Collectors.toList());
            }
        }
        return activityGoodsUtil.getResultByActGoodsList4Wx(activityGoodsList, wxuserId, appmodelId);
    }

    @Override
    public List<GoodsClass> getGoodsClass(String appmodelId, Long wxuserId) {
        List<GoodsClass> goodsClasses = new ArrayList<>();
        //得到预热活动和开始活动的所有拼团活动
        List<Activity> activities = activityService.findAct(appmodelId, ActivityConstant.ACTIVITY_GROUP);
        List<Long> actIds = activities.stream().map(Activity::getActivityId).collect(Collectors.toList());
        if (actIds.isEmpty()) {
            return goodsClasses;
        }
        //得到所有活动商品
        List<ActivityGoods> actGoods = tActivityGoodsMapper.selectActGoodsByActIds(actIds);
        List<ActGoodsInfoResult> actGoodsInfoResults = activityGoodsUtil.getResultByActGoodsList4Wx(actGoods, wxuserId, appmodelId);
        List<Long> goodsIdList = actGoodsInfoResults.stream().map(ActGoodsInfoResult::getGoodsId).distinct()
                .collect(Collectors.toList());
        List<Long> goodsClassIds = new ArrayList<>();
        for (Long id : goodsIdList) {
            List<GoodsClassMapping> list = goodsClassMappingService.findByList("goodsId", id);
            List<Long> classIds = list.stream().map(GoodsClassMapping::getGoodsClassId).collect(Collectors.toList());
            goodsClassIds.addAll(classIds);
        }
        goodsClassIds = goodsClassIds.stream().distinct().collect(Collectors.toList());
        //得到分类列表
        if (CollectionUtil.isNotEmpty(goodsClassIds)) {
            String classIds = goodsClassIds.stream().map(Object::toString).collect(Collectors.joining(","));
            List<GoodsClass> goodsClassList = goodsClassService.findByIds(classIds);
            goodsClassList.forEach(goodsClass -> {
                if (goodsClass.getFatherId() == 0) {
                    goodsClasses.add(goodsClass);
                } else {
                    GoodsClass fatherClass = goodsClassService.findById(goodsClass.getFatherId());
                    if (fatherClass.getFatherId() == 0) {
                        goodsClasses.add(fatherClass);
                    }
                }
            });
        }
        return goodsClasses.stream().distinct().collect(Collectors.toList());
    }

    @Override
    public Integer countByActId(Long activityId) {
        return tActivityGoodsMapper.countByActId(activityId);
    }

    @Override
    public List<ActGoodsInfoResult> getActGoodsByActId4Pc(Long activityId) {
        List<Long> list = new ArrayList<>();
        list.add(activityId);
        List<ActivityGoods> activityGoodsList = tActivityGoodsMapper.selectActGoodsByActIds(list);
        return this.getResultByActGoodsList4Pc(activityGoodsList);
    }

    @Override
    public List<ActGoodsInfoResult> getActGoodsByActIds(List<Long> activityIds) {
        if (activityIds != null && !activityIds.isEmpty()) {
            List<ActivityGoods> activityGoodsList = tActivityGoodsMapper.selectActGoodsByActIds(activityIds);
            return this.getResultByActGoodsList4Pc(activityGoodsList);
        }
        return new ArrayList<>();
    }

    @Override
    public List<IndexSeckillResult> getIndexSeckillGoods(String appmodelId, Long wxuserId) {
        List<IndexSeckillResult> results = new ArrayList<>();
        //得到进行中和预热中的秒杀活动
        List<Activity> activities = activityUtil.actOnDoingAndPreheat(appmodelId, ActivityConstant.ACTIVITY_SECKILL);
        List<Long> actIds = activities.stream().map(Activity::getActivityId).collect(Collectors.toList());
        //根据活动id得到首页商品
        if (CollectionUtil.isNotEmpty(actIds)) {
            List<ActivityGoods> goodsList = new ArrayList<>();
            for (Long actId : actIds) {
                Map<String, ActivityGoods> activityGoodsMap = activityGoodsUtil
                        .getIndexActGoodsCache(appmodelId, ActivityConstant.ACTIVITY_SECKILL, actId);
                ArrayList<ActivityGoods> seckillResults = new ArrayList<>(activityGoodsMap.values());
                goodsList.addAll(seckillResults);
            }
            //兼容v1.2版本之前的活动价格
            activityGoodsUtil.compatibleActPrice(goodsList);
            this.getActivityConsumer(appmodelId, wxuserId, activities, goodsList, results);
            for (IndexSeckillResult result : results) {
                //对活动商品排序
                result.setActGoodsInfoResults(activityGoodsUtil.sort(result.getActGoodsInfoResults(), appmodelId));
            }
        }
        return results;
    }

    @Override
    public List<IndexSeckillResult> getISecondSeckillGoods(String appmodelId, Long wxuserId) {
        List<ActivityGoods> activityGoodsList;
        List<IndexSeckillResult> results = new ArrayList<>();
        //得到进行中和预热中的秒杀活动
        List<Activity> activities = activityUtil.actOnDoingAndPreheat(appmodelId, ActivityConstant.ACTIVITY_SECKILL);
        List<Long> actIds = activities.stream().map(Activity::getActivityId).collect(Collectors.toList());
        //根据活动id得到活动商品
        if (!actIds.isEmpty()) {
            activityGoodsList = tActivityGoodsMapper.selectActGoodsByActIds(actIds);
            //兼容v1.2版本之前的活动价格
            activityGoodsUtil.compatibleActPrice(activityGoodsList);
            this.getActivityConsumer(appmodelId, wxuserId, activities, activityGoodsList, results);
        }
        return results;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteByActId(Long activityId, String appmodelId) {
        //删除活动时将原来的活动商品库存还给普通商品
        activityUtil.returnGoodsStock(activityId, appmodelId);
        //删除活动商品
        tActivityGoodsMapper.deleteByActivityId(activityId);
    }

    @Override
    public ActGoodsInfoResult getActGoodsById(Long actGoodsId, String appmodelId) {
        ActivityGoods activityGoods = tActivityGoodsMapper.selectByActGoodsId(actGoodsId);
        if (activityGoods == null) {
            throw new ServiceException("活动商品不存在或已被删除");
        }
        Activity act = activityService.findById(activityGoods.getActivityId());
        Goods goods = goodsService.findById(activityGoods.getGoodsId());
        GoodsDetail goodsDetail = goodsDetailService.getGoodsDetailByGoodsId(activityGoods.getGoodsId());
        //在投放区域中
        return this.getActGoodsInfoResultNotCache(act, activityGoods, goods, goodsDetail);
    }

    @Override
    public int saveActGoods(List<ActivityGoods> activityGoodsListAdd) {
        String goodsIdsAdd = activityGoodsListAdd.stream().map(obj -> obj.getGoodsId().toString())
                .collect(Collectors.joining(","));
        List<GoodsDetail> goodsDetails2 = goodsDetailService.findByGoodsIds(goodsIdsAdd);
        Map<String, GoodsDetail> goodsDetailMap = goodsDetails2.stream()
                .collect(Collectors.toMap(o -> o.getGoodsId().toString(), v -> v));
        List<Goods> goodsList2 = goodsService.findByIds(goodsIdsAdd);
        Map<String, Goods> goodsMap = goodsList2.stream()
                .collect(Collectors.toMap(o -> o.getGoodsId().toString(), v -> v));
        for (ActivityGoods activityGoods : activityGoodsListAdd) {
            //判断活动商品库存是否不大于商品库存
            GoodsDetail goodsDetail = goodsDetailMap.get(activityGoods.getGoodsId().toString());
            Goods goods = goodsMap.get(activityGoods.getGoodsId().toString());
            activityGoods.setDelFlag(0);
            activityGoods.setActivitySalesVolume(0);
            if (activityGoods.getActivityStock() > goodsDetail.getStock()) {
                throw new ServiceException("商品 " + goods.getGoodsName() + "的活动库存不能超过实际库存");
            } else {
                //商品库存大于活动库存，预减商品库存
                goodsDetail.setStock(goodsDetail.getStock() - activityGoods.getActivityStock());
                goodsDetailService.update(goodsDetail);
            }
        }
        return tActivityGoodsMapper.insertList(activityGoodsListAdd);
    }

    @Override
    public List<ActivityGoods> getActGoodsByGoodsId(Long goodsId) {
        return tActivityGoodsMapper.selectActGoodsByGoodsId(goodsId);
    }

    @Override
    public void removeExistsNoStartActivityGoods(List<Long> goodsIdList, String appmodelId) {
        List<ActivityGoods> activityGoods = tActivityGoodsMapper.selectByGoodsIds(goodsIdList);
        if (CollectionUtil.isNotEmpty(activityGoods)) {
            activityUtil.returnGoodsStock(activityGoods);
            //商品下架则更改活动商品状态为已下架
            List<Long> actGoodsIds = activityGoods.stream().map(ActivityGoods::getActivityGoodsId)
                    .collect(Collectors.toList());
            tActivityGoodsMapper.soldOutActGoods(actGoodsIds, 1);
            //刷新所有缓存
            goodsUtil.flushGoodsCache(appmodelId);
        }
    }

    @Override
    public void putAwayActivityGoods(List<Long> goodsIdList, String appmodelId) {
        List<ActivityGoods> activityGoods = tActivityGoodsMapper.selectByGoodsIds(goodsIdList);
        if (CollectionUtil.isNotEmpty(activityGoods)) {
            //商品上架架则更改活动商品状态为上架架
            List<Long> actGoodsIds = activityGoods.stream().map(ActivityGoods::getActivityGoodsId)
                    .collect(Collectors.toList());
            tActivityGoodsMapper.soldOutActGoods(actGoodsIds, 0);
            //刷新所有缓存
            goodsUtil.flushGoodsCache(appmodelId);
        }
    }

    @Override
    public List<ActivityGoods> getActGoodsByActIdList(List<Long> actGoodsId) {
        return tActivityGoodsMapper.selectActGoodsByActIds(actGoodsId);
    }

    /**
     * 获取用户小区是否有该商品
     * @param appmodelId 小程序模板id
     * @param wxuserId 用户id
     * @param actGoodsId 活动商品id
     */
    @Override
    public Boolean ifExistActGoods(String appmodelId, Long wxuserId, Long actGoodsId) {
        ActivityGoods activityGoods = tActivityGoodsMapper.selectByPrimaryKey(actGoodsId);
        Map<String,Goods> goodsMap = redisTemplate4Goods.opsForValue()
                .get(GroupMallProperties.getRedisPrefix().concat(appmodelId).concat(RedisPrefix.ALL_GOODS));
        Map<String,Activity> allMap = new HashMap<>(8);
        if (activityGoods.getActivityType() == 1) {
            Map<String,Activity> seckill = redisTemplate4Act.opsForValue()
                    .get(GroupMallProperties.getRedisPrefix().concat(appmodelId).concat(RedisPrefix.INDEX_SECKILL_ACT));
            if (seckill != null) {
                allMap.putAll(seckill);
            }
        }
        if (ActivityConstant.ACTIVITY_GROUP.equals(activityGoods.getActivityType())) {
            Map<String,Activity> group = redisTemplate4Act.opsForValue()
                    .get(GroupMallProperties.getRedisPrefix().concat(appmodelId).concat(RedisPrefix.INDEX_GROUP_ACT));
            if (group != null) {
                allMap.putAll(group);
            }
        }
        List<ActGoodsInfoResult> actGoodsInfoResults = activityGoodsUtil
                .packagingActGoodsInfoResults(Collections.singletonList(activityGoods), goodsMap, allMap, wxuserId);
        return CollectionUtil.isNotEmpty(actGoodsInfoResults);
    }

    private void getActivityConsumer(String appmodelId, Long wxuserId, List<Activity> activities,
                                     List<ActivityGoods> activityGoodsList, List<IndexSeckillResult> results) {
        for (Activity activity : activities) {
            Long activityId = activity.getActivityId();
            List<ActGoodsInfoResult> list = activityGoodsUtil.getResultByActGoodsList4Wx(activityGoodsList, wxuserId, appmodelId);
            list = list.stream().filter(o -> o.getActivityId().equals(activityId)).collect(Collectors.toList());
            activityGoodsUtil.sort(list, appmodelId);
            IndexSeckillResult si = new IndexSeckillResult();
            si.setActivityId(activityId);
            si.setActGoodsInfoResults(list);
            si.setActivityStatus(activity.getStatus());
            si.setActivityName(activity.getActivityName());
            si.setActivityPoster(activity.getActivityPoster());
            si.setEndTime(activity.getEndTime());
            si.setStartTime(activity.getStartTime());
            results.add(si);
        }
        if (CollectionUtil.isNotEmpty(results)) {
            //如果活动中的商品是空的,说明投放区域中没有,则不显示活动
            results.removeIf(next -> CollectionUtil.isEmpty(next.getActGoodsInfoResults()));
        }
    }

    /**
     * 获取活动商品信息
     * 用于PC端的活动商品获取的接口
     * @param activityGoodsList 活动商品列表
     */
    private List<ActGoodsInfoResult> getResultByActGoodsList4Pc(List<ActivityGoods> activityGoodsList) {
        List<ActGoodsInfoResult> actGoodsInfoResults = new ArrayList<>();
        if (activityGoodsList != null && activityGoodsList.size() > 0) {
            String goodsIds = activityGoodsList.stream().map(obj -> obj.getGoodsId().toString())
                    .collect(Collectors.joining(","));
            List<Goods> goodsList = goodsService.findByIds(goodsIds);
            Map<String, Goods> goodsMap = goodsList.stream()
                    .collect(Collectors.toMap(o -> o.getGoodsId().toString(), v -> v));
            List<GoodsDetail> goodsDetail = goodsDetailService.findByGoodsIds(goodsIds);
            String activityIds = activityGoodsList.stream().map(obj -> obj.getActivityId().toString())
                    .collect(Collectors.joining(","));
            List<Activity> activities = activityService.findByIds(activityIds);
            Map<String, Activity> activityMap = activities.stream()
                    .collect(Collectors.toMap(o -> o.getActivityId().toString(), v -> v));
            Map<String, GoodsDetail> goodsDetailMap = goodsDetail.stream()
                    .collect(Collectors.toMap(o -> o.getGoodsId().toString(), v -> v));
            activityGoodsList.forEach(activityGoods -> {
                Goods goods = goodsMap.get(activityGoods.getGoodsId().toString());
                GoodsDetail goodsDetail1 = goodsDetailMap.get(activityGoods.getGoodsId().toString());
                //在投放区域中
                Activity act = activityMap.get(activityGoods.getActivityId().toString());
                ActGoodsInfoResult actGoodsInfoResult = this
                        .getActGoodsInfoResultNotCache(act, activityGoods, goods, goodsDetail1);
                actGoodsInfoResult.setActEndDate(act.getEndTime());
                actGoodsInfoResult.setActivityStatus(act.getStatus());
                actGoodsInfoResult.setActStartTDate(act.getStartTime());
                actGoodsInfoResult.setActivityType(act.getActivityType());
                actGoodsInfoResult.setActivityName(act.getActivityName());
                actGoodsInfoResult.setActivityImg(act.getActivityPoster());
                actGoodsInfoResult.setActivityStock(activityGoods.getActivityStock());
                actGoodsInfoResult.setForecastReceiveTime(act.getForecastReceiveTime());
                actGoodsInfoResult.setGoodsVideoUrl(goods.getGoodsVideoUrl());
                actGoodsInfoResult.setProviderName(goodsDetail1.getProviderName());
                actGoodsInfoResult.setSalesVolume(goodsDetail1.getSalesVolume());
                actGoodsInfoResult.setShamSalesVolume(goodsDetail1.getShamVolume());
                actGoodsInfoResult.setJoinSolitaire(activityGoods.getJoinSolitaire());
                if (ActivityConstant.ACTIVITY_STATUS_START.equals(act.getStatus())
                        || ActivityConstant.ACTIVITY_STATUS_READY.equals(act.getStatus())) {
                    actGoodsInfoResult.setShamSalesVolume(goodsDetail1.getShamVolume());
                    if (goods.getGoodsStatus().equals(GoodsConstant.SOLD_OUT) || goods.getGoodsDelFlag()) {
                        //正在活动中的商品被强制下架或删除后后，活动商品显示已下架
                        actGoodsInfoResult.setSoldOutStatus(1);
                    }
                }
                if (activityGoods.getSoldOutFlag() != null && activityGoods.getSoldOutFlag() == 1) {
                    actGoodsInfoResult.setSoldOutStatus(1);
                }
                //获取实时库存和销量
                goodsUtil.realTimeStockAndSaleVolume(activityGoods.getAppmodelId(), actGoodsInfoResult);
                if (actGoodsInfoResult.getActivityStock().equals(0)) {
                    actGoodsInfoResult.setActivityStock(activityGoods.getActivityStock());
                }
                actGoodsInfoResults.add(actGoodsInfoResult);
            });
        }
        return actGoodsInfoResults;
    }


    private ActGoodsInfoResult getActGoodsInfoResultNotCache(Activity activity, ActivityGoods activityGoods,
                                                             Goods goods, GoodsDetail goodsDetail) {
        ActGoodsInfoResult actGoodsInfoResult = new ActGoodsInfoResult();
        actGoodsInfoResult.setActEndDate(activity.getEndTime());
        actGoodsInfoResult.setActivityName(activity.getActivityName());
        actGoodsInfoResult.setActivityStatus(activity.getStatus());
        actGoodsInfoResult.setActStartTDate(activity.getStartTime());
        actGoodsInfoResult.setActivityType(activity.getActivityType());
        actGoodsInfoResult.setActivityId(activityGoods.getActivityId());
        actGoodsInfoResult.setActivityImg(activity.getActivityPoster());
        actGoodsInfoResult.setActivityStock(activityGoods.getActivityStock());
        actGoodsInfoResult.setActivityGoodsId(activityGoods.getActivityGoodsId());
        actGoodsInfoResult.setActivityDiscount(activityGoods.getActivityDiscount());
        actGoodsInfoResult.setGoodsImg(goods.getGoodsImg());
        actGoodsInfoResult.setGoodsName(goods.getGoodsName());
        actGoodsInfoResult.setGoodsTitle(goods.getGoodsTitle());
        actGoodsInfoResult.setGoodsId(activityGoods.getGoodsId());
        boolean blank = "\\\"\\\"".equalsIgnoreCase(goods.getGoodsVideoUrl());
        if (blank) {
            actGoodsInfoResult.setGoodsVideoUrl("false");
        } else {
            actGoodsInfoResult.setGoodsVideoUrl(goods.getGoodsVideoUrl());
        }
        actGoodsInfoResult.setGoodsProperty(goodsDetail.getGoodsProperty());
        actGoodsInfoResult.setAppmodelId(activityGoods.getAppmodelId());
        actGoodsInfoResult.setDesc(goodsDetail.getGoodsDesc());
        actGoodsInfoResult.setForecastReceiveTime(activity.getForecastReceiveTime());
        actGoodsInfoResult.setIndexDisplay(activityGoods.getIndexDisplay());
        actGoodsInfoResult.setPrice(goods.getPrice());
        actGoodsInfoResult.setProviderName(goodsDetail.getProviderName());
        actGoodsInfoResult.setPreheatStatus(activityGoods.getPreheatStatus());
        actGoodsInfoResult.setMaxQuantity(activityGoods.getMaxQuantity());
        actGoodsInfoResult.setSalesVolume(goodsDetail.getSalesVolume());
        actGoodsInfoResult.setShamSalesVolume(goodsDetail.getShamVolume());
        actGoodsInfoResult.setSortPosition(activityGoods.getSortPosition());
        actGoodsInfoResult.setStock(goodsDetail.getStock() + actGoodsInfoResult.getActivityStock());
        actGoodsInfoResult.setText(goodsDetail.getText());
        if (ActivityConstant.ACTIVITY_STATUS_START.equals(activity.getStatus())
                || ActivityConstant.ACTIVITY_STATUS_READY.equals(activity.getStatus())) {
            if (goods.getGoodsStatus().equals(GoodsConstant.SOLD_OUT) || goods.getGoodsDelFlag()) {
                //正在活动中的商品被强制下架或删除后后，活动商品显示已下架
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
            actPrice = NumberUtil.mul(goods.getPrice(), discount);
        }
        if (actPrice.doubleValue() < OrderConstant.MIN_PAYFEE) {
            actGoodsInfoResult.setActPrice(BigDecimal.valueOf(OrderConstant.MIN_PAYFEE));
        } else {
            actGoodsInfoResult.setActPrice(actPrice);
        }
        //获取实时库存和销量
        goodsUtil.realTimeStockAndSaleVolume(goods.getAppmodelId(), actGoodsInfoResult);
        return actGoodsInfoResult;
    }

}
