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
import java.math.BigDecimal;
import java.util.List;

/**
 * The type Group backstage vo.
 *
 * @author whh
 */
@Data
public class GroupBackstageVO implements Serializable {

    @ApiModelProperty(value = "用户昵称")
    private String wxuserName;

    @ApiModelProperty(value = "用户id")
    private Long wxuserId;

    @ApiModelProperty(value = "用户头像")
    private String icon;

//	@ApiModelProperty(value = "用户批注")
//	private String postil;

    @ApiModelProperty(value = "用户地址")
    private String userAdders;

    @ApiModelProperty(value = "用户订单")
    private List<GroupOrderVO> groupOrderVOList;

    @ApiModelProperty(value = "总订单金额")
    private BigDecimal orderTotlePayfee;


}
