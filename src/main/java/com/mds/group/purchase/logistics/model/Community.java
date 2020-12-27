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
 * The type Community.
 *
 * @author pavawi
 */
@Table(name = "t_community")
@Data
public class Community {
    /**
     * 小区id
     */
    @Id
    @Column(name = "community_id")
    @GeneratedValue(generator = "JDBC")
    @ApiModelProperty(value = "小区id")
    private Long communityId;

    /**
     * 小区名称
     */
    @Column(name = "community_name")
    @ApiModelProperty(value = "小区名称")
    private String communityName;

    /**
     * 所属街道id
     */
    @Column(name = "street_id")
    @ApiModelProperty(value = "街道编号")
    private Long streetId;

    /**
     * 县id
     */
    @Column(name = "area_id")
    @ApiModelProperty(value = "县区编号")
    private String areaId;

    /**
     * 市id
     */
    @Column(name = "city_id")
    @ApiModelProperty(value = "市编号")
    private String cityId;

    /**
     * 省id
     */
    @Column(name = "province_id")
    @ApiModelProperty(value = "省编号")
    private String provinceId;

    /**
     * 小程序模板id
     */
    @Column(name = "appmodel_id")
    @ApiModelProperty(value = "小程序模板id")
    private String appmodelId;

    /**
     * 所属的省市县名称
     */
    @Column(name = "pca_adr")
    @ApiModelProperty(value = "所属的省市县名称")
    private String pcaAdr;

    /**
     * 所属区域名称
     */
    @Column(name = "street_name")
    @ApiModelProperty(value = "所属的区域名称")
    private String streetName;

    /**
     * 所属县区名称
     */
    @Column(name = "area_name")
    @ApiModelProperty(value = "所属的县区名称")
    private String areaName;

    /**
     * 小区经纬度
     */
    @Column(name = "location")
    @ApiModelProperty(value = "小区的经纬度")
    private String location;

    /**
     * 删除标志
     */
    @Column(name = "del_flag")
    @ApiModelProperty(value = "删除标志")
    private Integer delFlag;
}