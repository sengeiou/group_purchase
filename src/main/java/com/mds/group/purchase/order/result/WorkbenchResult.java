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

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * The type Workbench result.
 *
 * @author pavawi
 */
@Data
@ApiModel("团长工作台首页数据")
public class WorkbenchResult {


    @ApiModelProperty(value = "今日订单数")
    private Integer todayOrderNum;


    @ApiModelProperty(value = "今日成交额")
    private BigDecimal todayTurnover;


    @ApiModelProperty(value = "今日佣金")
    private BigDecimal todayCommission;


    @ApiModelProperty(value = "累积提现")
    private BigDecimal cumulativeCashWithdrawal;


    @ApiModelProperty(value = "我的客户")
    private Integer customers;


    @ApiModelProperty(value = "佣金余额")
    private BigDecimal commission;


    @ApiModelProperty(value = "待签收商品")
    private Integer waitReceipt;


    @ApiModelProperty(value = "售后订单")
    private Integer afterOrder;


}
