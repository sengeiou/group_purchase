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

package com.mds.group.purchase.order.result;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * The type My team members result.
 *
 * @author pavawi
 */
@Data
@ApiModel("签收单查看团员")
public class MyTeamMembersResult {
    /**
     * 商品名称
     */
    @ApiModelProperty(value = "商品名称")
    private Long goodsId;
    /**
     * 活动商品名称
     */
    @ApiModelProperty(value = "活动商品名称")
    private Long actGoodsId;
    /**
     * 活动商品价格
     */
    @ApiModelProperty(value = "活动商品价格")
    private BigDecimal actGoodsPrice;

    /**
     * 商品主图
     */
    @ApiModelProperty(value = "商品主图")
    private String goodsImg;

    /**
     * 商品名称
     */
    @ApiModelProperty(value = "商品名称")
    private String goodsName;

    /**
     * 商品数量
     */
    @ApiModelProperty(value = "商品数量")
    private Integer goodsNum;

    /**
     * 商品原价
     */
    @ApiModelProperty(value = "商品原价")
    private BigDecimal price;

    /**
     * 买家姓名
     */
    @ApiModelProperty(value = "买家姓名")
    private String buyerName;

    /**
     * 买家电话
     */
    @ApiModelProperty(value = "买家电话")
    private String buyerPhone;

    /**
     * 买家地址
     */
    @ApiModelProperty(value = "买家地址")
    private String buyerAddress;

    /**
     * 买家头像
     */
    @ApiModelProperty(value = "买家头像")
    private String wxuserIcon;

    /**
     * 买家昵称
     */
    @ApiModelProperty(value = "买家昵称")
    private String wxuserName;

    /**
     * 用户备注
     */
    @ApiModelProperty(value = "用户备注")
    private String userDesc;

    /**
     * 订单ID
     */
    @ApiModelProperty(value = "订单ID")
    private Long orderId;

    /**
     * 订单详细id
     */
    @ApiModelProperty(value = "订单详细id")
    private Long orderDetailId;
    /**
     * 团长签收单详情ID
     */
    @ApiModelProperty(value = "团长签收单详情ID")
    private Long billDetailId;
    /**
     * 订单状态 0.等待买家付款 1.买家已付款2.卖家已发货3.待评价4.交易成功 5用户超时关闭订单
     */
    @ApiModelProperty(value = "订单状态 0.等待买家付款 1.买家已付款2.卖家已发货3.待评价4.交易成功 5用户超时关闭订单")
    private Integer payStatus;

    /**
     * 订单类型
     */
    @ApiModelProperty(value = "订单类型")
    private Integer orderType;

    /**
     * 所属售后单ID
     */
    @ApiModelProperty(name = "所属售后单ID")
    private Long afterSaleOrderId;


    /**
     * 售后类型(1.团长换货 2.团长退款 3.退款 4.换货 5.退货退款)
     */
    @ApiModelProperty(name = "售后类型")
    private Integer afterSaleType;

    /**
     * 审核状态(1.待商家审核 2.商家拒绝 3.待退货 4.待团长确认收货 5.待发货 6.待提货  7.成功 99.关闭)
     */
    @ApiModelProperty(name = "审核状态")
    private Integer afterSaleStatus;

    /**
     * 售后生成的订单
     */
    @ApiModelProperty(name = "售后生成的订单")
    private Long childOrderId;

    @ApiModelProperty(name = "售后单状态")
    private String afterSaleStatusText;
}
