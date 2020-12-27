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
import com.mds.group.purchase.order.model.OrderSendBillMapping;

import java.util.List;


/**
 * 订单发货单关系映射业务接口类
 *
 * @author shuke
 * @date 2019 /02/18
 */
public interface OrderSendBillMappingService extends Service<OrderSendBillMapping> {

    /**
     * 根据appmodelId获取状态为为生成发货单的记录
     *
     * @param appmodelId the appmodel id
     * @return list list
     */
    List<OrderSendBillMapping> findByAppmodelId(String appmodelId);

    /**
     * 更新生成状态
     * 更新发货单id
     * 条件：生成状态 generate为0
     *
     * @param sendBillId the send bill id
     * @param orderIds   the order ids
     */
    void updateGenerateAndSendBillId(Long sendBillId, List<Long> orderIds);

    /**
     * 根据orderid获取映射对象
     *
     * @param orderId the order id
     * @return order send bill mapping
     */
    OrderSendBillMapping findByOrderId(Long orderId);

    /**
     * 根据发货单id和appmodelId获取订单发货单映射集合
     *
     * @param sendBillId 发货单id
     * @param appmodelId 小程序模板id
     * @return 订单发货单映射对象集合 list
     */
    List<OrderSendBillMapping> findBySendBillId(Long sendBillId, String appmodelId);

    /**
     * 根据发货单id和appmodelId获取订单ID
     *
     * @param sendBillId 发货单id
     * @param appmodelId 小程序模板id
     * @return 订单发货单映射对象集合 list
     */
    List<Long> selectAllBySendBillId(Long sendBillId, String appmodelId);


    /**
     * 将订单移出发货单
     *
     * @param appmodelId the appmodel id
     * @param orderId    the order id
     */
    void removeFromSendBill(String appmodelId, List<Long> orderId);

    /**
     * 根据orderids获取映射对象
     *
     * @param appmodelId  the appmodel id
     * @param orderIdList the order id list
     * @return list list
     */
    List<OrderSendBillMapping> findByOrderIds(String appmodelId, List<Long> orderIdList);

}
