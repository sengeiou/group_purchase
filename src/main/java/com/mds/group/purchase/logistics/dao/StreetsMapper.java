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
import com.mds.group.purchase.logistics.model.Streets;
import com.mds.group.purchase.logistics.result.StreetsResult;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * The interface Streets mapper.
 *
 * @author pavawi
 */
public interface StreetsMapper extends Mapper<Streets> {

    /**
     * Select streets by area id list.
     *
     * @param areaId     the area id
     * @param appmodelId the appmodel id
     * @return the list
     */
    List<StreetsResult> selectStreetsByAreaId(@Param("areaId") Integer areaId, @Param("appmodelId") String appmodelId);

    /**
     * Select by street id streets.
     *
     * @param streetId the street id
     * @return the streets
     */
    Streets selectByStreetId(String streetId);

    /**
     * Select count by area id int.
     *
     * @param value the value
     * @return the int
     */
    int selectCountByAreaId(String value);

    /**
     * 根据app model获取所有街道
     *
     * @param appmodelId the appmodel id
     * @return the list
     */
    List<StreetsResult> selectByappmodelId(String appmodelId);

    /**
     * Select by street id list list.
     *
     * @param streetIdList the street id list
     * @return the list
     */
    List<Streets> selectByStreetIdList(@Param("streetIdList") List<Long> streetIdList);
}