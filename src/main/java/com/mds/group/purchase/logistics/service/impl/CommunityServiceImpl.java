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

import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import com.mds.group.purchase.constant.GroupLeaderStatus;
import com.mds.group.purchase.core.AbstractService;
import com.mds.group.purchase.core.CodeMsg;
import com.mds.group.purchase.exception.GlobalException;
import com.mds.group.purchase.exception.ServiceException;
import com.mds.group.purchase.logistics.dao.CommunityMapper;
import com.mds.group.purchase.logistics.dto.CommunityLineInfoDTO;
import com.mds.group.purchase.logistics.dto.CommunityMoreDTO;
import com.mds.group.purchase.logistics.model.Areas;
import com.mds.group.purchase.logistics.model.Community;
import com.mds.group.purchase.logistics.model.LineDetail;
import com.mds.group.purchase.logistics.result.Community4GroupApply;
import com.mds.group.purchase.logistics.result.CommunityHaveGroupLeaderInfo;
import com.mds.group.purchase.logistics.service.*;
import com.mds.group.purchase.logistics.vo.CommunityGetVo;
import com.mds.group.purchase.logistics.vo.CommunityVo;
import com.mds.group.purchase.user.model.GroupLeader;
import com.mds.group.purchase.user.service.GroupLeaderService;
import com.mds.group.purchase.utils.GeoCodeUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;


/**
 * The type Community service.
 *
 * @author shuke
 * @date 2018 /11/27
 */
@Service
public class CommunityServiceImpl extends AbstractService<Community> implements CommunityService {

    @Resource
    private LineService lineService;
    @Resource
    private AreasService areasService;
    @Resource
    private CommunityMapper tCommunityMapper;
    @Resource
    private LineDetailService lineDetailService;
    @Resource
    private GroupLeaderService groupLeaderService;
    @Resource
    private GoodsAreaMappingService goodsAreaMappingService;

    @Override
    public List<Community> getCommunitysByStreetId(Long streetId, String appmodelId) {
        return tCommunityMapper.selectCommunitysByStreetId(streetId.intValue(), appmodelId);
    }

    @Override
    public Map<Long, List<Community4GroupApply>> getCommunitysByStreetIdCanAddToLine(List<Long> streetIds,
                                                                                     String appmodelId) {
        if (streetIds == null || streetIds.isEmpty()) {
            return Maps.newHashMap();
        }
        List<Community> lists = tCommunityMapper.selectCommunitysByStreetIds(streetIds);
        if (lists == null || lists.size() == 0) {
            return new HashMap<>(8);
        }
        Map<Long, List<Community>> collect = lists.stream().collect(Collectors.groupingBy(Community::getStreetId));
        Map<Long, List<Community4GroupApply>> cmap = Maps.newHashMapWithExpectedSize(16);
        for (Map.Entry<Long, List<Community>> next : collect.entrySet()) {
            List<Community> list = next.getValue();
            //查找线路详情
            List<Long> communityIds = new ArrayList<>();
            list.forEach(community -> communityIds.add(community.getCommunityId()));
            String communityIdStr = communityIds.stream().map(Object::toString).collect(Collectors.joining(","));
            List<LineDetail> lineDetails = lineDetailService.findByCommunityIds(communityIdStr);
            Map<Long, LineDetail> lineDetailMap = lineDetails.stream()
                    .collect(Collectors.toMap(LineDetail::getCommunityId, v -> v));
            Iterator<Community> iterator = list.iterator();
            List<Community4GroupApply> community4GroupApplyList = new ArrayList<>();
            while (iterator.hasNext()) {
                Community4GroupApply community4GroupApply = new Community4GroupApply();
                Community community = iterator.next();
                LineDetail lineDetail = lineDetailMap.get(community.getCommunityId());
                community4GroupApply.communityToThis(community);
                if (lineDetail != null && lineDetail.getLineId() != null && lineDetail.getLineId() > 0) {
                    //该小区已经存在于其他线路
                    community4GroupApply.setCanApply(false);
                } else {
                    community4GroupApply.setCanApply(true);
                }
                community4GroupApplyList.add(community4GroupApply);
            }
            cmap.put(next.getKey(), community4GroupApplyList);
        }
        return cmap;
    }

    @Override
    public void saveCommunity(CommunityVo communityVo, String appmodelId) {
        Community community = communityVo.voToCommunity();
        community.setCommunityId(null);
        community.setAppmodelId(appmodelId);
        Areas area = areasService.findBy("areaid", community.getAreaId());
        community.setAreaName(area.getArea());
        community.setDelFlag(0);
        tCommunityMapper.insertSelective(community);
        lineDetailService
                .addLineDetails(communityVo.getLineId(), appmodelId,
                        Collections.singletonList(community.getCommunityId()));
    }

    /**
     * saveCommunityV2的实现方法
     */
    @Override
    public Long saveCommunityV2(CommunityVo communityVo, String appmodelId) {
        Community community = communityVo.voToCommunity();
        community.setCommunityId(null);
        community.setAppmodelId(appmodelId);
        Areas area = areasService.findBy("areaid", community.getAreaId());
        community.setAreaName(area.getArea());
        community.setDelFlag(0);
        tCommunityMapper.insertSelective(community);
        return community.getCommunityId();
    }

    @Override
    public Community getCommunityById(Long communityId) {
        return tCommunityMapper.selectByPrimaryKey(communityId);
    }

    @Override
    public List<Community> getCommunitysByAreaId(String areaId, String appmodelId) {
        return tCommunityMapper.selectCommunitysByAreaId(Integer.parseInt(areaId), appmodelId);
    }

    @Override
    public List<Community> findNotGroupCommunity(String appmodelId) {
        return tCommunityMapper.selectByNotGroupCommunity(appmodelId);
    }

    @Override
    public List<CommunityHaveGroupLeaderInfo> getCommunitiesByStreetId(CommunityGetVo communityVo, String appmodelId) {
        List<Community> communities = getCommunities(communityVo, appmodelId);
        if (CollectionUtil.isEmpty(communities)) {
            return new ArrayList<>();
        }
        List<CommunityHaveGroupLeaderInfo> communityHaveGroupLeaderInfos1 = new ArrayList<>();
        List<GroupLeader> byAppmodelId = groupLeaderService.findByAppmodelId(appmodelId);
        Map<Long, GroupLeader> collect = byAppmodelId.stream()
                .collect(Collectors.toMap(GroupLeader::getCommunityId, v -> v));
        //封装线路信息
        List<Long> communityIds = communities.stream().map(Community::getCommunityId).collect(Collectors.toList());
        List<CommunityLineInfoDTO> communityLineInfoDTOS = lineService.findCommunityLine(communityIds);
        Map<Long, CommunityLineInfoDTO> communityLineInfoDTOMap = communityLineInfoDTOS.stream()
                .collect(Collectors.toMap(CommunityLineInfoDTO::getCommunityId, v -> v));
        communities.forEach(obj -> {
            CommunityHaveGroupLeaderInfo communityHaveGroupLeaderInfo = new CommunityHaveGroupLeaderInfo(obj);
            communityHaveGroupLeaderInfo.setHaveGroup(false);
            if (collect.containsKey(obj.getCommunityId())) {
                communityHaveGroupLeaderInfo.setHaveGroup(true);
            }
            CommunityLineInfoDTO communityLineInfoDTO = communityLineInfoDTOMap.get(obj.getCommunityId());
            if (communityLineInfoDTO != null) {
                communityHaveGroupLeaderInfo.setLineId(communityLineInfoDTO.getLineId());
                communityHaveGroupLeaderInfo.setLineName(communityLineInfoDTO.getLineName());
            } else {
                communityHaveGroupLeaderInfo.setLineName("暂无分配至线路");
            }
            communityHaveGroupLeaderInfos1.add(communityHaveGroupLeaderInfo);
        });
        return communityHaveGroupLeaderInfos1;
    }

    @Override
    public List<Community> findByAppmodelId(String appmodelId) {
        return tCommunityMapper.selectByAppmodelId(appmodelId);
    }

    @Override
    public void updateCommunity(CommunityVo communityVo) {
        if (communityVo.getCommunityId() == null) {
            throw new GlobalException(CodeMsg.BIND_ERROR);
        }
        tCommunityMapper.updateByPrimaryKeySelective(communityVo.voToCommunity());
        List<CommunityLineInfoDTO> communityLine = lineService
                .findCommunityLine(Collections.singletonList(communityVo.getCommunityId()));
        //存在分配的线路则修改
        //修改分配的线路
        if (CollectionUtil.isNotEmpty(communityLine)) {
            CommunityLineInfoDTO communityLineInfoDTO = communityLine.get(0);
            //删除已分配的线路
            if (communityVo.getLineId() == null || communityVo.getLineId() == 0) {
                lineDetailService.delById(communityLineInfoDTO.getLineDetailid());
            } else {
                //更新线路详情的小区信息
                LineDetail lineDetail = new LineDetail();
                lineDetail.setLineDetailId(communityLineInfoDTO.getLineDetailid());
                lineDetail.setCommunityId(communityVo.getCommunityId());
                lineDetail.setLineId(communityVo.getLineId());
                lineDetail.setCommunityName(communityVo.getCommunityName());
                lineDetail.setStreetId(communityVo.getStreetId());
                lineDetail.setStreetName(communityVo.getStreetName());
                lineDetailService.update(lineDetail);
            }
            return;
        }
        //没有分配线路,线路新增该小区
        if (communityVo.getLineId() != null && communityVo.getLineId() > 0) {
            lineDetailService.addLineDetails(communityVo.getLineId(), communityVo.getAppmodelId(),
                    Collections.singletonList(communityVo.getCommunityId()));
        }
    }

    @Override
    public List<CommunityMoreDTO> getCommunityAll(List<Long> communityIds) {
        return tCommunityMapper.selectByCommunityAll(communityIds);
    }

    @Override
    public List<Community> getCommunities(CommunityGetVo communityVo, String appmodelId) {
        if (communityVo == null) {
            communityVo = new CommunityGetVo();
        }
        Community community = communityVo.voToCommunity();
        community.setAppmodelId(appmodelId);
        return tCommunityMapper.fuzzySelect(community);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteCommunityByIds(String communityIds, String appmodelId) {
        String[] cIds = communityIds.split(",");
        List<String> cIdList = Arrays.asList(cIds);
        List<Long> c = cIdList.parallelStream().map(Long::valueOf).collect(Collectors.toList());
        List<GroupLeader> list = groupLeaderService.findByAppmodelId(appmodelId);
        list.forEach(obj -> {
            if (c.contains(obj.getCommunityId())) {
                throw new ServiceException("该小区存在团长，删除失败");
            }
        });
        tCommunityMapper.delByIds(c);
        //删除小区后，将对应的线路详情删除
        List<LineDetail> byCommunityIds = lineDetailService.findByCommunityIds(communityIds);
        if (byCommunityIds != null && !byCommunityIds.isEmpty()) {
            List<Long> lineDetailIdList = byCommunityIds.parallelStream().map(LineDetail::getLineDetailId)
                    .collect(Collectors.toList());
            lineDetailService.delLineDetails(lineDetailIdList);
        }
        //删除之后更新对应的商品投放区域
        goodsAreaMappingService.deletebyCommunityIds(String.join(",", cIdList), appmodelId);

    }

    @Override
    public List<Community4GroupApply> getGroupApplyCommunities(Long streetId, String appmodelId) {
        //获取已经申请的小区
        List<GroupLeader> groupLeaders = groupLeaderService.findByAppmodelId(appmodelId);
        if (groupLeaders == null) {
            groupLeaders = new ArrayList<>();
        }
        List<Long> communityIdList = groupLeaders.stream().map(GroupLeader::getCommunityId)
                .collect(Collectors.toList());
        //获取当前街道id下的所有小区
        List<Community> communities = this.getCommunitysByStreetId(streetId, appmodelId);
        List<Community4GroupApply> community4GroupApplyList = new ArrayList<>();
        communities.forEach(community -> {
            Community4GroupApply community4GroupApply = new Community4GroupApply();
            community4GroupApply.setCanApply(true);
            community4GroupApply.communityToThis(community);
            if (communityIdList.contains(community.getCommunityId())) {
                community4GroupApply.setCanApply(false);
            }
            community4GroupApplyList.add(community4GroupApply);
        });
        return community4GroupApplyList;
    }

    @Override
    public Community findByGroupleaderId(String groupId) {
        return tCommunityMapper.selectByGroupleaderId(groupId);
    }

    @Override
    public void deleteCommunityByStreetId(Long streetId, String appmodelId) {
        List<Community> comm = findByList("streetId", streetId);
        if (comm != null && !comm.isEmpty()) {
            List<Long> c = comm.parallelStream().map(Community::getCommunityId).collect(Collectors.toList());
            List<GroupLeader> list = groupLeaderService.findByAppmodelId(appmodelId);
            list.forEach(obj -> {
                if (c.contains(obj.getCommunityId())) {
                    throw new ServiceException("该区域存在团长，删除失败");
                }
            });
            deleteCommunityByIds(c.stream().map(Object::toString).collect(Collectors.joining(",")), appmodelId);
        }
    }

    @Override
    public List<Community> userCanPickCommunities(String appmodelId, String findCityName) {
        List<GroupLeader> groupLeaders = groupLeaderService
                .findByStatusAppmodelId(GroupLeaderStatus.STATUS_NORMAL, appmodelId);
        List<Community> communities = new ArrayList<>();
        if (groupLeaders != null && !groupLeaders.isEmpty()) {
            List<Long> ids = groupLeaders.stream().map(GroupLeader::getCommunityId).collect(Collectors.toList());
            communities = tCommunityMapper.selectByIdList(ids);
        }
        //排除其他市的小区
        communities = this.getAssignCityCommunity(findCityName, communities);
        return communities;
    }

    private List<Community> getAssignCityCommunity(String findCityName, List<Community> communities) {
        if (CollectionUtil.isNotEmpty(communities) && StringUtils.isNotBlank(findCityName)) {
            String[] split = GeoCodeUtil.getLongitudeAndLatitude(findCityName).split(",");
            JSONObject jsonObject = JSONObject.parseObject(GeoCodeUtil.getArea(split[0], split[1], false));
            String cityName = jsonObject.getJSONObject("regeocode").getJSONObject("addressComponent").getString("city");
            String province = jsonObject.getJSONObject("regeocode").getJSONObject("addressComponent")
                    .getString("province");
            communities = communities.stream()
                    .filter(obj -> obj.getPcaAdr().contains(cityName) || obj.getPcaAdr().contains(province))
                    .collect(Collectors.toList());
        }
        return communities;
    }

    @Override
    public List<Community> getCommunitysByCityId(String cityId) {
        return tCommunityMapper.selectCommunitysByCityId(cityId);
    }

    @Override
    public List<Community> getCommunitysHaveGroupByCityId(String cityId, String appmodelId) {
        List<GroupLeader> groupLeaders = groupLeaderService
                .findByStatusAppmodelId(GroupLeaderStatus.STATUS_NORMAL, appmodelId);
        List<Long> communityIdList = groupLeaders.stream().map(GroupLeader::getCommunityId)
                .collect(Collectors.toList());
        List<Community> communities = tCommunityMapper.selectCommunitysByCityId(cityId);
        List<Community> communityRes = new ArrayList<>();
        communities.forEach(o -> {
            if (communityIdList.contains(o.getCommunityId())) {
                communityRes.add(o);
            }
        });
        return communityRes;
    }

    @Override
    public List<Community> searchCommunitysByName(String communityName, String appmodelId, String cityName) {
        List<GroupLeader> groupLeaders = groupLeaderService
                .findByStatusAppmodelId(GroupLeaderStatus.STATUS_NORMAL, appmodelId);
        List<Long> communityIdList = groupLeaders.stream().map(GroupLeader::getCommunityId)
                .collect(Collectors.toList());
        List<Community> communities = tCommunityMapper.searchCommunitysByName(communityName, appmodelId);
        communities = getAssignCityCommunity(cityName, communities);
        List<Community> communityRes = new ArrayList<>();
        communities.forEach(o -> {
            if (communityIdList.contains(o.getCommunityId())) {
                communityRes.add(o);
            }
        });
        return communityRes;
    }

    @Override
    public void updateStreetNameByStreetId(Long streetid, String street) {
        tCommunityMapper.updateStreetNameByStreetId(streetid, street);
    }

    @Override
    public Map<Long, Community> getLongCommunityMap(List<GroupLeader> groupLeaderList) {
        //查询小区
        String communityIds = groupLeaderList.stream().map(obj -> obj.getCommunityId().toString())
                .collect(Collectors.joining(","));
        return this.findByIds(communityIds).stream().collect(Collectors.toMap(Community::getCommunityId, v -> v));
    }


}
