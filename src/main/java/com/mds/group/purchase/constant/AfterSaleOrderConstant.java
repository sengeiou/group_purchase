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
 * The interface After sale order Constant.
 *
 * @author pavawi
 */
public interface AfterSaleOrderConstant {
    //关闭原因(1.用户撤销 2.团长撤销 3.团长未收到退货 4.重新申请换货)

    /**
     * The Constant CLOSE_BY_USER_CANCEL.
     */
    Integer CLOSE_BY_USER_CANCEL = 1;

    /**
     * The Constant CLOSE_BY_LEADER_CANCEL.
     */
    Integer CLOSE_BY_LEADER_CANCEL = 2;

    /**
     * The Constant CLOSE_BY_LEADER_NOT_RECEIVED.
     */
    Integer CLOSE_BY_LEADER_NOT_RECEIVED = 3;

    /**
     * The Constant CLOSE_BY_EXCHANGE.
     */
    Integer CLOSE_BY_EXCHANGE = 4;
}
