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
import com.mds.group.purchase.logistics.model.LineDetail;
import com.mds.group.purchase.logistics.result.LineDetailResult;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 线路详情的数据库操作接口类
 *
 * @author shuke
 * @date 2018 -12-11
 */
public interface LineDetailMapper extends Mapper<LineDetail> {

    /**
     * 根据线路id查询线路详情
     *
     * @param lineId 线路id
     * @return list list
     */
    List<LineDetailResult> selectByLineId(Long lineId);

    /**
     * 根据线路详情id删除
     *
     * @param lineDetailId 线路详情id
     */
    void deleteLineDetail(Long lineDetailId);

    /**
     * 根据线路id删除对应的线路详情
     *
     * @param lineId 线路id
     */
    void deleteByLineId(Long lineId);

    /**
     * 根据线路id查询区域名称
     *
     * @param lineId 线路id
     * @return 区域名称列表 list
     */
    List<String> selectStreetNameByLineId(Long lineId);

    /**
     * 根据区域id查询线路详情
     *
     * @param streetId 区域id
     * @return 线路详情列表 list
     */
    List<LineDetail> selectDetailByStreetId(Long streetId);

    /**
     * 根据用户id查询对应的线路详情
     *
     * @param wxuserId 用户id
     * @return LineDetail line detail
     */
    LineDetail selectByUserId(Long wxuserId);

    /**
     * Delete by line ids.
     *
     * @param lineIds the line ids
     */
    void deleteByLineIds(List<Long> lineIds);

    /**
     * 根据线路id查询线路详情
     *
     * @param lineIds 线路id
     * @return list list
     */
    List<LineDetailResult> selectByLineIds(String lineIds);

    /**
     * 根据线路id查询线路详情
     *
     * @param lineIds 线路id
     * @return list list
     */
    List<LineDetail> selectByLineIdList(List<Long> lineIds);

    /**
     * 根据小区id查询
     *
     * @param communities the communities
     * @return list list
     */
    List<LineDetail> selectByCommunityIds(List<Long> communities);

    /**
     * 批量删除
     *
     * @param lineDetails the line details
     */
    void deleteByLDIds(List<Long> lineDetails);

    /**
     * 根据街道id修改线路详情的街道名称字段
     *
     * @param streetid the streetid
     * @param street   the street
     */
    void updateStreetNameByStreetId(@Param("streetid") Long streetid, @Param("street") String street);

    /**
     * 查询所有未删除详情
     *
     * @param appmodelId the appmodel id
     * @return the list
     */
    List<LineDetail> selectByAppmodelId(String appmodelId);

    /**
     * Select by community id line detail.
     *
     * @param communityId the community id
     * @return the line detail
     */
    LineDetail selectByCommunityId(@Param("communityId") Long communityId);

    /**
     * 根据小区id 删除线路详情
     *
     * @param communities the communities
     */
    void deleteByCommunityIds(@Param("communities") List<Long> communities);
}