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

package com.mds.group.purchase.logistics.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 线路中的小区添加/更改参数类
 *
 * @author shuke
 * @since v1.2
 */
@Data
public class LineAddCommunityV12Vo {

    @ApiModelProperty(value = "线路id")
    private Long lineId;

    @ApiModelProperty(value = "小程序模板id")
    private String appmodelId;

    @ApiModelProperty(value = "小区id集合")
    @NotNull
    private List<Long> communities;
}
