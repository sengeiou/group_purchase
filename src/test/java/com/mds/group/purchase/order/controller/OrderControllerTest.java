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

package com.mds.group.purchase.order.controller;

import com.mds.group.purchase.constant.OrderConstant;
import com.mds.group.purchase.order.model.Order;
import com.mds.group.purchase.order.result.OrderResult;
import com.mds.group.purchase.order.service.OrderService;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Date;

@Transactional
@Rollback
@SpringBootTest
@RunWith(SpringRunner.class)
public class OrderControllerTest {

    @Resource
    private OrderService orderService;
    @Resource
    private MockHttpServletResponse response;

    private Order order1 ;

    @Before
    public void setUp() throws Exception {
        String appmodelId = "S00050001wx435b58f761882566";
        Order order = new Order();
        order.setOrderId(12345678L);
        order.setGroupId("123456");
        order.setGroupLeaderPhone("13666666666");
        order.setGroupLeaderName("测试团长");
        order.setAppmodelId(appmodelId);
        order.setPayStatus(OrderConstant.WAIT4PAY);
        order.setWxuserId(123456789L);
        order.setOrderNo("191111223123121");
        order.setPayFee(new BigDecimal("0.5"));
        order.setCreateTime(new Date());
        order.setBuyerPhone("13333333333");
        order.setDeleteFlag(false);
        order1 = order;
        this.saveGroupOrder();
    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void list() {

    }

    @Test
    public void orderListByLine() {

    }

    @Test
    public void searchOrder() {

    }

    @Test
    public void searchOrderV2() {

    }

    @Test
    public void updateBatch() {
        //商家关闭订单
        Order orderUp = new Order();
        orderUp.setOrderId(order1.getOrderId());
        orderUp.setPayStatus(OrderConstant.ORDER_CLOSE_BY_SHOP);
        orderService.update(orderUp);
        OrderResult byId = orderService.getById(order1.getOrderId());
        assert byId!=null&&OrderConstant.ORDER_CLOSE_BY_SHOP==byId.getPayStatus();
    }

    @Test
    public void saveGroupOrder() {
        orderService.save(order1);
        Order byId = orderService.findById(order1.getOrderId());
        assert byId != null;
    }

    @Test
    public void saveSeckillOrder() {

    }

    @Test
    public void pay() {

    }

    @Test
    public void notifyV2() {

    }

    @Test
    public void userOrder() {

    }

    @Test
    public void saveComment() {

    }

    @Test
    public void cancelOrder() {

    }

    @Test
    public void cancelOrder1() {

    }

    @Test
    public void getUserComment() {

    }

    @Test
    public void confirmReceipt() {

    }

    @Test
    public void deleteOrder() {

    }

    @Test
    public void payokNotify() {

    }

    @Test
    public void myOrderSum() {

    }

    @Test
    public void getLine() {

    }

    @Test
    public void get3SendBill() {

    }

    @Test
    public void removeFromSendBill() {

    }

}