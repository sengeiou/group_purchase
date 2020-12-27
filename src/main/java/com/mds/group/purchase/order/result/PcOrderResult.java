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
 * PC端订单列表返回结果类
 *
 * @author shuke
 * @date 2019 -2-20
 */
@Data
public class PcOrderResult {

    /**
     * 订单id
     */
    private Long orderId;

    /**
     * 买家id
     */
    private Long wxuserId;

    /**
     * 订单编号
     */
    private String orderNo;
    /**
     * 订单类型
     */
    private Integer orderType;

    /**
     * 订单状态 0.等待买家付款 1.买家已付款2.卖家已发货3.待评价 4.交易成功 5用户超时关闭订单 6 用户主动关闭订单
     */
    private Integer payStatus;

    /**
     * 买家姓名
     */
    private String buyerName;

    /**
     * 买家电话
     */
    private String buyerPhone;

    /**
     * 买家地址
     */
    private String buyerAddress;

    /**
     * 取货地点（团长的地址）
     */
    private String pickupLocation;

    /**
     * 订单总价
     */
    private BigDecimal totalFee;

    /**
     * 优惠金额
     */
    private BigDecimal discountsFee;

    /**
     * 批注
     */
    private String postil;

    /**
     * 团长id
     */
    private String groupId;

    /**
     * 团长名称
     */
    private String groupLeaderName;

    /**
     * 团长电话
     */
    private String groupLeaderPhone;

    /**
     * 团长头像
     */
    private String groupLeaderIcon;

    /**
     * 用户备注
     */
    private String userDesc;

    /**
     * 商家备注
     */
    private String shopDesc;

    /**
     * 支付价格
     */
    private BigDecimal payFee;

    /**
     * 支付渠道
     */
    private Integer payChannel;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 支付时间
     */
    private Date payTime;

    /**
     * 关闭时间
     */
    private Date closeTime;

    /**
     * 小程序模块id
     */
    private String appmodelId;

    /**
     * 商品的评价内容
     */
    @ApiModelProperty(value = "商品的评价内容")
    private String goodsComment;

    /**
     * 评价团长的内容
     */
    @ApiModelProperty(value = "评价团长的内容")
    private String groupComment;

    /**
     * 商品的评价分数
     */
    @ApiModelProperty(value = "商品的评价分数")
    private Double goodsScore;

    /**
     * 团长的评价分数
     */
    @ApiModelProperty(value = "团长的评价分数")
    private Double groupScore;

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
     * 线路名称
     */
    @ApiModelProperty(value = "线路名称")
    private String lineName;

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
     * 活动商品价格
     */
    @ApiModelProperty(value = "活动商品价格")
    private BigDecimal actGoodsPrice;

    /**
     * 商品优惠后的价格
     */
    @ApiModelProperty(value = "商品优惠后的价格")
    private BigDecimal preferential;

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
     * 订单是否在发货单中，且发货单的状态为未发货状态
     */
    @ApiModelProperty(value = "订单是否在发货单中，且发货单的状态为未发货状态")
    private boolean inSendBill;

    private Integer sendBillStatus;

    @ApiModelProperty(value = "发货单名称")
    private String sendBillName;

    @ApiModelProperty(value = "发货单id")
    private Long sendBillId;

    @ApiModelProperty(value = "发货单生成时间")
    private String sendBillCreateDate;

    @ApiModelProperty(value = "是否是2019年3月12日21点之前的订单")
    private boolean oldOrder;

    @ApiModelProperty(value = "售后单ID,不为空表示当前订单有申请过售后单")
    private Long afterSaleOrderId;

    @ApiModelProperty(value = "user_after_sale_num")
    private Integer userAfterSaleNum;


    /**
     * Gets group score.
     *
     * @return the group score
     */
    public Double getGroupScore() {
        if (goodsScore != null && groupScore != null) {
            return (groupScore + goodsScore) / 2;
        } else {
            return groupScore;
        }
    }

    /**
     * Gets goods score.
     *
     * @return the goods score
     */
    public Double getGoodsScore() {
        if (goodsScore != null && groupScore != null) {
            return (groupScore + goodsScore) / 2;
        } else {
            return goodsScore;
        }
    }
}
