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

import com.mds.group.purchase.logistics.model.Line;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * The type Line result.
 *
 * @author pavawi
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class LineResult extends Line implements Comparable<LineResult> {

    @ApiModelProperty(value = "线路详情结果对象集合")
    private List<LineDetailResult> lineDetailResults;

    /**
     * Instantiates a new Line result.
     *
     * @param line the line
     */
    public LineResult(Line line) {
        setAppmodelId(line.getAppmodelId());
        setAreaId(line.getAreaId());
        setCityId(line.getCityId());
        setDriverName(line.getDriverName());
        setDriverPhone(line.getDriverPhone());
        setLineId(line.getLineId());
        setLineName(line.getLineName());
        setProvinceId(line.getProvinceId());
    }

    /**
     * Instantiates a new Line result.
     */
    public LineResult() {
    }

    @Override
    public int compareTo(@NotNull LineResult o) {
        if (this.getLineId() != null && o.getLineId() != null) {
            return (this.getLineId().intValue() - o.getLineId().intValue());
        }
        return 0;
    }
}
