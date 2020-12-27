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
import com.mds.group.purchase.logistics.model.Areas;
import com.mds.group.purchase.logistics.result.AreaResult;

import java.util.List;


/**
 * The interface Areas service.
 *
 * @author pavawi
 */
public interface AreasService extends Service<Areas> {

    /**
     * 根据城市ID获取区域
     *
     * @param cityId the city id
     * @return the areas by city id
     */
    List<AreaResult> getAreasByCityId(String cityId);

    /**
     * 统计城市ID下的区域数量
     *
     * @param value the value
     * @return the int
     */
    int countAreasByCityId(String value);

    /**
     * 根据areaid集合查询
     *
     * @param areaIdList the area id list
     * @return the areas by ids
     */
    List<Areas> getAreasByIds(List<String> areaIdList);

    /**
     * 根据线路名称查询areaid
     *
     * @param cityId the city id
     * @param areas  the areas
     * @return the string
     */
    String findIdLikeName(String cityId, String areas);
}
