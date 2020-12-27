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

package com.mds.group.purchase.constant;

/**
 * 订单常量
 *
 * @author shuke
 * @date 2018 -12-10
 */
public interface OrderConstant {

    /**
     * 订单状态 0.等待买家付款 1.买家已付款 2.卖家已发货  3.待评价4.交易成功 5用户超时关闭订单 6.用户主动取消订单,7.商家关闭,8.团长已签收,9.换货成功,10.退款成功,11.换货关闭
     */
    int WAIT4PAY = 0;

    /**
     * The Constant PAYED.
     */
    int PAYED = 1;

    /**
     * The Constant SENDED.
     */
    int SENDED = 2;

    /**
     * The Constant NOT_COMMENT.
     */
    int NOT_COMMENT = 3;

    /**
     * The Constant ORDER_COMPLETE.
     */
    int ORDER_COMPLETE = 4;

    /**
     * The Constant ORDER_CLOSE.
     */
    int ORDER_CLOSE = 5;

    /**
     * The Constant ORDER_CLOSE_BY_USER.
     */
    int ORDER_CLOSE_BY_USER = 6;

    /**
     * The Constant ORDER_CLOSE_BY_SHOP.
     */
    int ORDER_CLOSE_BY_SHOP = 7;

    /**
     * The Constant GROUP_LEADER_RECEIPT.
     */
    int GROUP_LEADER_RECEIPT = 8;

    /**
     * The Constant ORDER_SUCCESS_BY_EXCHANGE.
     */
    int ORDER_SUCCESS_BY_EXCHANGE = 9;

    /**
     * The Constant ORDER_CLOSE_BY_REFUND.
     */
    int ORDER_CLOSE_BY_REFUND = 10;

    /**
     * The Constant ORDER_CLOSE_BY_EXCHANGE.
     */
    int ORDER_CLOSE_BY_EXCHANGE = 11;


    /**
     * 最小付款额
     */
    double MIN_PAYFEE = 0.01;

    /**
     * 换货
     */
    int EXCHANGE_ORDER = 11;

    /**
     * The Constant GROUP_ORDER.
     */
    int GROUP_ORDER = 2;

    /**
     * The Constant SECKILL_ORDER.
     */
    int SECKILL_ORDER = 1;

    /**
     * The Constant SOLITAIRE_ORDER.
     */
    int SOLITAIRE_ORDER = 4;

    /**
     * 支付方式
     */
    int WX_PAY = 1;

    /**
     * Is close boolean.
     *
     * @param status the status
     * @return the boolean
     */
    static boolean isClose(Integer status) {
        return status == ORDER_CLOSE || status == ORDER_CLOSE_BY_SHOP || status == ORDER_CLOSE_BY_USER || status == ORDER_SUCCESS_BY_EXCHANGE || status == ORDER_CLOSE_BY_REFUND || status == ORDER_CLOSE_BY_EXCHANGE;
    }

    /**
     * Is end boolean.
     *
     * @param status the status
     * @return the boolean
     */
    static boolean isEnd(Integer status) {
        return status == NOT_COMMENT || status == ORDER_COMPLETE || isClose(status);
    }

    /**
     * Is receipt boolean.
     *
     * @param status the status
     * @return the boolean
     */
    static boolean isReceipt(Integer status) {
        return status == OrderConstant.NOT_COMMENT || status == OrderConstant.ORDER_COMPLETE || status == OrderConstant.GROUP_LEADER_RECEIPT || status == OrderConstant.ORDER_SUCCESS_BY_EXCHANGE || status == OrderConstant.ORDER_CLOSE_BY_REFUND || status == OrderConstant.ORDER_CLOSE_BY_EXCHANGE;
    }


}
