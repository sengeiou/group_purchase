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
import com.mds.group.purchase.logistics.model.LineDetail;

import java.util.List;


/**
 * The interface Line detail service.
 *
 * @author pavawi
 */
public interface LineDetailService extends Service<LineDetail> {

    /**
     * 添加线路详情
     *
     * @param lineId      线路id
     * @param appmodelId  小程序模板id
     * @param communities 小区id列表
     */
    void addLineDetails(Long lineId, String appmodelId, List<Long> communities);


    /**
     * 根据线路详情id批量删除
     *
     * @param delLineDetails 线路详情id列表
     */
    void delLineDetails(List<Long> delLineDetails);

    /**
     * 根据线路id删除对应的线路详情
     *
     * @param lineId 线路id
     */
    void deleteByLineId(Long lineId);

    /**
     * 根据用户id查询线路详情
     *
     * @param wxuserId 用户id
     * @return LineDetail line detail by user id
     */
    LineDetail getLineDetailByUserId(Long wxuserId);


    /**
     * 查询指定线路下的线路详情
     *
     * @param lineIds the line ids
     * @return list list
     */
    List<LineDetail> findByLineIds(String lineIds);

    /**
     * Delete by line ids.
     *
     * @param lineIds the line ids
     */
    void deleteByLineIds(List<Long> lineIds);

    /**
     * Find by community ids list.
     *
     * @param communityIds the community ids
     * @return the list
     */
    List<LineDetail> findByCommunityIds(String communityIds);


    /**
     * 根据id删除
     *
     * @param lineDetailId the line detail id
     */
    void delById(Long lineDetailId);


    /**
     * 根据街道id修改线路详情的街道名称字段
     *
     * @param streetid the streetid
     * @param street   the street
     */
    void updateStreetNameByStreetId(Long streetid, String street);


    /**
     * 查询所有未删除线路详情
     *
     * @param appmodelId the appmodel id
     * @return the list
     */
    List<LineDetail> findByAppmodelId(String appmodelId);


    /**
     * 根据小区id删除线路详情
     *
     * @param communities the communities
     */
    void deleteByCommunityIds(List<Long> communities);
}
