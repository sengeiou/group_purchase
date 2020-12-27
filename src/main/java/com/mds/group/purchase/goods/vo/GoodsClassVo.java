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

import java.util.List;

/**
 * 新增或更新商品分类的参数类
 *
 * @author shuke
 */
@Data
public class GoodsClassVo {

    /**
     * 商品分类id（新增时传入-1标识新增，传入id则标识修改）
     */
    private Long goodsClassId;

    /**
     * 商品分类名称
     */
    @ApiModelProperty(value = "商品分类名称")
    private String goodsClassName;

    /**
     * 父分类id
     */
    @ApiModelProperty(value = "父分类id(一级分类的父级id是0，默认为0)")
    private Long fatherId;

    private List<ClassTwoVo> classTwos;

}
