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

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;


/**
 * The type Group apply for vo.
 *
 * @author pavawi
 */
@Data
public class GroupApplyForVO {

    @ApiModelProperty(value = "用户id")
    @NotNull
    private Long wxuserId;

    @ApiModelProperty(value = "申请者姓名")
    @NotBlank
    private String groupName;

    @ApiModelProperty(value = "申请者电话")
    @NotBlank
    private String groupPhone;

    @ApiModelProperty(value = "线路id")
    private Long lineId;

    @ApiModelProperty(value = "申请的小区id")
    @NotNull
    private Long communityId;

    @ApiModelProperty(value = "配送小区")
    @NotNull
    private String address;

    @ApiModelProperty(value = "团长id")
    private String groupLeaderId;

    @ApiModelProperty(value = "操作类型  1-申请  2-新增|修改")
    @NotNull
    @Max(2)
    @Min(1)
    private Integer optionType;
    @ApiModelProperty(value = "小程序模板id", hidden = true)
    private String appmodelId;
    @ApiModelProperty(value = "发送模板消息所需formId", hidden = true)
    private String formId;

    /**
     * The interface Option type.
     *
     * @author pavawi
     */
    public interface OptionType {
        //操作类型
        /**
         * 申请
         */
        int APPLY = 1;
        /**
         * 新增/修改
         */
        int ADDORMODIFY = 2;
    }

}
