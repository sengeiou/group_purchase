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
import com.mds.group.purchase.logistics.model.Streets;
import com.mds.group.purchase.logistics.result.StreetsResult;
import com.mds.group.purchase.logistics.vo.StreetsVo;

import java.util.List;


/**
 * The interface Streets service.
 *
 * @author pavawi
 */
public interface StreetsService extends Service<Streets> {
    /**
     * 根据县id查街道
     * 用在添加线路前的获取
     *
     * @param areaId     the area id
     * @param appmodelId the appmodel id
     * @return streets by area id
     */
    List<StreetsResult> getStreetsByAreaId(String areaId, String appmodelId);

    /**
     * 批量更新街道
     *
     * @param appmodelId the appmodel id
     * @param streetsVo  the streets vo
     */
    void updateBatch(String appmodelId, List<StreetsVo> streetsVo);

    /**
     * 得到县区下街道数量
     *
     * @param value the value
     * @return int int
     */
    int countStreetsByAreaId(String value);

    /**
     * 根据app model获取所有街道
     *
     * @param appmodelId the appmodel id
     * @return the list
     */
    List<StreetsResult> findByAppmodelId(String appmodelId);

    /**
     * 根据街道id删除指定的街道
     * 并且删除街道下面的小区
     *
     * @param streetId   the street id
     * @param appmodelId the appmodel id
     */
    void deleteStreet(Long streetId, String appmodelId);

    /**
     * 根据街道id查询
     *
     * @param streetIdList the street id list
     * @return streets by id list
     */
    List<Streets> getStreetsByIdList(List<Long> streetIdList);
}
