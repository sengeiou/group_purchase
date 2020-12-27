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

import com.mds.group.purchase.logistics.model.Provinces;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * The type Provinces result.
 *
 * @author pavawi
 */
@Data
public class ProvincesResult {

    @ApiModelProperty(value = "id")
    private Integer id;

    @ApiModelProperty(value = "省编号")
    private String value;

    @ApiModelProperty(value = "省名称")
    private String label;

    @ApiModelProperty(value = "是否有下级")
    private Boolean isNode;

    /**
     * Instantiates a new Provinces result.
     *
     * @param p the p
     */
    public ProvincesResult(Provinces p) {
        this.setId(p.getId());
        this.setValue(p.getProvinceid());
        this.setLabel(p.getProvince());
    }

    /**
     * Instantiates a new Provinces result.
     */
    public ProvincesResult() {

    }
}
