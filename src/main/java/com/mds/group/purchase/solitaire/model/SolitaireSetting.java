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

package com.mds.group.purchase.solitaire.model;

import lombok.Data;

import javax.persistence.*;

/**
 * The type Solitaire setting.
 *
 * @author pavawi
 */
@Data
@Table(name = "t_solitaire_setting")
public class SolitaireSetting {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    /**
     * 小程序模板id
     */
    @Column(name = "appmodel_id")
    private String appmodelId;
    /**
     * 状态 0 关闭  1：开启
     */
    private Integer status;
    /**
     * 接龙活动名称
     */
    private String name;
    /**
     * 接龙活动描述
     */
    @Column(name = "solitaire_desc")
    private String solitaireDesc;
    /**
     * 接龙活动海报
     */
    @Column(name = "post_url")
    private String postUrl;
    /**
     * 活动提醒描述
     */
    private String notify;
    /**
     * 创建时间
     */
    @Column(name = "create_time")
    private Long createTime;
    /**
     * 修改时间
     */
    @Column(name = "modify_time")
    private Long modifyTime;

    /**
     * The interface Status.
     *
     * @author pavawi
     */
    public interface Status {
        /**
         * The Constant OPEN.
         */
        Integer OPEN = 1;
        /**
         * The Constant CLOSE.
         */
        Integer CLOSE = 2;
    }

}