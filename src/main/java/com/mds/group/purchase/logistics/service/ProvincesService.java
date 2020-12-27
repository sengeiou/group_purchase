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
import com.mds.group.purchase.logistics.model.Provinces;
import com.mds.group.purchase.logistics.result.PCAResult;

import java.util.List;


/**
 * The interface Provinces service.
 *
 * @author pavawi
 */
public interface ProvincesService extends Service<Provinces> {

    /**
     * Gets all.
     *
     * @param appmodelId the appmodel id
     * @return the all
     */
    List<PCAResult> getAll(String appmodelId);

    /**
     * 获取包含小区的省份信息
     *
     * @param appmodelId the appmodel id
     * @return list list
     */
    List<PCAResult> haveCommunities(String appmodelId);

    /**
     * Find id like name string.
     *
     * @param province the province
     * @return the string
     */
    String findIdLikeName(String province);
}
