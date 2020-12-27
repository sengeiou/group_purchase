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

import javax.persistence.*;

/**
 * The type Send bill activity.
 *
 * @author pavawi
 */
@Table(name = "t_send_bill_activity")
public class SendBillActivity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 创建活动时设置的发货单生成时间
     */
    @Column(name = "send_bill_generate_date")
    private String sendBillGenerateDate;

    /**
     * 活动id
     */
    @Column(name = "activity_id")
    private Long activityId;

    /**
     * 小程序模板id
     */
    @Column(name = "appmodel_id")
    private String appmodelId;

    /**
     * 删除标识，0/未删除 1/删除
     */
    @Column(name = "del_flag")
    private Integer delFlag;

    /**
     * Gets id.
     *
     * @return id id
     */
    public Long getId() {
        return id;
    }

    /**
     * Sets id.
     *
     * @param id the id
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * 获取创建活动时设置的发货单生成时间
     *
     * @return send_bill_generate_date - 创建活动时设置的发货单生成时间
     */
    public String getSendBillGenerateDate() {
        return sendBillGenerateDate;
    }

    /**
     * 设置创建活动时设置的发货单生成时间
     *
     * @param sendBillGenerateDate 创建活动时设置的发货单生成时间
     */
    public void setSendBillGenerateDate(String sendBillGenerateDate) {
        this.sendBillGenerateDate = sendBillGenerateDate;
    }

    /**
     * 获取活动id
     *
     * @return activity_id - 活动id
     */
    public Long getActivityId() {
        return activityId;
    }

    /**
     * 设置活动id
     *
     * @param activityId 活动id
     */
    public void setActivityId(Long activityId) {
        this.activityId = activityId;
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
     * 获取删除标识，0/未删除 1/删除
     *
     * @return del_flag - 删除标识，0/未删除 1/删除
     */
    public Integer getDelFlag() {
        return delFlag;
    }

    /**
     * 设置删除标识，0/未删除 1/删除
     *
     * @param delFlag 删除标识，0/未删除 1/删除
     */
    public void setDelFlag(Integer delFlag) {
        this.delFlag = delFlag;
    }
}