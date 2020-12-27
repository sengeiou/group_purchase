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
import com.mds.group.purchase.user.model.ProviderApply;

import java.util.List;
import java.util.Map;

/**
 * The interface Provider apply mapper.
 *
 * @author pavawi
 */
public interface ProviderApplyMapper extends Mapper<ProviderApply> {

    /**
     * 查询申请
     *
     * @param map the map
     * @return list list
     */
    List<ProviderApply> selectProviderApplySearch(Map<String, Object> map);

    /**
     * 批量删除申请
     *
     * @param idsList the ids list
     * @return int int
     */
    int deleteIds(List<String> idsList);
}