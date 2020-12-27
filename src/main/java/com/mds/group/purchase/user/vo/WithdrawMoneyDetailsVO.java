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

package com.mds.group.purchase.user.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * The type Withdraw money details vo.
 *
 * @author pavawi
 */
@Data
public class WithdrawMoneyDetailsVO {

    @ApiModelProperty(value = "审核id")
    private Long groupBpavawiceOrderId;


    @ApiModelProperty(value = "提现余额")
    private BigDecimal outBpavawice;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    @ApiModelProperty(value = "创建时间(Long类型)")
    private Long createTimeValue;

    @ApiModelProperty(value = "团长id")
    private String groupLeaderId;

    @ApiModelProperty(value = "团长名称")
    private String groupName;

    @ApiModelProperty(value = "团长电话")
    private String groupPhone;

    @ApiModelProperty(value = " 1-微信钱包  2-线下核销")
    private Integer outType;

    @ApiModelProperty(value = " 0-待审核 1-通过审核  2-拒绝")
    private Integer applyforState;

}
