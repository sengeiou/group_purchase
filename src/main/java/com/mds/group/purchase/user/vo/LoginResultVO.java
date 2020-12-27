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

import java.io.Serializable;

/**
 * The type Login result vo.
 *
 * @author pavawi
 */
@Data
public class LoginResultVO implements Serializable {

    private static final long serialVersionUID = 5996486360130776851L;

    @ApiModelProperty(value = "用户id")
    private Long wxuserId;

    @ApiModelProperty(value = "0禁用  1:正常")
    private Integer userStatus;

    @ApiModelProperty(value = "用户团长身份是否禁用 userStatus==2的才返回  1-正常 2-禁用")
    private Integer groupState;

    @ApiModelProperty(value = "用户团长id userStatus==2的才返回")
    private String groupLeaderId;


}
