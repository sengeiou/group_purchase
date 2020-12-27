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
import com.mds.group.purchase.order.model.AfterSaleOrder;
import com.mds.group.purchase.order.model.NegotiateHistory;
import com.mds.group.purchase.order.model.Order;
import com.mds.group.purchase.order.vo.ApplyAfterSaleOrderVO;
import com.mds.group.purchase.order.vo.BaseApplyAfterSaleOrderVO;

import java.util.List;


/**
 * The interface Negotiate history service.
 *
 * @author pavawi
 */
public interface NegotiateHistoryService extends Service<NegotiateHistory> {

    /**
     * 初始化协商历史
     *
     * @param order                     the order
     * @param baseApplyAfterSaleOrderVO the base apply after sale order vo
     * @param user                      the user
     */
    void init(Order order, BaseApplyAfterSaleOrderVO baseApplyAfterSaleOrderVO, String user);

    /**
     * 商家拒绝
     *
     * @param order          the order
     * @param afterSaleOrder the after sale order
     */
    void sellerRefusal(Order order, AfterSaleOrder afterSaleOrder);

    /**
     * 商家同意
     *
     * @param order          the order
     * @param afterSaleOrder the after sale order
     * @param isSystem       the is system
     */
    void sellerApprove(Order order, AfterSaleOrder afterSaleOrder, Boolean isSystem);

    /**
     * 换货结束
     *
     * @param order          the order
     * @param afterSaleOrder the after sale order
     */
    void exchangeEnd(Order order, AfterSaleOrder afterSaleOrder);

    /**
     * 商家同意并且需要退货
     *
     * @param order          the order
     * @param afterSaleOrder the after sale order
     * @param isSystem       the is system
     */
    void sellerApproveNeedReturn(Order order, AfterSaleOrder afterSaleOrder, Boolean isSystem);

    //void close(Order order, String reason);

    /**
     * 退款
     *
     * @param afterSaleOrder the after sale order
     * @param order          the order
     * @param isSystem       the is system
     */
    void refund(AfterSaleOrder afterSaleOrder, Order order, Boolean isSystem);

    /**
     * 用户确认退货
     *
     * @param order the order
     */
    void userReturn(Order order);

    /**
     * 团长确认收到用户退货
     *
     * @param order    the order
     * @param isSystem the is system
     */
    void leaderApprove(Order order, Boolean isSystem);

    /**
     * 团长没有收到用户退货
     *
     * @param order          the order
     * @param afterSaleOrder the after sale order
     */
    void leaderRefusal(Order order, AfterSaleOrder afterSaleOrder);

    /**
     * 用户取消或超时未操作自动取消
     *
     * @param order          the order
     * @param afterSaleOrder the after sale order
     * @param isSystem       the is system
     * @param type           the type
     */
    void cancel(Order order, AfterSaleOrder afterSaleOrder, Boolean isSystem, int type);

    /**
     * 换货订单已发货
     *
     * @param order          the order
     * @param afterSaleOrder the after sale order
     */
    void exchangeSend(Order order, AfterSaleOrder afterSaleOrder);

    /**
     * 换货订单确认收货
     *
     * @param order          the order
     * @param afterSaleOrder the after sale order
     */
    void exchangeReceipt(Order order, AfterSaleOrder afterSaleOrder);

    /**
     * 根据订单ID查询协商历史
     *
     * @param orderId the order id
     * @param type    the type
     * @return list list
     */
    List<NegotiateHistory> findByOrderId(Long orderId, String type);

    /**
     * 拒绝收货
     *
     * @param order                 the order
     * @param applyAfterSaleOrderVO the apply after sale order vo
     */
    void refuseConfirm(Order order, ApplyAfterSaleOrderVO applyAfterSaleOrderVO);
}
