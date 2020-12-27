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
 * The type Line sorting order.
 *
 * @author pavawi
 */
@Table(name = "t_line_sorting_order")
@Data
public class LineSortingOrder {
    /**
     * 线路分拣单id
     */
    @Id
    @Column(name = "line_sorting_order_id")
    @GeneratedValue(generator = "JDBC")
    @ApiModelProperty("线路分拣单id")
    private Integer lineSortingOrderId;

    @ApiModelProperty("司机手机号")
    private String phone;


    @Column(name = "driver_name")
    @ApiModelProperty("司机名称")
    private String driverName;

    @Column(name = "line_name")
    @ApiModelProperty("线路名")
    private String lineName;


    @Column(name = "generate_time")
    @ApiModelProperty("生成时间")
    private Date generateTime;

    @ApiModelProperty("备注")
    private String remark;


    @Column(name = "line_id")
    @ApiModelProperty("线路id")
    private Long lineId;

    @Column(name = "appmodel_id")
    private String appmodelId;

    @Column(name = "send_bill_id")
    @ApiModelProperty("发货单id")
    private Long sendBillId;


}