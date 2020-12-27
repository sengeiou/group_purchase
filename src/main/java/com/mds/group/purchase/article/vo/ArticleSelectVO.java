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

package com.mds.group.purchase.article.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * The type Article select vo.
 *
 * @author pavawi
 */
@Data
public class ArticleSelectVO {

    @NotNull(message = "用户id不能为空")
    @ApiModelProperty(value = "用户id")
    private Long wxuserId;

    @NotBlank(message = "文章id不能为空")
    @ApiModelProperty(value = "文章id")
    private String articleId;

    @NotNull(message = "pageNum不能为空")
    @ApiModelProperty(value = "页数")
    @Min(value = 1, message = "pageNum不能小于1")
    private Integer pageNum;

    @NotNull(message = "pageSize不能为空")
    @ApiModelProperty(value = "条数")
    @Min(value = 1, message = "pageSize不能小于1")
    private Integer pageSize;
}
