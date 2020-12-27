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
import org.jetbrains.annotations.NotNull;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * 商品分类实体类
 *
 * @author shuke
 */
@Data
@Table(name = "t_goods_class")
public class GoodsClass implements Comparable<GoodsClass> {
    /**
     * 商品分类id
     */
    @Id
    @Column(name = "goods_class_id")
    @GeneratedValue(generator = "JDBC")
    @ApiModelProperty(value = "商品分类id")
    private Long goodsClassId;

    /**
     * 商品分类名称
     */
    @Column(name = "goods_class_name")
    @ApiModelProperty(value = "商品分类名称")
    private String goodsClassName;

    /**
     * 父分类id
     */
    @Column(name = "father_id")
    @ApiModelProperty(value = "父分类id")
    private Long fatherId;

    /**
     * 创建时间
     */
    @Column(name = "create_time")
    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    /**
     * 创建时间
     */
    @Column(name = "update_time")
    @ApiModelProperty(value = "创建时间")
    private Date updateTime;

    /**
     * 小程序模块id
     */
    @Column(name = "appmodel_id")
    @ApiModelProperty(value = "小程序模块id")
    private String appmodelId;

    /**
     * 删除标志0：未删除 1删除
     */
    @Column(name = "del_flag")
    @ApiModelProperty(value = "删除标志0：未删除 1删除")
    private Integer delFlag;


    @ApiModelProperty(value = "排序值")
    private Integer sort;


    @Override
    public int compareTo(@NotNull GoodsClass o) {
        if ( o.getGoodsClassId() != null && this.getGoodsClassId() != null) {
            return (int) (this.getGoodsClassId() - o.getGoodsClassId());
        }
        return 0;
    }
}