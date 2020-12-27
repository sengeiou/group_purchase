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
import com.mds.group.purchase.order.model.ReceiptBillDetail;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Set;

/**
 * The interface Receipt bill detail mapper.
 *
 * @author pavawi
 */
public interface ReceiptBillDetailMapper extends Mapper<ReceiptBillDetail> {
    /**
     * Select by bill ids list.
     *
     * @param billIds the bill ids
     * @return the list
     */
    List<ReceiptBillDetail> selectByBillIds(@Param("billIds") Set<Long> billIds);

    /**
     * Select by order detail id receipt bill detail.
     *
     * @param orderDetailId the order detail id
     * @return the receipt bill detail
     */
    ReceiptBillDetail selectByOrderDetailId(@Param("orderDetailId") Long orderDetailId);

    /**
     * Select by order id receipt bill detail.
     *
     * @param orderId the order id
     * @return the receipt bill detail
     */
    ReceiptBillDetail selectByOrderId(@Param("orderId") Long orderId);

    /**
     * Delete by bill ids.
     *
     * @param billIds the bill ids
     */
    void deleteByBillIds(@Param("billIds") List<Long> billIds);
}