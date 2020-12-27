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
import java.util.Date;

/**
 * The type Order detail.
 *
 * @author pavawi
 */
@Table(name = "t_order_detail")
@Data
public class OrderDetail {
    /**
     * 订单详细id
     */
    @Id
    @Column(name = "order_detail_id")
    private Long orderDetailId;

    /**
     * 订单详情编号
     */
    @Column(name = "order_detail_no")
    private String orderDetailNo;

    /**
     * 订单id
     */
    @Column(name = "order_id")
    private Long orderId;

    /**
     * 订单类型
     */
    @Column(name = "order_type")
    private Integer orderType;

    /**
     * 商品id
     */
    @Column(name = "goods_id")
    private Long goodsId;

    /**
     * 用户id
     */
    @Column(name = "wxuser_id")
    private Long wxuserId;

    /**
     * 活动商品id
     */
    @Column(name = "act_goods_id")
    private Long actGoodsId;


    /**
     * 优惠价格
     */
    private BigDecimal preferential;

    /**
     * 付款价格
     */
    @Column(name = "pay_fee")
    private BigDecimal payFee;

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
     * 更新时间
     */
    @Column(name = "update_time")
    private String updateTime;

    /**
     * 发货时间
     */
    @Column(name = "send_time")
    private Date sendTime;

    /**
     * 收货时间
     */
    @Column(name = "record_time")
    private Date recordTime;

    /**
     * 线路id
     */
    @Column(name = "line_id")
    private Long lineId;

    /**
     * 活动id
     */
    @Column(name = "activity_id")
    private Long activityId;

    /**
     * 小区id
     */
    @Column(name = "community_id")
    private Long communityId;

    /**
     * 区域id
     */
    @Column(name = "street_id")
    private Long streetId;

    /**
     * 商品数量
     */
    @Column(name = "goods_num")
    private Integer goodsNum;

    /**
     * 小程序模块id
     */
    @Column(name = "appmodel_id")
    private String appmodelId;

    /**
     * 商品详情（json）
     */
    @Column(name = "goods_detail")
    private String goodsDetail;

    /**
     * 团长佣金（按照商品的数量计算）
     */
    @Column(name = "group_leader_commission")
    private BigDecimal groupLeaderCommission;

    /**
     * 商品原价
     */
    @Column(name = "goods_price")
    private BigDecimal goodsPrice;

    /**
     * 是否是接龙订单
     */
    @Column(name = "is_solitaire_order")
    private Boolean isSolitaireOrder;
}
