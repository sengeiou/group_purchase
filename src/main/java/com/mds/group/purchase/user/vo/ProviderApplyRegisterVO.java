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

package com.mds.group.purchase.user.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * The type Provider apply register vo.
 *
 * @author pavawi
 */
@Data
public class ProviderApplyRegisterVO {

    @ApiModelProperty(value = "供应商名称")
    private String providerName;

    @ApiModelProperty(value = "供应商电话")
    private String providerPhone;

    @ApiModelProperty(value = "用户id")
    private String wxuserId;

    @ApiModelProperty(value = "产品类目")
    private String goodsClass;

    @ApiModelProperty(value = "商家appmodelId", hidden = true)
    private String appmodelId;

}
