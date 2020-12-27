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

package com.mds.group.purchase.shop.service;

import com.mds.group.purchase.core.Service;
import com.mds.group.purchase.shop.model.Manager;


/**
 * Created by CodeGenerator on 2018/11/27.
 *
 * @author pavawi
 */
public interface ManagerService extends Service<Manager> {

    /**
     * Update enterprise pay state.
     *
     * @param appmodelId         the appmodel id
     * @param enterprisePayState the enterprise pay state
     */
    void updateEnterprisePayState(String appmodelId, Boolean enterprisePayState);

    /**
     * 更新迷药
     *
     * @param appid           the appid
     * @param certificatePath the certificate path
     * @param mchId           the mch id
     * @param mchKey          the mch key
     * @return the integer
     */
    Integer updateSecretKey(String appid, String certificatePath, String mchId, String mchKey);

    /**
     * 根据appmodelId查询
     *
     * @param appmodelId the appmodel id
     * @return the manager
     */
    Manager findByAppmodelId(String appmodelId);
}
