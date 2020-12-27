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

package com.mds.group.purchase.activity.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;

/**
 * 活动实体类
 *
 * @author shuke on 2018-12-4
 */
@Data
@Table(name = "t_activity")
public class Activity {

    /**
     * 活动id
     */
    @Id
    @GeneratedValue(generator = "JDBC")
    @ApiModelProperty(value = "活动id")
    @Column(name = "activity_id")
    private Long activityId;

    /**
     * 活动类型（1：秒杀 2拼团）
     */
    @ApiModelProperty(value = "活动类型（1：秒杀 2拼团）")
    @Column(name = "activity_type")
    private Integer activityType;

    /**
     * 活动名称
     */
    @ApiModelProperty(value = "活动名称")
    @Column(name = "activity_name")
    private String activityName;

    /**
     * 活动海报
     */
    @ApiModelProperty(value = "活动海报")
    @Column(name = "activity_poster")
    private String activityPoster;

    /**
     * 预热时间（小时）
     */
    @ApiModelProperty(value = "预热时间（小时）")
    @Column(name = "ready_time")
    private String readyTime;

    /**
     * 开始时间
     */
    @ApiModelProperty(value = "开始时间")
    @Column(name = "start_time")
    private String startTime;

    /**
     * 删除标识
     */
    @ApiModelProperty(value = "删除标识(true:删除)")
    @Column(name = "delete_status")
    private Boolean deleteStatus;

    /**
     * 结束时间
     */
    @ApiModelProperty(value = "结束时间")
    @Column(name = "end_time")
    private String endTime;

    /**
     * 预计提货时间
     */
    @ApiModelProperty(value = "预计提货时间")
    @Column(name = "forecast_receive_time")
    private String forecastReceiveTime;

    /**
     * 活动状态（0:未开始 1：预热中 2：进行中 3：已经结束）
     */
    @ApiModelProperty(value = "活动状态（0:未开始 1：预热中 2：进行中 3：已经结束）")
    @Column(name = "status")
    private Integer status;

    /**
     * 小程序模板id
     */
    @ApiModelProperty(value = "小程序模板id")
    private String appmodelId;

    /**
     * 参与人数
     */
    @ApiModelProperty(value = "参与人数")
    @Column(name = "participants")
    private Integer participants;

    /**
     * 活动商品数
     */
    @ApiModelProperty(value = "活动商品数")
    @Column(name = "activity_goods_num")
    private Integer actGoodsNum;

    /**
     * 成交量（笔）
     */
    @ApiModelProperty(value = "成交量（笔）")
    @Column(name = "turnover")
    private Integer turnover;

    /**
     * 成交额（元）
     */
    @ApiModelProperty(value = "成交额（元）")
    @Column(name = "gmv")
    private BigDecimal gmv;

    /**
     * 活动商品是否自动按销量排序，否则按照手动排序
     * @since v1.2
     */
    @ApiModelProperty(value = "活动商品是否自动按销量排序，否则按照手动排序")
    @Column(name = "act_goods_auto_sort")
    private Boolean actGoodsAutoSort;


}