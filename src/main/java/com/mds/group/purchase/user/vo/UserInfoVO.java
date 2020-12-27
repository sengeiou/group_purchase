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

import java.math.BigDecimal;

/**
 * The type User info vo.
 *
 * @author pavawi
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class UserInfoVO extends LoginResultVO {

    @ApiModelProperty(value = "用户昵称")
    private String wxuserName;

    @ApiModelProperty(value = "解密sessionkey")
    private String sessionKey;

    @ApiModelProperty(value = "用户头像")
    private String icon;

    @ApiModelProperty(value = "所在小区id")
    private Long communityId;

    @ApiModelProperty(value = "所在城市id")
    private String cityId;

    @ApiModelProperty(value = "城市名称")
    private String cityName;

    @ApiModelProperty(value = "用户核销码")
    private String receiptCode;

    @ApiModelProperty(value = "团长是否禁用  0-正常 1禁用")
    private Integer communityState;

    @ApiModelProperty(value = "小区名称")
    private String communityName;

    @ApiModelProperty(value = "团长佣金 userStatus==2的才返回")
    private BigDecimal brokerage;

    @ApiModelProperty(value = "接龙活动是否开启")
    private Boolean solitaireOpen;

    @ApiModelProperty(value = "接龙记录是否开启")
    private Boolean solitaireRecordOpen;

}
