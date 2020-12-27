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
 * The type Provider manager vo.
 *
 * @author pavawi
 */
@Data
public class ProviderManagerVO {


    @ApiModelProperty(value = "ID")
    private String providerId;

    @ApiModelProperty(value = "供应商名称")
    private String providerName;

    @ApiModelProperty(value = "供应商电话")
    private String providerPhone;

    @ApiModelProperty(value = "供应商地址")
    private String providerAddress;

    @ApiModelProperty(value = "产品类目   怎么存怎么取")
    private String goodsClass;

    @ApiModelProperty(value = "申请时间")
    private Date createTime;

    @ApiModelProperty(value = "状态（0：禁用，1：正常）")
    private Integer providerStatus;

    @ApiModelProperty(value = "审核状态 0-待审核   1-已通过  2-已拒绝")
    private Integer applyState;

    @ApiModelProperty(value = "评分")
    private String grade;

    @ApiModelProperty(value = "地区   (怎么存怎么取)")
    private String providerArea;


}
