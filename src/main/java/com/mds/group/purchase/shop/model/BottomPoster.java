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

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * The type Bottom poster.
 *
 * @author pavawi
 */
@Data
@Table(name = "t_bottom_poster")
public class BottomPoster {
    /**
     * 底部海报id
     */
    @Id
    @GeneratedValue(generator = "JDBC")
    private Long id;

    /**
     * 海报链接
     */
    @Column(name = "poster_url")
    private String posterUrl;

    /**
     * 海报链接的电话号
     */
    private String phone;

    @Column(name = "appmodel_id")
    private String appmodelId;

    /**
     * 开启状态（1：开启，0：关闭）
     */
    @Column(name = "status")
    private Integer status;

}