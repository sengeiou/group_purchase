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

import javax.persistence.*;

/**
 * The type Line sorting order mapping.
 *
 * @author pavawi
 */
@Table(name = "t_line_sorting_order_mapping")
@Data
public class LineSortingOrderMapping {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "JDBC")
    @ApiModelProperty(value = "标记")
    private Integer id;

    @Column(name = "line_sorting_order_id")
    @ApiModelProperty(value = "线路分拣单id")
    private Integer lineSortingOrderId;

    @Column(name = "send_bill_id")
    @ApiModelProperty(value = "发货单id")
    private Long sendBillId;

    @Column(name = "appmodel_id")
    @ApiModelProperty(value = "商品标示")
    private String appmodelId;

}