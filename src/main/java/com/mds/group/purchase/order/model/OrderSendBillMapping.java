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
 * 订单发货单关系映射实体类
 *
 * @author shuke
 * @date 2019 -2-18
 */
@Table(name = "t_order_send_bill_mapping")
public class OrderSendBillMapping {
    /**
     * id
     */
    @Id
    @GeneratedValue(generator = "JDBC")
    private Long id;

    /**
     * 订单id
     */
    @Column(name = "order_id")
    private Long orderId;

    /**
     * 发货单id
     */
    @Column(name = "send_bill_id")
    private Long sendBillId;

    /**
     * 订单是否已经生成发货单  generate_status int (1) (0,、未生成发货单  1、已经生成发货单)
     */
    private Integer generate;

    /**
     * 活动设置的发货单生成日期
     */
    private String generateDate;

    /**
     * 小程序模板ID
     */
    @Column(name = "appmodel_id")
    private String appmodelId;

    /**
     * 0、正常 1、已删除
     */
    @Column(name = "del_flag")
    private Integer delFlag;

    /**
     * 获取id
     *
     * @return id - id
     */
    public Long getId() {
        return id;
    }

    /**
     * 设置id
     *
     * @param id id
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * 获取订单id
     *
     * @return order_id - 订单id
     */
    public Long getOrderId() {
        return orderId;
    }

    /**
     * 设置订单id
     *
     * @param orderId 订单id
     */
    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    /**
     * 获取发货单id
     *
     * @return send_bill_id - 发货单id
     */
    public Long getSendBillId() {
        return sendBillId;
    }

    /**
     * 设置发货单id
     *
     * @param sendBillId 发货单id
     */
    public void setSendBillId(Long sendBillId) {
        this.sendBillId = sendBillId;
    }

    /**
     * 获取订单是否已经生成发货单  generate_status int (1) (0,、未生成发货单  1、已经生成发货单)
     *
     * @return generate - 订单是否已经生成发货单  generate_status int (1) (0,、未生成发货单  1、已经生成发货单)
     */
    public Integer getGenerate() {
        return generate;
    }

    /**
     * 设置订单是否已经生成发货单  generate_status int (1) (0,、未生成发货单  1、已经生成发货单)
     *
     * @param generate 订单是否已经生成发货单  generate_status int (1) (0,、未生成发货单  1、已经生成发货单)
     */
    public void setGenerate(Integer generate) {
        this.generate = generate;
    }

    /**
     * 获取小程序模板ID
     *
     * @return appmodel_id - 小程序模板ID
     */
    public String getAppmodelId() {
        return appmodelId;
    }

    /**
     * 设置小程序模板ID
     *
     * @param appmodelId 小程序模板ID
     */
    public void setAppmodelId(String appmodelId) {
        this.appmodelId = appmodelId;
    }

    /**
     * 获取0、正常 1、已删除
     *
     * @return del_flag - 0、正常 1、已删除
     */
    public Integer getDelFlag() {
        return delFlag;
    }

    /**
     * 设置0、正常 1、已删除
     *
     * @param delFlag 0、正常 1、已删除
     */
    public void setDelFlag(Integer delFlag) {
        this.delFlag = delFlag;
    }

    /**
     * Gets generate date.
     *
     * @return the generate date
     */
    public String getGenerateDate() {
        return generateDate;
    }

    /**
     * Sets generate date.
     *
     * @param generateDate the generate date
     */
    public void setGenerateDate(String generateDate) {
        this.generateDate = generateDate;
    }
}