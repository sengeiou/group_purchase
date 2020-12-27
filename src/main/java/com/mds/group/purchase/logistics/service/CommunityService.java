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

package com.mds.group.purchase.logistics.service;

import com.mds.group.purchase.core.Service;
import com.mds.group.purchase.logistics.dto.CommunityMoreDTO;
import com.mds.group.purchase.logistics.model.Community;
import com.mds.group.purchase.logistics.result.Community4GroupApply;
import com.mds.group.purchase.logistics.result.CommunityHaveGroupLeaderInfo;
import com.mds.group.purchase.logistics.vo.CommunityGetVo;
import com.mds.group.purchase.logistics.vo.CommunityVo;
import com.mds.group.purchase.user.model.GroupLeader;

import java.util.List;
import java.util.Map;


/**
 * The interface Community service.
 *
 * @author pavawi
 */
public interface CommunityService extends Service<Community> {

    /**
     * 新建小区
     *
     * @param communityVo the community vo
     * @param appmodelId  the appmodel id
     */
    void saveCommunity(CommunityVo communityVo, String appmodelId);

    /**
     * 新建小区
     * v1.2
     *
     * @param communityVo the community vo
     * @param appmodelId  the appmodel id
     * @return the long
     */
    Long saveCommunityV2(CommunityVo communityVo, String appmodelId);

    /**
     * 根据小区id批量删除
     *
     * @param communityIds the community ids
     * @param appmodelId   the appmodel id
     */
    void deleteCommunityByIds(String communityIds, String appmodelId);

    /**
     * 根据街道id逻辑删除小区
     * 街道含有团长则不能删除
     *
     * @param streetId   the street id
     * @param appmodelId the appmodel id
     */
    void deleteCommunityByStreetId(Long streetId, String appmodelId);

    /**
     * 跟新小区
     *
     * @param communityVo the community vo
     */
    void updateCommunity(CommunityVo communityVo);

    /**
     * 根据街道id修改街道名称
     *
     * @param streetid the streetid
     * @param street   the street
     */
    void updateStreetNameByStreetId(Long streetid, String street);

    /**
     * 查询指定小区的详情信息
     *
     * @param communityIds the community ids
     * @return community all
     */
    List<CommunityMoreDTO> getCommunityAll(List<Long> communityIds);

    /**
     * 根据传入的条件模糊查询小区
     *
     * @param communityVo the community vo
     * @param appmodelId  the appmodel id
     * @return communities communities
     */
    List<Community> getCommunities(CommunityGetVo communityVo, String appmodelId);

    /**
     * 根据appmodelId获得不可以申请为团长的小区（已经杯申请的小区）id
     *
     * @param streetId   the street id
     * @param appmodelId the appmodel id
     * @return group apply communities
     */
    List<Community4GroupApply> getGroupApplyCommunities(Long streetId, String appmodelId);

    /**
     * 查询团长所有的小区
     *
     * @param groupId the group id
     * @return community community
     */
    Community findByGroupleaderId(String groupId);

    /**
     * 获取用户可以选择的小区列表
     * 有团长的小区才能展示
     *
     * @param appmodelId the appmodel id
     * @param cityName   the city name
     * @return list list
     */
    List<Community> userCanPickCommunities(String appmodelId, String cityName);

    /**
     * 得到城市下面的所有小区
     *
     * @param cityId the city id
     * @return communitys by city id
     */
    List<Community> getCommunitysByCityId(String cityId);

    /**
     * 得到城市下面的所有包含团长的小区
     *
     * @param cityId     the city id
     * @param appmodelId the appmodel id
     * @return communitys have group by city id
     */
    List<Community> getCommunitysHaveGroupByCityId(String cityId, String appmodelId);

    /**
     * 根据小区名称搜索
     *
     * @param communityName the community name
     * @param appmodelId    the appmodel id
     * @param cityName      the city name
     * @return list list
     */
    List<Community> searchCommunitysByName(String communityName, String appmodelId, String cityName);

    /**
     * 得到街道下的所有小区
     *
     * @param streetId   the street id
     * @param appmodelId the appmodel id
     * @return communitys by street id
     */
    List<Community> getCommunitysByStreetId(Long streetId, String appmodelId);

    /**
     * 得到街道下的所有可以添加到线路的小区
     *
     * @param streetIds  the street ids
     * @param appmodelId the appmodel id
     * @return communitys by street id can add to line
     */
    Map<Long, List<Community4GroupApply>> getCommunitysByStreetIdCanAddToLine(List<Long> streetIds, String appmodelId);

    /**
     * 根据小区id查询小区
     *
     * @param communityId the community id
     * @return community by id
     */
    Community getCommunityById(Long communityId);

    /**
     * 得到县区下面的所有小区
     *
     * @param areaId     the area id
     * @param appmodelId the appmodel id
     * @return communitys by area id
     */
    List<Community> getCommunitysByAreaId(String areaId, String appmodelId);

    /**
     * 获取未分组的小区
     *
     * @param appmodelId the appmodel id
     * @return list list
     */
    List<Community> findNotGroupCommunity(String appmodelId);

    /**
     * 获取小区信息
     *
     * @param communityVo the community vo
     * @param appmodelId  the appmodel id
     * @return communities by street id
     */
    List<CommunityHaveGroupLeaderInfo> getCommunitiesByStreetId(CommunityGetVo communityVo, String appmodelId);

    /**
     * 根据app modelId获取小区
     *
     * @param appmodelId the appmodel id
     * @return the list
     */
    List<Community> findByAppmodelId(String appmodelId);

    /**
     * 查询小区
     *
     * @param groupLeaderList the group leader list
     * @return long community map
     */
    Map<Long, Community> getLongCommunityMap(List<GroupLeader> groupLeaderList);
}
