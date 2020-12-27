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

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.google.common.collect.Maps;
import com.mds.group.purchase.core.AbstractService;
import com.mds.group.purchase.core.CodeMsg;
import com.mds.group.purchase.exception.GlobalException;
import com.mds.group.purchase.exception.ServiceException;
import com.mds.group.purchase.logistics.dao.LineMapper;
import com.mds.group.purchase.logistics.dto.CommunityLineInfoDTO;
import com.mds.group.purchase.logistics.model.Line;
import com.mds.group.purchase.logistics.model.LineDetail;
import com.mds.group.purchase.logistics.result.LineDetailResult;
import com.mds.group.purchase.logistics.result.LineResult;
import com.mds.group.purchase.logistics.service.LineDetailService;
import com.mds.group.purchase.logistics.service.LineService;
import com.mds.group.purchase.logistics.vo.LineAddCommunityV12Vo;
import com.mds.group.purchase.logistics.vo.LineGetVo;
import com.mds.group.purchase.logistics.vo.LineV12Vo;
import com.mds.group.purchase.logistics.vo.LineVo;
import com.mds.group.purchase.utils.GoodsAreaMappingUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;


/**
 * The type Line service.
 *
 * @author CodeGenerator
 * @date 2018 /11/27
 */
@Service
public class LineServiceImpl extends AbstractService<Line> implements LineService {

    @Resource
    private LineMapper tLineMapper;
    @Resource
    private LineDetailService lineDetailService;
    @Resource
    private GoodsAreaMappingUtil goodsAreaMappingUtil;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveLine(LineVo lineVo) {
        //1、插入line对象
        Line line = lineVo.voToLine();
        line.setDelFlag(0);
        tLineMapper.insertSelective(line);
        //2、取得要添加小区,插入lineDetail对象
        List<Long> communities = lineVo.getCommunities();
        lineDetailService.addLineDetails(line.getLineId(), line.getAppmodelId(), communities);
        //刷新投放区域缓存
        goodsAreaMappingUtil.flushGoodsAreaMapping(lineVo.getAppmodelId());
    }

    @Override
    public Long saveLineV12(LineV12Vo lineVo) {
        //1、插入line对象
        Line line = lineVo.voToLine();
        line.setDelFlag(0);
        tLineMapper.insertSelective(line);
        return line.getLineId();
    }

    @Override
    public void lineAddCommunity(LineAddCommunityV12Vo lineVo) {
        lineDetailService.deleteByLineId(lineVo.getLineId());
        List<Long> communities = lineVo.getCommunities();
        lineDetailService.addLineDetails(lineVo.getLineId(), lineVo.getAppmodelId(), communities);
        //刷新投放区域缓存
        goodsAreaMappingUtil.flushGoodsAreaMapping(lineVo.getAppmodelId());
    }

    @Override
    public Map<String, Object> getLine(LineGetVo lineGetVo, String appmodelId) {
        PageHelper.startPage(lineGetVo.getPage(), lineGetVo.getSize());
        List<Line> lineList = tLineMapper.selectByVo(lineGetVo, appmodelId);
        Page page = (Page) lineList;

        Map<String, Object> map = new HashMap<>(8);
        map.put("page", page);
        List<LineResult> lineResults = this.getLineResultByLine(lineList);
        map.put("list", lineResults);
        return map;
    }

    @Override
    public Map<String, Object> getLineV12(LineGetVo lineGetVo, String appmodelId) {
        PageHelper.startPage(lineGetVo.getPage(), lineGetVo.getSize());
        List<Line> lineList = tLineMapper.selectByV12Vo(lineGetVo, appmodelId);
        Page page = (Page) lineList;

        Map<String, Object> map = Maps.newHashMapWithExpectedSize(2);
        map.put("page", page);
        List<LineResult> lineResults = this.getLineResultByLine(lineList);
        map.put("list", lineResults);
        return map;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateLine(LineVo lineVo) {
        if (lineVo.getLineId() == null) {
            throw new GlobalException(CodeMsg.BIND_ERROR);
        }
        if (lineVo.getCommunities() == null || lineVo.getCommunities().isEmpty()) {
            throw new ServiceException("请选择小区");
        }
        Line line = lineVo.voToLine();

        lineDetailService.deleteByLineId(line.getLineId());
        lineDetailService.addLineDetails(line.getLineId(), line.getAppmodelId(), lineVo.getCommunities());
        tLineMapper.updateByPrimaryKeySelective(line);
        //刷新投放区域缓存
        goodsAreaMappingUtil.flushGoodsAreaMapping(lineVo.getAppmodelId());
    }

    @Override
    public void updateLineV12(LineV12Vo lineVo) {
        if (lineVo.getLineId() == null) {
            throw new GlobalException(CodeMsg.BIND_ERROR.fillArgs("线路id不能为空"));
        }
        Line line = lineVo.voToLine();
        tLineMapper.updateByPrimaryKeySelective(line);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteLineByIds(String lineIds, String appmodelId) {
        List<Long> lineIdList = Arrays.asList(lineIds.split(",")).parallelStream().map(Long::valueOf)
                .collect(Collectors.toList());
        lineDetailService.deleteByLineIds(lineIdList);
        tLineMapper.deleteByLineIds(lineIdList);
        goodsAreaMappingUtil.flushGoodsAreaMapping(appmodelId);
    }


    @Override
    public List<LineResult> getLineStreetCommunity(String appmodelId) {
        List<Line> lines = tLineMapper.selectByAppmodelId(appmodelId);
        return this.getLineResultByLine(lines);
    }

    @Override
    public List<Line> findByAreaid(String areaid, String appmodelId) {
        return tLineMapper.selectByAreaId(areaid, appmodelId);
    }

    @Override
    public List<CommunityLineInfoDTO> findCommunityLine(List<Long> communityIds) {
        return tLineMapper.selectByCommunityLine(communityIds);
    }

    @Override
    public List<Line> findByAppmodelId(String appmodelId) {
        return tLineMapper.selectByAppmodelId(appmodelId);
    }

    @Override
    public Object getAll(String appmodelId) {
        return tLineMapper.selectByAppmodelId(appmodelId);
    }

    @Override
    public List<Line> findByAppmodelIdV2(String appmodelId) {
        return tLineMapper.selectByVo(new LineGetVo(), appmodelId);
    }

    private List<LineResult> getLineResultByLine(List<Line> lines) {
        String lineIds = lines.stream().map(obj -> obj.getLineId().toString()).collect(Collectors.joining(","));
        //将线路详情按照线路id封装成map
        List<LineDetail> lineDetails = lineDetailService.findByLineIds(lineIds);
        if (lineDetails == null || lineDetails.isEmpty()) {
            if (lines.isEmpty()) {
                return new ArrayList<>();
            } else {
                List<LineResult> results1 = new ArrayList<>();
                lines.forEach(line -> {
                    LineResult lineResult = new LineResult(line);
                    results1.add(lineResult);
                });
                Collections.sort(results1);
                return results1;
            }

        }
        Map<Long, List<LineDetail>> lineDetailList = lineDetails.stream()
                .collect(Collectors.groupingBy(LineDetail::getLineId));
        //将listmap的值按照街道id封装成map
        Map<Long, List<LineDetailResult>> lineDetailResultMap = new HashMap<>(8);
        lineDetailList.forEach((k, v) -> {
            Map<Long, List<LineDetail>> collect =
                    v.stream().filter(o -> o.getStreetId() != null).collect(Collectors.groupingBy(LineDetail::getStreetId));
            List<LineDetailResult> lineDetailResults = new ArrayList<>();
            collect.forEach((k1, v2) -> {
                LineDetailResult detailResult = new LineDetailResult();
                detailResult.setStreetId(v2.get(0).getStreetId());
                detailResult.setStreetName(v2.get(0).getStreetName());
                detailResult.setLineId(v2.get(0).getLineId());
                detailResult.setLineDetails(v2);
                lineDetailResults.add(detailResult);
            });
            lineDetailResultMap.put(k, lineDetailResults);
        });

        List<LineResult> results1 = new ArrayList<>();
        lines.forEach(line -> {
            LineResult lineResult = new LineResult(line);
            lineResult.setLineDetailResults(lineDetailResultMap.get(line.getLineId()));
            results1.add(lineResult);
        });
        Collections.sort(results1);
        return results1;
    }
}
