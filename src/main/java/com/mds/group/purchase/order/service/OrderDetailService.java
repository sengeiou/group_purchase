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

import cn.hutool.core.date.DateTime;
import com.mds.group.purchase.core.Service;
import com.mds.group.purchase.order.model.OrderDetail;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;


/**
 * The interface Order detail service.
 *
 * @author shuke
 * @date 2018 /12/01
 */
public interface OrderDetailService extends Service<OrderDetail> {

    /**
     * 查询活动对应的订单嫌弃,并且订单支付成功的订单
     *
     * @param activityId the activity id
     * @return list list
     */
    @Deprecated
    List<OrderDetail> findByActivityIdAndOrderPaySuccess(Long activityId);

    /**
     * 更新发货时间
     *
     * @param list the list
     * @param date the date
     */
    void updateSendTime(List<Long> list, DateTime date);

    /**
     * 查询活动中未支付的订单
     *
     * @param actId the act id
     * @return the list
     */
    List<OrderDetail> findByActivityIdAndOrderNotPay(Long actId);

    /**
     * 查询指定订单的订单详情
     *
     * @param orderIdList the order id list
     * @return list list
     */
    List<OrderDetail> findByOrderIds(List<Long> orderIdList);

    /**
     * 查询未付款,和待发货的订单
     *
     * @param activityId the activity id
     * @param statu      the statu
     * @return list list
     */
    List<OrderDetail> findByNoSendActivityGoods(Long activityId, int statu);

    /**
     * 根据订单ID统计佣金
     *
     * @param collect the collect
     * @return the big decimal
     */
    BigDecimal countCommissionByOrderId(Set<Long> collect);

    /**
     * 查询团长等待签收的订单
     *
     * @param wxuserId the wxuser id
     * @return the list
     */
    List<OrderDetail> findWait4SignByGroupLeaderId(String wxuserId);

    /**
     * 根据团长ID查询待签收的佣金
     *
     * @param groupLeaderId the group leader id
     * @return big decimal
     */
    BigDecimal countSettlementCommission(String groupLeaderId);
}
