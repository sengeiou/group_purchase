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
import java.math.BigDecimal;
import java.util.Date;

/**
 * The type After sale order.
 *
 * @author pavawi
 */
@Data
@Table(name = "t_after_sale_order")
public class AfterSaleOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long afterSaleOrderId;

    /**
     * 售后编号（原订单号加当前售后类型数字加售后次数）
     */
    @Column(name = "after_sale_no")
    private String afterSaleNo;

    /**
     * 售后类型(1.团长换货 2.团长退款 3.退款 4.换货 5.退货退款)
     */
    @ApiModelProperty("售后类型(1.团长换货 2.团长退款 3.退款 4.换货 5.退货退款)")
    @Column(name = "after_sale_type")
    private Integer afterSaleType;

    /**
     * 申请人ID
     */
    @ApiModelProperty("申请人ID")
    @Column(name = "wxuser_id")
    private Long wxuserId;
    /**
     * 团长ID
     */
    @ApiModelProperty("团长ID")
    @Column(name = "group_id")
    private String groupId;

    /**
     * 审核状态(1.待商家审核 2.待退货 3.待团长确认收货 4.待发货 5.待提货  6.成功)
     */
    @ApiModelProperty("审核状态(1.待商家审核 2.待退货 3.待团长确认收货 4.待发货 5.待提货  6.成功)")
    @Column(name = "after_sale_status")
    private Integer afterSaleStatus;
    /**
     * 关闭原因(1.用户撤销 2.团长撤销 3.团长未收到退货 4.重新申请换货)
     */
    @ApiModelProperty("关闭原因(1.用户撤销 2.团长撤销 3.团长未收到退货 4.重新申请换货)")
    @Column(name = "close_type")
    private Integer closeType;

    /**
     * form_id
     */
    @Column(name = "form_id")
    private String formId;

    /**
     * 售后生成的订单
     */
    @ApiModelProperty("售后生成的订单")
    @Column(name = "order_id")
    private Long orderId;
    /**
     * 原订单ID
     */
    @ApiModelProperty("原订单ID")
    @Column(name = "original_order_id")
    private String originalOrderId;

    /**
     * 商品ID
     */
    @ApiModelProperty("商品ID")
    @Column(name = "goods_id")
    private Long goodsId;

    /**
     * 申请数量
     */
    @ApiModelProperty("申请数量")
    @Column(name = "application_num")
    private Integer applicationNum;

    /**
     * 退款金额
     */
    @ApiModelProperty("退款金额")
    @Column(name = "refund_fee")
    private BigDecimal refundFee;

    /**
     * 过期时间
     */
    @ApiModelProperty("过期时间")
    @Column(name = "deadline")
    private Date deadline;

    /**
     * 说明
     */
    @ApiModelProperty("说明")
    @Column(name = "description")
    private String description;

    /**
     * 照片
     */
    @ApiModelProperty("照片")
    @Column(name = "images")
    private String images;

    /**
     * 申请时间
     */
    @ApiModelProperty("申请时间")
    @Column(name = "create_time")
    private Date createTime;

    /**
     * 关闭时间
     */
    @ApiModelProperty("关闭时间")
    @Column(name = "close_time")
    private Date closeTime;

    /**
     * 拒绝时间
     */
    @ApiModelProperty("拒绝时间")
    @Column(name = "refusal_time")
    private Date refusalTime;

    /**
     * 成功时间
     */
    @ApiModelProperty("成功时间")
    @Column(name = "success_time")
    private Date successTime;

    /**
     * 活动商品id
     */
    @ApiModelProperty("活动商品id")
    @Column(name = "act_goods_id")
    private Long actGoodsId;

    /**
     * 活动商品卖出价格
     */
    @ApiModelProperty("活动商品卖出价格")
    @Column(name = "act_goods_price")
    private BigDecimal actGoodsPrice;

    /**
     * 商品名称
     */
    @ApiModelProperty("商品名称")
    @Column(name = "goods_name")
    private String goodsName;

    /**
     * 商品图片
     */
    @ApiModelProperty("商品图片")
    @Column(name = "goods_image")
    private String goodsImage;

    /**
     * 拒绝原因
     */
    @ApiModelProperty("拒绝原因")
    @Column(name = "refusal_reason")
    private String refusalReason;

    @Column(name = "appmodel_id")
    private String appmodelId;

}