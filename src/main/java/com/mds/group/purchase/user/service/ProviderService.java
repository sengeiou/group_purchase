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

package com.mds.group.purchase.user.service;

import com.mds.group.purchase.core.Service;
import com.mds.group.purchase.user.model.Provider;
import com.mds.group.purchase.user.vo.*;

import java.util.List;


/**
 * Created by CodeGenerator on 2018/11/27.
 *
 * @author pavawi
 */
public interface ProviderService extends Service<Provider> {

    /**
     * 查询供应商管理
     *
     * @param searchType    the search type
     * @param appmodelId    the appmodel id
     * @param providerName  the provider name
     * @param providerPhone the provider phone
     * @param providerId    the provider id
     * @param pageNum       the page num
     * @param pageSize      the page size
     * @return list list
     */
    List<ProviderManagerVO> providerApplySearch(Integer searchType, String appmodelId, String providerName,
                                                String providerPhone, String providerId,
                                                Integer pageNum, Integer pageSize);

    /**
     * 批量删除供货商
     *
     * @param ids the ids
     * @return list list
     */
    List<String> providerDelete(DeleteVO ids);

    /**
     * 禁用或开启
     *
     * @param providerStatuVO the provider statu vo
     * @return int int
     */
    int providerStatu(ProviderStatuVO providerStatuVO);

    /**
     * 供应商申请
     *
     * @param providerApplyRegisterVO the provider apply register vo
     * @return int int
     */
    int providerApplyRegister(ProviderApplyRegisterVO providerApplyRegisterVO);

    /**
     * 同意申请或拒绝
     *
     * @param providerApplyVO the provider apply vo
     * @return int int
     */
    int providerApply(ProviderApplyVO providerApplyVO);


    /**
     * Find by provider id list.
     *
     * @param providerIdList the provider id list
     * @return the list
     */
    List<Provider> findByProviderId(List<String> providerIdList);
}
