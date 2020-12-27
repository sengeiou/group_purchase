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
import java.util.Date;

/**
 * The type Statistics.
 *
 * @author pavawi
 */
@Table(name = "t_statistics")
@Data
public class Statistics {
    @Id
    @GeneratedValue(generator = "JDBC")
    @Column(name = "statistics_id")
    private Integer statisticsId;

    @Column(name = "appmodel_id")
    private String appmodelId;

    /**
     * 浏览量统计
     */
    private Integer pageview;

    /**
     * 访客量统计
     */
    private Integer visitorsum;

    /**
     * 创建时间
     */
    @Column(name = "create_time")
    private Date createTime;

}