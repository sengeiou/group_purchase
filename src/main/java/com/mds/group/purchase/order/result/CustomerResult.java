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
 * The type Customer result.
 *
 * @author pavawi
 */
@Data
@ApiModel("'订单核销中的客户列表'")
public class CustomerResult {
    /**
     * 买家id
     */
    @ApiModelProperty(value = "买家id")
    private Long wxuserId;

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

    @ApiModelProperty(value = "待发货数量")
    private Integer waitSendNum;

    @ApiModelProperty(value = "待提货数量")
    private Integer waitPickNum;
}
