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
 * 团长签收单相关常量
 *
 * @author shuke
 * @date 2019 -2-18
 */
public interface ReceiptBillConstant {

    /**
     * 签收单状态
     * 1、待发货  2、配送中  3、提货中  4、提货完成
     */
    Integer WAIT_SEND = 1;
    /**
     * The Constant DISPATCHING.
     */
    Integer DISPATCHING = 2;
    /**
     * The Constant WAIT_RECEIPT.
     */
    Integer WAIT_RECEIPT = 3;
    /**
     * The Constant RECEIPT_SUCCESS.
     */
    Integer RECEIPT_SUCCESS = 4;
}
