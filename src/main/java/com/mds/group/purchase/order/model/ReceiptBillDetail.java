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
import java.math.BigDecimal;

/**
 * The type Receipt bill detail.
 *
 * @author pavawi
 */
@Table(name = "t_receipt_bill_detail")
@Data
public class ReceiptBillDetail {

    @Id
    @Column(name = "bill_detail_id")
    private Long billDetailId;

    /**
     * 团长签收单id
     */
    @Column(name = "bill_id")
    private Long billId;

    /**
     * 商品名称
     */
    @Column(name = "goods_id")
    private Long goodsId;

    /**
     * 商品主图
     */
    @Column(name = "goods_img")
    private String goodsImg;

    /**
     * 商品名称
     */
    @Column(name = "goods_name")
    private String goodsName;

    /**
     * 商品数量
     */
    @Column(name = "goods_num")
    private Integer goodsNum;

    /**
     * 已签收商品数量
     */
    @Column(name = "receipt_goods_num")
    private Integer receiptGoodsNum;

    /**
     * 商品优惠后的原价
     */
    private BigDecimal price;

    /**
     * 订单id
     */
    @Column(name = "order_ids")
    private String orderIds;

    /**
     * 订单详情id
     */
    @Column(name = "order_detail_ids")
    private String orderDetailIds;


    /**
     * 活动id
     */
    @Column(name = "activity_id")
    private Long activityId;


    /**
     * 小程序模板id
     */
    @Column(name = "appmodel_id")
    private String appmodelId;

    /**
     * 团长佣金
     */
    @Column(name = "group_leader_commission")
    private BigDecimal groupLeaderCommission;

    /**
     * 团长售后次数
     */
    @Column(name = "leader_after_sale_num")
    private Integer leaderAfterSaleNum;


}