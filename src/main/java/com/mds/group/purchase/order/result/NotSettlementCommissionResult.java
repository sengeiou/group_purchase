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

import cn.hutool.core.date.DateUtil;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * The type Not settlement commission result.
 *
 * @author pavawi
 */
@Data
@ApiModel("我的佣金-未结算佣金")
public class NotSettlementCommissionResult {

    /**
     * 订单id
     */
    @ApiModelProperty(value = "订单id")
    private Long orderId;
    /**
     * 订单编号
     */
    @ApiModelProperty(value = "订单编号")
    private Long orderNo;
    /**
     * 订单状态
     */
    @ApiModelProperty(value = "订单状态")
    private Integer payStatus;
    /**
     * 支付时间
     */
    @ApiModelProperty(value = "支付时间")
    private Date payTime;
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
     * 团长佣金
     */
    @ApiModelProperty(value = "团长佣金")
    private BigDecimal groupLeaderCommission;


    @ApiModelProperty(value = "预计到账时间（按照订单状态计算）")
    private Date endTime;

    /**
     * 商品ID
     */
    @ApiModelProperty(value = "商品ID")
    private Long goodsId;
    /**
     * 活动商品ID
     */
    @ApiModelProperty(value = "活动商品ID")
    private Long actGoodsId;

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
     * 商品优惠后的原价
     */
    @ApiModelProperty(value = "商品优惠后的原价")
    private BigDecimal goodsPrice;

    /**
     * Sets estimated time.
     */
    public void setEstimatedTime() {
        Integer offset = 0;
        switch (this.payStatus) {
            default:
                offset = 7;
        }
        this.endTime = DateUtil.offsetDay(this.payTime, offset);
    }
}
