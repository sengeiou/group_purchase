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

import javax.validation.constraints.NotNull;

/**
 * The type Withdraw money vo.
 *
 * @author pavawi
 */
@Data
public class WithdrawMoneyVO {

    /**
     * The Constant OK.
     */
    public final static int OK = 1;
    /**
     * The Constant REFUSE.
     */
    public final static int REFUSE = 2;

    @ApiModelProperty(value = "团长余额提现id")
    @NotNull
    private Long groupBpavawiceOrderId;

    @ApiModelProperty(value = "操作类型  optionType 1-同意  2-拒绝")
    @NotNull
    private Integer optionType;

    @ApiModelProperty(value = "手机号", hidden = true)
    private String phone;
    @ApiModelProperty(value = "验证码", hidden = true)
    private String code;
    @ApiModelProperty(value = "appmodelId", hidden = true)
    private String appmodelId;


}
