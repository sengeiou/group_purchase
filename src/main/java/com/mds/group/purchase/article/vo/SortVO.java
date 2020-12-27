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

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

/**
 * The type Sort vo.
 *
 * @author Created by wx on 2018/06/07.
 */
@Data
public class SortVO {

    @ApiModelProperty(value = "模板id")
    private String appmodelId;

    @ApiModelProperty(value = "海报id")
    @NotBlank(message = "海报id不能为空")
    private String articleId;

    @ApiModelProperty(value = "操作类型(1置顶 2上移 3下移 4置底)")
    @Max(message = "最大值为4",value = 4)
    @Min(message = "最小值为1",value = 1)
    private Integer handleType;
}
