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

/**
 * 更新商品投放区域参数
 *
 * @author shuke
 */
@Data
public class UpdateGoodsAreaVo {

    @ApiModelProperty(value = "商品id")
    private String goodsIds;

    @ApiModelProperty(value = "小程序模板id")
    private String appmodelId;

    @ApiModelProperty(value = "要投放的小区的id，中间用逗号,隔开。当传空字符串时，投放到所有小区。默认投放所有小区")
    private String communityIds;

    @ApiModelProperty(value = "新增小区时，是否要将该商品自动添加到该小区")
    private boolean autoAdd;

}
