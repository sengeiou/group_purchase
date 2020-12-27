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

package com.mds.group.purchase.logistics.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * The type Goods area vo.
 *
 * @author pavawi
 */
@Data
public class GoodsAreaVo {
    /**
     * id
     */
    private Long goodsAreaId;

    /**
     * 商品id
     */
    private Long goodsId;

    /**
     * 商品名称
     */
    private String goodsName;

    /**
     * 小区id
     */
    private List<Long> communityIds;

    /**
     * 投放区域名
     */
    private List<String> goodsAreaNames;

    /**
     * 小程序模板id
     */
    private String appmodelId;

    @ApiModelProperty(value = "新增小区时，是否要将该商品自动添加到该小区")
    private boolean autoAdd;
}
