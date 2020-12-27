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
import com.mds.group.purchase.logistics.model.Cities;
import com.mds.group.purchase.logistics.result.CityResult;
import com.mds.group.purchase.logistics.result.CityResultHaveGroup;

import java.util.List;
import java.util.Map;


/**
 * The interface Cities service.
 *
 * @author pavawi
 */
public interface CitiesService extends Service<Cities> {

    /**
     * 根据省id得到城市列表
     *
     * @param provinceId the province id
     * @return cities by province id
     */
    List<CityResult> getCitiesByProvinceId(String provinceId);

    /**
     * 根据省id得到城市计数
     *
     * @param provinceid the provinceid
     * @return int int
     */
    int countCitiesByProvinceId(String provinceid);

    /**
     * 指定城市
     *
     * @param cityIdsList the city ids list
     * @return list list
     */
    List<Cities> findByCityIds(List<String> cityIdsList);

    /**
     * 获取含有团长的的城市
     *
     * @param appmodelId the appmodel id
     * @return cities have group
     */
    Map<String, List<CityResultHaveGroup>> getCitiesHaveGroup(String appmodelId);

    /**
     * 根据城市idList
     *
     * @param cityIdList the city id list
     * @return cities by city id list
     */
    List<Cities> getCitiesByCityIdList(List<String> cityIdList);

    /**
     * Find id like name string.
     *
     * @param provinceId the province id
     * @param cityName   the city name
     * @return the string
     */
    String findIdLikeName(String provinceId, String cityName);
}
