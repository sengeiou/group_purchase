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

package com.mds.group.purchase.order.dao;

import com.mds.group.purchase.core.Mapper;
import com.mds.group.purchase.order.model.ReceiptBill;
import com.mds.group.purchase.order.result.ReceiptBillResult;
import com.mds.group.purchase.order.vo.SearchReceiptBillVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * The interface Receipt bill mapper.
 *
 * @author pavawi
 */
public interface ReceiptBillMapper extends Mapper<ReceiptBill> {


    /**
     * Select by receipt bill list.
     *
     * @param sendBillId the send bill id
     * @param lineId     the line id
     * @param appmodelId the appmodel id
     * @return the list
     */
    List<ReceiptBillResult> selectByReceiptBill(
            @Param("sendBillId") Long sendBillId, @Param("lineId") Long lineId, @Param("appmodelId") String appmodelId);

    /**
     * Select by group leader id list.
     *
     * @param searchReceiptBillVO the search receipt bill vo
     * @return the list
     */
    List<ReceiptBillResult> selectByGroupLeaderId(SearchReceiptBillVO searchReceiptBillVO);

    /**
     * Update status.
     *
     * @param billId     the bill id
     * @param status     the status
     * @param appmodelId the appmodel id
     */
    void updateStatus(@Param("billId") Long billId, @Param("status") Integer status,
                      @Param("appmodelId") String appmodelId);

    /**
     * Update status by send bill id.
     *
     * @param sendBillId the send bill id
     * @param status     the status
     * @param appmodelId the appmodel id
     */
    void updateStatusBySendBillId(@Param("sendBillId") Long sendBillId, @Param("status") Integer status, @Param(
            "appmodelId") String appmodelId);
}