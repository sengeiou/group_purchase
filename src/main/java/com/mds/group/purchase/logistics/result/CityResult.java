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

import com.mds.group.purchase.logistics.model.Cities;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * The type City result.
 *
 * @author pavawi
 */
@Data
public class CityResult {

    @ApiModelProperty(value = "id")
    private Integer id;

    @ApiModelProperty(value = "市编号")
    private String value;

    @ApiModelProperty(value = "市名称")
    private String label;

    @ApiModelProperty(value = "省编号")
    private String provinceid;

    @ApiModelProperty(value = "是否有下级")
    private Boolean isNode;

    /**
     * Instantiates a new City result.
     */
    public CityResult() {

    }

    /**
     * Instantiates a new City result.
     *
     * @param cities the cities
     */
    public CityResult(Cities cities) {
        this.setValue(cities.getCityid());
        this.setLabel(cities.getCity());
        this.setId(cities.getId());
        this.setProvinceid(cities.getProvinceid());
    }
}
