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

package com.mds.group.purchase.order.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * The type Line sorting order goods.
 *
 * @author pavawi
 */
@Table(name = "t_line_sorting_order_goods")
@Data
public class LineSortingOrderGoods {

    @Id
    @Column(name = "line_sorting_order_goods_id")
    @ApiModelProperty(value = "线路分拣单商品id")
    private Integer lineSortingOrderGoodsId;


    @ApiModelProperty(value = "线路分拣单id")
    @Column(name = "line_sorting_order_id")
    private Integer lineSortingOrderId;


    @Column(name = "activity_id")
    @ApiModelProperty(value = "活动id")
    private Long activityId;

    @Column(name = "activity_type")
    @ApiModelProperty(value = "活动类型")
    private Integer activityType;

    @Column(name = "activity_name")
    @ApiModelProperty(value = "活动名称")
    private String activityName;


    @Column(name = "goods_image")
    @ApiModelProperty(value = "商品图片")
    private String goodsImage;

    @Column(name = "goods_id")
    @ApiModelProperty(value = "商品id")
    private Long goodsId;

    @Column(name = "act_goods_price")
    @ApiModelProperty(value = "活动商品卖出价格")
    private String actGoodsPrice;

    @Column(name = "act_goods_id")
    @ApiModelProperty(value = "活动商品id")
    private String actGoodsId;

    @Column(name = "goods_name")
    @ApiModelProperty(value = "商品名称")
    private String goodsName;

    @Column(name = "appmodel_id")
    @ApiModelProperty(value = "商家标示")
    private String appmodelId;


    @Column(name = "goods_totle_sum")
    @ApiModelProperty(value = "商品总数量")
    private Integer goodsTotleSum;


}