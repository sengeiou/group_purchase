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

package com.mds.group.purchase.user.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;

/**
 * The type Consignee.
 *
 * @author pavawi
 */
@Data
@Table(name = "t_consignee")
public class Consignee {
    /**
     * 收货人信息id
     */
    @Id
    @Column(name = "consignee_id")
    private Long consigneeId;

    @ApiModelProperty(value = "用户id")
    private Long wxuserId;


    @ApiModelProperty(value = "地区")
    private String area;


    @ApiModelProperty(value = "收货人电话")
    private String phone;

    @ApiModelProperty(value = "收货人姓名")
    @Column(name = "user_name")
    private String userName;


    @ApiModelProperty(value = "收货人地址")
    @NotBlank(message = "地址不能为空")
    private String address;


    @ApiModelProperty(value = "是否默认地址 1-默认  0-非默认 不修改传null")
    @Column(name = "default_adderss")
    private Boolean defaultAdderss;

    /**
     * 小程序模板id
     */
    @Column(name = "appmodel_id")
    @ApiModelProperty(hidden = true)
    private String appmodelId;

}