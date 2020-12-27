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

import com.mds.group.purchase.order.model.AfterSaleOrder;
import com.mds.group.purchase.order.model.Order;
import com.mds.group.purchase.order.model.OrderDetail;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 订单返回结果类
 *
 * @author shuke
 * @date 2018 -12-7
 */
@Data
public class OrderResult {

    /**
     * 订单id
     */
    @ApiModelProperty(value = "订单id")
    private Long orderId;

    /**
     * 订单类型
     */
    @ApiModelProperty(value = "订单类型:1: 秒杀2: 拼团 3: 换货 4:接龙")
    private Integer orderType;
    /**
     * 售后单ID
     */
    @ApiModelProperty(value = "售后单ID,不为空表示当前订单有申请过售后单")
    private Long afterSaleOrderId;
    /**
     * 用户售后次数
     */
    @ApiModelProperty(value = "user_after_sale_num")
    private Integer userAfterSaleNum;

    /**
     * 买家id
     */
    @ApiModelProperty(value = "买家id")
    private Long wxuserId;


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
     * 订单状态 0.等待买家付款 1.买家已付款2.卖家已发货3.待评价4.交易成功 5用户超时关闭订单
     */
    @ApiModelProperty(value = "订单状态 0.等待买家付款 1.买家已付款2.卖家已发货3.待评价4.交易成功 5用户超时关闭订单")
    private Integer payStatus;

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
     * 预计提货时间
     */
    @ApiModelProperty(value = "预计提货时间")
    private Date forecastReceiveTime;

    /**
     * 运费
     */
    @ApiModelProperty(value = "运费")
    private Integer transportationFee;

    /**
     * 支付价格
     */
    @ApiModelProperty(value = "支付价格")
    private BigDecimal payFee;

    /**
     * 支付单号
     */
    @ApiModelProperty(value = "支付单号")
    private String payOrderId;

    /**
     * formId
     */
    @ApiModelProperty(value = "formId")
    private String formId;

    /**
     * 支付渠道
     */
    @ApiModelProperty(value = "支付渠道（1：微信支付）")
    private Integer payChannel;

    /**
     * 批注
     */
    @ApiModelProperty(value = "批注")
    private String postil;

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
     * 团长头像
     */
    @ApiModelProperty(value = "团长头像")
    private String groupLeaderIcon;

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
     * 订单详细id
     */
    @ApiModelProperty(value = "订单详细id")
    private Long orderDetailId;

    /**
     * 小区id
     */
    @ApiModelProperty(value = "小区")
    private Long communityId;

    /**
     * 订单详情编号
     */
    @ApiModelProperty(value = "订单详情编号")
    private String orderDetailNo;

    /**
     * 商品id
     */
    @ApiModelProperty(value = "商品id")
    private Long goodsId;


    /**
     * 活动商品id
     */
    @ApiModelProperty(value = "活动商品id")
    private Long actGoodsId;

    /**
     * 活动价格
     */
    @ApiModelProperty(value = "活动价格")
    private BigDecimal actGoodsPrice;
    /**
     * 活动id
     */
    @ApiModelProperty(value = "活动id")
    private Long activityId;

    /**
     * 商品主图
     */
    @ApiModelProperty(value = "商品主图")
    private String goodsImg;

    /**
     * 更新时间
     */
    @ApiModelProperty(value = "更新时间")
    private String updateTime;

    /**
     * 发货时间
     */
    @ApiModelProperty(value = "发货时间")
    private Date sendTime;

    /**
     * 收货时间
     */
    @ApiModelProperty(value = "收货时间")
    private Date recordTime;

    /**
     * 订单关闭时间
     */
    @ApiModelProperty(value = "订单关闭时间")
    private Date closeTime;


    /**
     * 用户备注
     */
    @ApiModelProperty(value = "用户备注")
    private String userDesc;

    /**
     * 商家备注
     */
    @ApiModelProperty(value = "商家备注")
    private String shopDesc;

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
     * 区域集合
     */
    @ApiModelProperty(value = "区域集合")
    private List<String> streetNames;

    /**
     * 商品数量
     */
    @ApiModelProperty(value = "商品购买数量")
    private Integer goodsNum;

    /**
     * 商品详情（json）
     */
    @ApiModelProperty(value = "商品详情（json）")
    private String goodsDetail;

    /**
     * 商品的评价内容
     */
    @ApiModelProperty(value = "商品的评价内容")
    private String goodsComment;

    /**
     * 商品的评价分数
     */
    @ApiModelProperty(value = "商品的评价分数")
    private Double goodsScore;


    /**
     * 售后编号（原订单号加当前售后类型数字加售后次数）
     */
    @ApiModelProperty("售后编号（原订单号加当前售后类型数字加售后次数）")
    private String afterSaleNo;

    /**
     * 售后类型(1.团长换货 2.团长退款 3.退款 4.换货 5.退货退款)
     */
    @ApiModelProperty("售后类型(1.团长换货 2.团长退款 3.退款 4.换货 5.退货退款)")
    private Integer afterSaleType;

    /**
     * 审核状态(1.待商家审核 2.待退货 3.待团长确认收货 4.待发货 5.待提货  6.成功)
     */
    @ApiModelProperty("审核状态(1.待商家审核 2.待退货 3.待团长确认收货 4.待发货 5.待提货  6.成功)")
    private Integer afterSaleStatus;

    /**
     * 退款金额
     */
    @ApiModelProperty("退款金额")
    private BigDecimal refundFee;

    @ApiModelProperty("是否是接龙订单")
    private Boolean isSolitaireOrder;


    /**
     * Instantiates a new Order result.
     */
    public OrderResult() {

    }

    /**
     * Instantiates a new Order result.
     *
     * @param order          the order
     * @param orderDetail    the order detail
     * @param afterSaleOrder the after sale order
     */
    public OrderResult(Order order, OrderDetail orderDetail, AfterSaleOrder afterSaleOrder) {
        setOrderType(order.getOrderType());
        setPayFee(order.getPayFee());
        setActivityId(orderDetail.getActivityId());
        setAppmodelId(order.getAppmodelId());
        setBuyerAddress(order.getBuyerAddress());
        setBuyerName(order.getBuyerName());
        setBuyerPhone(order.getBuyerPhone());
        setCloseTime(order.getCloseTime());
        setCreateTime(order.getCreateTime());
        setDiscountsFee(order.getDiscountsFee());
//		setForecastReceiveTime(com.mds.group.purchase.order.);
        setFormId(order.getFormId());
//		setGoodsComment();
        setGoodsDetail(orderDetail.getGoodsDetail());
        setActGoodsId(orderDetail.getActGoodsId());
        setGoodsId(orderDetail.getGoodsId());
        setGoodsImg(orderDetail.getGoodsImg());
        setGoodsName(orderDetail.getGoodsName());
        setGoodsNum(orderDetail.getGoodsNum());
        setGoodsPrice(orderDetail.getGoodsPrice());
//		setGoodsScore();
        setGroupId(order.getGroupId());
        setGroupLeaderIcon(order.getGroupLeaderIcon());
        setGroupLeaderName(order.getGroupLeaderName());
        setGroupLeaderPhone(order.getGroupLeaderPhone());
        setOrderDetailId(orderDetail.getOrderDetailId());
//		setSendBillId();
//		setLineName();
        setOrderId(order.getOrderId());
        setOrderNo(order.getOrderNo());
        setPayChannel(order.getPayChannel());
        setPayOrderId(order.getPayOrderId());
        setPayStatus(order.getPayStatus());
        setPayTime(order.getPayTime());
        setPickupLocation(order.getPickupLocation());
        setPostil(order.getPostil());
        setPreferential(orderDetail.getPreferential());
        setRecordTime(orderDetail.getRecordTime());
        setSalerId(order.getSalerId());
        setSendTime(orderDetail.getSendTime());
        setShopDesc(order.getShopDesc());
//		setStreetNames(com.mds.group.purchase.order);
        setTotalFee(order.getPayFee());
        setTransportationFee(order.getTransportationFee());
        setUpdateTime(orderDetail.getUpdateTime());
        setUserDesc(order.getUserDesc());
//		setWxuserIcon(orderDe);
        setAfterSaleNo(afterSaleOrder.getAfterSaleNo());
        setAfterSaleStatus(afterSaleOrder.getAfterSaleStatus());
        setAfterSaleType(afterSaleOrder.getAfterSaleType());
        setWxuserId(order.getWxuserId());
        setWxuserId(order.getWxuserId());
        setWxuserId(order.getWxuserId());
//		setWxuserName();
        setCommunityId(orderDetail.getCommunityId());
        setRefundFee(order.getRefundFee());
        setIsSolitaireOrder(orderDetail.getIsSolitaireOrder() == null ? false : orderDetail.getIsSolitaireOrder());
    }


}
