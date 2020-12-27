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
import java.math.BigDecimal;

/**
 * 发货单实体类
 *
 * @author shuke
 * @date 2019 -2-18
 */
@Table(name = "t_send_bill")
public class SendBill {
    /**
     * 发货单id
     */
    @Id
    @GeneratedValue(generator = "JDBC")
    @Column(name = "send_bill_id")
    private Long sendBillId;

    /**
     * 发货单名称（根据时间自动生成）
     */
    @Column(name = "send_bill_name")
    private String sendBillName;

    /**
     * 发货单生成时间
     */
    @Column(name = "create_date")
    private String createDate;

    /**
     * 订单数量
     */
    private Integer orders;

    /**
     * 发货单内所有订单的成交额
     */
    private BigDecimal amount;

    /**
     * 发货单内所有订单的佣金
     */
    private BigDecimal commission;

    /**
     * 1、待发货 2、配送中 3、待提货 4、已完成 5 已关闭
     */
    private Integer status;

    /**
     * 小程序模板id
     */
    @Column(name = "appmodel_id")
    private String appmodelId;

    /**
     * 逻辑删除标识 （0、正常 1、删除）
     */
    @Column(name = "del_flag")
    private Integer delFlag;

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
     * 获取发货单名称（根据时间自动生成）
     *
     * @return send_bill_name - 发货单名称（根据时间自动生成）
     */
    public String getSendBillName() {
        return sendBillName;
    }

    /**
     * 设置发货单名称（根据时间自动生成）
     *
     * @param sendBillName 发货单名称（根据时间自动生成）
     */
    public void setSendBillName(String sendBillName) {
        this.sendBillName = sendBillName;
    }

    /**
     * 获取发货单生成时间
     *
     * @return create_date - 发货单生成时间
     */
    public String getCreateDate() {
        return createDate;
    }

    /**
     * 设置发货单生成时间
     *
     * @param createDate 发货单生成时间
     */
    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    /**
     * 获取订单数量
     *
     * @return orders - 订单数量
     */
    public Integer getOrders() {
        return orders;
    }

    /**
     * 设置订单数量
     *
     * @param orders 订单数量
     */
    public void setOrders(Integer orders) {
        this.orders = orders;
    }

    /**
     * 获取发货单内所有订单的成交额
     *
     * @return amount - 发货单内所有订单的成交额
     */
    public BigDecimal getAmount() {
        return amount;
    }

    /**
     * 设置发货单内所有订单的成交额
     *
     * @param amount 发货单内所有订单的成交额
     */
    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    /**
     * 获取发货单内所有订单的佣金
     *
     * @return commission - 发货单内所有订单的佣金
     */
    public BigDecimal getCommission() {
        return commission;
    }

    /**
     * 设置发货单内所有订单的佣金
     *
     * @param commission 发货单内所有订单的佣金
     */
    public void setCommission(BigDecimal commission) {
        this.commission = commission;
    }

    /**
     * 获取1、待发货 2、配送中 3、待提货 4、已完成 5 已关闭
     *
     * @return status - 1、待发货 2、配送中 3、待提货 4、已完成 5 已关闭
     */
    public Integer getStatus() {
        return status;
    }

    /**
     * 设置1、待发货 2、配送中 3、待提货 4、已完成 5 已关闭
     *
     * @param status 1、待发货 2、配送中 3、待提货 4、已完成 5 已关闭
     */
    public void setStatus(Integer status) {
        this.status = status;
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
     * 获取逻辑删除标识 （0、正常 1、删除）
     *
     * @return del_flag - 逻辑删除标识 （0、正常 1、删除）
     */
    public Integer getDelFlag() {
        return delFlag;
    }

    /**
     * 设置逻辑删除标识 （0、正常 1、删除）
     *
     * @param delFlag 逻辑删除标识 （0、正常 1、删除）
     */
    public void setDelFlag(Integer delFlag) {
        this.delFlag = delFlag;
    }
}