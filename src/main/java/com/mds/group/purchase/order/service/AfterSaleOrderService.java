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
import com.mds.group.purchase.order.model.Order;
import com.mds.group.purchase.order.result.*;
import com.mds.group.purchase.order.vo.*;

import java.util.List;
import java.util.Set;


/**
 * The interface After sale order service.
 *
 * @author pavawi
 */
public interface AfterSaleOrderService extends Service<AfterSaleOrder> {


    /**
     * 用户申请售后
     *
     * @param applyAfterSaleOrderVO the apply after sale order vo
     */
    void userApply(ApplyAfterSaleOrderVO applyAfterSaleOrderVO);

    /**
     * 团长申请售后
     *
     * @param batchApplyAfterSaleOrderVO the batch apply after sale order vo
     */
    void leaderApply(BatchApplyAfterSaleOrderVO batchApplyAfterSaleOrderVO);

    /**
     * 取消售后申请
     *
     * @param applyAfterSaleOrderId the apply after sale order id
     * @param isSystem              the is system
     * @param type                  the type
     */
    void cancel(String applyAfterSaleOrderId, Boolean isSystem, int type);

    /**
     * 商家拒绝申请
     *
     * @param applyAfterSaleOrderVO the apply after sale order vo
     */
    void sellerRefusal(AfterSaleOrderUpdateVO applyAfterSaleOrderVO);

    /**
     * 商家同意申请
     *
     * @param applyAfterSaleOrderVO the apply after sale order vo
     * @param isSystem              the is system
     */
    void sellerApprove(AfterSaleOrderUpdateVO applyAfterSaleOrderVO, Boolean isSystem);

    /**
     * 用户确认退货
     *
     * @param applyAfterSaleOrderId the apply after sale order id
     */
    void userReturn(String applyAfterSaleOrderId);

    /**
     * 团长确认收货
     *
     * @param applyAfterSaleOrderId the apply after sale order id
     * @param isSystem              the is system
     */
    void leaderApprove(String applyAfterSaleOrderId, Boolean isSystem);

    /**
     * 团长拒绝收货
     *
     * @param applyAfterSaleOrderId the apply after sale order id
     */
    void leaderRefusal(String applyAfterSaleOrderId);

    /**
     * 根据用户ID统计用户未结束的售后
     *
     * @param collect the collect
     * @return the integer
     */
    Integer countNotEndByUserId(Set<Long> collect);

    /**
     * 查询团长工作台的售后单
     *
     * @param searchAfterSaleOrderVo the search after sale order vo
     * @return the list
     */
    List<AfterSaleOrderResult> searchAfterSaleOrder(SearchAfterSaleOrderVO searchAfterSaleOrderVo);

    /**
     * 团长工作台的客户类别
     *
     * @param groupLeaderId the group leader id
     * @param search        the search
     * @return the list
     */
    List<CustomerResult> customerList(Long groupLeaderId, String search);

    /**
     * 客户的售后详情
     *
     * @param wxuserId      the wxuser id
     * @param groupLeaderId the group leader id
     * @param type          the type
     * @return the list
     */
    List<OrderResult> customerOrderInfo(Long wxuserId, String groupLeaderId, Integer type);

    /**
     * 查询控制台的售后列表
     *
     * @param appmodelId      the appmodel id
     * @param searchOrderVoV2 the search order vo v 2
     * @return the list
     */
    List<AfterSaleOrderManageResult> searchManageAfterSaleOrder(String appmodelId, SearchOrderVoV2 searchOrderVoV2);

    /**
     * 获取售后申请此数
     *
     * @param orderId the order id
     * @return the after sale apply number result
     */
    AfterSaleApplyNumberResult applyNumber(Long orderId);

    /**
     * 获取用户个人售后订单列表
     *
     * @param orderVoV2 the order vo v 2
     * @return user after sale order list
     */
    List<AfterSaleOrderResult> getUserAfterSaleOrderList(OrderVoV2 orderVoV2);

    /**
     * 获取售后订单详情
     *
     * @param applyAfterSaleOrderId the apply after sale order id
     * @return after sale order detail result
     */
    AfterSaleOrderDetailResult findDetailById(String applyAfterSaleOrderId);

    /**
     * 根据ID获取售后单的团员
     *
     * @param applyAfterSaleOrderId the apply after sale order id
     * @return member list by id
     */
    List<MembersResult> getMemberListById(String applyAfterSaleOrderId);

    /**
     * 关闭售后单中的换货单及生成此换货单的售后单
     *
     * @param afterSaleOrder the after sale order
     */
    void closeAfterSaleOrderAndExChangeOrder(AfterSaleOrder afterSaleOrder);

    /**
     * Find order by original order list.
     *
     * @param order the order
     * @return the list
     */
    List<Order> findOrderByOriginalOrder(Order order);

}
