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
import com.mds.group.purchase.order.model.OrderSendBillMapping;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 订单发货单关系映射DAO
 *
 * @author shuke
 * @date 2019 -2-18
 */
public interface OrderSendBillMappingMapper extends Mapper<OrderSendBillMapping> {

    /**
     * 根据appmodelId查询
     * generate状态为未生成
     * del_flag状态为正常
     *
     * @param appmodelId the appmodel id
     * @return list list
     */
    List<OrderSendBillMapping> selectByAppmodelId(@Param("appmodelId") String appmodelId);

    /**
     * 更新生成状态
     * 更新发货单id
     * 条件：生成状态 generate为0
     *
     * @param sendBillId the send bill id
     * @param orderIds   the order ids
     */
    void updateGenerateAndSendBillId(@Param("sendBillId") Long sendBillId, @Param("orderIds") List orderIds);

    /**
     * 根据orderId查询
     *
     * @param orderId the order id
     * @return order send bill mapping
     */
    OrderSendBillMapping selectByOrderId(@Param("orderId") Long orderId);

    /**
     * 根据发货单id和appmodelId查询订单发货单映射集合
     * 且del_flag = 0
     *
     * @param sendBillId 发货单id
     * @param appmodelId 小程序模板id
     * @return the list
     */
    List<OrderSendBillMapping> selectBySendBillId(@Param("sendBillId") Long sendBillId,
                                                  @Param("appmodelId") String appmodelId);

    /**
     * 根据发货单id和appmodelId查询订单发货单映射集合(包括已经生成过的)
     * 且del_flag = 0
     *
     * @param sendBillId 发货单id
     * @param appmodelId 小程序模板id
     * @return the list
     */
    List<OrderSendBillMapping> selectAllBySendBillId(@Param("sendBillId") Long sendBillId,
                                                     @Param("appmodelId") String appmodelId);

    /**
     * Select by generate list.
     *
     * @param appmodelId     the appmodel id
     * @param generateStatus the generate status
     * @return list list
     */
    List<OrderSendBillMapping> selectByGenerate(@Param("appmodelId") String appmodelId,
                                                @Param("generateStatus") int generateStatus);

    /**
     * 根据订单id修改生成状态为未生成
     *
     * @param appmodelId the appmodel id
     * @param orderIds   the order ids
     * @param generate   the generate
     */
    void updateGenerateByOrderIds(@Param("appmodelId") String appmodelId, @Param("orderIds") List<Long> orderIds,
                                  @Param("generate") Integer generate);

    /**
     * 根据订单id查询
     * 且generate状态为1
     *
     * @param appmodelId  the appmodel id
     * @param orderIdList the order id list
     * @return list list
     */
    List<OrderSendBillMapping> selectByOrderIds(@Param("appmodelId") String appmodelId,
                                                @Param("orderIdList") List<Long> orderIdList);

    /**
     * 查询指定发货单已生成的订单的订单id
     *
     * @param sendBillId the send bill id
     * @return list list
     */
    List<Long> selectByCreatedSendBillOrder(Long sendBillId);

    /**
     * Delete by order ids.
     *
     * @param orderIds the order ids
     */
    void deleteByOrderIds(@Param("orderIds") String orderIds);
}