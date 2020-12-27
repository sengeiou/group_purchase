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

import cn.hutool.core.date.DateTime;
import com.mds.group.purchase.core.Mapper;
import com.mds.group.purchase.order.model.OrderDetail;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

/**
 * The interface Order detail mapper.
 *
 * @author pavawi
 */
public interface OrderDetailMapper extends Mapper<OrderDetail> {

    /**
     * Select by order id order detail.
     *
     * @param orderId the order id
     * @return the order detail
     */
    OrderDetail selectByOrderId(Long orderId);

    /**
     * 查询订单下的所有订单详情
     *
     * @param orderId the order id
     * @return list list
     */
    List<OrderDetail> selectOrderDetails(Long orderId);

    /**
     * 根据活动id统计参与活动人数
     *
     * @param activityId the activity id
     * @return integer integer
     */
    Integer selectCountByActIdUser(Long activityId);

    /**
     * 查询活动对应的订单先详情,并且订单是支付成功的订单
     * 需求修改无用
     *
     * @param activityId the activity id
     * @return list list
     */
    List<OrderDetail> selectByActivityIdAndOrderPaySuccess(@Param("activityId") Long activityId);

    /**
     * 根据订单id更新发货时间
     *
     * @param list the list
     * @param date the date
     */
    void updateSendTimeByOrderId(@Param("list") List<Long> list, @Param("date") DateTime date);

    /**
     * Select by activity id and order not pay list.
     *
     * @param actId the act id
     * @return the list
     */
    List<OrderDetail> selectByActivityIdAndOrderNotPay(@Param("actId") Long actId);

    /**
     * 查询指定用户参加的活动商品详情,支付成功才算有效订单
     *
     * @param wxuserId   the wxuser id
     * @param actGoodsId the act goods id
     * @return list list
     */
    List<OrderDetail> selectByUserIdAndActGoodsId(@Param("wxuserId") Long wxuserId,
                                                  @Param("actGoodsId") Long actGoodsId);

    /**
     * 查询未付款,和待发货的订单
     *
     * @param activityId the activity id
     * @param statu      the statu
     * @return list list
     */
    List<OrderDetail> selectByNoSendAndNoPayActivityGoods(@Param("activityId") Long activityId,
                                                          @Param("statu") int statu);

    /**
     * 根据订单ID统计团长佣金
     *
     * @param orderIds the order ids
     * @return big decimal
     */
    BigDecimal countCommissionByOrderId(@Param("orderIds") Set<Long> orderIds);

    /**
     * 根据团长ID查询等待签收的订单详情
     *
     * @param groupLeaderId the group leader id
     * @return list list
     */
    List<OrderDetail> findWait4SignByGroupLeaderId(@Param("groupLeaderId") String groupLeaderId);

    /**
     * 根据团长ID查询待签收的佣金
     *
     * @param groupLeaderId the group leader id
     * @return big decimal
     */
    BigDecimal countSettlementCommission(@Param("groupLeaderId") String groupLeaderId);

}