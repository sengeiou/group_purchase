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

package com.mds.group.purchase.listener;

import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.mds.group.purchase.configurer.GroupMallProperties;
import com.mds.group.purchase.constant.*;
import com.mds.group.purchase.order.model.Order;
import com.mds.group.purchase.order.model.OrderSendBillMapping;
import com.mds.group.purchase.order.model.SendBill;
import com.mds.group.purchase.order.result.OrderResult;
import com.mds.group.purchase.order.service.OrderSendBillMappingService;
import com.mds.group.purchase.order.service.OrderService;
import com.mds.group.purchase.order.service.SendBillService;
import com.mds.group.purchase.order.vo.GoodsSortingOrderViewVO;
import com.mds.group.purchase.order.vo.LineSortingOrderViewVo;
import com.mds.group.purchase.utils.GoodsSortingOrderUtil;
import com.mds.group.purchase.utils.LineSortingOrderUtil;
import com.mds.group.purchase.utils.ReceiptBillUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 发货单队列监听类
 *
 * @author shuke
 * @date 2019 -2-19
 */
@Component
public class SendBillListener {

    @Resource
    private OrderService orderService;
    @Resource
    private RedisTemplate redisTemplate;
    @Resource
    private SendBillService sendBillService;
    @Resource
    private ReceiptBillUtil receiptBillUtil;
    @Resource
    private LineSortingOrderUtil lineSortingOrderUtil;
    @Resource
    private GoodsSortingOrderUtil goodsSortingOrderUtil;
    @Resource
    private OrderSendBillMappingService orderSendBillMappingService;

    private Logger logger = LoggerFactory.getLogger(SendBillListener.class);

    /**
     * Generate send bill.
     *
     * @param appmodelId the appmodel id
     */
    @JmsListener(destination = ActiviMqQueueName.GENERATE_SENDBILL)
    public void generateSendBill(String appmodelId) {
        logger.info(DateUtil.today() + "开始生成appmodelId为：" + appmodelId + "的发货单");
        sendBillService.generateSendBill(appmodelId);
        logger.info(DateUtil.today() + " appmodelId为：" + appmodelId + "的发货单生成成功");
    }

    /**
     * Update send bill status.
     *
     * @param orderId the order id
     */
    @JmsListener(destination = ActiviMqQueueName.SENDBILL_SUCCESS)
    public void updateSendBillStatus(String orderId) {
        //查询订单发货单映射获取发货单
        OrderSendBillMapping byOrderId = orderSendBillMappingService.findByOrderId(Long.valueOf(orderId));
        if (byOrderId == null) {
            logger.error("订单号" + orderId + "该订单没有发货单");
            return;
        }
        //查询该发货单所有下属订单是否都已经完成，是则改变发货单状态为已完成，否则不改变
        List<OrderSendBillMapping> bySendBillId = orderSendBillMappingService
                .findBySendBillId(byOrderId.getSendBillId(), byOrderId.getAppmodelId());
        List<Long> orderIds = bySendBillId.stream().map(OrderSendBillMapping::getOrderId).collect(Collectors.toList());
        if (orderIds.isEmpty()) {
            return;
        }
        List<OrderResult> orderResultByOrderIds = orderService.findOrderResultByOrderIds(orderIds);
        //用户全部签收的订单才算是已完成
        List<OrderResult> collect =
                orderResultByOrderIds.stream().filter(order ->!OrderConstant.isEnd(order.getPayStatus()))
                        .collect(Collectors.toList());


        if (collect.isEmpty()) {
            //已经没有待收货订单，改变发货单状态为已完成
            SendBill sendBill = new SendBill();
            sendBill.setSendBillId(byOrderId.getSendBillId());
            sendBill.setStatus(SendBillConstant.SUCCESS);
            sendBillService.update(sendBill);
        }

        List<OrderResult> notCloseOrder =
                orderResultByOrderIds.stream().filter(order -> OrderConstant.isClose(order.getPayStatus()))
                        .collect(Collectors.toList());
        if (notCloseOrder.size() == orderResultByOrderIds.size()) {
            //已经没有未关闭的订单，改变发货单状态为已关闭
            SendBill sendBill = new SendBill();
            sendBill.setSendBillId(byOrderId.getSendBillId());
            sendBill.setStatus(SendBillConstant.CLOSE);
            sendBillService.update(sendBill);
        }
    }

    /**
     * 线路分拣单生成
     *
     * @param jsonData the json data
     */
    @JmsListener(destination = ActiviMqQueueName.LINE_SORTING_ORDER_CACHE)
    public void lineSortingOrderCache(String jsonData) {
        JSONObject jsonObject = JSON.parseObject(jsonData);
        Long sendBillId = jsonObject.getLong("id");
        String appmodelId = jsonObject.getString("appmodelId");
        List<LineSortingOrderViewVo> lineSortingOrderViewVos = lineSortingOrderUtil
                .generateLineSortOrder(sendBillId, appmodelId);
        if (lineSortingOrderViewVos != null && !lineSortingOrderViewVos.isEmpty()) {
            redisTemplate.opsForValue()
                    .set(GroupMallProperties.getRedisPrefix() + appmodelId + RedisPrefix.LINE_SORTING_ORDER_CACHE + sendBillId,
                            lineSortingOrderViewVos, 1, TimeUnit.HOURS);
        }
    }


    /**
     * 商品分拣单生成
     *
     * @param jsonData the json data
     */
    @JmsListener(destination = ActiviMqQueueName.GOODS_SORTING_ORDER_CACHE)
    public void goodsSortingOrderCache(String jsonData) {
        JSONObject jsonObject = JSON.parseObject(jsonData);
        Long sendBillId = jsonObject.getLong("id");
        String appmodelId = jsonObject.getString("appmodelId");
        List<GoodsSortingOrderViewVO> goodsSortingOrderViewVOS = goodsSortingOrderUtil
                .generateGoodsSortOrder(sendBillId, appmodelId);
        if (goodsSortingOrderViewVOS != null && !goodsSortingOrderViewVOS.isEmpty()) {
            redisTemplate.opsForValue()
                    .set(GroupMallProperties.getRedisPrefix() + appmodelId + RedisPrefix.GOODS_SORTING_ORDER_CACHE + sendBillId,
                            goodsSortingOrderViewVOS, 1, TimeUnit.HOURS);
        }
    }


    /**
     * 团长签收单生成
     *
     * @param jsonData the json data
     */
    @JmsListener(destination = ActiviMqQueueName.RECEIPT_BILL_CACHE)
    public void receiptBillCache(String jsonData) {
        JSONObject jsonObject = JSON.parseObject(jsonData);
        Long sendBillId = jsonObject.getLong("id");
        String appmodelId = jsonObject.getString("appmodelId");
        receiptBillUtil.generateReceiptBill(sendBillId, appmodelId);
    }

    /**
     * 支付成功后会异步生成对应该订单的订单发货单关系映射数据
     *
     * @param orderJsonData the order json data
     */
    @JmsListener(destination = ActiviMqQueueName.GENERATE_ORDER_SENDBILL_MAPPING)
    public void generateOrderSendbillMapping(String orderJsonData) {
        logger.info("/////////////////////////" + orderJsonData);
        JSONObject jsonObject = JSON.parseObject(orderJsonData);
        Order order = jsonObject.toJavaObject(Order.class);
        OrderSendBillMapping osbm = orderSendBillMappingService.findByOrderId(order.getOrderId());
        if (osbm == null) {
            osbm = new OrderSendBillMapping();
            osbm.setAppmodelId(order.getAppmodelId());
            osbm.setDelFlag(Common.DEL_FLAG_FALSE);
            osbm.setGenerate(SendBillConstant.GENERATE_FALSE);
            osbm.setOrderId(order.getOrderId());
            orderSendBillMappingService.save(osbm);
        }
    }

}
