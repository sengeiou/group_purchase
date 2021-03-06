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
import com.mds.group.purchase.logistics.model.Areas;
import com.mds.group.purchase.logistics.result.AreaResult;
import com.mds.group.purchase.logistics.result.AreaResultF;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * The interface Areas mapper.
 *
 * @author pavawi
 */
public interface AreasMapper extends Mapper<Areas> {

    /**
     * Select areas by city id list.
     *
     * @param cityId the city id
     * @return the list
     */
    List<AreaResult> selectAreasByCityId(Integer cityId);

    /**
     * Select by areaid areas.
     *
     * @param areaid the areaid
     * @return the areas
     */
    Areas selectByAreaid(String areaid);

    /**
     * Select area result f by city id list.
     *
     * @param cityId the city id
     * @return the list
     */
    List<AreaResultF> selectAreaResultFByCityId(Integer cityId);

    /**
     * Select by areaid list list.
     *
     * @param areaIdList the area id list
     * @return list list
     */
    List<Areas> selectByAreaidList(@Param("areaIdList") List<String> areaIdList);

    /**
     * Select id like name string.
     *
     * @param cityId the city id
     * @param areas  the areas
     * @return the string
     */
    String selectIdLikeName(@Param("cityId") String cityId, @Param("areas") String areas);
}