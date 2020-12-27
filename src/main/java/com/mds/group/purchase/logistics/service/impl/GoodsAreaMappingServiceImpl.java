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

package com.mds.group.purchase.logistics.service.impl;

import com.mds.group.purchase.core.AbstractService;
import com.mds.group.purchase.goods.model.Goods;
import com.mds.group.purchase.goods.service.GoodsAutoAddAreaService;
import com.mds.group.purchase.goods.service.GoodsService;
import com.mds.group.purchase.goods.vo.UpdateGoodsAreaVo;
import com.mds.group.purchase.logistics.dao.GoodsAreaMappingMapper;
import com.mds.group.purchase.logistics.model.Community;
import com.mds.group.purchase.logistics.model.GoodsAreaMapping;
import com.mds.group.purchase.logistics.model.Line;
import com.mds.group.purchase.logistics.model.LineDetail;
import com.mds.group.purchase.logistics.result.*;
import com.mds.group.purchase.logistics.service.CommunityService;
import com.mds.group.purchase.logistics.service.GoodsAreaMappingService;
import com.mds.group.purchase.logistics.service.LineDetailService;
import com.mds.group.purchase.logistics.service.LineService;
import com.mds.group.purchase.logistics.vo.GoodsAreaSearchVo;
import com.mds.group.purchase.logistics.vo.GoodsAreaVo;
import com.mds.group.purchase.utils.GoodsAreaMappingUtil;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;


/**
 * The type Goods area mapping service.
 *
 * @author shuke
 * @date 2018 /12/20
 */
@Service
public class GoodsAreaMappingServiceImpl extends AbstractService<GoodsAreaMapping> implements GoodsAreaMappingService {

    @Resource
    private LineService lineService;
    @Resource
    private GoodsService goodsService;
    @Resource
    private CommunityService communityService;
    @Resource
    private LineDetailService lineDetailService;
    @Resource
    private GoodsAreaMappingUtil goodsAreaMappingUtil;
    @Resource
    private GoodsAreaMappingMapper tGoodsAreaMappingMapper;
    @Resource
    private GoodsAutoAddAreaService goodsAutoAddAreaService;

    @Override
    public void saveGoodsAreaAndFlushCache(GoodsAreaVo goodsAreaVo) {
        this.saveGoodsArea(goodsAreaVo);
        goodsAreaMappingUtil.flushGoodsAreaMapping(goodsAreaVo.getAppmodelId());
    }

    @Override
    public List<GoodsAreaMappingResult> findBySearch(GoodsAreaSearchVo goodsAreaSearchVo) {
        List<GoodsAreaMapping> list = tGoodsAreaMappingMapper.selectByFuzzy(goodsAreaSearchVo.voToPo());
        if (list == null || list.size() == 0) {
            return null;
        }
        if (!"".equalsIgnoreCase(goodsAreaSearchVo.getGoodsAreaName())
                && goodsAreaSearchVo.getGoodsAreaName() != null) {
            List<Long> goodsIdList = list.stream().map(GoodsAreaMapping::getGoodsId).collect(Collectors.toList());
            list = tGoodsAreaMappingMapper.selectByGoodsIds(goodsIdList);
        }
        List<GoodsAreaMappingResult> resultList = new ArrayList<>();
        String communityIds = list.stream().map(obj -> obj.getCommunityId().toString()).distinct()
                .collect(Collectors.joining(","));
        List<Community> communities = communityService.findByIds(communityIds);
        Map<Long, Community> communityMap = communities.stream()
                .collect(Collectors.toMap(Community::getCommunityId, v -> v));
        List<LineDetail> lineDetails = lineDetailService.findByCommunityIds(communityIds);

        String lineIds = lineDetails.stream().filter(obj -> obj.getLineId() != null)
                .map(obj -> obj.getLineId().toString()).collect(Collectors.joining(","));
        List<Line> lines = new ArrayList<>();
        if (lineDetails.size() != 0) {
            lines = lineService.findByIds(lineIds);
        }
        Map<Long, Line> lineMap = lines.stream().collect(Collectors.toMap(Line::getLineId, v -> v));
        Map<Long, LineDetail> longLineDetailMap = lineDetails.stream()
                .collect(Collectors.toMap(LineDetail::getCommunityId, v -> v));
        for (GoodsAreaMapping goodsAreaMapping : list) {
            //根据商品id来分装
            Community community = communityMap.get(goodsAreaMapping.getCommunityId());
            if (community == null) {
                continue;
            }
            int i = 0;
            for (GoodsAreaMappingResult goodsAreaMappingResult : resultList) {
                if (goodsAreaMappingResult.getGoodsId().equals(goodsAreaMapping.getGoodsId())) {
                    LineAreaResult lineAreaResult = new LineAreaResult();
                    lineAreaResult.setAreaId(community.getCommunityId());
                    lineAreaResult.setAreaName(community.getAreaName());
                    lineAreaResult.setCommunityId(community.getCommunityId());
                    lineAreaResult.setCommunityName(community.getCommunityName());
                    LineDetail lineDetlail = longLineDetailMap.get(community.getCommunityId());
                    if (lineDetlail != null) {
                        Line line = lineMap.get(lineDetlail.getLineId());
                        lineAreaResult.setLineId(line.getLineId());
                        lineAreaResult.setLineName(line.getLineName());
                    }
                    lineAreaResult.setStreetId(community.getStreetId());
                    lineAreaResult.setStreetName(community.getStreetName());
                    List<LineAreaResult> lineAreaResults = goodsAreaMappingResult.getLineAreaResults();
                    lineAreaResults.add(lineAreaResult);
                    goodsAreaMappingResult.setLineAreaResults(lineAreaResults);
                    i++;
                    break;
                }
            }
            if (i > 0) {
                continue;
            }
            GoodsAreaMappingResult result = new GoodsAreaMappingResult();
            result.setAppmodelId(goodsAreaMapping.getAppmodelId());
            result.setGoodsId(goodsAreaMapping.getGoodsId());
            result.setGoodsImg(goodsAreaMapping.getGoodsImg());
            result.setGoodsName(goodsAreaMapping.getGoodsName());
            result.setGoodsAreaId(goodsAreaMapping.getGoodsAreaId());
            LineAreaResult lineAreaResult = new LineAreaResult();
            lineAreaResult.setAreaId(community.getCommunityId());
            lineAreaResult.setAreaName(community.getAreaName());
            lineAreaResult.setCommunityId(community.getCommunityId());
            lineAreaResult.setCommunityName(community.getCommunityName());
            LineDetail lineDetlail = longLineDetailMap.get(community.getCommunityId());
            if (lineDetlail != null) {
                Line line = lineMap.get(lineDetlail.getLineId());
                lineAreaResult.setLineId(line.getLineId());
                lineAreaResult.setLineName(line.getLineName());
            }
            lineAreaResult.setStreetId(community.getStreetId());
            lineAreaResult.setStreetName(community.getStreetName());
            List<LineAreaResult> lineAreaResults = new ArrayList<>();
            lineAreaResults.add(lineAreaResult);
            result.setLineAreaResults(lineAreaResults);
            resultList.add(result);
        }
        return resultList;
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deletebyGoodsIds(String goodsIds, String appmodelId) {
        String[] strings = goodsIds.split(",");
        List<String> goodsIdList = Arrays.asList(strings);
        List<Long> longs = goodsIdList.stream().map(Long::valueOf).collect(Collectors.toList());
        tGoodsAreaMappingMapper.deleteByGoodsIds(longs);
        goodsAreaMappingUtil.flushGoodsAreaMapping(appmodelId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deletebyCommunityIds(String communityIds, String appmodelId) {
        String[] strings = communityIds.split(",");
        List<String> communityIdList = Arrays.asList(strings);
        List<Long> longs = communityIdList.stream().map(Long::valueOf).collect(Collectors.toList());
        tGoodsAreaMappingMapper.deleteByCommunityIds(longs);
        goodsAreaMappingUtil.flushGoodsAreaMapping(appmodelId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateGoodsAreaMapping(GoodsAreaVo goodsAreaVo) {
        //删除原有记录
        this.deletebyGoodsIds(goodsAreaVo.getGoodsId().toString(), goodsAreaVo.getAppmodelId());
        //重新插入
        this.saveGoodsAreaAndFlushCache(goodsAreaVo);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateGoodsAreaMappingBatch(UpdateGoodsAreaVo goodsAreaVo) {
        String goodsIds = goodsAreaVo.getGoodsIds();
        List<String> goodsIdsStr = Arrays.asList(goodsIds.split(","));
        List<Long> goodsId = goodsIdsStr.stream().map(Long::valueOf).collect(Collectors.toList());
        List<Goods> byIds = goodsService.findByIds(goodsIds);

        tGoodsAreaMappingMapper.deleteByGoodsIds(goodsId);

        String communityIdsStr = goodsAreaVo.getCommunityIds();
        //没有社区ID时不需要新增记录
        if (StringUtils.isNotBlank(communityIdsStr)) {
            List<String> communityIdStr = Arrays.asList(communityIdsStr.split(","));
            List<Long> communityIdList = communityIdStr.stream().map(Long::valueOf).collect(Collectors.toList());
            List<LineDetail> lineDetails = lineDetailService.findByCommunityIds(communityIdsStr);
            String lineIds = lineDetails.stream().map(obj -> obj.getLineId().toString()).collect(Collectors.joining(
                    ","));
            List<Line> lines = lineService.findByIds(lineIds);
            List<Community> communities = communityService.findByIds(communityIdsStr);
            //删除原有记录
            for (Goods goods : byIds) {
                GoodsAreaVo vo = new GoodsAreaVo();
                vo.setGoodsName(goods.getGoodsName());
                vo.setAppmodelId(goods.getAppmodelId());
                vo.setCommunityIds(communityIdList);
                vo.setGoodsId(goods.getGoodsId());
                //重新插入
                this.saveGoodsAreaBatch(vo, lineDetails, lines, communities, goods);
            }
        }
        goodsAutoAddAreaService.updateBatch(goodsIds, goodsAreaVo.isAutoAdd(), goodsAreaVo.getAppmodelId());
        goodsAreaMappingUtil.flushGoodsAreaMapping(goodsAreaVo.getAppmodelId());
    }

    private void saveGoodsAreaBatch(GoodsAreaVo goodsAreaVo, List<LineDetail> lineDetails, List<Line> lines,
                                    List<Community> communities, Goods goods) {
        List<GoodsAreaMapping> goodsAreaMappings = new ArrayList<>();
        List<Long> communityIds = goodsAreaVo.getCommunityIds();
        if (!communityIds.isEmpty()) {
            if (!lineDetails.isEmpty()) {
                Map<Long, Line> lineMap = lines.stream().collect(Collectors.toMap(Line::getLineId, v -> v));
                Map<Long, LineDetail> longLineDetailMap = lineDetails.stream()
                        .collect(Collectors.toMap(LineDetail::getCommunityId, v -> v));
                Map<Long, Community> communityMap = communities.stream()
                        .collect(Collectors.toMap(Community::getCommunityId, v -> v));
                for (Long communityId : communityIds) {
                    Community community = communityMap.get(communityId);
                    if (community != null) {
                        GoodsAreaMapping goodsAreaMapping = new GoodsAreaMapping();
                        goodsAreaMapping.setAppmodelId(goodsAreaVo.getAppmodelId());
                        goodsAreaMapping.setCommunityId(communityId);
                        goodsAreaMapping.setGoodsId(goodsAreaVo.getGoodsId());
                        goodsAreaMapping.setGoodsName(goods.getGoodsName());
                        goodsAreaMapping.setGoodsImg(goods.getGoodsImg());
                        //根据小区id得到线路名称
                        LineDetail lineDetlail = longLineDetailMap.get(community.getCommunityId());
                        Line line = lineMap.get(lineDetlail.getLineId());
                        String lineName = line.getLineName();
                        String goodsAreaName =
                                lineName + community.getAreaName() + community.getStreetName() + community
                                        .getCommunityName();
                        goodsAreaMapping.setGoodsAreaName(goodsAreaName);
                        goodsAreaMappings.add(goodsAreaMapping);
                    }
                }
                tGoodsAreaMappingMapper.insertList(goodsAreaMappings);
            }
        }
    }

    private void saveGoodsArea(GoodsAreaVo goodsAreaVo) {
        List<GoodsAreaMapping> goodsAreaMappings = new ArrayList<>();
        List<Long> communityIds = goodsAreaVo.getCommunityIds();
        String ids = goodsAreaVo.getCommunityIds().stream().map(Object::toString).collect(Collectors.joining(","));
        if (!communityIds.isEmpty()) {
            List<LineDetail> lineDetails = lineDetailService.findByCommunityIds(ids);
            String lineIds = lineDetails.stream().map(obj -> obj.getLineId().toString())
                    .collect(Collectors.joining(","));
            if (StringUtils.isNotBlank(lineIds)) {
                List<Line> lines = lineService.findByIds(lineIds);
                Map<Long, Line> lineMap = lines.stream().collect(Collectors.toMap(Line::getLineId, v -> v));
                Map<Long, LineDetail> longLineDetailMap = lineDetails.stream()
                        .collect(Collectors.toMap(LineDetail::getCommunityId, v -> v));
                List<Community> communities = communityService.findByIds(ids);
                Map<Long, Community> communityMap = communities.stream()
                        .collect(Collectors.toMap(Community::getCommunityId, v -> v));
                Goods goods = goodsService.findById(goodsAreaVo.getGoodsId());
                for (Long communityId : communityIds) {
                    Community community = communityMap.get(communityId);
                    if (community != null) {
                        GoodsAreaMapping goodsAreaMapping = new GoodsAreaMapping();
                        goodsAreaMapping.setAppmodelId(goodsAreaVo.getAppmodelId());
                        goodsAreaMapping.setCommunityId(communityId);
                        goodsAreaMapping.setGoodsId(goodsAreaVo.getGoodsId());
                        goodsAreaMapping.setGoodsName(goods.getGoodsName());
                        goodsAreaMapping.setGoodsImg(goods.getGoodsImg());
                        //根据小区id得到线路名称
                        LineDetail lineDetlail = longLineDetailMap.get(community.getCommunityId());
                        Line line = lineMap.get(lineDetlail.getLineId());
                        String lineName = line.getLineName();
                        String goodsAreaName =
                                lineName + community.getAreaName() + community.getStreetName() + community
                                        .getCommunityName();
                        goodsAreaMapping.setGoodsAreaName(goodsAreaName);
                        goodsAreaMappings.add(goodsAreaMapping);
                    }
                }
                tGoodsAreaMappingMapper.insertList(goodsAreaMappings);
            }
        }
    }

    @Override
    public List<GoodsAreaMappingLineResultV2> findByGoodsId(Long goodsId, String appmodelId) {
        List<LineDetail> byAppmodelId = lineDetailService.findByAppmodelId(appmodelId);
        if (byAppmodelId.isEmpty()) {
            return new ArrayList<>();
        }

        List<Long> goodsIdList = new ArrayList<>();
        goodsIdList.add(goodsId);
        List<GoodsAreaMapping> goodsAreaMappings = tGoodsAreaMappingMapper.selectByGoodsIds(goodsIdList);
        List<Long> selectedCommunityIds = new ArrayList<>();
        if (goodsAreaMappings != null) {
            selectedCommunityIds = goodsAreaMappings.stream().map(GoodsAreaMapping::getCommunityId)
                    .collect(Collectors.toList());
        }

        Map<Long, List<GoodsAreaMappingCommunityResultV2>> comMap = new HashMap<>(16);
        for (LineDetail lineDetail : byAppmodelId) {
            List<GoodsAreaMappingCommunityResultV2> byStreetId = comMap.get(lineDetail.getStreetId());
            if (byStreetId == null) {
                byStreetId = new ArrayList<>();
            }
            GoodsAreaMappingCommunityResultV2 gc = new GoodsAreaMappingCommunityResultV2();
            gc.setCommunityId(lineDetail.getCommunityId());
            gc.setCommunityName(lineDetail.getCommunityName());
            if (selectedCommunityIds.contains(lineDetail.getCommunityId())) {
                gc.setSelected(true);
            } else {
                gc.setSelected(false);
            }
            byStreetId.add(gc);
            comMap.put(lineDetail.getStreetId(), byStreetId);
        }
        return getGoodsAreaMappingLineResultV2s(appmodelId, byAppmodelId, comMap);
    }

    @Override
    public List<GoodsAreaMappingLineResultV2> canPick(String appmodelId) {
        List<LineDetail> byAppmodelId = lineDetailService.findByAppmodelId(appmodelId);
        if (byAppmodelId.isEmpty()) {
            return new ArrayList<>();
        }
        Map<Long, List<GoodsAreaMappingCommunityResultV2>> comMap = new HashMap<>(16);
        for (LineDetail lineDetail : byAppmodelId) {
            List<GoodsAreaMappingCommunityResultV2> byStreetId = comMap.get(lineDetail.getStreetId());
            if (byStreetId == null) {
                byStreetId = new ArrayList<>();
            }
            GoodsAreaMappingCommunityResultV2 gc = new GoodsAreaMappingCommunityResultV2();
            gc.setCommunityId(lineDetail.getCommunityId());
            gc.setCommunityName(lineDetail.getCommunityName());
            gc.setSelected(false);
            byStreetId.add(gc);
            comMap.put(lineDetail.getStreetId(), byStreetId);
        }
        return getGoodsAreaMappingLineResultV2s(appmodelId, byAppmodelId, comMap);
    }

    @Override
    public void autoAdd(List<Long> goodsId, List<Long> communityId, String appmodelId) {
        String goodsIds = goodsId.stream().map(Objects::toString).collect(Collectors.joining(","));
        if (StringUtils.isNotBlank(goodsIds)) {
            List<Goods> byIds = goodsService.findByIds(goodsIds);
            String communityIdsStr = communityId.stream().map(Objects::toString).collect(Collectors.joining(","));
            if (StringUtils.isNotBlank(communityIdsStr)) {
                for (Goods goods : byIds) {
                    List<GoodsAreaMapping> goodsAreaMappings =
                            tGoodsAreaMappingMapper.selectByGoodsId(goods.getGoodsId());
                    List<Long> collect = goodsAreaMappings.stream().map(GoodsAreaMapping::getCommunityId)
                            .collect(Collectors.toList());
                    for (Long aLong : communityId) {
                        if (!collect.contains(aLong)) {
                            GoodsAreaMapping vo = new GoodsAreaMapping();
                            vo.setGoodsName(goods.getGoodsName());
                            vo.setAppmodelId(goods.getAppmodelId());
                            vo.setCommunityId(aLong);
                            vo.setGoodsImg(goods.getGoodsImg());
                            vo.setGoodsId(goods.getGoodsId());
                            tGoodsAreaMappingMapper.insert(vo);
                        }
                    }
                }
            }
            goodsAreaMappingUtil.flushGoodsAreaMapping(appmodelId);
        }
    }

    @NotNull
    private List<GoodsAreaMappingLineResultV2> getGoodsAreaMappingLineResultV2s(String appmodelId,
                                                                                List<LineDetail> byAppmodelId,
                                                                                Map<Long,
                                                                                        List<GoodsAreaMappingCommunityResultV2>> comMap) {
        Map<Long, List<GoodsAreaMappingStreetResultV2>> streetMap = new HashMap<>(16);

        Map<Long, LineDetail> collect = byAppmodelId.stream()
                .collect(Collectors.toMap(LineDetail::getStreetId, v -> v, (k, v) -> v));
        collect.forEach((key, value) -> {

            List<GoodsAreaMappingStreetResultV2> svs = streetMap.get(value.getLineId());
            if (svs == null) {
                svs = new ArrayList<>();
            }
            GoodsAreaMappingStreetResultV2 sv = new GoodsAreaMappingStreetResultV2();
            sv.setStreetId(value.getStreetId());
            sv.setStreetName(value.getStreetName());

            List<GoodsAreaMappingCommunityResultV2> comList = comMap.get(key);
            if (comList == null) {
                comList = new ArrayList<>();
            }
            int selectedNum = 0;
            for (GoodsAreaMappingCommunityResultV2 o : comList) {
                if (o.getSelected()) {
                    selectedNum++;
                }
            }
            sv.setTotalNum(comList.size());
            sv.setSelectedNum(selectedNum);
            sv.setCommunityList(comList);
            svs.add(sv);
            streetMap.put(value.getLineId(), svs);
        });

        List<GoodsAreaMappingLineResultV2> lineV2s = new ArrayList<>();
        List<Line> lines = lineService.findByAppmodelId(appmodelId);
        for (Line line : lines) {
            GoodsAreaMappingLineResultV2 lineV2 = new GoodsAreaMappingLineResultV2();
            lineV2.setLineName(line.getLineName());
            lineV2.setLineId(line.getLineId());
            lineV2.setAppmodelId(appmodelId);
            List<GoodsAreaMappingStreetResultV2> stv2 = streetMap.get(line.getLineId());
            lineV2.setStreetList(stv2);
            if (stv2 != null && !stv2.isEmpty()) {
                lineV2s.add(lineV2);
            }
        }
        return lineV2s;
    }
}
