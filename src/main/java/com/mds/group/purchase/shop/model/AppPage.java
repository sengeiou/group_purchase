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

/**
 * The type App page.
 *
 * @author pavawi
 */
@Table(name = "t_app_page")
@Data
public class AppPage {

    @Id
    @Column(name = "app_page_id")
    @ApiModelProperty(value = "页面id")
    private Integer appPageId;


    @Column(name = "page_name")
    @ApiModelProperty(value = "页面名称")
    private String pageName;


    @Column(name = "page_path")
    @ApiModelProperty(value = "页面路径")
    private String pagePath;


    @ApiModelProperty(value = "版本号", hidden = true)
    private Integer version;

    @ApiModelProperty
    private Integer sort;
}