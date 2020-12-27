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

package com.mds.group.purchase.order.service.impl;

import com.mds.group.purchase.core.AbstractService;
import com.mds.group.purchase.order.dao.OrderSendBillMappingMapper;
import com.mds.group.purchase.order.model.OrderSendBillMapping;
import com.mds.group.purchase.order.service.OrderSendBillMappingService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;


/**
 * 订单发货单关系映射业务实现类
 *
 * @author shuke
 * @date 2019 /02/18
 */
@Service
public class OrderSendBillMappingServiceImpl extends AbstractService<OrderSendBillMapping>
        implements OrderSendBillMappingService {

    @Resource
    private OrderSendBillMappingMapper tOrderSendBillMappingMapper;

    @Override
    public List<OrderSendBillMapping> findByAppmodelId(String appmodelId) {
        return tOrderSendBillMappingMapper.selectByAppmodelId(appmodelId);
    }

    @Override
    public OrderSendBillMapping findByOrderId(Long orderId) {
        return tOrderSendBillMappingMapper.selectByOrderId(orderId);
    }

    @Override
    public List<OrderSendBillMapping> findBySendBillId(Long sendBillId, String appmodelId) {
        return tOrderSendBillMappingMapper.selectBySendBillId(sendBillId, appmodelId);
    }

    @Override
    public List<Long> selectAllBySendBillId(Long sendBillId, String appmodelId) {
        return tOrderSendBillMappingMapper.selectAllBySendBillId(sendBillId, appmodelId).stream()
                .map(OrderSendBillMapping::getOrderId).collect(Collectors.toList());
    }


    @Override
    public void removeFromSendBill(String appmodelId, List<Long> orderIds) {
        tOrderSendBillMappingMapper.updateGenerateByOrderIds(appmodelId, orderIds, 0);
    }

    @Override
    public List<OrderSendBillMapping> findByOrderIds(String appmodelId, List<Long> orderIdList) {
        return tOrderSendBillMappingMapper.selectByOrderIds(appmodelId, orderIdList);
    }


    @Override
    public void updateGenerateAndSendBillId(Long sendBillId, List orderIds) {
        tOrderSendBillMappingMapper.updateGenerateAndSendBillId(sendBillId, orderIds);
    }
}
