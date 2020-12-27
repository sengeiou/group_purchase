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

package com.mds.group.purchase.logistics.result;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * The type Streets result.
 *
 * @author pavawi
 */
@Data
public class StreetsResult {
//    @ApiModelProperty(value = "id")
//    private Long id;

    /**
     * 街道编号
     */
    @ApiModelProperty(value = "街道编号")
    private Long value;

    /**
     * 街道名称
     */
    @ApiModelProperty(value = "街道名称")
    private String label;

    /**
     * 所属县区编号
     */
    @ApiModelProperty(value = "所属县区编号")
    private String areaid;

    /**
     * 区域下的所有小区
     */
    @ApiModelProperty("区域下的所有小区")
    private List<Community4GroupApply> list;
}
