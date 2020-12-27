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

package com.mds.group.purchase.user.dao;

import com.mds.group.purchase.core.Mapper;
import com.mds.group.purchase.user.model.Provider;
import com.mds.group.purchase.user.vo.ProviderManagerVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * The interface Provider mapper.
 *
 * @author pavawi
 */
public interface ProviderMapper extends Mapper<Provider> {

    /**
     * 搜索供应商管理
     *
     * @param map the map
     * @return list list
     */
    List<ProviderManagerVO> selectProviderApplySearch(Map<String, Object> map);

    /**
     * 逻辑删除供货商
     *
     * @param ids the ids
     * @return int int
     */
    int deleteProviderDelete(@Param("ids") List<String> ids);

    /**
     * 根据id获取供应商名称
     *
     * @param providerId the provider id
     * @return name name
     */
    String getName(Long providerId);

    /**
     * Select by provider ids list.
     *
     * @param providerIdList the provider id list
     * @return the list
     */
    List<Provider> selectByProviderIds(List<String> providerIdList);
}