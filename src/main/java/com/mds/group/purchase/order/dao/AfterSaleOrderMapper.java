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
import com.mds.group.purchase.order.model.AfterSaleOrder;
import com.mds.group.purchase.order.result.AfterSaleOrderManageResult;
import com.mds.group.purchase.order.result.AfterSaleOrderResult;
import com.mds.group.purchase.order.vo.OrderVoV2;
import com.mds.group.purchase.order.vo.SearchOrderVoV2;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Set;

/**
 * The interface After sale order mapper.
 *
 * @author pavawi
 */
public interface AfterSaleOrderMapper extends Mapper<AfterSaleOrder> {

    /**
     * Find by group id list.
     *
     * @param groupId the group id
     * @param search  the search
     * @return the list
     */
    List<AfterSaleOrderResult> findByGroupId(@Param("groupId") String groupId, @Param("search") String search);

    /**
     * Count not end by user id integer.
     *
     * @param userIds the user ids
     * @return the integer
     */
    Integer countNotEndByUserId(@Param("userIds") Set<Long> userIds);

    /**
     * Find by original order ids list.
     *
     * @param orderIds the order ids
     * @return the list
     */
    List<AfterSaleOrder> findByOriginalOrderIds(@Param("orderIds") List<Long> orderIds);


    /**
     * Find by new order id after sale order.
     *
     * @param orderId the order id
     * @return the after sale order
     */
    AfterSaleOrder findByNewOrderId(@Param("orderId") Long orderId);

    /**
     * Find by app model id list.
     *
     * @param appmodelId      the appmodel id
     * @param searchOrderVoV2 the search order vo v 2
     * @return the list
     */
    List<AfterSaleOrder> findByAppModelId(@Param("appmodelId") String appmodelId, SearchOrderVoV2 searchOrderVoV2);

    /**
     * Select user after sale order list list.
     *
     * @param orderVoV2 the order vo v 2
     * @return the list
     */
    List<AfterSaleOrderResult> selectUserAfterSaleOrderList(OrderVoV2 orderVoV2);

    /**
     * 根据新生成的订单ID查询售后单
     *
     * @param orderIds the order ids
     * @return list list
     */
    List<AfterSaleOrder> findByNewOrderIds(@Param("orderIds") List<Long> orderIds);

    /**
     * Search after sale order list.
     *
     * @param startDate       the start date
     * @param endDate         the end date
     * @param appmodelId      the appmodel id
     * @param searchOrderVoV2 the search order vo v 2
     * @return the list
     */
    List<AfterSaleOrderManageResult> searchAfterSaleOrder(@Param("startDate") String startDate,
                                                          @Param("endDate") String endDate,
                                                          @Param("appmodelId") String appmodelId, @Param(
            "searchOrderVo") SearchOrderVoV2 searchOrderVoV2);

    /**
     * Find list by ids list.
     *
     * @param afterSaleOrderIds the after sale order ids
     * @return the list
     */
    List<AfterSaleOrder> findListByIds(@Param("afterSaleOrderIds") List<Long> afterSaleOrderIds);


    /**
     * Update sataus by order ids.
     *
     * @param orderIds the order ids
     * @param value    the value
     */
    void updateSatausByOrderIds(@Param("orderIds") List<Long> orderIds, @Param("status") int value);

    /**
     * Select user apply order by original order id after sale order.
     *
     * @param orderId the order id
     * @return the after sale order
     */
    AfterSaleOrder selectUserApplyOrderByOriginalOrderId(@Param("orderId") Long orderId);

    /**
     * Select leader apply order by original order id after sale order.
     *
     * @param orderId the order id
     * @return the after sale order
     */
    AfterSaleOrder selectLeaderApplyOrderByOriginalOrderId(@Param("orderId") Long orderId);

    /**
     * Select order by original order id after sale order.
     *
     * @param orderId the order id
     * @return the after sale order
     */
    AfterSaleOrder selectOrderByOriginalOrderId(@Param("orderId") Long orderId);

    /**
     * 根据订单ID查询不在此售后单中的包含未结束换货单的售后单
     *
     * @param originalOrderIds the original order ids
     * @param afterSaleOrderId the after sale order id
     * @return list list
     */
    List<AfterSaleOrder> findNotEndByOriginalOrderIdsAndIdNotIn(@Param("originalOrderIds") String originalOrderIds,
                                                                @Param("afterSaleOrderId") Long afterSaleOrderId);

    /**
     * 根据换货单ID查询原订单最新的用户申请的售后单
     *
     * @param orderId the order id
     * @return after sale order
     */
    AfterSaleOrder selectUserApplyOrderByExchangeOrderId(@Param("orderId") Long orderId);

    /**
     * 根据换货单ID查询原订单最新的团长申请的售后单
     *
     * @param orderId the order id
     * @return after sale order
     */
    AfterSaleOrder selectLeaderApplyOrderByExchangeOrderId(@Param("orderId") Long orderId);
}