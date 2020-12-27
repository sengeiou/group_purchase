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
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * The type Goods sorting order.
 *
 * @author pavawi
 */
@Table(name = "t_goods_sorting_order")
@Data
public class GoodsSortingOrder {
    /**
     * 线路分拣单id
     */
    @Id
    @Column(name = "goods_sorting_order_id")
    @GeneratedValue(generator = "JDBC")
    private Integer goodsSortingOrderId;

    /**
     * 发货单id
     */
    @Column(name = "sendb_bill_id")
    private Long sendBillId;

    /**
     * 发货单id
     */
    @Column(name = "send_bill_name")
    private String sendBillName;

    /**
     * 商品id
     */
    @Column(name = "goods_id")
    private Long goodsId;


    /**
     * 活动商品卖出价格
     */
    private String price;

    /**
     * 商品图片
     */
    @Column(name = "goods_image")
    private String goodsImage;

    /**
     * 商品名称
     */
    @Column(name = "goods_name")
    private String goodsName;

    @Column(name = "appmodel_id")
    private String appmodelId;

    /**
     * 商品总数量
     */
    @Column(name = "goods_totle_sum")
    private Integer goodsTotleSum;


}