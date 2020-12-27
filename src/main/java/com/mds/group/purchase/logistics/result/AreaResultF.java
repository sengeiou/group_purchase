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
 * The type Area result f.
 *
 * @author pavawi
 */
@Data
public class AreaResultF {

    @ApiModelProperty(value = "县区编号")
    private String value;

    @ApiModelProperty(value = "县区名称")
    private String label;

    @ApiModelProperty(value = "街道列表")
    private List<StreetsResultF> list;

    @ApiModelProperty(value = "市编号")
    private String cityid;
}
