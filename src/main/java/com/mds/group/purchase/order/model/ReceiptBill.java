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

package com.mds.group.purchase.order.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * The type Receipt bill.
 *
 * @author pavawi
 */
@Table(name = "t_receipt_bill")
@Data
public class ReceiptBill {
    /**
     * 团长签收单id
     */
    @Id
    @GeneratedValue(generator = "JDBC")
    @Column(name = "bill_id")
    private Long billId;

    /**
     * 签收单状态 0.未开始，1.进行中，2.已完结
     */
    @Column(name = "status")
    private Integer status;

    /**
     * 团长id
     */
    @Column(name = "group_leader_id")
    private String groupLeaderId;

    /**
     * 团长电话
     */
    @Column(name = "group_leader_phone")
    private String groupLeaderPhone;

    /**
     * 发货单id
     */
    @Column(name = "send_bill_id")
    private Long sendBillId;

    /**
     * 发货单名称
     */
    @Column(name = "send_bill_name")
    private String sendBillName;

    /**
     * 团长名称
     */
    @Column(name = "group_leader_name")
    private String groupLeaderName;

    /**
     * 线路id
     */
    @Column(name = "line_id")
    private Long lineId;

    /**
     * 线路名称
     */
    @Column(name = "line_name")
    private String lineName;

    /**
     * 小区id
     */
    @Column(name = "community_id")
    private Long communityId;

    /**
     * 小区名称
     */
    @Column(name = "community_name")
    private String communityName;

    /**
     * 创建时间
     */
    @Column(name = "create_time")
    private Date createTime;

    /**
     * 小程序模板id
     */
    @Column(name = "appmodel_id")
    private String appmodelId;

    @ApiModelProperty("司机手机号")
    private String phone;


    @Column(name = "driver_name")
    @ApiModelProperty("司机名称")
    private String driverName;

}