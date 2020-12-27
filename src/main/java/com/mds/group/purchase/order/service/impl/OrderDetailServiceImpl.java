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

import cn.hutool.core.date.DateTime;
import com.mds.group.purchase.core.AbstractService;
import com.mds.group.purchase.order.dao.OrderDetailMapper;
import com.mds.group.purchase.order.model.OrderDetail;
import com.mds.group.purchase.order.service.OrderDetailService;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Condition;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;


/**
 * The type Order detail service.
 *
 * @author CodeGenerator
 * @date 2018 /12/01
 */
@Service
public class OrderDetailServiceImpl extends AbstractService<OrderDetail> implements OrderDetailService {

    @Resource
    private OrderDetailMapper tOrderDetailMapper;


    @Override
    public List<OrderDetail> findByActivityIdAndOrderPaySuccess(Long activityId) {
        return tOrderDetailMapper.selectByActivityIdAndOrderPaySuccess(activityId);
    }

    @Override
    public void updateSendTime(List<Long> list, DateTime date) {
        tOrderDetailMapper.updateSendTimeByOrderId(list, date);
    }

    @Override
    public List<OrderDetail> findByActivityIdAndOrderNotPay(Long actId) {
        return tOrderDetailMapper.selectByActivityIdAndOrderNotPay(actId);
    }

    @Override
    public List<OrderDetail> findByOrderIds(List<Long> orderIdList) {
        Condition condition = new Condition(OrderDetail.class);
        if (orderIdList == null || orderIdList.isEmpty()) {
            return new ArrayList<>();
        }
        condition.createCriteria().andIn("orderId", orderIdList);
        return tOrderDetailMapper.selectByCondition(condition);
    }

    @Override
    public List<OrderDetail> findByNoSendActivityGoods(Long activityId, int status) {
        return tOrderDetailMapper.selectByNoSendAndNoPayActivityGoods(activityId, status);
    }

    @Override
    public BigDecimal countCommissionByOrderId(Set<Long> collect) {
        if (collect.isEmpty()) {
            return BigDecimal.ZERO;
        }
        return tOrderDetailMapper.countCommissionByOrderId(collect);
    }

    @Override
    public List<OrderDetail> findWait4SignByGroupLeaderId(String wxuserId) {
        return tOrderDetailMapper.findWait4SignByGroupLeaderId(wxuserId);
    }

    @Override
    public BigDecimal countSettlementCommission(String groupLeaderId) {
        return tOrderDetailMapper.countSettlementCommission(groupLeaderId);
    }

}
