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
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * The type Provider apply vo.
 *
 * @author whh
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ProviderApplyVO extends ProviderApplyRegisterVO {

    //操作类型
    /**
     * 同意
     */
    public final static int OK = 1;
    /**
     * 拒绝
     */
    public final static int REFUSE = 0;
    /**
     * 新增
     */
    public final static int ADD = 2;


    @ApiModelProperty(value = "id,optionTpye=2时id传0")
    @NotBlank
    private String id;

    @ApiModelProperty(value = "操作类型 0-拒绝 1-同意 2-新增")
    @NotNull
    private Integer optionType;

    @ApiModelProperty(value = "供应商地区   怎么存怎么取只做显示用")
    private String providerArea;

    @ApiModelProperty(value = "供应商地址")
    private String providerAddress;
}
