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

package com.mds.group.purchase.shop.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 * The type Style.
 *
 * @author pavawi
 */
@Table(name = "t_style")
@Data
public class Style {


    @Id
    @Column(name = "style_id")
    @ApiModelProperty(value = "样式id")
    private Integer styleId;


    @Column(name = "style_value")
    @ApiModelProperty(value = "颜色参数按顺序逗号分隔")
    private String styleValue;

    @ApiModelProperty(value = "店铺当前默认值 true为当前选中的值 false未未选中")
    @Transient
    private Boolean def;

}