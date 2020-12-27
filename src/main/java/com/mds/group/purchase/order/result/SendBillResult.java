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

package com.mds.group.purchase.order.result;

import lombok.Data;

import java.math.BigDecimal;

/**
 * The type Send bill result.
 *
 * @author shuke
 * @date 2019 -2-20
 */
@Data
public class SendBillResult {
    /**
     * 发货单id
     */
    private Long sendBillId;

    /**
     * 发货单名称（根据时间自动生成）
     */
    private String sendBillName;

    /**
     * 发货单生成时间
     */
    private String createDate;

    /**
     * 订单数量
     */
    private Integer orders;

    /**
     * 发货单内所有订单的成交额
     */
    private BigDecimal amount;

    /**
     * 发货单内所有订单的佣金
     */
    private BigDecimal commission;

    /**
     * 1、待发货 2、配送中 3、待提货 4、已完成 5 已关闭
     */
    private Integer status;

    private Boolean canSend;
}
