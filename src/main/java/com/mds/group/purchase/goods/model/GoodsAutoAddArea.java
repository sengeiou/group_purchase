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

import lombok.Data;

import javax.persistence.*;

/**
 * 商品自动投放实体类
 *
 * @author shuke
 */
@Table(name = "t_goods_auto_add_area")
@Data
public class GoodsAutoAddArea {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 商品id
     */
    @Column(name = "goods_id")
    private Long goodsId;

    /**
     * 商品是否自动加入新建小区（0：不加人  1：加入）
     */
    @Column(name = "auto_add")
    private Boolean autoAdd;

    /**
     * 小程序模板ID
     */
    @Column(name = "appmodel_id")
    private String appmodelId;

    /**
     * 商品名称
     */
    @Column(name = "goods_name")
    private String goodsName;

}