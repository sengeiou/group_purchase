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

import javax.validation.constraints.NotBlank;

/**
 * The type Set category vo.
 *
 * @author Created by wx on 2018/06/07.
 */
@Data
public class SetCategoryVO {

    @ApiModelProperty(value = "选择的文章id  逗号分隔")
    @NotBlank(message = "文章id不能为空")
    private String articleIds;

    @ApiModelProperty(value = "所有的文章有要的分类")
    @NotBlank(message = "所有的文章有要的分类不能为空")
    private String entirelyIncludeCategoryIds;

    @ApiModelProperty(value = "完全排除的分类")
    @NotBlank(message = "完全排除的分类不能为空")
    private String entirelyExcludeCategoryIds;

    @ApiModelProperty(value = "商家appmodelId")
    @NotBlank(message = "商家appmodelId不能为空")
    private String appmodelId;
}
