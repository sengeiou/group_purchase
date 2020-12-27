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
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * The type Line detail.
 *
 * @author pavawi
 */
@Table(name = "t_line_detail")
@Data
public class LineDetail {
    /**
     * 线路详情id
     */
    @Id
    @Column(name = "line_detail_id")
    @ApiModelProperty(value = "线路详情id")
    private Long lineDetailId;

    /**
     * 线路id
     */
    @Column(name = "line_id")
    @ApiModelProperty(value = "线路id")
    private Long lineId;

    /**
     * 区域id
     */
    @Column(name = "street_id")
    @ApiModelProperty(value = "区域id")
    private Long streetId;

    /**
     * 区域名称
     */
    @Column(name = "street_name")
    @ApiModelProperty(value = "区域名称")
    private String streetName;

    /**
     * 小区id
     */
    @Column(name = "community_id")
    @ApiModelProperty(value = "小区id")
    private Long communityId;

    /**
     * 冗余的小区名称
     */
    @Column(name = "community_name")
    @ApiModelProperty(value = "小区名称")
    private String communityName;

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