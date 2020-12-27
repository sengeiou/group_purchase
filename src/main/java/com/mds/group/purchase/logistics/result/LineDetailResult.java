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

import com.mds.group.purchase.logistics.model.LineDetail;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * The type Line detail result.
 *
 * @author pavawi
 */
@Data
public class LineDetailResult {

    /**
     * 区域名称
     */
    @ApiModelProperty(value = "区域名称")
    private String streetName;

    /**
     * 区域id
     */
    @ApiModelProperty(value = "区域id")
    private Long streetId;

    /**
     * 线路详情的集合
     */
    @ApiModelProperty("线路详情的集合")
    private List<LineDetail> lineDetails;

    /**
     * 线路id
     */
    @ApiModelProperty(value = "线路id")
    private Long lineId;
}
