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

import com.mds.group.purchase.configurer.WxServiceUtils;
import com.mds.group.purchase.core.AbstractService;
import com.mds.group.purchase.order.model.Order;
import com.mds.group.purchase.order.model.RefundBill;
import com.mds.group.purchase.order.service.RefundBillService;
import com.mds.group.purchase.utils.IdGenerateUtils;
import com.mds.group.purchase.utils.OrderUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;

/**
 * The type Refund bill service.
 *
 * @author pavawi
 */
@Service
public class RefundBillServiceImpl extends AbstractService<RefundBill> implements RefundBillService {


    @Autowired
    private OrderUtil orderUtil;
    @Autowired
    private WxServiceUtils wxServiceUtils;

    @Override
    public void create(Order order, BigDecimal refundFee) {

        RefundBill refundBill = new RefundBill();
        refundBill.setFee(refundFee);
        refundBill.setCreateTime(new Date());
        refundBill.setOrderId(order.getOrderId());
        refundBill.setSuccess(false);
        refundBill.setPayRefundId(IdGenerateUtils.getOrderNum());
        this.save(refundBill);
        //发送退款消息
        orderUtil.sendRefundMsg(order);
        Boolean res = wxServiceUtils.wechatRefund(order.getPayOrderId(), refundBill.getPayRefundId(),
                order.getPayFee().toString(), refundBill.getFee().toString(), order.getAppmodelId());
        if (res) {
            refundBill.setSuccess(true);
            this.update(refundBill);
        }


    }
}

