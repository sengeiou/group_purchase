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
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * The type Group apply register v 119 vo.
 *
 * @author Administrator
 */
@Data
public class GroupApplyRegisterV119VO {

    @ApiModelProperty(value = "用户id")
    @NotNull
    private Long wxuserId;

    @ApiModelProperty(value = "团长姓名")
    @NotBlank
    private String groupName;

    @ApiModelProperty(value = "团长电话")
    @NotBlank
    private String groupPhone;

    @ApiModelProperty(value = "团长的地址定位")
    @NotBlank
    private String groupLocation;

    @ApiModelProperty(value = "申请的小区名称")
    @NotBlank
    @Length(min = 1, max = 28, message = "小区名不能大于28个字符")
    private String communityName;

    @ApiModelProperty(value = "取货地址")
    @NotBlank
    private String pickUpAddr;

    @ApiModelProperty(value = "发送模板消息所需formId", hidden = true)
    private String formId;

    private String appmodelId;
}
