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

package com.mds.group.purchase.shop.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * The type Poster.
 *
 * @author Created by wx on 2018/05/25.
 */
@Table(name = "t_poster")
@Data
public class Poster {

    @ApiModelProperty(value = "轮播图编号")
    @Id
    @Column(name = "poster_id")
    private Integer posterId;

    @ApiModelProperty(value = "轮播图跳转类型（0-不转跳 1-跳转到商品，2-跳转到发现 3-跳转到申请团长 4-跳转到资源入住）")
    @Column(name = "jump_type")
    private Integer jumpType;

    @ApiModelProperty(value = "轮播图图片")
    @Column(name = "poster_img")
    private String posterImg;

    @ApiModelProperty(value = "创建时间")
    @Column(name = "create_time")
    private Date createTime;

    @ApiModelProperty(value = "跳转地址")
    @Column(name = "target_url")
    private String targetUrl;

    @ApiModelProperty(value = "模板id", hidden = true)
    @Column(name = "appmodel_id")
    private String appmodelId;

    @ApiModelProperty(value = "排序值")
    @Column(name = "sort")
    private Integer sort;

    @ApiModelProperty(value = "活动商品id")
    @Column(name = "activity_goods_id")
    private Long activityGoodsId;

    @ApiModelProperty(value = "链接名称")
    @Column(name = "target_name")
    private String targetName;

    @ApiModelProperty(value = "文章id")
    @Column(name = "article_id")
    private String articleId;

    @ApiModelProperty(value = "商品分类id", hidden = true)
    @Column(name = "category_id")
    private Long categoryId;


}