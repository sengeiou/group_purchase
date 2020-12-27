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

package com.mds.group.purchase.goods.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.RandomUtil;
import com.github.pagehelper.PageInfo;
import com.mds.group.purchase.activity.model.Activity;
import com.mds.group.purchase.activity.model.ActivityGoods;
import com.mds.group.purchase.activity.service.ActivityGoodsService;
import com.mds.group.purchase.constant.ActiviMqQueueName;
import com.mds.group.purchase.constant.ActivityConstant;
import com.mds.group.purchase.constant.GoodsConstant;
import com.mds.group.purchase.core.AbstractService;
import com.mds.group.purchase.core.CodeMsg;
import com.mds.group.purchase.exception.GlobalException;
import com.mds.group.purchase.exception.ServiceException;
import com.mds.group.purchase.goods.dao.GoodsMapper;
import com.mds.group.purchase.goods.model.*;
import com.mds.group.purchase.goods.result.*;
import com.mds.group.purchase.goods.service.*;
import com.mds.group.purchase.goods.vo.*;
import com.mds.group.purchase.jobhandler.ActiveDelaySendJobHandler;
import com.mds.group.purchase.logistics.model.GoodsAreaMapping;
import com.mds.group.purchase.logistics.model.LineDetail;
import com.mds.group.purchase.logistics.service.GoodsAreaMappingService;
import com.mds.group.purchase.logistics.service.LineDetailService;
import com.mds.group.purchase.user.model.Provider;
import com.mds.group.purchase.user.service.ProviderService;
import com.mds.group.purchase.utils.ActivityUtil;
import com.mds.group.purchase.utils.GoodsUtil;
import com.mds.group.purchase.utils.PageUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;


/**
 * The type Goods service.
 *
 * @author shuke
 * @date 2018 /11/27
 */
@Service
public class GoodsServiceImpl extends AbstractService<Goods> implements GoodsService, GoodsConstant {

    @Resource
    private GoodsUtil goodsUtil;
    @Resource
    private ActivityUtil activityUtil;
    @Resource
    private GoodsMapper goodsMapper;
    @Resource
    private ProviderService providerService;
    @Resource
    private LineDetailService lineDetailService;
    @Resource
    private GoodsClassService goodsClassService;
    @Resource
    private ActiveDelaySendJobHandler jobHandler;
    @Resource
    private GoodsDetailService goodsDetailService;
    @Resource
    private ActivityGoodsService activityGoodsService;
    @Resource
    private GoodsAutoAddAreaService goodsAutoAddAreaService;
    @Resource
    private GoodsAreaMappingService goodsAreaMappingService;
    @Resource
    private GoodsClassMappingService goodsClassMappingService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void add(SaveGoodsVo goodsVo) {
        Goods goods = goodsVo.resolveGoodsFromVo();
        goods.setCreateTime(new Date());
        goods.setGoodsStatus(goodsVo.getGoodsStatus());
        goods.setGoodsDelFlag(NORMAL);
        goodsMapper.insert(goods);
        GoodsDetail goodsDetail = goodsVo.resolveGoodsDetailFromVo();
        goodsDetail.setGoodsId(goods.getGoodsId());
        Provider provider = providerService.findById(goodsDetail.getProviderId());
        goodsDetail.setProviderName(provider.getProviderName());
        goodsDetail.setSalesVolume(DEFAULT_SALES_VOLUME);
        goodsDetailService.addDetail(goodsDetail);
        //将商品分类与商品对应
        //1.取得分类id集合
        Long[] goodsClassIds = goodsVo.getGoodsClassIds();
        Long[] goodsIds = new Long[1];
        goodsIds[0] = goods.getGoodsId();
        //2.保存分类映射记录
        goodsClassMappingService.addGoodsClassMapping(goodsIds, goodsClassIds, goodsVo.getAppmodelId());
        //添加商品时，默认将该商品添加到投放区域中并默认投放所有区域，删除时将投放区域删除
        goodsUtil.defaultToGoodsAreaMapping(goods);
        //刷新商品缓存
        goodsUtil.flushGoodsCache(goodsVo.getAppmodelId());

    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addV2(SaveGoodsVo goodsVo) {
        String communityIds = goodsVo.getCommunityIds();
        String goodsImg = goodsVo.getGoodsImg();
        goodsImg = goodsImg.replaceAll("\\[\"", "").replaceAll("\"]", "").replaceAll("\"", "").replaceAll("\\\\", "");
        goodsVo.setGoodsImg(goodsImg);
        long goodsId;
        do {
            //生成一个12位的商品id
            goodsId = RandomUtil.randomLong(GoodsConstant.MIN_ID, GoodsConstant.MAX_ID);
            //验证改id是否已经被使用
            Goods goods1 = goodsMapper.selectByPrimaryKey(goodsId);
            if (goods1 == null) {
                break;
            }
        } while (true);
        Goods goods = goodsVo.resolveGoodsFromVo();
        goods.setGoodsId(goodsId);
        goods.setCreateTime(new Date());
        goods.setGoodsId(goodsId);
        goods.setGoodsStatus(goodsVo.getGoodsStatus());
        goods.setGoodsDelFlag(NORMAL);
        goodsMapper.insert(goods);
        GoodsDetail goodsDetail = goodsVo.resolveGoodsDetailFromVo();
        goodsDetail.setGoodsId(goodsId);
        Provider provider = providerService.findById(goodsDetail.getProviderId());
        goodsDetail.setProviderName(provider.getProviderName());
        goodsDetail.setSalesVolume(DEFAULT_SALES_VOLUME);
        goodsDetailService.addDetail(goodsDetail);
        //将商品信息存入t_goods_auto_add_area表
        boolean autoAdd = goodsVo.isAutoAdd();
        GoodsAutoAddArea goodsAutoAddArea = new GoodsAutoAddArea();
        goodsAutoAddArea.setAppmodelId(goodsVo.getAppmodelId());
        goodsAutoAddArea.setAutoAdd(autoAdd);
        goodsAutoAddArea.setGoodsId(goodsId);
        goodsAutoAddAreaService.save(goodsAutoAddArea);
        //将商品分类与商品对应
        //1.取得分类id集合
        Long[] goodsClassIds = goodsVo.getGoodsClassIds();
        Long[] goodsIds = new Long[1];
        goodsIds[0] = goods.getGoodsId();
        //2.保存分类映射记录
        goodsClassMappingService.addGoodsClassMapping(goodsIds, goodsClassIds, goodsVo.getAppmodelId());
        if (communityIds == null || "".equalsIgnoreCase(communityIds)) {
            //添加商品时，默认将该商品添加到投放区域中并默认投放所有区域，删除时将投放区域删除
            goodsUtil.defaultToGoodsAreaMapping(goods);
        } else {
            //将商品投放到设置的小区中
            goodsUtil.setGoodsAreaMapping(goods, communityIds);
        }
        //刷新商品缓存
        goodsUtil.flushGoodsCache(goodsVo.getAppmodelId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addV119(SaveGoodsV119Vo goodsVo) {
        addV2(goodsVo);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addV12(SaveGoodsV12Vo goodsVo) {
        addV2(goodsVo);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateGoods(UpdateGoodsVo goodsVo) {
        //        String goodsImg = goodsVo.getGoodsImg();
        //        goodsImg = goodsImg.replaceAll("\\[\"", "").replaceAll("\"]", "").replaceAll("\"", "").replaceAll
        //        ("\\\\", "");
        //        goodsVo.setGoodsImg(goodsImg);
        //        Goods goods = goodsVo.resolveGoodsFromVo();
        //        // 删除原有记录，插入新纪录
        //        goodsClassMappingService.delGoodsClassMappingByGoodsId(goodsVo.getGoodsId());
        //        //插入新纪录
        //        Long[] newGoodsClassIds = goodsVo.getGoodsClassIds();
        //        for (int j = 0; j < goodsVo.getGoodsClassIds().length; j++) {
        //            GoodsClassMapping goodsClassMapping1 = new GoodsClassMapping();
        //            goodsClassMapping1.setAppmodelId(goodsVo.getAppmodelId());
        //            goodsClassMapping1.setGoodsClassId(newGoodsClassIds[j]);
        //            goodsClassMapping1.setGoodsId(goods.getGoodsId());
        //            goodsClassMappingService.save(goodsClassMapping1);
        //        }
        //        GoodsDetail goodsDetail = goodsVo.resolveGoodsDetailFromVo();
        //        Long goodsDetailId = goodsDetailService.getGoodsDetailByGoodsId(goodsVo.getGoodsId())
        //        .getGoodsDetailId();
        //        goodsDetail.setGoodsDetailId(goodsDetailId);
        //        if (goodsDetail.getStock() <= 0) {
        //            goodsDetail.setStock(0);
        //            //获取该商品的活动库存，如果活动库存存在则商品不改变为已售尽
        //            List<ActivityGoods> list = activityGoodsService.getActGoodsByGoodsId(goods.getGoodsId());
        //            int actGoodsStock = 0;
        //            for (ActivityGoods o : list) {
        //                actGoodsStock += o.getActivityStock();
        //            }
        //            if (actGoodsStock == 0) {
        //                goods.setGoodsStatus(GoodsConstant.SELL_OUT);
        //            }
        //        }
        //        if (goodsVo.getGoodsStatus().equals(0)) {
        //            List<GoodsAreaMapping> list = goodsAreaMappingService.findByList("goodsId", goods.getGoodsId());
        //            //判断是否已存在投放区域,没有投放至所有区域
        //            if (CollectionUtil.isEmpty(list)) {
        //                goodsUtil.defaultToGoodsAreaMapping(goods);
        //            }
        //        }
        //        Provider provider = providerService.findById(goodsDetail.getProviderId());
        //        goodsDetail.setProviderName(provider.getProviderName());
        //        goodsDetailService.update(goodsDetail);
        //        goodsMapper.updateSelective(goods);
        //        //刷新商品缓存
        //        goodsUtil.flushGoodsCache(goodsVo.getAppmodelId());
    }

    @Override
    public void updateGoodsV119(UpdateGoodsV119Vo goodsVo) {
        String goodsImg = goodsVo.getGoodsImg();
        goodsImg = goodsImg.replaceAll("\\[\"", "").replaceAll("\"]", "").replaceAll("\"", "").replaceAll("\\\\", "");
        goodsVo.setGoodsImg(goodsImg);
        Goods goods = goodsVo.resolveGoodsFromVo();
        String communityIds = goodsVo.getCommunityIds();
        if (communityIds == null || "".equalsIgnoreCase(communityIds)) {
            GoodsAutoAddArea goodsAutoAddArea = new GoodsAutoAddArea();
            goodsAutoAddArea.setGoodsId(goods.getGoodsId());
            goodsAutoAddArea.setAppmodelId(goods.getAppmodelId());
            goodsAutoAddArea.setAutoAdd(goodsVo.isAutoAdd());
            goodsAutoAddArea.setGoodsName(goods.getGoodsName());
            goodsAutoAddAreaService.updateByGoodsId(goodsAutoAddArea);
        }
        if (communityIds != null && !"".equalsIgnoreCase(communityIds)) {
            //将商品投放到设置的小区中
            goodsUtil.updateGoodsAreaMapping(goods, communityIds, goodsVo.isAutoAdd());
        } else if (goodsVo.getGoodsStatus().equals(0)) {
            List<GoodsAreaMapping> list = goodsAreaMappingService.findByList("goodsId", goods.getGoodsId());
            //判断是否已存在投放区域,没有投放至所有区域
            if (CollectionUtil.isEmpty(list)) {
                goodsUtil.defaultToGoodsAreaMapping(goods);
            }
        }
        // 删除原有记录，插入新纪录
        goodsClassMappingService.delGoodsClassMappingByGoodsId(goodsVo.getGoodsId());
        //插入新纪录
        Long[] newGoodsClassIds = goodsVo.getGoodsClassIds();
        for (int j = 0; j < goodsVo.getGoodsClassIds().length; j++) {
            GoodsClassMapping goodsClassMapping1 = new GoodsClassMapping();
            goodsClassMapping1.setAppmodelId(goodsVo.getAppmodelId());
            goodsClassMapping1.setGoodsClassId(newGoodsClassIds[j]);
            goodsClassMapping1.setGoodsId(goods.getGoodsId());
            goodsClassMappingService.save(goodsClassMapping1);
        }
        if (goodsVo.getGoodsStatus().equals(0)) {
            List<GoodsAreaMapping> list = goodsAreaMappingService.findByList("goodsId", goods.getGoodsId());
            //判断是否已存在投放区域,没有投放至所有区域
            if (CollectionUtil.isEmpty(list)) {
                goodsUtil.defaultToGoodsAreaMapping(goods);
            }
        }
        GoodsDetail goodsDetail = goodsVo.resolveGoodsDetailFromVo();
        Long goodsDetailId = goodsDetailService.getGoodsDetailByGoodsId(goodsVo.getGoodsId()).getGoodsDetailId();
        goodsDetail.setGoodsDetailId(goodsDetailId);
        if (goodsDetail.getStock() <= 0) {
            goodsDetail.setStock(0);
            //获取该商品的活动库存，如果活动库存存在则商品不改变为已售尽
            List<ActivityGoods> list = activityGoodsService.getActGoodsByGoodsId(goods.getGoodsId());
            int actGoodsStock = 0;
            for (ActivityGoods o : list) {
                actGoodsStock += o.getActivityStock();
            }
            if (actGoodsStock == 0) {
                goods.setGoodsStatus(GoodsConstant.SELL_OUT);
            }
        }
        Provider provider = providerService.findById(goodsDetail.getProviderId());
        goodsDetail.setProviderName(provider.getProviderName());
        goodsDetailService.update(goodsDetail);
        goodsMapper.updateSelective(goods);
        //刷新商品缓存
        goodsUtil.flushGoodsCache(goodsVo.getAppmodelId());
    }

    @Override
    public void updateGoodsV12(UpdateGoodsV12Vo goodsVo) {
        updateGoodsV119(goodsVo);
    }

    @Override
    public boolean isExists(Long goodsId, String appmodelId) {
        int f = goodsMapper.isExists(goodsId, appmodelId);
        return f == 1;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void soldOut(String goodsIds, String appmodelId) {
        List<Long> goodsIdList = Arrays.stream(goodsIds.split(",")).map(Long::valueOf).collect(Collectors.toList());
        if (CollectionUtil.isNotEmpty(goodsIdList)) {
            //批量下架
            goodsMapper.updateGoodsStatusBatch(goodsIdList);
            //商品是否在某个未开始或已开始的活动中,存在的或则移除,并且归还库存
            activityGoodsService.removeExistsNoStartActivityGoods(goodsIdList, appmodelId);
        }
    }

    /**
     * Putaway.
     *
     * @param goodsIdList the goods id list
     * @param appmodelId  the appmodel id
     */
    @Transactional(rollbackFor = Exception.class)
    public void putaway(Long[] goodsIdList, String appmodelId) {
        //校验goodsId是否存在
        if (!goodsIsExists(goodsIdList, appmodelId)) {
            throw new GlobalException(CodeMsg.PARAMETER_ERROR.fillArgs("无效的商品ID"));
        }
        String goodsIds = Arrays.stream(goodsIdList).map(Object::toString).distinct().collect(Collectors.joining(","));
        List<GoodsDetail> detailList = goodsDetailService.findByGoodsIds(goodsIds);
        Map<Long, GoodsDetail> goodsDetailMap = detailList.stream()
                .collect(Collectors.toMap(GoodsDetail::getGoodsId, v -> v));
        List<String> providerIdList = detailList.stream().map(GoodsDetail::getProviderId).collect(Collectors.toList());
        List<Provider> providerList = providerService.findByProviderId(providerIdList);
        Map<String, Provider> providerMap = providerList.stream()
                .collect(Collectors.toMap(Provider::getProviderId, v -> v));
        List<Long> notDel = new LinkedList<>();
        for (Long goodsId : goodsIdList) {
            GoodsDetail goodsDetail = goodsDetailMap.get(goodsId);
            Provider provider = providerMap.get(goodsDetail.getProviderId());
            if (provider.getProviderStatus().equals(0)) {
                notDel.add(goodsId);
                continue;
            }
            Goods goods = new Goods();
            goods.setGoodsId(goodsId);
            goods.setAppmodelId(appmodelId);
            goods.setGoodsStatus(ON_SALE);
            goodsMapper.updateSelective(goods);
            List<GoodsAreaMapping> list = goodsAreaMappingService.findByList("goodsId", goods.getGoodsId());
            //判断是否已存在投放区域,没有投放至所有区域
            if (CollectionUtil.isEmpty(list)) {
                goodsUtil.defaultToGoodsAreaMapping(goods);
            }
        }
        if (CollectionUtil.isNotEmpty(notDel)) {
            if (notDel.size() - 1 < goodsIdList.length) {
                jobHandler.savaTask(appmodelId, ActiviMqQueueName.GOODS_POSTER_CACHE, 0L, appmodelId, false);
                throw new ServiceException("部分商品因供应商禁用无法上架");
            } else {
                throw new ServiceException("商品因供应商禁用无法上架");
            }
        }
        if (CollectionUtil.isNotEmpty(Arrays.asList(goodsIdList))) {
            //商品是否在某个未开始或已开始的活动中,存在的或则移除,并且归还库存
            activityGoodsService.putAwayActivityGoods(Arrays.asList(goodsIdList), appmodelId);
        }
        //刷新商品缓存
        goodsUtil.flushGoodsCache(appmodelId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delGoods(String goodsIds, String appmodelId) {
        List<Long> goodsIdList = Arrays.stream(goodsIds.split(",")).map(Long::valueOf).collect(Collectors.toList());
        if (CollectionUtil.isNotEmpty(goodsIdList)) {
            //批量删除
            goodsMapper.deleteBatch(goodsIdList);
            //删除商品分类映射
            goodsClassMappingService.deleteByGoodsIds(goodsIds);
            //删除商品时，将投放区域删除
            goodsAreaMappingService.deletebyGoodsIds(goodsIds, appmodelId);
            //将自动投放商品到小区的数据跟新
            goodsAutoAddAreaService.deleteByGoodsId(goodsIds);
            //刷新商品缓存
            goodsUtil.flushGoodsCache(appmodelId);
        }
    }

    @Override
    public List<GoodsResult> getGoodsLikeName(String appmodelId, String goodsName) {
        goodsName = '%' + goodsName + '%';
        return goodsMapper.selectLikeName(appmodelId, goodsName);
    }

    @Override
    public List<GoodsResult> getGoodsLikeNameAndStatus(String appmodelId, String goodsName, Integer goodsStatus) {
        goodsName = '%' + goodsName + '%';
        return goodsMapper.selectByStatusLikeName(appmodelId, goodsName, goodsStatus);
    }

    @Override
    public boolean goodsIsExists(Long[] goodsIds, String appmodelId) {
        if (goodsIds == null || goodsIds.length <= 0) {
            throw new GlobalException(CodeMsg.NULL_PARAM);
        }
        for (Long goodsId : goodsIds) {
            if (!isExists(goodsId, appmodelId)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public List<GoodsFuzzyResult> getGoods(String appmodelId, GetGoodsVo getGoodsVo) {
        List<GoodsFuzzyResult> results = new ArrayList<>();
        List<Long> goodsClassIds = new ArrayList<>();
        if (getGoodsVo.getGoodsClassId() != null) {
            //得到分类的所有下级分类
            goodsClassIds.add(getGoodsVo.getGoodsClassId());
            List<GoodsClass> classList = goodsClassService.findByList("fatherId", getGoodsVo.getGoodsClassId());
            classList.forEach(o -> goodsClassIds.add(o.getGoodsClassId()));
        }
        List<GoodsResult> list = goodsMapper.selectGoods(appmodelId, getGoodsVo, goodsClassIds);
        if (CollectionUtil.isEmpty(list)) {
            return results;
        }
        if (CollectionUtil.isNotEmpty(list)) {
            String goodsIds = list.stream().map(o -> o.getGoodsId().toString()).collect(Collectors.joining(","));
            //得到所有分类映射
            List<GoodsClassMapping> goodsClassMappingList = goodsClassMappingService.findByGoodsIds(goodsIds);
            //根据商品id分类
            Map<String, List<GoodsClassMapping>> goodsClassMap = new HashMap<>(8);
            goodsUtil.goodsClassMap(goodsClassMappingList, goodsClassMap);
            //得到所有商品对应的商品分类
            List<GoodsClass> goodsClassList = goodsClassService.findByList("appmodelId", appmodelId);
            Collections.sort(goodsClassList);
            Map<String, GoodsClass> classmap = goodsClassList.stream()
                    .collect(Collectors.toMap(o -> o.getGoodsClassId().toString(), v -> v));
            Map<String, Double> avgScoreByGoodsIds = goodsUtil.getAllGoodsScore(appmodelId);
            goodsUtil.goodsResultToFuzzyResult(list, goodsClassMap, classmap, avgScoreByGoodsIds, results);
        }
        return results;
    }

    @Override
    public PageInfo<GoodsFuzzyResult> getGoodsV2(String appmodelId, GetGoodsVo getGoodsVo) {
        List<GoodsFuzzyResult> results = new ArrayList<>();
        List<Long> goodsClassIds = new ArrayList<>();
        if (getGoodsVo.getGoodsClassId() != null) {
            //得到分类的所有下级分类
            goodsClassIds.add(getGoodsVo.getGoodsClassId());
            List<GoodsClass> classList = goodsClassService.findByList("fatherId", getGoodsVo.getGoodsClassId());
            classList.forEach(o -> goodsClassIds.add(o.getGoodsClassId()));
        }
        int total = goodsMapper.selectCountGoods(appmodelId, getGoodsVo, goodsClassIds);
        if (total > 0) {
            List<GoodsResult> list = goodsMapper.selectGoods(appmodelId, getGoodsVo, goodsClassIds);
            if (CollectionUtil.isNotEmpty(list)) {
                String goodsIds = list.stream().map(o -> o.getGoodsId().toString()).collect(Collectors.joining(","));
                //得到所有分类映射
                List<GoodsClassMapping> goodsClassMappingList = goodsClassMappingService.findByGoodsIds(goodsIds);
                //根据商品id分类
                Map<String, List<GoodsClassMapping>> goodsClassMap = new HashMap<>(8);
                goodsUtil.goodsClassMap(goodsClassMappingList, goodsClassMap);
                //得到所有商品对应的商品分类
                List<GoodsClass> goodsClassList = goodsClassService.findByList("appmodelId", appmodelId);
                Collections.sort(goodsClassList);
                Map<String, GoodsClass> classmap = goodsClassList.stream()
                        .collect(Collectors.toMap(o -> o.getGoodsClassId().toString(), v -> v));
                Map<String, Double> avgScoreByGoodsIds = goodsUtil.getAllGoodsScore(appmodelId);
                goodsUtil.goodsResultToFuzzyResultV2(list, goodsClassMap, classmap, avgScoreByGoodsIds, results,
                        appmodelId);
            }
        }
        return PageUtil.pageInfo(total, getGoodsVo.getPage(), getGoodsVo.getSize(), results);
    }

    @Override
    public List<GoodsResult4AddAct> getGoodsCanAddToAct(String appmodelId) {
        List<GoodsResult> goodsResults = goodsMapper.selectInPutInGoods(appmodelId);
        return fillGoodsResult4AddAct(goodsResults);
    }

    private List<GoodsResult4AddAct> fillGoodsResult4AddAct(List<GoodsResult> goodsResults) {
        goodsUtil.goodsResultFillGoodsDetail(goodsResults);
        List<GoodsResult4AddAct> goodsResult4AddActs = new ArrayList<>();
        goodsResults.forEach(goodsResult -> {
            GoodsResult4AddAct goodsResult4AddAct = new GoodsResult4AddAct();
            goodsResult4AddAct.setAppmodelId(goodsResult.getAppmodelId());
            goodsResult4AddAct.setCreateTime(goodsResult.getCreateTime());
            goodsResult4AddAct.setCommissionType(goodsResult.getGoodsDetail().getCommissionType());
            goodsResult4AddAct.setExpirationDate(goodsResult.getGoodsDetail().getExpirationDate());
            goodsResult4AddAct.setGoodsId(goodsResult.getGoodsId());
            goodsResult4AddAct.setGoodsImg(goodsResult.getGoodsImg());
            goodsResult4AddAct.setGoodsName(goodsResult.getGoodsName());
            goodsResult4AddAct.setGoodsTitle(goodsResult.getGoodsTitle());
            goodsResult4AddAct.setGoodsStatus(goodsResult.getGoodsStatus());
            goodsResult4AddAct.setGoodsDelFlag(goodsResult.getGoodsDelFlag());
            goodsResult4AddAct.setGoodsDesc(goodsResult.getGoodsDetail().getGoodsDesc());
            goodsResult4AddAct.setGoodsDetailId(goodsResult.getGoodsDetail().getGoodsDetailId());
            goodsResult4AddAct.setGoodsProperty(goodsResult.getGoodsDetail().getGoodsProperty());
            goodsResult4AddAct.setGroupLeaderCommission(goodsResult.getGoodsDetail().getGroupLeaderCommission());
            goodsResult4AddAct.setPrice(goodsResult.getPrice());
            goodsResult4AddAct.setProviderId(goodsResult.getGoodsDetail().getProviderId());
            goodsResult4AddAct.setProviderName(goodsResult.getGoodsDetail().getProviderName());
            goodsResult4AddAct.setStock(goodsResult.getGoodsDetail().getStock());
            goodsResult4AddAct.setSalesVolume(goodsResult.getGoodsDetail().getSalesVolume());
            goodsResult4AddAct.setText(goodsResult.getGoodsDetail().getText());
            goodsResult4AddActs.add(goodsResult4AddAct);
        });
        return goodsResult4AddActs;
    }

    @Override
    public List<GoodsResult4AddAct> goodsCanAddToAct(String appmodelId) {
        List<GoodsResult> goodsResults = goodsMapper.selectInPutInGoods(appmodelId);
        //查询已经在活动中的商品
        //查询已开始和预热中的活动
        List<Activity> groupAct = activityUtil.actOnDoingAndPreheat(appmodelId, ActivityConstant.ACTIVITY_GROUP);
        List<Activity> skillAct = activityUtil.actOnDoingAndPreheat(appmodelId, ActivityConstant.ACTIVITY_SECKILL);
        List<Long> actIds = new ArrayList<>();
        if (CollectionUtil.isNotEmpty(groupAct)) {
            List<Long> groupActIds = groupAct.stream().map(Activity::getActivityId).collect(Collectors.toList());
            actIds.addAll(groupActIds);
        }
        if (CollectionUtil.isNotEmpty(skillAct)) {
            List<Long> skillActIds = skillAct.stream().map(Activity::getActivityId).collect(Collectors.toList());
            actIds.addAll(skillActIds);
        }
        if (CollectionUtil.isNotEmpty(actIds)) {
            List<ActivityGoods> actGoodsByActIdList = activityGoodsService.getActGoodsByActIdList(actIds);
            //获取所有已在活动的商品
            List<Long> goodsIds = actGoodsByActIdList.stream().map(ActivityGoods::getGoodsId).distinct()
                    .collect(Collectors.toList());
            //过滤掉所有已在活动中的商品
            goodsResults =
                    goodsResults.stream().filter(o -> goodsIds.contains(o.getGoodsId())).collect(Collectors.toList());
        }
        return fillGoodsResult4AddAct(goodsResults);
    }

    @Override
    @Deprecated
    public List<ClassAndGoodsResult> findClassAndGoods(String appmodelId) {
        List<ClassAndGoodsResult> result = new ArrayList<>();
        // 一级分类
        List<GoodsClassResult> goodsClasses = goodsClassService.selectFirstClass(appmodelId);
        for (GoodsClassResult goodsClassFirst : goodsClasses) {
            GoodsClass f = new GoodsClass();
            f.setGoodsClassId(goodsClassFirst.getGoodsClassId());
            f.setFatherId(goodsClassFirst.getFatherId());
            f.setGoodsClassName(goodsClassFirst.getGoodsClassName());
            f.setAppmodelId(goodsClassFirst.getAppmodelId());
            f.setCreateTime(goodsClassFirst.getCreateTime());
            ClassAndGoodsResult first = new ClassAndGoodsResult();
            first.setFirstClass(f);
            result.add(first);
            // 一级分类下的所有商品
            List<Goods> goodsFirst = this.selectByClassId(goodsClassFirst.getGoodsClassId());
            goodsFirst.forEach(goods -> {
                ClassAndGoodsResult g = new ClassAndGoodsResult();
                g.setGoods(goods);
                result.add(g);
            });
            // 二级分类
            List<GoodsClass> sencondClass = goodsClassFirst.getClassTwos();
            // 此一级分类下有二级分类
            if (sencondClass.size() > 0) {
                sencondClass.forEach(goodsClass -> {
                    ClassAndGoodsResult classTwo = new ClassAndGoodsResult();
                    classTwo.setSecondClass(goodsClass);
                    result.add(classTwo);
                    List<Goods> goodsSecond = this.selectByClassId(goodsClass.getGoodsClassId());
                    goodsSecond.forEach(goods -> {
                        ClassAndGoodsResult g = new ClassAndGoodsResult();
                        g.setGoods(goods);
                        result.add(g);
                    });
                });
            }
        }
        return result;
    }

    @Override
    public List<Goods> selectByClassId(Long goodsClassId) {
        List<Goods> goodsList = new ArrayList<>();
        List<GoodsClassMapping> list = goodsClassMappingService.findByList("goodsClassId", goodsClassId);
        List<Long> goodsIdList = list.stream().map(GoodsClassMapping::getGoodsId).collect(Collectors.toList());
        if (CollectionUtil.isNotEmpty(goodsIdList)) {
            goodsList = goodsMapper.selectByIdListOnSaleNotDel(goodsIdList);
        }
        return goodsList;
    }

    @Override
    public void updateProviderGoods(Map<String, Object> providerId) {
        goodsMapper.updateProviderGoods(providerId);
    }

    @Override
    public List<String> findByProviderPutawayOfGoods(List<String> providerIds, String appmodelId) {
        return goodsMapper.selectByProviderPutawayOfgoods(providerIds, appmodelId);
    }

    @Override
    public List<Goods> findNoMappinGoods(String appmodelId) {
        return goodsMapper.selectByNoMappinGoods(appmodelId);
    }

    @Override
    public List<Goods> findByIdListNotDel(List<Long> goodsIds) {
        return goodsMapper.selectByIdListNotDel(goodsIds);
    }


    @Override
    public void branchUpdate(String appmodelId, BranchUpdateVo branchUpdateVo) {
        Long[] goodsIdArr1 = (Long[]) goodsUtil.parseArr(branchUpdateVo.getGoodsIds(), Long.class);
        switch (branchUpdateVo.getType()) {
            //删除
            case 1:
                this.delGoods(branchUpdateVo.getGoodsIds(), appmodelId);
                break;
            //修改商品分类:
            case 2:
                Long[] goodsClassArr1 = (Long[]) goodsUtil.parseArr(branchUpdateVo.getGoodsClassIds(), Long.class);
                goodsClassMappingService.addGoodsClassMapping(goodsIdArr1, goodsClassArr1, appmodelId);
                jobHandler.savaTask(appmodelId, ActiviMqQueueName.GOODS_POSTER_CACHE, 0L, appmodelId, false);
                break;
            //批量上架:
            case 3:
                this.putaway(goodsIdArr1, appmodelId);
                break;
            //批量下架
            case 4:
                this.soldOut(branchUpdateVo.getGoodsIds(), appmodelId);
                break;
            default:
                throw new ServiceException("操作类型错误");
        }
    }

    @Override
    public GoodsFuzzyResult getGoodsById(String appmodelId, Long goodsId) {
        GoodsFuzzyResult goodsFuzzyResult = new GoodsFuzzyResult();
        Goods goods = goodsMapper.selectByPrimaryKey(goodsId);
        goodsFuzzyResult.setPrice(goods.getPrice());
        goodsFuzzyResult.setGoodsId(goods.getGoodsId());
        goodsFuzzyResult.setGoodsImg(goods.getGoodsImg());
        goodsFuzzyResult.setGoodsVideoUrl(goods.getGoodsVideoUrl());
        goodsFuzzyResult.setGoodsName(goods.getGoodsName());
        goodsFuzzyResult.setAppmodelId(goods.getAppmodelId());
        goodsFuzzyResult.setCreateTime(goods.getCreateTime());
        goodsFuzzyResult.setGoodsTitle(goods.getGoodsTitle());
        GoodsDetail goodsDetail = goodsDetailService.getGoodsDetailByGoodsId(goods.getGoodsId());
        goodsFuzzyResult.setSalesVolume(goodsDetail.getSalesVolume());
        goodsFuzzyResult.setCommissionType(goodsDetail.getCommissionType());
        goodsFuzzyResult.setExpirationDate(goodsDetail.getExpirationDate());
        goodsFuzzyResult.setGoodsDesc(goodsDetail.getGoodsDesc());
        goodsFuzzyResult.setText(goodsDetail.getText());
        goodsFuzzyResult.setStock(goodsDetail.getStock());
        goodsFuzzyResult.setProviderName(goodsDetail.getProviderName());
        goodsFuzzyResult.setShamVolume(goodsDetail.getShamVolume());
        goodsFuzzyResult.setGoodsProperty(goodsDetail.getGoodsProperty());
        goodsFuzzyResult.setGroupLeaderCommission(goodsDetail.getGroupLeaderCommission());
        goodsFuzzyResult.setProviderId(goodsDetail.getProviderId());
        List<GoodsClassMapping> goodsClassMappings = goodsClassMappingService.findByList("goodsId", goods.getGoodsId());
        List<GoodsClass> goodsClasses = new ArrayList<>();
        //得到所有商品对应的商品分类
        List<GoodsClass> goodsclasslist = goodsClassService.findByList("appmodelId", appmodelId);
        Map<Long, GoodsClass> classmap = goodsclasslist.stream()
                .collect(Collectors.toMap(GoodsClass::getGoodsClassId, v -> v));
        for (GoodsClassMapping goodsClassMapping : goodsClassMappings) {
            goodsClasses.add(classmap.get(goodsClassMapping.getGoodsClassId()));
        }
        goodsFuzzyResult.setGoodsClass(goodsClasses);

        //得到所有可投放小区数量
        List<LineDetail> byAppmodelId = lineDetailService.findByAppmodelId(appmodelId);
        int allAreaNum = byAppmodelId.size();
        goodsFuzzyResult.setAllAreaNum(allAreaNum);

        //获得投放区域缓存
        //		Map<String, List<GoodsAreaMapping>> goodsAreaMappingMap = goodsAreaMappingUtil.getCacheMap(appmodelId);
        //		List<GoodsAreaMapping> goodsAreaMappings = goodsAreaMappingMap.get(goodsId.toString());
        List<GoodsAreaMapping> goodsAreaMappings = goodsUtil.getGoodsAreaMappingByGoodsId(goodsId, appmodelId);
        if (goodsAreaMappings != null && !goodsAreaMappings.isEmpty()) {
            goodsFuzzyResult.setSelectedAreaNum(goodsAreaMappings.size());
        } else {
            goodsFuzzyResult.setSelectedAreaNum(0);
        }
        return goodsFuzzyResult;
    }


}
