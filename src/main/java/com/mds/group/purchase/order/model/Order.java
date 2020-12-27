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
import java.math.BigDecimal;
import java.util.Date;

/**
 * The type Order.
 *
 * @author pavawi
 */
@Table(name = "t_order")
@Data
public class Order {
    /**
     * 订单id
     */
    @Id
    @Column(name = "order_id")
    @GeneratedValue(generator = "JDBC")
    private Long orderId;

    /**
     * 买家id
     */
    @Column(name = "wxuser_id")
    private Long wxuserId;

    /**
     * 卖家id
     */
    @Column(name = "saler_id")
    private Long salerId;

    /**
     * 订单编号
     */
    @Column(name = "order_no")
    private String orderNo;

    /**
     * 订单状态 0.等待买家付款 1.买家已付款2.卖家已发货3.待评价 4.交易成功 5用户超时关闭订单 6 用户主动关闭订单
     */
    @Column(name = "pay_status")
    private Integer payStatus;

    /**
     * 买家姓名
     */
    @Column(name = "buyer_name")
    private String buyerName;

    /**
     * 买家电话
     */
    @Column(name = "buyer_phone")
    private String buyerPhone;

    /**
     * 买家地址
     */
    @Column(name = "buyer_address")
    private String buyerAddress;

    /**
     * 取货地点（团长的地址）
     */
    @Column(name = "pickup_location")
    private String pickupLocation;

    /**
     * 订单总价
     */
    @Column(name = "total_fee")
    private BigDecimal totalFee;

    /**
     * 优惠金额
     */
    @Column(name = "discounts_fee")
    private BigDecimal discountsFee;

    /**
     * 批注
     */
    private String postil;

    /**
     * 团长id
     */
    @Column(name = "group_id")
    private String groupId;

    /**
     * 团长名称
     */
    @Column(name = "group_leader_name")
    private String groupLeaderName;

    /**
     * 团长电话
     */
    @Column(name = "group_leader_phone")
    private String groupLeaderPhone;

    /**
     * 团长头像
     */
    @Column(name = "group_leader_icon")
    private String groupLeaderIcon;

    /**
     * 用户备注
     */
    @Column(name = "user_desc")
    private String userDesc;

    /**
     * 商家备注
     */
    @Column(name = "shop_desc")
    private String shopDesc;


    /**
     * 运费
     */
    @Column(name = "transportation_fee")
    private Integer transportationFee;

    /**
     * 支付价格
     */
    @Column(name = "pay_fee")
    private BigDecimal payFee;

    /**
     * 支付单号
     */
    @Column(name = "pay_order_id")
    private String payOrderId;

    /**
     * 支付渠道
     */
    @Column(name = "pay_channel")
    private Integer payChannel;

    /**
     * 创建时间
     */
    @Column(name = "create_time")
    private Date createTime;

    /**
     * 支付时间
     */
    @Column(name = "pay_time")
    private Date payTime;

    /**
     * 关闭时间
     */
    @Column(name = "close_time")
    private Date closeTime;

    @Column(name = "form_id")
    private String formId;


    @Column(name = "delete_flag")
    private Boolean deleteFlag;

    /**
     * 是否通知团长接单（true：立即向微信群发送自己的购买记录 false不发送）
     */
    @Column(name = "notice_group_leader_flag")
    private Boolean noticeGroupLeaderFlag;

    /**
     * 小程序模块id
     */
    @Column(name = "appmodel_id")
    private String appmodelId;

    /**
     * 售后单ID
     */
    @Column(name = "after_sale_order_id")
    private Long afterSaleOrderId;

    /**
     * 用户申请售后次数
     */
    @Column(name = "user_after_sale_num")
    private Integer userAfterSaleNum;


    /**
     * 确认收货时间
     */
    @Column(name = "confirm_time")
    private Date confirmTime;

    /**
     * 退款金额
     */
    @Column(name = "refund_fee")
    private BigDecimal refundFee;

    /**
     * 订单类型
     */
    @Column(name = "order_type")
    private Integer orderType;

}