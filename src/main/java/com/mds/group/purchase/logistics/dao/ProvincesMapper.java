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
import com.mds.group.purchase.logistics.model.Provinces;
import com.mds.group.purchase.logistics.result.PCAResult;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * The interface Provinces mapper.
 *
 * @author pavawi
 */
public interface ProvincesMapper extends Mapper<Provinces> {

    /**
     * Select by provinceid provinces.
     *
     * @param key the key
     * @return the provinces
     */
    Provinces selectByProvinceid(String key);

    /**
     * Gets all.
     *
     * @return the all
     */
    List<PCAResult> getAll();

    /**
     * 根据id集合查询
     *
     * @param provinceIdList the province id list
     * @return list list
     */
    List<Provinces> selectByProvinceids(@Param("provinceIdList") List<String> provinceIdList);

    /**
     * Select id like name string.
     *
     * @param province the province
     * @return the string
     */
    String selectIdLikeName(@Param("province") String province);
}