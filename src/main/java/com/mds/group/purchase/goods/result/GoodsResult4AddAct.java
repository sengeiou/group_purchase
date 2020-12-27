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

package com.mds.group.purchase.goods.result;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Transient;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 添加活动时查询商品的结果类
 *
 * @author shuke
 */
@Data
public class GoodsResult4AddAct {
    /**
     * 商品id
     */
    @ApiModelProperty(value = "商品id")
    private Long goodsId;

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
     * 商品删除标识（1：删除，0：正常）
     */
    @ApiModelProperty(value = "商品删除标识")
    private Boolean goodsDelFlag;

    /**
     * 销售价
     */
    @ApiModelProperty(value = "销售价")
    private BigDecimal price;

    /**
     * 市场价（划线价）
     */
    @ApiModelProperty(value = "市场价（划线价）")
    private BigDecimal marketPrice;

    /**
     * 状态(默认上架，0--上架，1--下架（仓库中），2--已售完)
     */
    @ApiModelProperty(value = "状态(默认上架，0--上架，1--下架（仓库中），2--已售完)")
    private Integer goodsStatus;

    /**
     * 创建时间
     */
    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    /**
     * 小程序模块id
     */
    @ApiModelProperty(value = "小程序模板id")
    private String appmodelId;

    /**
     * 商品图片
     */
    @ApiModelProperty(value = "商品图片地址，多个地址用逗号分隔")
    private String goodsImg;

    /**
     * 商品详情id
     */
    @ApiModelProperty(value = "商品详情id")
    private Long goodsDetailId;


    /**
     * 供应商id
     */
    @ApiModelProperty(value = "供应商id")
    private String providerId;

    /**
     * 销量
     */
    @ApiModelProperty(value = "销量")
    private Integer salesVolume;

    /**
     * 手动虚假销量
     */
    @ApiModelProperty(value = "手动虚假销量")
    private Integer shamVolume;

    /**
     * 库存
     */
    @ApiModelProperty(value = "库存")
    private Integer stock;

    /**
     * 商品描述
     */
    @ApiModelProperty(value = "商品描述")
    private String goodsDesc;

    /**
     * 商品属性，json字符串
     */
    @ApiModelProperty(value = "商品属性，json字符串")
    private String goodsProperty;

    /**
     * 保质期
     */
    @ApiModelProperty(value = "保质期")
    private String expirationDate;

    /**
     * 团长佣金
     */
    @ApiModelProperty(value = "团长佣金")
    private BigDecimal groupLeaderCommission;

    /**
     * 团长佣金类型（1：比率 2：固定金额）
     */
    @ApiModelProperty(value = "团长佣金类型（1：比率 2：固定金额）")
    private Integer commissionType;

    /**
     * 文本介绍
     */
    @ApiModelProperty(value = "文本介绍")
    private String text;

    @ApiModelProperty(value = "供应商名称")
    @Transient
    private String providerName;
}
