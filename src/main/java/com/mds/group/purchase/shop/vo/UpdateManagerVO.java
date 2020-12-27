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

package com.mds.group.purchase.shop.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * The type Update manager vo.
 *
 * @author pavawi
 */
@Data
public class UpdateManagerVO {

    @ApiModelProperty(value = "小程序appid")
    private String appid;
    @ApiModelProperty(value = "证书路径")
    private String certificatePath;
    @ApiModelProperty(value = "商户号")
    private String mchId;
    @ApiModelProperty(value = "秘钥")
    private String mchKey;

}
