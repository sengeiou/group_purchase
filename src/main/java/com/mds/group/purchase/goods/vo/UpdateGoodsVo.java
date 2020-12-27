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


import com.mds.group.purchase.exception.ServiceException;
import com.mds.group.purchase.goods.model.Goods;
import com.mds.group.purchase.goods.model.GoodsDetail;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 更新商品的参数类
 *
 * @author shuke
 */
@Data
public class UpdateGoodsVo {

    @ApiModelProperty(value = "商品id")
    private Long goodsId;

    @ApiModelProperty(value = "商品名称")
    private String goodsName;

    @ApiModelProperty(value = "商品标题")
    private String goodsTitle;

    @ApiModelProperty(value = "商品描述")
    private String goodsDesc;

    @ApiModelProperty(value = "供应商id")
    private String providerId;

    @ApiModelProperty(value = "商品分类id")
    private Long[] goodsClassIds;

    @ApiModelProperty(value = "手动虚假销量")
    private Integer shamVolume;

    @ApiModelProperty(value = "商品删除标识")
    private Boolean goodsDelFlag;

    @ApiModelProperty(value = "商品状态")
    private Integer goodsStatus;

    @ApiModelProperty(value = "市场价（划线价）")
    private BigDecimal markPrice;

    @ApiModelProperty(value = "销售价")
    private BigDecimal price;

    @ApiModelProperty(value = "商品图片地址，多个地址用逗号分隔")
    private String goodsImg;

    @ApiModelProperty(value = "库存")
    private Integer stock;

    @ApiModelProperty(value = "商品详情id")
    private Long goodsDetailId;

    @ApiModelProperty(value = "商品销量")
    private Integer salesVolume;

    @ApiModelProperty(value = "商品属性，json字符串")
    private String goodsProperty;

    @ApiModelProperty(value = "保质期")
    private String expirationDate;

    @ApiModelProperty(value = "团长佣金")
    private BigDecimal groupLeaderCommission;

    @ApiModelProperty(value = "团长佣金类型（1：比率 2：固定金额）")
    private Integer commissionType;

    @ApiModelProperty(value = "文本介绍")
    private String text;

    @ApiModelProperty(value = "小程序模板id")
    private String appmodelId;

    @ApiModelProperty(value = "要投放的小区的id，中间用逗号,隔开。")
    private String communityIds;

    @ApiModelProperty(value = "新增小区时，是否要将该商品自动添加到该小区")
    private boolean autoAdd;

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

    /**
     * 从this提取goods对象
     *
     * @return goods
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
        if (this.getGoodsId() != null) {
            goods.setGoodsId(this.getGoodsId());
        }
        if (this.getGoodsDelFlag() != null) {
            goods.setGoodsDelFlag(this.getGoodsDelFlag());
        }
        if (this.getGoodsStatus() != null) {
            goods.setGoodsStatus(this.getGoodsStatus());
        }
        if (this.getGoodsImg() != null) {
            goods.setGoodsImg(this.getGoodsImg());
        }
        if (this.getCommissionType().equals(1) && this.getGroupLeaderCommission().doubleValue() > 100) {
            throw new ServiceException("佣金比例不能超出100%");
        }
        if (this.getCommissionType().equals(2) && this.getPrice().doubleValue() < this
                .getGroupLeaderCommission().doubleValue()) {
            throw new ServiceException("固定金额不能大于商品金额");
        }
        return goods;
    }


    /**
     * 从this提取goodsDetail对象
     *
     * @return 商品详情 goods detail
     */
    public GoodsDetail resolveGoodsDetailFromVo() {
        GoodsDetail goodsDetail = new GoodsDetail();
        if (this.getGoodsDetailId() != null) {
            goodsDetail.setGoodsDetailId(this.getGoodsDetailId());
        }
        if (this.getGoodsId() != null) {
            goodsDetail.setGoodsId(this.getGoodsId());
        }
        if (this.getProviderId() != null) {
            goodsDetail.setProviderId(this.getProviderId());
        }
        if (this.getSalesVolume() != null) {
            goodsDetail.setSalesVolume(this.getSalesVolume());
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
        if (this.getText() != null && !"".equalsIgnoreCase(this.getText())) {
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
        if (this.getGoodsProperty() != null && !"".equalsIgnoreCase(this.getGoodsProperty())) {
            goodsDetail.setGoodsProperty(this.getGoodsProperty());
        }
        return goodsDetail;
    }
}
