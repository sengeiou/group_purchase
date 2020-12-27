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

package com.mds.group.purchase.order.model;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 发货单生成时间实体类
 *
 * @author shuke
 * @date 2019 -2-18
 */
@Table(name = "t_send_bill_time_set")
public class SendBillTimeSet {
    /**
     * 发货单生成时间id
     */
    @Id
    @GeneratedValue(generator = "JDBC")
    @Column(name = "time_set_id")
    private Long timeSetId;

    /**
     * 设置的发货单生成时间
     */
    @Column(name = "set_time")
    private String setTime;

    /**
     * 小程序模板id
     */
    @Column(name = "appmodel_id")
    private String appmodelId;

    /**
     * 逻辑删除标识（0、正常 1、删除）
     */
    @Column(name = "del_flag")
    private Integer delFlag;

    /**
     * 获取发货单生成时间id
     *
     * @return time_set_id - 发货单生成时间id
     */
    public Long getTimeSetId() {
        return timeSetId;
    }

    /**
     * 设置发货单生成时间id
     *
     * @param timeSetId 发货单生成时间id
     */
    public void setTimeSetId(Long timeSetId) {
        this.timeSetId = timeSetId;
    }

    /**
     * 获取设置的发货单生成时间
     *
     * @return set_time - 设置的发货单生成时间
     */
    public String getSetTime() {
        return setTime;
    }

    /**
     * 设置设置的发货单生成时间
     *
     * @param setTime 设置的发货单生成时间
     */
    public void setSetTime(String setTime) {
        this.setTime = setTime;
    }

    /**
     * 获取小程序模板id
     *
     * @return appmodel_id - 小程序模板id
     */
    public String getAppmodelId() {
        return appmodelId;
    }

    /**
     * 设置小程序模板id
     *
     * @param appmodelId 小程序模板id
     */
    public void setAppmodelId(String appmodelId) {
        this.appmodelId = appmodelId;
    }

    /**
     * 获取逻辑删除标识（0、正常 1、删除）
     *
     * @return del_flag - 逻辑删除标识（0、正常 1、删除）
     */
    public Integer getDelFlag() {
        return delFlag;
    }

    /**
     * 设置逻辑删除标识（0、正常 1、删除）
     *
     * @param delFlag 逻辑删除标识（0、正常 1、删除）
     */
    public void setDelFlag(Integer delFlag) {
        this.delFlag = delFlag;
    }
}