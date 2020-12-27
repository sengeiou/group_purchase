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

import cn.hutool.core.util.HashUtil;
import com.mds.group.purchase.activity.result.ActGoodsInfoResult;
import com.mds.group.purchase.configurer.GroupMallProperties;
import com.mds.group.purchase.constant.ActiviMqQueueName;
import com.mds.group.purchase.constant.RedisPrefix;
import com.mds.group.purchase.goods.dao.GoodsMapper;
import com.mds.group.purchase.goods.model.*;
import com.mds.group.purchase.goods.result.GoodsFuzzyResult;
import com.mds.group.purchase.goods.result.GoodsResult;
import com.mds.group.purchase.goods.service.GoodsAutoAddAreaService;
import com.mds.group.purchase.goods.service.GoodsClassMappingService;
import com.mds.group.purchase.goods.service.GoodsClassService;
import com.mds.group.purchase.goods.service.GoodsDetailService;
import com.mds.group.purchase.jobhandler.ActiveDelaySendJobHandler;
import com.mds.group.purchase.logistics.model.GoodsAreaMapping;
import com.mds.group.purchase.logistics.model.LineDetail;
import com.mds.group.purchase.logistics.service.GoodsAreaMappingService;
import com.mds.group.purchase.logistics.service.LineDetailService;
import com.mds.group.purchase.logistics.vo.GoodsAreaVo;
import com.mds.group.purchase.order.service.CommentService;
import org.apache.commons.beanutils.ConvertUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 普通商品相关公共方法
 *
 * @author shuke
 */
@Component
public class GoodsUtil {

    @Resource
    private GoodsMapper goodsMapper;
    @Resource
    private ActivityUtil activityUtil;
    @Resource
    private GoodsDetailService goodsDetailService;
    @Resource
    private GoodsAreaMappingUtil goodsAreaMappingUtil;
    @Resource
    private RedisTemplate<String, Integer> redisTemplate;
    @Resource
    private RedisTemplate<String,GoodsAreaMapping> redisTemplate4;
    @Resource
    private RedisTemplate<String, Map<String, Double>> redisTemplate4Double;
    @Resource
    private CommentService commentService;
    @Resource
    private LineDetailService lineDetailService;
    @Resource
    private GoodsClassService goodsClassService;
    @Resource
    private ActiveDelaySendJobHandler jobHandler;
    @Resource
    private GoodsAreaMappingService goodsAreaMappingService;
    @Resource
    private GoodsAutoAddAreaService goodsAutoAddAreaService;
    @Resource
    private GoodsClassMappingService goodsClassMappingService;

    /**
     * 1、刷新商品缓存
     * 2、刷新海报页可添加商品缓存
     * 3、更新商品时同时发送队列消息更新活动商品缓存
     * 4、刷新所有活动商品和活动
     *
     * @param appmodelId the appmodel id
     */
    public void flushGoodsCache(String appmodelId) {
        //刷新商品缓存
        jobHandler.savaTask(appmodelId, ActiviMqQueueName.GOODS_INFO_CACHE, 0L, appmodelId, false);
        //刷新海报页可添加商品缓存
        jobHandler.savaTask(appmodelId, ActiviMqQueueName.GOODS_POSTER_CACHE, 0L, appmodelId, false);
        //更新商品时同时发送队列消息更新活动商品缓存
        jobHandler.savaTask(appmodelId, ActiviMqQueueName.UPDATE_GOODS_FLASH_ACTGOODS_CACHE, 0L, appmodelId, false);
        //刷新所有活动商品和活动
        activityUtil.flushMethod(appmodelId);
    }

    /**
     * 将String字符串转换成数组
     *
     * @param string the string
     * @param c      the c
     * @return the object [ ]
     */
    public Object[] parseArr(String string, Class c) {
        String[] strArr = string.split(",");
        return (Object[]) ConvertUtils.convert(strArr, c);
    }

    /**
     * 得到刷新商品缓存所需的结果值
     *
     * @param appmodelId the appmodel id
     * @return the goods flush result
     */
    public List<GoodsFuzzyResult> getGoodsFlushResult(String appmodelId) {
        List<GoodsResult> list = goodsMapper.selectAllGoods(appmodelId);
        if (list.isEmpty()) {
            return new ArrayList<>();
        }
        List<Long> collect = list.stream().map(Goods::getGoodsId).collect(Collectors.toList());
        String goodsIds = collect.stream().map(Object::toString).collect(Collectors.joining(","));
        //得到所有分类映射
        List<GoodsClassMapping> goodsClassMappingList = goodsClassMappingService.findByGoodsIds(goodsIds);
        //根据商品id分类
        Map<String, List<GoodsClassMapping>> goodsClassMap = new HashMap<>(8);
        goodsClassMap(goodsClassMappingList, goodsClassMap);
        //得到所有商品对应的商品分类
        List<GoodsClass> goodsClassList = goodsClassService.findByList("appmodelId", appmodelId);
        Collections.sort(goodsClassList);
        Map<String, GoodsClass> classmap = goodsClassList.stream()
                .collect(Collectors.toMap(o -> o.getGoodsClassId().toString(), v -> v));
        Map<String, Double> avgScoreByGoodsIds = this.getAllGoodsScore(appmodelId);
        List<GoodsFuzzyResult> results = new ArrayList<>();
        this.goodsResultToFuzzyResultV2(list, goodsClassMap, classmap, avgScoreByGoodsIds, results, appmodelId);
        return results;
    }

    /**
     * Goods class map.
     *
     * @param goodsClassMappingList the goods class mapping list
     * @param goodsClassMap         the goods class map
     */
    public void goodsClassMap(List<GoodsClassMapping> goodsClassMappingList,
                              Map<String, List<GoodsClassMapping>> goodsClassMap) {
        goodsClassMappingList.parallelStream().forEach(obj -> {
            List<GoodsClassMapping> list1 = goodsClassMap.get(obj.getGoodsId().toString());
            if (list1 == null) {
                list1 = new ArrayList<>();
            }
            list1.add(obj);
            goodsClassMap.put(obj.getGoodsId().toString(), list1);
        });
    }

    /**
     * 根据GoodsResult对象，得到FuzzyResult对象
     *
     * @param list               the list
     * @param goodsClassMap      the goods class map
     * @param classMap           the class map
     * @param avgScoreByGoodsIds the avg score by goods ids
     * @param results            the results
     */
    public void goodsResultToFuzzyResult(List<GoodsResult> list, Map<String, List<GoodsClassMapping>> goodsClassMap,
                                         Map<String, GoodsClass> classMap, Map<String, Double> avgScoreByGoodsIds,
                                         List<GoodsFuzzyResult> results) {
        this.goodsResultFillGoodsDetail(list);
        for (GoodsResult goods : list) {
            GoodsFuzzyResult result = new GoodsFuzzyResult();
            result.setAppmodelId(goods.getAppmodelId());
            result.setGoodsImg(goods.getGoodsImg());
            result.setGoodsName(goods.getGoodsName());
            result.setGoodsTitle(goods.getGoodsTitle());
            result.setGoodsVideoUrl(goods.getGoodsVideoUrl());
            result.setPrice(goods.getPrice());
            result.setGoodsId(goods.getGoodsId());
            result.setCreateTime(goods.getCreateTime());
            GoodsDetail goodsDetail = goods.getGoodsDetail();
            result.setSalesVolume(goodsDetail.getSalesVolume());
            result.setCommissionType(goodsDetail.getCommissionType());
            result.setExpirationDate(goodsDetail.getExpirationDate());
            result.setGoodsDesc(goodsDetail.getGoodsDesc());
            result.setGoodsStatus(goods.getGoodsStatus());
            result.setStock(goodsDetail.getStock());
            result.setText(goodsDetail.getText());
            result.setGroupLeaderCommission(goodsDetail.getGroupLeaderCommission());
            result.setGoodsProperty(goodsDetail.getGoodsProperty());
            Double aDouble = avgScoreByGoodsIds.get(goods.getGoodsId().toString());
            result.setScore(aDouble == null ? null : aDouble.toString());
            result.setShamVolume(goodsDetail.getShamVolume());
            result.setProviderId(goodsDetail.getProviderId());
            result.setProviderName(goodsDetail.getProviderName());
            result.setGoodsDelFlag(goods.getGoodsDelFlag());
            List<GoodsClassMapping> goodsClassMappings = goodsClassMap.get(goods.getGoodsId().toString());
            if (goodsClassMappings == null) {
                goodsClassMappings = new ArrayList<>();
            }
            List<GoodsClass> goodsClasses = new ArrayList<>();
            for (GoodsClassMapping goodsClassMapping : goodsClassMappings) {
                goodsClasses.add(classMap.get(goodsClassMapping.getGoodsClassId().toString()));
            }
            Collections.sort(goodsClasses);
            result.setGoodsClass(goodsClasses);
            results.add(result);
        }
    }

    /**
     * Gets goods area mapping by goods id.
     *
     * @param goodsId    the goods id
     * @param appmodelId the appmodel id
     * @return the goods area mapping by goods id
     */
    public List<GoodsAreaMapping> getGoodsAreaMappingByGoodsId(Long goodsId, String appmodelId) {
        String key = GroupMallProperties.getRedisPrefix() + appmodelId + RedisPrefix.GOODS_AREA_MAPPING_HASH;
        String hashKey = String.valueOf(HashUtil.bkdrHash(key.concat(":").concat(goodsId.toString())));
        List<GoodsAreaMapping> goodsAreaMappings = (List<GoodsAreaMapping>) redisTemplate4.opsForHash()
                .get(key, hashKey);
        if (goodsAreaMappings == null) {
            Map<String, List<GoodsAreaMapping>> cacheMap = goodsAreaMappingUtil.getCacheMap(appmodelId);
            goodsAreaMappings = cacheMap.get(goodsId.toString());
        }
        return goodsAreaMappings;
    }

    /**
     * 根据GoodsResult对象，得到FuzzyResult对象
     * v1.2
     *
     * @param list               the list
     * @param goodsClassMap      the goods class map
     * @param classMap           the class map
     * @param avgScoreByGoodsIds the avg score by goods ids
     * @param results            the results
     * @param appmodelId         the appmodel id
     */
    public void goodsResultToFuzzyResultV2(List<GoodsResult> list, Map<String, List<GoodsClassMapping>> goodsClassMap,
                                           Map<String, GoodsClass> classMap, Map<String, Double> avgScoreByGoodsIds, List<GoodsFuzzyResult> results,
                                           String appmodelId) {
        this.goodsResultFillGoodsDetail(list);
        List<GoodsAutoAddArea> byAppmodelId = goodsAutoAddAreaService.findByAppmodelId(appmodelId);
        Map<String, Boolean> collect = new HashMap<>(16);
        if (byAppmodelId != null) {
            collect = byAppmodelId.parallelStream().collect(
                    Collectors.toMap(o -> o.getGoodsId().toString(), GoodsAutoAddArea::getAutoAdd, (k1, k2) -> k1));
        }
        List<LineDetail> lineDetails = lineDetailService.findByAppmodelId(appmodelId);
        List<String> commityIds = lineDetails.parallelStream().map(o -> o.getCommunityId().toString())
                .collect(Collectors.toList());
        for (GoodsResult goods : list) {
            GoodsFuzzyResult result = new GoodsFuzzyResult();
            List<GoodsAreaMapping> goodsAreaMapping = this.getGoodsAreaMappingByGoodsId(goods.getGoodsId(), appmodelId);
            if (goodsAreaMapping == null) {
                result.setSelectedAreaNum(0);
            } else {
                List<GoodsAreaMapping> selectedAreaNum = goodsAreaMapping.parallelStream()
                        .filter(o -> commityIds.contains(o.getCommunityId().toString())).collect(Collectors.toList());
                result.setSelectedAreaNum(selectedAreaNum.size());
            }
            Boolean aBoolean = collect.get(goods.getGoodsId().toString());
            aBoolean = aBoolean == null ? false : aBoolean;
            result.setAutoAdd(aBoolean);
            //得到所有可投放小区数量
            int allAreaNum = lineDetails.size();
            result.setAllAreaNum(allAreaNum);
            result.setAppmodelId(goods.getAppmodelId());
            result.setGoodsVideoUrl(goods.getGoodsVideoUrl());
            result.setGoodsImg(goods.getGoodsImg());
            result.setGoodsName(goods.getGoodsName());
            result.setGoodsTitle(goods.getGoodsTitle());
            result.setPrice(goods.getPrice());
            result.setGoodsId(goods.getGoodsId());
            result.setCreateTime(goods.getCreateTime());
            GoodsDetail goodsDetail = goods.getGoodsDetail();
            result.setSalesVolume(goodsDetail.getSalesVolume());
            result.setCommissionType(goodsDetail.getCommissionType());
            result.setExpirationDate(goodsDetail.getExpirationDate());
            result.setGoodsDesc(goodsDetail.getGoodsDesc());
            result.setGoodsStatus(goods.getGoodsStatus());
            result.setStock(goodsDetail.getStock());
            result.setText(goodsDetail.getText());
            result.setGroupLeaderCommission(goodsDetail.getGroupLeaderCommission());
            result.setGoodsProperty(goodsDetail.getGoodsProperty());
            Double aDouble = avgScoreByGoodsIds.get(goods.getGoodsId().toString());
            if (aDouble != null) {
                result.setScore(aDouble.toString());
            }
            result.setShamVolume(goodsDetail.getShamVolume());
            result.setProviderId(goodsDetail.getProviderId());
            result.setProviderName(goodsDetail.getProviderName());
            result.setGoodsDelFlag(goods.getGoodsDelFlag());
            List<GoodsClassMapping> goodsClassMappings = goodsClassMap.get(goods.getGoodsId().toString());
            if (goodsClassMappings == null) {
                goodsClassMappings = new ArrayList<>();
            }
            List<GoodsClass> goodsClasses = new ArrayList<>();
            for (GoodsClassMapping goodsClassMapping : goodsClassMappings) {
                if (classMap.get(goodsClassMapping.getGoodsClassId().toString()) != null
                        && classMap.get(goodsClassMapping.getGoodsClassId().toString()).getFatherId() != 0) {
                    goodsClasses.add(classMap.get(goodsClassMapping.getGoodsClassId().toString()));
                }
            }
            Collections.sort(goodsClasses);
            result.setGoodsClass(goodsClasses);
            results.add(result);
        }
    }

    /**
     * Goods result fill goods detail.
     *
     * @param list the list
     */
    public void goodsResultFillGoodsDetail(List<GoodsResult> list) {
        String goodsIdList = list.stream().map(o -> o.getGoodsId().toString()).collect(Collectors.joining(","));
        Map<Long, GoodsDetail> goodsDetailMap = goodsDetailService.findByGoodsIds(goodsIdList).parallelStream()
                .collect(Collectors.toMap(GoodsDetail::getGoodsId, v -> v));
        list.forEach(o -> o.setGoodsDetail(goodsDetailMap.get(o.getGoodsId())));
    }

    /**
     * 得到所有商品的评分
     *
     * @param appmodelId the appmodel id
     * @return the all goods score
     */
    public Map<String, Double> getAllGoodsScore(String appmodelId) {
        String key = GroupMallProperties.getRedisPrefix() + appmodelId + RedisPrefix.ALL_GOODS_SCORE;
        Map<String, Double> goodsScore = redisTemplate4Double.opsForValue().get(key);
        if (goodsScore == null) {
            goodsScore = commentService.findAvgScoreByGoodsIds(appmodelId);
        }
        return goodsScore;
    }


    /**
     * 获取实时库存和销量
     *
     * @param appmodelId         the appmodel id
     * @param actGoodsInfoResult the act goods info result
     */
    public void realTimeStockAndSaleVolume(String appmodelId, ActGoodsInfoResult actGoodsInfoResult) {
        //从缓存中获取库存
        Integer stock = getActStock(appmodelId, actGoodsInfoResult.getActivityGoodsId(),
                actGoodsInfoResult.getActivityId());
        //从缓存中获取活动销量
        Integer saleVolume = getActGoodsSaleVolume(appmodelId, actGoodsInfoResult.getActivityGoodsId());
        actGoodsInfoResult.setActivitySalesVolume(saleVolume);
        actGoodsInfoResult.setActivityStock(stock);

    }

    /**
     * Gets act stock.
     *
     * @param appmodelId      the appmodel id
     * @param activityGoodsId the activity goods id
     * @param activityId      the activity id
     * @return the act stock
     */
    public Integer getActStock(String appmodelId, Long activityGoodsId, Long activityId) {
        String redisKey = GroupMallProperties.getRedisPrefix().concat(appmodelId).concat(RedisPrefix.ACT_GOODS_STOCK)
                .concat(activityGoodsId.toString());
        Integer o = redisTemplate.opsForValue().get(redisKey);
        Integer stock = 0;
        if (o == null) {
            activityUtil.afterPropertiesSet(activityId);
            o = redisTemplate.opsForValue().get(redisKey);
            if (o != null) {
                stock = o;
            }
        } else {
            stock = o;
        }
        return stock;
    }

    /**
     * Gets act goods sale volume.
     *
     * @param appmodelId      the appmodel id
     * @param activityGoodsId the activity goods id
     * @return the act goods sale volume
     */
    public Integer getActGoodsSaleVolume(String appmodelId, Long activityGoodsId) {
        String redisKeySaleVolum =
                GroupMallProperties.getRedisPrefix().concat(appmodelId).concat(RedisPrefix.ACT_GOODS_SALES_VOLUME)
                        .concat(activityGoodsId.toString());
        Integer redisSaleVolume = redisTemplate.opsForValue().get(redisKeySaleVolum);
        Integer saleVolume = 0;
        if (redisSaleVolume != null) {
            saleVolume = redisSaleVolume;
        }
        return saleVolume;
    }

    /**
     * 将商品默认添加到投放区域，并关联所有小区
     *
     * @param goods the goods
     */
    public void defaultToGoodsAreaMapping(Goods goods) {
        GoodsAreaVo goodsAreaVo = new GoodsAreaVo();
        goodsAreaVo.setAppmodelId(goods.getAppmodelId());
        goodsAreaVo.setGoodsId(goods.getGoodsId());
        goodsAreaVo.setGoodsName(goods.getGoodsName());
        //获取所有线路下的小区
        List<LineDetail> byAppmodelId = lineDetailService.findByAppmodelId(goods.getAppmodelId());
        if (byAppmodelId != null) {
            List<Long> communityIds = byAppmodelId.stream().map(LineDetail::getCommunityId)
                    .collect(Collectors.toList());
            goodsAreaVo.setCommunityIds(communityIds);
            goodsAreaMappingService.saveGoodsAreaAndFlushCache(goodsAreaVo);
        }
    }

    /**
     * 将商品添加到设置的投放区域
     *
     * @param goods        the goods
     * @param communityIds the community ids
     */
    public void setGoodsAreaMapping(Goods goods, String communityIds) {
        GoodsAreaVo goodsAreaVo = new GoodsAreaVo();
        goodsAreaVo.setAppmodelId(goods.getAppmodelId());
        goodsAreaVo.setGoodsId(goods.getGoodsId());
        goodsAreaVo.setGoodsName(goods.getGoodsName());
        List<String> communityIdListStr = Arrays.asList(communityIds.split(","));
        List<Long> communityIdList = communityIdListStr.stream().map(Long::valueOf).collect(Collectors.toList());
        goodsAreaVo.setCommunityIds(communityIdList);
        goodsAreaMappingService.saveGoodsAreaAndFlushCache(goodsAreaVo);

    }

    /**
     * 更新商品添加到设置的投放区域
     *
     * @param goods        the goods
     * @param communityIds the community ids
     * @param isAutoAdd    the is auto add
     */
    public void updateGoodsAreaMapping(Goods goods, String communityIds, boolean isAutoAdd) {
        GoodsAreaVo goodsAreaVo = new GoodsAreaVo();
        goodsAreaVo.setAppmodelId(goods.getAppmodelId());
        goodsAreaVo.setGoodsId(goods.getGoodsId());
        goodsAreaVo.setGoodsName(goods.getGoodsName());
        List<String> communityIdListStr = Arrays.asList(communityIds.split(","));
        List<Long> communityIdList = communityIdListStr.stream().map(Long::valueOf).collect(Collectors.toList());
        goodsAreaVo.setCommunityIds(communityIdList);
        goodsAreaVo.setAutoAdd(isAutoAdd);
        goodsAreaMappingService.updateGoodsAreaMapping(goodsAreaVo);
        GoodsAutoAddArea goodsAutoAddArea = new GoodsAutoAddArea();
        goodsAutoAddArea.setGoodsId(goods.getGoodsId());
        goodsAutoAddArea.setAppmodelId(goods.getAppmodelId());
        goodsAutoAddArea.setAutoAdd(isAutoAdd);
        goodsAutoAddArea.setGoodsName(goods.getGoodsName());
        int update = goodsAutoAddAreaService.updateByGoodsId(goodsAutoAddArea);
        if (update == 0) {
            goodsAutoAddAreaService.save(goodsAutoAddArea);
        }
    }

}
