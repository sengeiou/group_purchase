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

package com.mds.group.purchase.order.result;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * The type Members result.
 *
 * @author pavawi
 */
@Data
@ApiModel("查看团员")
public class MembersResult {
    /**
     * 买家姓名
     */
    @ApiModelProperty(value = "买家姓名")
    private String buyerName;

    /**
     * 买家电话
     */
    @ApiModelProperty(value = "买家电话")
    private String buyerPhone;

    /**
     * 买家地址
     */
    @ApiModelProperty(value = "买家地址")
    private String buyerAddress;

    /**
     * 买家头像
     */
    @ApiModelProperty(value = "买家头像")
    private String wxuserIcon;

    /**
     * 买家昵称
     */
    @ApiModelProperty(value = "买家昵称")
    private String wxuserName;
    /**
     * 买家昵称
     */
    @ApiModelProperty(value = "买家ID")
    private String wxuserId;

}
