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

/**
 * The type Goods area mapping community result v 2.
 *
 * @author shuke
 * @date 2019 -2-25
 */
@Data
public class GoodsAreaMappingCommunityResultV2 {

    @ApiModelProperty(value = "小区id")
    private Long communityId;

    @ApiModelProperty(value = "小区名称")
    private String communityName;

    @ApiModelProperty(value = "该小区是否已经在投放区域中")
    private Boolean selected;
}
