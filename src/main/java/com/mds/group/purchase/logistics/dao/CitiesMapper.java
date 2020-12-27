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
import com.mds.group.purchase.logistics.model.Cities;
import com.mds.group.purchase.logistics.result.CityResult;
import com.mds.group.purchase.logistics.result.CityResultF;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * The interface Cities mapper.
 *
 * @author pavawi
 */
public interface CitiesMapper extends Mapper<Cities> {

    /**
     * Select cities by province id list.
     *
     * @param provinceId the province id
     * @return the list
     */
    List<CityResult> selectCitiesByProvinceId(Integer provinceId);

    /**
     * Select city result by province id list.
     *
     * @param provinceId the province id
     * @return the list
     */
    List<CityResultF> selectCityResultByProvinceId(Integer provinceId);

    /**
     * Select by city id cities.
     *
     * @param cityid the cityid
     * @return the cities
     */
    Cities selectByCityId(String cityid);


    /**
     * 根据cityId列表查询
     *
     * @param cityIdList the city id list
     * @return list list
     */
    List<Cities> selectByCityIdList(@Param("cityIdList") List<String> cityIdList);

    /**
     * Select id like name string.
     *
     * @param provinceId the province id
     * @param cityName   the city name
     * @return the string
     */
    String selectIdLikeName(@Param("provinceId") String provinceId, @Param("cityName") String cityName);
}