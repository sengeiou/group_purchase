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

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.mds.group.purchase.constant.ActiviMqQueueName;
import com.mds.group.purchase.order.service.AfterSaleOrderService;
import com.mds.group.purchase.order.service.OrderService;
import com.mds.group.purchase.order.vo.AfterSaleOrderUpdateVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 自动确认监听类
 *
 * @author shuke
 * @date 2019 -2-19
 */
@Component
public class AutoConfirmListener {

    @Resource
    private AfterSaleOrderService afterSaleOrderService;
    @Resource
    private OrderService orderService;

    private Logger logger = LoggerFactory.getLogger(AutoConfirmListener.class);

    /**
     * Merchant auto confirm.
     *
     * @param jsonData the json data
     */
    @JmsListener(destination = ActiviMqQueueName.MERCHANT_AUTO_CONFIRM)
    public void merchantAutoConfirm(String jsonData) {
        JSONObject jsonObject = JSON.parseObject(jsonData);
        try {
            AfterSaleOrderUpdateVO afterSaleOrderUpdateVO = new AfterSaleOrderUpdateVO();
            afterSaleOrderUpdateVO.setAfterSaleOrderId(jsonObject.getString("id"));
            afterSaleOrderService.sellerApprove(afterSaleOrderUpdateVO, true);
            logger.info("售后单：{}商家自动确认", jsonObject.getString("id"));
        } catch (Exception e) {
            logger.warn("售后单：{}商家自动确认失败", jsonObject.getString("id"));
        }

    }

    /**
     * User auto cancel.
     *
     * @param jsonData the json data
     */
    @JmsListener(destination = ActiviMqQueueName.USER_AUTO_CANCEL)
    public void userAutoCancel(String jsonData) {
        JSONObject jsonObject = JSON.parseObject(jsonData);
        try {
            afterSaleOrderService.cancel(jsonObject.getString("id"), true, 1);
            logger.info("售后单：{}用户自动取消", jsonObject.getString("id"));

        } catch (Exception e) {
            logger.warn("售后单：{}用户自动取消失败", jsonObject.getString("id"));
        }
    }

    /**
     * Leader auto confirm.
     *
     * @param jsonData the json data
     */
    @JmsListener(destination = ActiviMqQueueName.LEADER_AUTO_CONFIRM)
    public void LeaderAutoConfirm(String jsonData) {
        JSONObject jsonObject = JSON.parseObject(jsonData);
        try {
            afterSaleOrderService.leaderApprove(jsonObject.getString("id"), true);
            logger.info("售后单：{}团长自动确认", jsonObject.getString("id"));
        } catch (Exception e) {
            logger.warn("售后单：{}团长自动确认失败", jsonObject.getString("id"));
        }

    }

    /**
     * User auto confirm.
     *
     * @param jsonData the json data
     */
    @JmsListener(destination = ActiviMqQueueName.USER_AUTO_CONFIRM)
    public void UserAutoConfirm(String jsonData) {
        JSONObject jsonObject = JSON.parseObject(jsonData);
        try {
            orderService.confirmReceipt(jsonObject.getLong("id"), false);
            logger.info("订单：{}用户自动确认", jsonObject.getString("id"));
        } catch (Exception e) {
            logger.warn("订单：{}用户自动确认失败", jsonObject.getString("id"));
        }

    }


}
