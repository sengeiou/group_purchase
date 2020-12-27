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

import java.util.Date;

/**
 * The type Wxuser manager vo.
 *
 * @author pavawi
 */
@Data
public class WxuserManagerVO {


    @ApiModelProperty(value = "用户id")
    private Long wxuserId;

    @ApiModelProperty(value = "用户昵称")
    private String wxuserName;

    @ApiModelProperty(value = "备注")
    private String remark;

    @ApiModelProperty(value = "注册时间")
    private Date createTime;

    @ApiModelProperty(value = "用户手机号")
    private String phone;

    @ApiModelProperty(value = "用户头像")
    private String icon;

    @ApiModelProperty(value = "0禁用  1:正常  2：团长")
    private Integer userStatus;

    @ApiModelProperty(value = "最后购买时间")
    private Date lastBuyTime;

    @ApiModelProperty(value = "所在小区id", hidden = true)
    private Long communityId;

    @ApiModelProperty(value = "所在小区名称")
    private String communityName;

    @ApiModelProperty(value = "所在小区地址")
    private String communityAddres;

    @ApiModelProperty(value = "消费金额")
    private Double consume;

    @ApiModelProperty(value = "购次")
    private Integer buySum;


}
