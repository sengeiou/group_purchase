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

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * The type Send bill excel.
 *
 * @author shuke
 * @date 2019 -2-20
 */
@Data
public class SendBillExcel {

    /**
     * 订单id
     */
    @ApiModelProperty(value = "订单id")
    private Long orderId;

    /**
     * 买家id
     */
    @ApiModelProperty(value = "买家id")
    private Long wxuserId;

    /**
     * 买家昵称
     */
    @ApiModelProperty(value = "买家昵称")
    private String wxuserName;

    /**
     * 卖家id
     */
    @ApiModelProperty(value = "卖家id")
    private Long salerId;

    /**
     * 订单编号
     */
    @ApiModelProperty(value = "订单编号")
    private String orderNo;

    /**
     * 商品名称
     */
    @ApiModelProperty(value = "商品名称")
    private String goodsName;

    /**
     * 商品原价
     */
    @ApiModelProperty(value = "商品原价")
    private BigDecimal goodsPrice;

    /**
     * 商品优惠的价格
     */
    @ApiModelProperty(value = "商品优惠的价格")
    private BigDecimal preferential;

    /**
     * 买家姓名
     */
    @ApiModelProperty(value = "收货人姓名")
    private String buyerName;

    /**
     * 买家电话
     */
    @ApiModelProperty(value = "收货人电话")
    private String buyerPhone;

    /**
     * 买家地址
     */
    @ApiModelProperty(value = "收货人地址")
    private String buyerAddress;

    /**
     * 取货地点（团长的地址）
     */
    @ApiModelProperty(value = "取货地点（团长的地址）")
    private String pickupLocation;

    /**
     * 订单总价
     */
    @ApiModelProperty(value = "订单总价")
    private BigDecimal totalFee;

    /**
     * 优惠金额
     */
    @ApiModelProperty(value = "优惠金额")
    private BigDecimal discountsFee;

    /**
     * 团长佣金
     */
    @ApiModelProperty(value = "团长佣金")
    private BigDecimal groupLeaderCommission;

    /**
     * 支付价格
     */
    @ApiModelProperty(value = "支付价格")
    private BigDecimal payFee;

    /**
     * 支付渠道
     */
    @ApiModelProperty(value = "支付渠道（1：微信支付）")
    private Integer payChannel;

    /**
     * 团长id
     */
    @ApiModelProperty(value = "团长id")
    private String groupId;

    /**
     * 团长名称
     */
    @ApiModelProperty(value = "团长名称")
    private String groupLeaderName;

    /**
     * 团长电话
     */
    @ApiModelProperty(value = "团长电话")
    private String groupLeaderPhone;

    /**
     * 创建时间
     */
    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    /**
     * 支付时间
     */
    @ApiModelProperty(value = "支付时间")
    private Date payTime;

    /**
     * 小程序模块id
     */
    @ApiModelProperty(value = "小程序模块id")
    private String appmodelId;

    /**
     * 用户备注
     */
    @ApiModelProperty(value = "用户备注")
    private String userDesc;

    /**
     * 线路id
     */
    @ApiModelProperty(value = "线路id")
    private Long lineId;

    /**
     * 线路名称
     */
    @ApiModelProperty(value = "线路名称")
    private String lineName;

    /**
     * 商品数量
     */
    @ApiModelProperty(value = "商品购买数量")
    private Integer goodsNum;

    /**
     * 司机名称
     */
    @ApiModelProperty(value = "司机名称")
    private String driverName;

    /**
     * 司机电话
     */
    @ApiModelProperty(value = "司机电话")
    private String driverPhone;

    /**
     * 小区名称
     */
    @ApiModelProperty(value = "小区名称")
    private String communityName;
}
