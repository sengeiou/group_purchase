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
 * The type Goods sorting order detail.
 *
 * @author pavawi
 */
@Table(name = "t_goods_sorting_order_detail")
@Data
public class GoodsSortingOrderDetail {
    /**
     * 线路分拣单详情
     */
    @Id
    @Column(name = "goods_sorting_order_detail_id")
    private Integer goodsSortingOrderDetailId;

    /**
     * 线路分拣单id
     */
    @Column(name = "goods_sorting_order_id")
    private Integer goodsSortingOrderId;


    @Column(name = "line_id")
    private Long lineId;

    /**
     * 线路名称
     */
    @Column(name = "line_name")
    private String lineName;

    /**
     * 数量
     */
    @Column(name = "goods_number")
    private Integer goodsNumber;

    /**
     * 活动id
     */
    @Column(name = "activity_id")
    private Long activityId;

    /**
     * 活动类型
     */
    @Column(name = "activity_type")
    private Integer activityType;

    @Column(name = "activity_name")
    private String activityName;
    /**
     * 活动商品id
     */
    @Column(name = "act_goods_id")
    private Long actGoodsId;

}