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
import com.mds.group.purchase.order.model.Order;
import com.mds.group.purchase.order.model.ReceiptBill;
import com.mds.group.purchase.order.model.ReceiptBillDetail;
import com.mds.group.purchase.order.result.ReceiptBillResult;
import com.mds.group.purchase.order.vo.SearchReceiptBillVO;

import javax.servlet.http.HttpServletResponse;
import java.util.List;


/**
 * 团长签收单
 *
 * @author CodeGenerator
 * @date 2018 /12/19
 */
public interface ReceiptBillService extends Service<ReceiptBill> {


    /**
     * 查询团长签收当
     *
     * @param appmodelId the appmodel id
     * @param sendBillId the send bill id
     * @param lineId     the line id
     * @return receipt bill
     */
    List<ReceiptBillResult> getReceiptBill(String appmodelId, Long sendBillId, Long lineId);

    /**
     * 团长签签收当导出
     *
     * @param appmodelId the appmodel id
     * @param pageNum    the page num
     * @param pageSize   the page size
     * @param lineId     the line id
     * @param sendBillId the send bill id
     * @param response   the response
     */
    void export(String appmodelId, Integer pageNum, Integer pageSize, Long lineId, Long sendBillId,
                HttpServletResponse response);

    /**
     * 按照团长ID和状态查询团长签收单
     *
     * @param searchReceiptBillVO the search receipt bill vo
     * @return list list
     */
    List<ReceiptBillResult> searchReceiptBill(SearchReceiptBillVO searchReceiptBillVO);

    /**
     * Receipt by bill id.
     *
     * @param billId the bill id
     */
    void receiptByBillId(Long billId);

    /**
     * Check receipt status.
     *
     * @param receiptBillDetail the receipt bill detail
     */
    void checkReceiptStatus(ReceiptBillDetail receiptBillDetail);

    /**
     * Receipt by order ids.
     *
     * @param receiptBillDetailId the receipt bill detail id
     * @param orderIds            the order ids
     */
    void receiptByOrderIds(String receiptBillDetailId, String orderIds);

    /**
     * Delete receipt by send bill id.
     *
     * @param sendBillId the send bill id
     * @param appmodelId the appmodel id
     */
    void deleteReceiptBySendBillId(Long sendBillId, String appmodelId);

    /**
     * Check after sale order status.
     *
     * @param order the order
     */
    void checkAfterSaleOrderStatus(Order order);
}
