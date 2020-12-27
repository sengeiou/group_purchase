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

package com.mds.group.purchase.shop.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * The type Sort vo.
 *
 * @author Created by wx on 2018/05/25.
 */
@Data
public class SortVO {

    @ApiModelProperty(value = "模板id", hidden = true)
    private String appmodelId;

    @ApiModelProperty(value = "海报id")
    private Integer posterId;

    @ApiModelProperty(value = "商品分类id")
    private Long goodsClassId;

    @ApiModelProperty(value = "操作类型(1置顶 2上移 3下移 4置底)")
    @NotNull
    private Integer handleType;

}
