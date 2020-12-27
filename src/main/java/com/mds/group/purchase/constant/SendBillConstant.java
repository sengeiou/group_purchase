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
 * 发货单相关常量
 *
 * @author shuke
 * @date 2019 -2-18
 */
public interface SendBillConstant {

    /**
     * 最大发货单生成时间个数
     */
    Integer MAX_SEND_BILL_TIME_NUMBER = 4;

    /**
     * The Constant GENERATE_TRUE.
     */
    Integer GENERATE_TRUE = 1;

    /**
     * The Constant GENERATE_FALSE.
     */
    Integer GENERATE_FALSE = 0;

    /**
     * 发货单状态
     * 1、待发货  2、配送中  3、待提货  4、成功  5关闭
     */
    Integer WAIT_SEND = 1;
    /**
     * The Constant DISPATCHING.
     */
    Integer DISPATCHING = 2;
    /**
     * The Constant WAIT_RECEIVE.
     */
    Integer WAIT_RECEIVE = 3;
    /**
     * The Constant SUCCESS.
     */
    Integer SUCCESS = 4;
    /**
     * The Constant CLOSE.
     */
    Integer CLOSE = 5;
}
