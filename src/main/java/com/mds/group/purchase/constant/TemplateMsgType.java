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
 * The interface Template msg type.
 *
 * @author pavawi
 */
public interface TemplateMsgType {

    /**
     * 客户预约通知
     */
    Integer SUBSCRIBE = 1;

    /**
     * 订单关闭通知
     */
    Integer ORDERCLOSE = 2;

    /**
     * 团长申请审核结果通知
     */
    Integer GROUPAPPLY = 101;

    /**
     * 待付款通知
     */
    Integer WAITPAY = 3;

    /**
     * 退款成功通知
     */
    Integer REFUNDSUCCESS = 4;

    /**
     * 订单发货提醒
     */
    Integer ORDERSEND = 5;

    /**
     * 供应商申请审核结果通知
     */
    Integer PROVIDERAPPLY = 6;

    /**
     * 提现到账通知
     */
    Integer WITHDRAWSUCCESS = 7;

    /**
     * 提现失败通知
     */
    Integer WITHDRAWFAIL = 8;
    /**
     * 提货通知
     */
    Integer SENDORDERARRIVE = 9;
    /**
     * 退款拒绝通知
     */
    Integer SENDREFUNDFAIL = 102;
}
