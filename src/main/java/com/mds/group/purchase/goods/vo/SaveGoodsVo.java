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

package com.mds.group.purchase.goods.vo;


import com.mds.group.purchase.goods.model.Goods;
import com.mds.group.purchase.goods.model.GoodsDetail;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * 新建商品的参数类
 *
 * @author shuke
 */
@Data
public class SaveGoodsVo {

    @NotNull
    @ApiModelProperty(value = "商品名称")
    private String goodsName;

    @ApiModelProperty(value = "商品标题")
    private String goodsTitle;

    @NotNull
    @ApiModelProperty(value = "商品描述")
    private String goodsDesc;

    @NotNull
    @ApiModelProperty(value = "供应商id")
    private String providerId;

    @NotNull
    @ApiModelProperty(value = "商品分类id")
    private Long[] goodsClassIds;

    @ApiModelProperty(value = "手动虚假销量")
    private Integer shamVolume;

    @ApiModelProperty(value = "市场价（划线价）")
    private BigDecimal markPrice;

    @ApiModelProperty(value = "销售价")
    private BigDecimal price;

    @NotNull
    @ApiModelProperty(value = "商品图片地址，多个地址用逗号分隔")
    private String goodsImg;

    @NotNull
    @ApiModelProperty(value = "库存")
    private Integer stock;

    @ApiModelProperty(value = "商品属性，json字符串")
    private String goodsProperty;

    @ApiModelProperty(value = "保质期")
    private String expirationDate;

    @NotNull
    @ApiModelProperty(value = "团长佣金")
    private BigDecimal groupLeaderCommission;

    @NotNull
    @ApiModelProperty(value = "团长佣金类型（1：比率 2：固定金额）")
    private Integer commissionType;

    @NotNull
    @ApiModelProperty(value = "文本介绍")
    private String text;

    @ApiModelProperty(value = "小程序模板id")
    private String appmodelId;

    @NotNull
    @ApiModelProperty(value = "商品状态，0为直接发布，1为发布至仓库")
    private Integer goodsStatus;

    @ApiModelProperty(value = "商品id，保存商品时不用传参")
    private Long goodsId;

    @ApiModelProperty(value = "要投放的小区的id，中间用逗号,隔开。当传空字符串时，投放到所有小区。默认投放所有小区")
    private String communityIds;

    @ApiModelProperty(value = "新增小区时，是否要将该商品自动添加到该小区")
    private boolean autoAdd;

    /**
     * Gets text.
     *
     * @return the text
     */
    public String getText() {
        return text.replaceAll("&amp;", "&");
    }

    /**
     * Sets text.
     *
     * @param text the text
     */
    public void setText(String text) {
        this.text = text.replaceAll("&amp;", "&");
    }

    /**
     * Resolve goods from vo goods.
     *
     * @return the goods
     */
    public Goods resolveGoodsFromVo() {
        Goods goods = new Goods();
        if (this.getMarkPrice() != null) {
            goods.setMarketPrice(this.getMarkPrice());
        }
        if (this.getPrice() != null) {
            goods.setPrice(this.getPrice());
        }
        if (this.getGoodsTitle() != null) {
            goods.setGoodsTitle(this.getGoodsTitle());
        }
        if (this.getGoodsName() != null) {
            goods.setGoodsName(this.getGoodsName());
        }
        if (this.getAppmodelId() != null) {
            goods.setAppmodelId(this.getAppmodelId());
        }
        if (this.getGoodsImg() != null) {
            goods.setGoodsImg(this.getGoodsImg());
        }
        return goods;
    }

    /**
     * Resolve goods detail from vo goods detail.
     *
     * @return the goods detail
     */
    public GoodsDetail resolveGoodsDetailFromVo() {
        GoodsDetail goodsDetail = new GoodsDetail();
        if (this.getProviderId() != null) {
            goodsDetail.setProviderId(this.getProviderId());
        }
        if (this.getShamVolume() != null) {
            goodsDetail.setShamVolume(this.getShamVolume());
        }
        if (this.getStock() != null) {
            goodsDetail.setStock(this.getStock());
        }
        if (this.getGoodsDesc() != null) {
            goodsDetail.setGoodsDesc(this.getGoodsDesc());
        }
        if (this.getText() != null) {
            goodsDetail.setText(this.getText());
        }
        if (this.getExpirationDate() != null) {
            goodsDetail.setExpirationDate(this.getExpirationDate());
        }
        if (this.getGroupLeaderCommission() != null) {
            goodsDetail.setGroupLeaderCommission(this.getGroupLeaderCommission());
        }
        if (this.getCommissionType() != null) {
            goodsDetail.setCommissionType(this.getCommissionType());
        }
        if (this.getAppmodelId() != null) {
            goodsDetail.setAppmodelId(this.getAppmodelId());
        }
        if (this.getGoodsProperty() != null) {
            goodsDetail.setGoodsProperty(this.getGoodsProperty());
        }
        return goodsDetail;
    }
}
