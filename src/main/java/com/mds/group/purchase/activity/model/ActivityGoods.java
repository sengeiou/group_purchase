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

package com.mds.group.purchase.activity.model;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;

/**
 * 活动商品实体类
 *
 * @author shuke on 2018-12-4
 */
@Data
@Table(name = "t_activity_goods")
public class ActivityGoods {

    /**
     * 活动商品id
     */
    @Id
    @Column(name = "activity_goods_id")
    private Long activityGoodsId;

    /**
     * 商品id
     */
    @Column(name = "goods_id")
    private Long goodsId;

    /**
     * 活动id
     */
    @Column(name = "activity_id")
    private Long activityId;

    /**
     * 活动类型，1秒杀 2拼团
     */
    @Column(name = "activity_type")
    private Integer activityType;

    /**
     * 活动折扣
     */
    @Column(name = "activity_discount")
    private Double activityDiscount;

    /**
     * 活动价格
     * @since v1.2
     */
    @Column(name = "activity_price")
    private BigDecimal activityPrice;

    /**
     * 活动库存
     */
    @Column(name = "activity_stock")
    private Integer activityStock;

    /**
     * 活动销量
     */
    @Column(name = "activity_sales_volume")
    private Integer activitySalesVolume;

    /**
     * 限购
     */
    @Column(name = "max_quantity")
    private Integer maxQuantity;

    /**
     * 是否加入接龙活动
     * @since v1.2.3
     */
    @Column(name = "join_solitaire")
    private Boolean joinSolitaire;

    /**
     * 是否在主页显示
     */
    @Column(name = "index_display")
    private Boolean indexDisplay;

    /**
     * 活动商品排序位置
     */
    @Column(name = "sort_position")
    private Integer sortPosition;

    /**
     * 活动商品的状态（0:不能购买，不能展示 1:预热状态 2:活动开始可购买）
     */
    @Column(name = "preheat_status")
    private Integer preheatStatus;

    /**
     * 小程序模块id
     */
    @Column(name = "appmodel_id")
    private String appmodelId;

    /**
     * 逻辑删除标识（0、未删除  1、已删除）
     */
    @Column(name = "del_flag")

    private Integer delFlag;
    /**
     * 下架状态（0、未下架  1、已下架）
     */
    @Column(name = "sold_out_flag")
    private Integer soldOutFlag;
}