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

package com.mds.group.purchase.order.service;

import com.mds.group.purchase.core.Service;
import com.mds.group.purchase.order.model.ReceiptBillDetail;
import com.mds.group.purchase.order.result.MyTeamMembersResult;
import com.mds.group.purchase.order.vo.ReceiptBillInfoVO;

import java.util.List;
import java.util.Set;


/**
 * The interface Receipt bill detail service.
 *
 * @author CodeGenerator
 * @date 2019 /01/25
 */
public interface ReceiptBillDetailService extends Service<ReceiptBillDetail> {

    /**
     * Gets info.
     *
     * @param receiptBillInfoVO the receipt bill info vo
     * @return the info
     */
    List<MyTeamMembersResult> getInfo(ReceiptBillInfoVO receiptBillInfoVO);

    /**
     * Select by bill ids list.
     *
     * @param collect the collect
     * @return the list
     */
    List<ReceiptBillDetail> selectByBillIds(Set<Long> collect);

    /**
     * Update group leader commission by order detail id.
     *
     * @param orderDetailId the order detail id
     */
    void updateGroupLeaderCommissionByOrderDetailId(Long orderDetailId);

    /**
     * Update group leader commission.
     */
    void updateGroupLeaderCommission();

    /**
     * Find by order id receipt bill detail.
     *
     * @param orderId the order id
     * @return the receipt bill detail
     */
    ReceiptBillDetail findByOrderId(Long orderId);
}
