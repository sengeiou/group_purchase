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

import com.mds.group.purchase.constant.ActiviMqQueueName;
import com.mds.group.purchase.core.AbstractService;
import com.mds.group.purchase.exception.ServiceException;
import com.mds.group.purchase.jobhandler.ActiveDelaySendJobHandler;
import com.mds.group.purchase.logistics.dao.GoodsAreaMappingMapper;
import com.mds.group.purchase.logistics.dao.LineDetailMapper;
import com.mds.group.purchase.logistics.model.Community;
import com.mds.group.purchase.logistics.model.LineDetail;
import com.mds.group.purchase.logistics.service.CommunityService;
import com.mds.group.purchase.logistics.service.LineDetailService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;


/**
 * The type Line detail service.
 *
 * @author CodeGenerator
 * @date 2018 /11/27
 */
@Service
public class LineDetailServiceImpl extends AbstractService<LineDetail> implements LineDetailService {

    @Resource
    private CommunityService communityService;
    @Resource
    private LineDetailMapper tLineDetailMapper;
    @Resource
    private GoodsAreaMappingMapper tGoodsAreaMappingMapper;
    @Resource
    private ActiveDelaySendJobHandler activeDelaySendJobHandler;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addLineDetails(Long lineId, String appmodelId, List<Long> communities) {
        //查询小区id是否已经被添加到其他线路了
        List<LineDetail> lds = tLineDetailMapper.selectByCommunityIds(communities);
        if (lds != null && !lds.isEmpty()) {
            Map<Long, LineDetail> lineDetailMap = lds.parallelStream()
                    .collect(Collectors.toMap(LineDetail::getCommunityId, v -> v));
            //已经在其他线路，则跳过此小区不做处理
            communities.removeIf(id -> lineDetailMap.get(id) != null);
        }
        if (communities == null || communities.isEmpty()) {
            throw new ServiceException("未选择小区");
        }
        String collect = communities.parallelStream().distinct().map(Object::toString).collect(Collectors.joining(","));
        List<Community> communityList = communityService.findByIds(collect);
        Map<Long, Community> collect1 = communityList.parallelStream()
                .collect(Collectors.toMap(Community::getCommunityId, v -> v));
        List<LineDetail> lineDetails = new ArrayList<>();
        for (Long communityId : communities) {

            LineDetail lineDetail = new LineDetail();
            lineDetail.setAppmodelId(appmodelId);
            lineDetail.setLineId(lineId);
            lineDetail.setCommunityId(communityId);
            Community community = collect1.get(communityId);
            if (community == null) {
                throw new ServiceException("小区id：" + communityId + "不存在");
            }
            lineDetail.setCommunityName(community.getCommunityName());
            lineDetail.setStreetId(community.getStreetId());
            lineDetail.setStreetName(community.getStreetName());
            lineDetail.setDelFlag(0);
            lineDetails.add(lineDetail);
        }
        if (!lineDetails.isEmpty()) {
            tLineDetailMapper.insertList(lineDetails);
        }
        //*************************
        activeDelaySendJobHandler
                .savaTask(communities.stream().map(Objects::toString).collect(Collectors.joining(",")),
                        ActiviMqQueueName.AUTO_ADD_COMMUNITY, 0L, appmodelId, true);
    }


    @Override
    public void delLineDetails(List<Long> lineDetails) {
        tLineDetailMapper.deleteByLDIds(lineDetails);
    }

    @Override
    public void deleteByLineId(Long lineId) {
        tLineDetailMapper.deleteByLineId(lineId);
    }

    @Override
    public LineDetail getLineDetailByUserId(Long wxuserId) {
        return tLineDetailMapper.selectByUserId(wxuserId);
    }

    @Override
    public List<LineDetail> findByLineIds(String lineIds) {
        if ("".equalsIgnoreCase(lineIds)) {
            return new ArrayList<>();
        }
        String[] lineId = lineIds.split(",");
        List<String> li = Arrays.asList(lineId);
        List<Long> list = li.parallelStream().map(Long::valueOf).collect(Collectors.toList());
        return tLineDetailMapper.selectByLineIdList(list);
    }

    @Override
    public void deleteByLineIds(List<Long> lineIds) {

        List<LineDetail> lineDetails = tLineDetailMapper.selectByLineIdList(lineIds);
        List<Long> communityIds = lineDetails.stream().map(LineDetail::getCommunityId).collect(Collectors.toList());
        if (!communityIds.isEmpty()) {
            tGoodsAreaMappingMapper.deleteByCommunityIds(communityIds);
        }
        tLineDetailMapper.deleteByLineIds(lineIds);
    }

    @Override
    public List<LineDetail> findByCommunityIds(String communityIds) {
        List<Long> list = Arrays.stream(communityIds.split(",")).map(Long::valueOf).collect(Collectors.toList());
        return tLineDetailMapper.selectByCommunityIds(list);
    }


    @Override
    public void delById(Long lineDetailId) {
        tLineDetailMapper.deleteLineDetail(lineDetailId);
    }


    @Override
    public void updateStreetNameByStreetId(Long streetid, String street) {
        tLineDetailMapper.updateStreetNameByStreetId(streetid, street);
    }


    @Override
    public List<LineDetail> findByAppmodelId(String appmodelId) {
        return tLineDetailMapper.selectByAppmodelId(appmodelId);
    }


    @Override
    public void deleteByCommunityIds(List<Long> communities) {
        if (communities != null && !communities.isEmpty()) {
            tLineDetailMapper.deleteByCommunityIds(communities);
        }
    }


}
