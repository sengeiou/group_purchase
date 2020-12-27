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

package com.mds.group.purchase.logistics.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * The type Line.
 *
 * @author pavawi
 */
@Table(name = "t_line")
@Data
public class Line {
    /**
     * 线路id
     */
    @Id
    @Column(name = "line_id")
    @GeneratedValue(generator = "JDBC")
    @ApiModelProperty(value = "线路id")
    private Long lineId;

    /**
     * 线路名称
     */
    @Column(name = "line_name")
    @ApiModelProperty(value = "线路名称")
    private String lineName;

    /**
     * 司机名称
     */
    @Column(name = "driver_name")
    @ApiModelProperty(value = "司机名称")
    private String driverName;

    /**
     * 司机电话
     */
    @Column(name = "driver_phone")
    @ApiModelProperty(value = "司机电话")
    private String driverPhone;

    /**
     * 省id
     */
    @Column(name = "province_id")
    @ApiModelProperty(value = "省id")
    private String provinceId;

    /**
     * 市id
     */
    @Column(name = "city_id")
    @ApiModelProperty(value = "省id")
    private String cityId;

    /**
     * 县区id
     */
    @Column(name = "area_id")
    @ApiModelProperty(value = "县区id")
    private String areaId;

    /**
     * 删除标志
     */
    @Column(name = "del_flag")
    @ApiModelProperty(value = "删除标志")
    private Integer delFlag;

    /**
     * 小程序模板id
     */
    @Column(name = "appmodel_id")
    @ApiModelProperty(value = "小程序模板id")
    private String appmodelId;

}