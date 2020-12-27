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

import com.mds.group.purchase.logistics.model.Streets;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;


/**
 * The type Streets vo.
 *
 * @author pavawi
 */
@Data
public class StreetsVo {

    @ApiModelProperty(value = "id")
    private Long value;

    /**
     * 街道名称
     */
    @ApiModelProperty(value = "街道名称")
    private String label;

    /**
     * 所属县区编号
     */
    @NotNull
    @ApiModelProperty(value = "所属县区编号")
    private String areaid;

    /**
     * 小程序模板id
     */
    @ApiModelProperty(value = "小程序模板id")
    private String appmodelId;


    /**
     * Vo to obj streets.
     *
     * @return the streets
     */
    public Streets voToObj() {
        Streets streets = new Streets();
        if (this.value != null && this.value != 0) {
            streets.setStreetid(this.value);
        }
        if (this.areaid != null && !"".equals(this.areaid)) {
            streets.setAreaid(this.areaid);
        }
        if (this.label != null && !"".equals(this.label)) {
            streets.setStreet(this.label);
        }
        streets.setAppmodelId(appmodelId);
        return streets;
    }
}
