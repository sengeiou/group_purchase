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

package com.mds.group.purchase.logistics.result;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * The type Goods area mapping line result v 2.
 *
 * @author shuke
 * @date 2018 -2-25
 */
@Data
public class GoodsAreaMappingLineResultV2 {

    /**
     * 线路id
     */
    @ApiModelProperty(value = "线路id")
    private Long lineId;

    /**
     * 线路名称
     */
    @ApiModelProperty(value = "线路名称")
    private String lineName;

    /**
     * 区域（街道）集合
     */
    @ApiModelProperty(value = "区域（街道）集合")
    private List<GoodsAreaMappingStreetResultV2> streetList;

    /**
     * 小程序模板id
     */
    @ApiModelProperty(value = "小程序模板id")
    private String appmodelId;
}
