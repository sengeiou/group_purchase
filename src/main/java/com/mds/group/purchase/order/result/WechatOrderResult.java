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

import com.mds.group.purchase.order.model.Order;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * The type Wechat order result.
 *
 * @author pavawi
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class WechatOrderResult extends Order implements Comparable<WechatOrderResult> {


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
     * 区域集合
     */
    @ApiModelProperty(value = "区域集合")
    private List<String> streetNames;

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
     * 商品优惠后的价格
     */
    @ApiModelProperty(value = "商品优惠后的价格")
    private BigDecimal preferential;

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
     * 支付时间
     */
    @ApiModelProperty(value = "发货时间")
    private Date payTime;

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
     * 订单详情编号
     */
    @ApiModelProperty(value = "订单详情编号")
    private String orderDetailNo;

    /**
     * 订单详细id
     */
    @ApiModelProperty(value = "订单详细id")
    private Long orderDetailId;

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
     * 预计提货时间
     */
    @ApiModelProperty(value = "预计提货时间")
    private String forecastReceiveTime;

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
     * 过期时间
     */
    @ApiModelProperty("过期时间")
    private Date deadline;
    /**
     * 原订单ID
     */
    @ApiModelProperty("原订单ID")
    private String originalOrderId;
    /**
     * 初始订单ID
     */
    @ApiModelProperty("初始订单ID")
    private String initialOrderId;

    @Override
    public int compareTo(@NotNull WechatOrderResult o) {
        if (this.getPayStatus() != null && o.getPayStatus() != null) {
            return o.getPayStatus() - this.getPayStatus();
        }
        return 0;
    }
}
