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

package com.mds.group.purchase.order.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 搜索团长签收单参数类
 *
 * @author shuke
 * @date 2018 -12-6
 */
@Data
public class SearchReceiptBillVO {
    @NotBlank
    @ApiModelProperty(value = "团长ID")
    private String groupLeaderId;

    @NotNull
    @ApiModelProperty(value = "类型：0.待签收签收单 1.历史签收单")
    private Integer type;

    @NotNull
    @ApiModelProperty(value = "当前页码，默认为0")
    private Integer page;

    @NotNull
    @ApiModelProperty(value = "页面数据数量，默认为0查询所有")
    private Integer size;

    @ApiModelProperty(value = "开始时间")
    private String startDate;

    @ApiModelProperty(value = "结束时间")
    private String endDate;
}
