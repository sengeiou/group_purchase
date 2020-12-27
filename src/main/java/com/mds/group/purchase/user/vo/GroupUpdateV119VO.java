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

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * The type Group update v 119 vo.
 *
 * @author pavawi
 */
@Data
public class GroupUpdateV119VO {

    private String id;

    private String groupPhone;

    @ApiModelProperty(value = "线路id")
    private Long lineId;

    @ApiModelProperty(value = "申请者姓名")
    @NotBlank
    private String groupName;

    @ApiModelProperty(value = "小区名称")
    @NotBlank
    private String communityName;

    @ApiModelProperty(value = "取货地址")
    @NotBlank
    private String pickUpAddr;

    @ApiModelProperty(value = "小程序模板id", hidden = true)
    private String appmodelId;

    @ApiModelProperty(value = "街道id")
    @NotNull
    private Long streetId;

    @ApiModelProperty(value = "县区id")
    @NotNull
    private String areaId;

    @ApiModelProperty(value = "市id")
    @NotNull
    private String cityId;

    @ApiModelProperty(value = "省id")
    @NotNull
    private String provinceId;

    @ApiModelProperty(value = "所属的省市县名称")
    private String pcaAdr;

    @ApiModelProperty(value = "所属的区域名称")
    private String streetName;

    @ApiModelProperty(value = "经纬度")
    private String location;

}
