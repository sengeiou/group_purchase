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

/**
 * 商品分类映射实体类
 *
 * @author shuke
 */
@Data
@Table(name = "t_goods_class_mapping")
public class GoodsClassMapping {
    /**
     * 商品与商品分类对应表
     */
    @Id
    @Column(name = "goods_class_mapping_id")
    @ApiModelProperty(value = "商品与商品分类对应表")
    private Long goodsClassMappingId;

    /**
     * 商品id
     */
    @Column(name = "goods_id")
    @ApiModelProperty(value = "商品id")
    private Long goodsId;

    /**
     * 商品分类id
     */
    @Column(name = "goods_class_id")
    @ApiModelProperty(value = "商品分类id")
    private Long goodsClassId;

    /**
     * 小程序模板id
     */
    @Column(name = "appmodel_id")
    @ApiModelProperty(value = "小程序模块id")
    private String appmodelId;
}