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

package com.mds.group.purchase.financial.dao;

import com.mds.group.purchase.core.Mapper;
import com.mds.group.purchase.financial.model.FinancialDetails;
import org.apache.ibatis.annotations.Param;

/**
 * The interface Financial details mapper.
 *
 * @author pavawi
 */
public interface FinancialDetailsMapper extends Mapper<FinancialDetails> {
    /**
     * Select last financial details.
     *
     * @param appmodelId the appmodel id
     * @return the financial details
     */
    FinancialDetails selectLast(@Param("appmodelId") String appmodelId);
}