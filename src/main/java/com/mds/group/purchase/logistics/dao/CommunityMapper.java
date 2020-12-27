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

package com.mds.group.purchase.logistics.dao;

import com.mds.group.purchase.core.Mapper;
import com.mds.group.purchase.logistics.dto.CommunityMoreDTO;
import com.mds.group.purchase.logistics.model.Community;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * The interface Community mapper.
 *
 * @author pavawi
 */
public interface CommunityMapper extends Mapper<Community> {

    /**
     * Select communitys by street id list.
     *
     * @param streetId   the street id
     * @param appmodelId the appmodel id
     * @return the list
     */
    List<Community> selectCommunitysByStreetId(@Param("streetId") Integer streetId,
                                               @Param("appmodelId") String appmodelId);

    /**
     * Select by appmodel id list.
     *
     * @param appmodelId the appmodel id
     * @return the list
     */
    List<Community> selectByAppmodelId(String appmodelId);

    /**
     * Select communitys by area id list.
     *
     * @param areaId     the area id
     * @param appmodelId the appmodel id
     * @return the list
     */
    List<Community> selectCommunitysByAreaId(@Param("areaId") Integer areaId,
                                             @Param("appmodelId") String appmodelId);

    /**
     * 查询指定小区的所有信息
     *
     * @param communityIds the community ids
     * @return list list
     */
    List<CommunityMoreDTO> selectByCommunityAll(@Param("communityIds") List<Long> communityIds);

    /**
     * 条件查询
     *
     * @param community the community
     * @return list list
     */
    List<Community> fuzzySelect(Community community);


    /**
     * Select by groupleader id community.
     *
     * @param groupId the group id
     * @return community community
     */
    Community selectByGroupleaderId(String groupId);

    /**
     * 根据街道id逻辑删除小区
     *
     * @param streetId the street id
     */
    void deleteCommunityByStreetId(Long streetId);

    /**
     * 根据城市id查询小区
     *
     * @param cityId the city id
     * @return list list
     */
    List<Community> selectCommunitysByCityId(String cityId);

    /**
     * 根据小区名称搜索
     *
     * @param communityName the community name
     * @param appmodelId    the appmodel id
     * @return list list
     */
    List<Community> searchCommunitysByName(@Param("communityName") String communityName,
                                           @Param("appmodelId") String appmodelId);

    /**
     * 批量删除
     *
     * @param c the c
     */
    void delByIds(List<Long> c);

    /**
     * 根据街道id批量查询
     *
     * @param streetIds the street ids
     * @return list list
     */
    List<Community> selectCommunitysByStreetIds(List<Long> streetIds);

    /**
     * 根据街道id更新街道名称字段
     *
     * @param streetid the streetid
     * @param street   the street
     */
    void updateStreetNameByStreetId(@Param("streetid") Long streetid, @Param("street") String street);

    /**
     * 查询未分组的小区
     *
     * @param appmodelId the appmodel id
     * @return list list
     */
    List<Community> selectByNotGroupCommunity(String appmodelId);

    /**
     * Select by id list list.
     *
     * @param ids the ids
     * @return list list
     */
    List<Community> selectByIdList(@Param("ids") List<Long> ids);
}