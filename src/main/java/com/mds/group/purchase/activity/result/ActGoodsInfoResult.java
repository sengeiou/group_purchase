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

package com.mds.group.purchase.activity.result;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 活动商品返回结果类
 *
 * @author shuke
 * @date 2018 -12-17
 */
@Data
public class ActGoodsInfoResult implements Serializable, Comparable<ActGoodsInfoResult> {

    /**
     * 活动商品id
     */
    @ApiModelProperty(value = "活动商品id")
    private Long activityGoodsId;

    /**
     * 商品id
     */
    @ApiModelProperty(value = "商品id")
    private Long goodsId;

    /**
     * 活动id
     */
    @ApiModelProperty(value = "活动id")
    private Long activityId;

    /**
     * 活动图片
     */
    @ApiModelProperty(value = "活动图片")
    private String activityImg;

    /**
     * 活动名称
     */
    @ApiModelProperty(value = "活动名称")
    private String activityName;

    /**
     * 活动状态
     */
    @ApiModelProperty("活动状态（0:未开始 1：预热中 2：进行中 3：已经结束）")
    private Integer activityStatus;

    /**
     * 活动类型（1：秒杀 2拼团）
     */
    @ApiModelProperty("活动类型（1：秒杀 2拼团）")
    private Integer activityType;

    /**
     * 活动开始时间
     */
    @ApiModelProperty("活动开始时间")
    private String actStartTDate;

    /**
     * 活动结束时间
     */
    @ApiModelProperty("活动结束时间")
    private String actEndDate;

    /**
     * 活动折扣
     */
    @ApiModelProperty(value = "活动折扣")
    private Double activityDiscount;

    /**
     * 活动价格
     */
    @ApiModelProperty(value = "活动价格")
    private BigDecimal actPrice;

    /**
     * 活动库存
     */
    @ApiModelProperty(value = "活动库存")
    private Integer activityStock;

    /**
     * 活动销量
     */
    @ApiModelProperty(value = "活动销量")
    private Integer activitySalesVolume;

    /**
     * 是否在主页显示
     */
    @ApiModelProperty(value = "是否在主页显示")
    private Boolean indexDisplay;

    /**
     * 活动商品排序位置
     */
    @ApiModelProperty(value = "活动商品排序位置")
    private Integer sortPosition;

    /**
     * 限购
     */
    @ApiModelProperty(value = "限购")
    private Integer maxQuantity;

    /**
     * 活动商品的状态（1:预热状态 2:活动开始可购买）
     */
    @ApiModelProperty(value = "活动商品的状态（0:不能购买，不能展示 1:预热状态 2:活动开始可购买）")
    private Integer preheatStatus;

    /**
     * Gets sold out status.
     *
     * @return the sold out status
     */
    public Integer getSoldOutStatus() {
        return soldOutStatus == null ? Integer.valueOf(0) : soldOutStatus;
    }

    /**
     * 活动商品上下架状态
     */
    @ApiModelProperty(value = "活动商品上下架状态：0：正常 1:已下架")
    private Integer soldOutStatus;

    /**
     * 活动商品售罄状态
     */
    @ApiModelProperty(value = "活动商品售罄状态：0：正常 1:已售罄")
    private Integer sellOutStatus;

    /**
     * 小程序模块id
     */
    @ApiModelProperty(value = "小程序模块id")
    private String appmodelId;

    /**
     * 商品标题
     */
    @ApiModelProperty(value = "商品标题")
    private String goodsTitle;

    /**
     * 商品名称
     */
    @ApiModelProperty(value = "商品名称")
    private String goodsName;

    /**
     * 销售价
     */
    @ApiModelProperty(value = "销售价")
    private BigDecimal price;

    /**
     * 商品图片
     */
    @ApiModelProperty(value = "商品图片地址，多个地址用逗号分隔")
    private String goodsImg;

    /**
     * 商品主图视频url
     * @since v1.2
     */
    @ApiModelProperty(value = "商品主图视频url")
    private String goodsVideoUrl;

    /**
     * 虚假销量
     */
    @ApiModelProperty(value = "虚假销量")
    private Integer shamSalesVolume;

    /**
     * 累计销量
     */
    @ApiModelProperty(value = "累计销量")
    private Integer salesVolume;

    /**
     * 库存
     */
    @ApiModelProperty(value = "库存")
    private Integer stock;

    /**
     * 商品描述
     */
    @ApiModelProperty(value = "商品描述")
    private String desc;

    /**
     * 供应商
     */
    @ApiModelProperty(value = "供应商")
    private String providerName;

    /**
     * 商品属性，json字符串
     */
    @ApiModelProperty(value = "商品属性，json字符串")
    private String goodsProperty;

    /**
     * 文本介绍
     */
    @ApiModelProperty(value = "文本介绍")
    private String text;

    /**
     * 预计提货时间
     */
    @ApiModelProperty(value = "预计提货时间")
    private String forecastReceiveTime;

    /**
     * 是否加入接龙活动
     * @since v1.2.3
     */
    private Boolean joinSolitaire;

    /**
     * 根据活动商品的排序值排序
     * @param o 活动商品结果对象
     */
    @Override
    public int compareTo(@NotNull ActGoodsInfoResult o) {
        if (this.getSortPosition() != null && o.getSortPosition() != null) {
            return (this.getSortPosition() - o.getSortPosition());
        }
        return 0;
    }

    /**
     * Gets act price.
     *
     * @return the act price
     */
    public BigDecimal getActPrice() {
        return actPrice.setScale(2, BigDecimal.ROUND_HALF_UP);
    }
}
