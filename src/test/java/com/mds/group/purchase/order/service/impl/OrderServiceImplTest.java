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

import com.github.binarywang.wxpay.bean.notify.WxPayOrderNotifyResult;
import com.mds.group.purchase.GroupPurchaseApplicationTests;
import com.mds.group.purchase.constant.Version;
import com.mds.group.purchase.order.model.Order;
import com.mds.group.purchase.order.service.SendBillService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;


public class OrderServiceImplTest extends GroupPurchaseApplicationTests {
    @Autowired
    private OrderServiceImpl orderService;

    @Autowired
    private SendBillService sendBillService;


    @Test
    public void notifyV2() {
        List<Order> orderList = orderService.findByWaitPay();
        for (Order order : orderList) {
            WxPayOrderNotifyResult wxPayOrderNotifyResult = new WxPayOrderNotifyResult();
            wxPayOrderNotifyResult.setResultCode("SUCCESS");
            //PayOrderId
            wxPayOrderNotifyResult.setOutTradeNo(order.getPayOrderId());
            orderService.notify(wxPayOrderNotifyResult, Version.V_1_2);
        }

    }

    @Test
    public void generateSendBill() {
        sendBillService.generateSendBill("S00050001wx17c66eb4da0ef6ab");
    }

}