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
import com.mds.group.purchase.financial.model.GroupBrokerage;
import org.apache.ibatis.annotations.Param;

/**
 * The interface Group brokerage mapper.
 *
 * @author pavawi
 */
public interface GroupBrokerageMapper extends Mapper<GroupBrokerage> {
    /**
     * Select last group brokerage.
     *
     * @param appmodelId the appmodel id
     * @return the group brokerage
     */
    GroupBrokerage selectLast(@Param("appmodelId") String appmodelId);

    /**
     * 更新团长佣金明细的状态
     *
     * @param orderId the order id
     * @param status  the status
     * @param now     the now
     */
    void updateStatusByOrderId(
            @Param("orderId") Long orderId, @Param("status") Integer status, @Param("now") String now);
}