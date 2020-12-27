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

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * The type Line sorting order detail.
 *
 * @author pavawi
 */
@Table(name = "t_line_sorting_order_detail")
@Data
public class LineSortingOrderDetail {
    /**
     * 线路分拣单详情
     */
    @Id
    @Column(name = "line_sorting_order_detail_id")
    private Integer lineSortingOrderDetailId;

    /**
     * 线路分拣单id
     */
    @Column(name = "line_sorting_order_id")
    private Integer lineSortingOrderId;

    /**
     * 小区名
     */
    @Column(name = "community_name")
    private String communityName;

    /**
     * 数量
     */
    @Column(name = "goods_number")
    private Integer goodsNumber;

}