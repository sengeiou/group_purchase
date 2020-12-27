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
 * The type Goods area mapping street result v 2.
 *
 * @author shuke
 * @date 2019 -2-25
 */
@Data
public class GoodsAreaMappingStreetResultV2 {

    @ApiModelProperty(value = "街道id")
    private Long streetId;

    @ApiModelProperty(value = "街道名称")
    private String streetName;

    @ApiModelProperty(value = "该街道下已经在投放区域的小区数量")
    private Integer selectedNum;

    @ApiModelProperty(value = "该街道下所有可被选中的小区数量")
    private Integer totalNum;

    @ApiModelProperty(value = "包含的小区集合")
    private List<GoodsAreaMappingCommunityResultV2> communityList;
}
