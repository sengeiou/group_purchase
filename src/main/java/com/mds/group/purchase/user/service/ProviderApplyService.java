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
import com.mds.group.purchase.user.model.ProviderApply;
import com.mds.group.purchase.user.vo.ProviderApplyRegisterVO;
import com.mds.group.purchase.user.vo.ProviderApplyVO;
import com.mds.group.purchase.user.vo.ProviderManagerVO;

import java.util.List;


/**
 * Created by CodeGenerator on 2018/11/27.
 *
 * @author pavawi
 */
public interface ProviderApplyService extends Service<ProviderApply> {

    /**
     * 供应商申请
     *
     * @param providerApplyVO the provider apply vo
     * @return int int
     */
    int providerApplyRegister(ProviderApplyRegisterVO providerApplyVO);

    /**
     * 申请表查询
     *
     * @param searchType the search type
     * @param appmodelId the appmodel id
     * @param provider   the provider
     * @param phone      the phone
     * @param providerId the provider id
     * @param pageNum    the page num
     * @param pageSize   the page size
     * @return the list
     */
    List<ProviderManagerVO> providerApplySearch(Integer searchType, String appmodelId, String provider, String phone,
                                                String providerId, Integer pageNum, Integer pageSize);

    /**
     * 删除申请表
     *
     * @param ids the ids
     * @return int int
     */
    int providerApplyDelete(String ids);

    /**
     * 同意申请或拒绝
     *
     * @param providerApplyVO the provider apply vo
     * @return int int
     */
    int providerApply(ProviderApplyVO providerApplyVO);


}
