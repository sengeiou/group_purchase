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

package com.mds.group.purchase.goods.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;

/**
 * 商品详情实体类
 *
 * @author shuke
 */
@Data
@Table(name = "t_goods_detail")
public class GoodsDetail {
    /**
     * 商品详情id
     */
    @Id
    @Column(name = "goods_detail_id")
    @ApiModelProperty(value = "商品详情id")
    private Long goodsDetailId;

    /**
     * 商品id
     */
    @Column(name = "goods_id")
    @ApiModelProperty(value = "商品id")
    private Long goodsId;

    /**
     * 供应商id
     */
    @Column(name = "provider_id")
    @ApiModelProperty(value = "供应商id")
    private String providerId;

    /**
     * 销量
     */
    @Column(name = "sales_volume")
    @ApiModelProperty(value = "销量")
    private Integer salesVolume;

    /**
     * 手动虚假销量
     */
    @Column(name = "sham_volume")
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
    @Column(name = "goods_desc")
    private String goodsDesc;

    /**
     * 商品属性，json字符串
     */
    @ApiModelProperty(value = "商品属性，json字符串")
    @Column(name = "goods_property")
    private String goodsProperty;

    /**
     * 保质期
     */
    @ApiModelProperty(value = "保质期")
    @Column(name = "expiration_date")
    private String expirationDate;

    /**
     * 团长佣金
     */
    @Column(name = "group_leader_commission")
    @ApiModelProperty(value = "团长佣金")
    private BigDecimal groupLeaderCommission;

    /**
     * 团长佣金类型（1：比率 2：固定金额）
     */
    @Column(name = "commission_type")
    @ApiModelProperty(value = "团长佣金类型（1：比率 2：固定金额）")
    private Integer commissionType;

    /**
     * 小程序模板id
     */
    @Column(name = "appmodel_id")
    @ApiModelProperty(value = "小程序模板id")
    private String appmodelId;

    /**
     * 文本介绍
     */
    @ApiModelProperty(value = "文本介绍")
    private String text;

    @Column(name = "provider_name")
    @ApiModelProperty(value = "供应商名称")
    private String providerName;

    /**
     * Gets text.
     *
     * @return the text
     */
    public String getText() {
        if (text != null) {
            text = text.replaceAll("&amp;", "&");
        }
        return text;
    }

    /**
     * Sets text.
     *
     * @param text the text
     */
    public void setText(String text) {
        if (text != null) {
            text = text.replaceAll("&amp;", "&");
        }
        this.text = text;
    }
}