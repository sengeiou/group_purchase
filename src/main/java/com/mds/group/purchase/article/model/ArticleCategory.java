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

package com.mds.group.purchase.article.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.data.annotation.Id;

/**
 * The type Article category.
 *
 * @author Created by wx on 2018/06/07.
 */
@Data
public class ArticleCategory {

    @Id
    @ApiModelProperty(value = "分类id")
    private String categoryId;

    @ApiModelProperty(value = "分类名")
    private String categoryName;

    @ApiModelProperty(value = "分类图片")
    private String categoryImg;

    @ApiModelProperty(value = "分类类型(0-系统添加无法删除 1-商家添加可删除)")
    private Integer categoryType;

    @ApiModelProperty(value = "删除标示")
    private Integer deleteState;

    @ApiModelProperty(value = "模板Id")
    private String appmodelId;

}
