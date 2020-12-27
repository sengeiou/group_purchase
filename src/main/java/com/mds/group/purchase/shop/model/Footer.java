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
 * The type Footer.
 *
 * @author Created by wx on 2018/06/04.
 */
@Table(name = "t_footer")
@Data
public class Footer {
    @ApiModelProperty(value = "导航id")
    @Id
    @Column(name = "footer_id")
    private Integer footerId;

    @ApiModelProperty(value = "模板Id")
    @Column(name = "appmodel_id")
    private String appmodelId;

    @ApiModelProperty(value = "导航名称")
    @Column(name = "footer_name")
    private String footerName;

    @ApiModelProperty(value = "未选中图片")
    @Column(name = "footer_img_no")
    private String footerImgNo;

    @ApiModelProperty(value = "选中图片")
    @Column(name = "footer_img_yes")
    private String footerImgYes;

    @ApiModelProperty(value = "页面id")
    @Column(name = "app_page_id")
    private Integer appPageId;

    @ApiModelProperty(value = "自定义排序")
    @Column(name = "sort")
    private Integer sort;

    @ApiModelProperty(value = "开启关闭状态")
    @Column(name = "footer_flag")
    private Boolean footerFlag;

    @ApiModelProperty(value = "页面首字母")
    @Column(name = "app_flag")
    private String appFlag;

}