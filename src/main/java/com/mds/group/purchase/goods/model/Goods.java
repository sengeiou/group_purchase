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
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.util.Date;


/**
 * 商品实体类
 *
 * @author shuke
 */
@Data
@Table(name = "t_goods")
public class Goods {
    /**
     * 商品id
     */
    @Id
    @Column(name = "goods_id")
    @GeneratedValue(generator = "JDBC")
    @ApiModelProperty(value = "商品id")
    private Long goodsId;

    /**
     * 商品标题
     */
    @Column(name = "goods_title")
    @ApiModelProperty(value = "商品标题")
    private String goodsTitle;

    /**
     * 商品名称
     */
    @Column(name = "goods_name")
    @ApiModelProperty(value = "商品名称")
    private String goodsName;

    /**
     * 商品删除标识（1：删除，0：正常）
     */
    @Column(name = "goods_del_flag")
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
    @Column(name = "market_price")
    @ApiModelProperty(value = "市场价（划线价）")
    private BigDecimal marketPrice;

    /**
     * 状态(默认上架，0--上架，1--下架（仓库中），2--已售完)
     */
    @Column(name = "goods_status")
    @ApiModelProperty(value = "状态(默认上架，0--上架，1--下架（仓库中），2--已售完)")
    private Integer goodsStatus;

    /**
     * 创建时间
     */
    @Column(name = "create_time")
    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    /**
     * 小程序模块id
     */
    @Column(name = "appmodel_id")
    @ApiModelProperty(value = "小程序模板id")
    private String appmodelId;

    /**
     * 商品图片
     */
    @Column(name = "goods_img")
    @ApiModelProperty(value = "商品图片地址，多个地址用逗号分隔")
    private String goodsImg;

    /**
     * 商品主图视频url
     */
    @Column(name = "goods_video_url")
    @ApiModelProperty(value = "商品主图视频url")
    private String goodsVideoUrl;

}