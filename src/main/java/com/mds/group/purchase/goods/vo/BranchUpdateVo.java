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

package com.mds.group.purchase.goods.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * The type Branch update vo.
 *
 * @author shuke
 * @date 2018 -12-18
 */
@Data
public class BranchUpdateVo {

    @ApiModelProperty("商品id")
    private String  goodsIds;

    @ApiModelProperty("商品分类id")
    private String goodsClassIds;

    @NotNull
    @ApiModelProperty("操作类型1：批量删除 2：批量修改商品分类 3：批量上架 4：批量下架")
    private Integer type;

}
