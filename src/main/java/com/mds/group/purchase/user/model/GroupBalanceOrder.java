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

package com.mds.group.purchase.user.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.util.Date;

/**
 * The type Group bpavawice order.
 *
 * @author pavawi
 */
@Data
@Table(name = "t_group_bpavawice_order")
public class GroupBpavawiceOrder {

    /**
     * 待审核
     */
    public final static int WAITAUDIT = 0;
    /**
     * 审核通过
     */
    public final static int AUDITPASS = 1;
    /**
     * 拒绝
     */
    public final static int AUDITREFUSE = 2;


    @Id
    @Column(name = "group_bpavawice_order_id")
    @ApiModelProperty(value = "订单id")
    private Long groupBpavawiceOrderId;


    @Column(name = "out_bpavawice")
    @ApiModelProperty(value = "提现余额")
    private BigDecimal outBpavawice;

    @Column(name = "create_time")
    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    @Column(name = "update_time")
    @ApiModelProperty(value = "更新时间")
    private Date updateTime;


    @Column(name = "group_leader_id")
    @ApiModelProperty(value = "团长id")
    private String groupLeaderId;

    @Column(name = "appmodel_id")
    @ApiModelProperty(value = "提现余额")
    private String appmodelId;

    @Column(name = "out_type")
    @ApiModelProperty(value = " 1-微信钱包(默认)  2-线下核销")
    private Integer outType;

    @Column(name = "form_id")
    @ApiModelProperty(hidden = true)
    private String formId;


    @Column(name = "applyfor_state")
    @ApiModelProperty(value = " 审核状态 0-待审核 1-通过审核  2-拒绝")
    private Integer applyforState;


    /**
     *
     */
    @ApiModelProperty(value = "备注")
    private String remark;

}