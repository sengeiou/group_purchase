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

import com.mds.group.purchase.logistics.model.Line;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * 新建线路参数类
 *
 * @author shuke
 * @since v1.2
 */
@Data
public class LineV12Vo {

    @ApiModelProperty(value = "线路id")
    private Long lineId;

    @ApiModelProperty(value = "线路名称")
    @NotNull
    private String lineName;

    @ApiModelProperty(value = "司机名称")
    @NotNull
    private String driverName;

    @ApiModelProperty(value = "司机电话")
    @NotNull
    private String driverPhone;

    @ApiModelProperty(value = "小程序模板id")
    private String appmodelId;

    @ApiModelProperty(value = "省id")
    @NotNull
    private String provinceId;

    @ApiModelProperty(value = "市id")
    @NotNull
    private String cityId;

    @ApiModelProperty(value = "县区id")
    @NotNull
    private String areaId;

    /**
     * Vo to line line.
     *
     * @return the line
     */
    public Line voToLine() {
        Line line = new Line();
        if (this.getLineId() != null) {
            line.setLineId(this.getLineId());
        }
        line.setAppmodelId(this.getAppmodelId());
        line.setDriverName(this.getDriverName());
        line.setDriverPhone(this.getDriverPhone());
        line.setLineName(this.getLineName());
        line.setProvinceId(this.provinceId);
        line.setCityId(this.cityId);
        line.setAreaId(this.areaId);
        return line;
    }
}
