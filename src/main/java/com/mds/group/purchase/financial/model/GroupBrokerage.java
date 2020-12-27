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

package com.mds.group.purchase.financial.model;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;

/**
 * The type Group brokerage.
 *
 * @author pavawi
 */
@Table(name = "t_group_brokerage")
public class GroupBrokerage {

    @Id
    private Long id;

    /**
     * 交易流水号
     */
    @Column(name = "serial_number")
    private String serialNumber;

    /**
     * 订单id
     */
    @Column(name = "order_id")
    private Long orderId;

    /**
     * 团长id
     */
    @Column(name = "group_id")
    private String groupId;

    /**
     * 交易时间
     */
    @Column(name = "created_time")
    private String createdTime;

    /**
     * 修改时间
     */
    @Column(name = "modify_time")
    private String modifyTime;

    /**
     * 金额
     */
    private BigDecimal account;

    /**
     * 状态 1/待结算 2/已结算 3/已到账
     */
    private Integer status;

    /**
     * 类型  1/商品交易 2/佣金提现
     */
    private Integer type;

    /**
     * 1/收入  2/支出
     */
    @Column(name = "account_type")
    private Integer accountType;

    @Column(name = "appmodel_id")
    private String appmodelId;

    /**
     * Gets id.
     *
     * @return the id
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
     * 获取交易流水号
     *
     * @return serial_number - 交易流水号
     */
    public String getSerialNumber() {
        return serialNumber;
    }

    /**
     * 设置交易流水号
     *
     * @param serialNumber 交易流水号
     */
    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    /**
     * 获取团长id
     *
     * @return group_id - 团长id
     */
    public String getGroupId() {
        return groupId;
    }

    /**
     * 设置团长id
     *
     * @param groupId 团长id
     */
    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    /**
     * 获取交易时间
     *
     * @return created_time - 交易时间
     */
    public String getCreatedTime() {
        return createdTime;
    }

    /**
     * 设置交易时间
     *
     * @param createdTime 交易时间
     */
    public void setCreatedTime(String createdTime) {
        this.createdTime = createdTime;
    }

    /**
     * 获取修改时间
     *
     * @return modify_time - 修改时间
     */
    public String getModifyTime() {
        return modifyTime;
    }

    /**
     * 设置修改时间
     *
     * @param modifyTime 修改时间
     */
    public void setModifyTime(String modifyTime) {
        this.modifyTime = modifyTime;
    }

    /**
     * 获取金额
     *
     * @return account - 金额
     */
    public BigDecimal getAccount() {
        return account;
    }

    /**
     * 设置金额
     *
     * @param account 金额
     */
    public void setAccount(BigDecimal account) {
        this.account = account;
    }

    /**
     * 获取状态 1/待结算 2/已结算 3/已到账
     *
     * @return status - 状态 1/待结算 2/已结算 3/已到账
     */
    public Integer getStatus() {
        return status;
    }

    /**
     * 设置状态 1/待结算 2/已结算 3/已到账
     *
     * @param status 状态 1/待结算 2/已结算 3/已到账
     */
    public void setStatus(Integer status) {
        this.status = status;
    }

    /**
     * 获取类型  1/商品交易 2/佣金提现
     *
     * @return type - 类型  1/商品交易 2/佣金提现
     */
    public Integer getType() {
        return type;
    }

    /**
     * 设置类型  1/商品交易 2/佣金提现
     *
     * @param type 类型  1/商品交易 2/佣金提现
     */
    public void setType(Integer type) {
        this.type = type;
    }

    /**
     * 获取1/收入  2/支出
     *
     * @return account_type - 1/收入  2/支出
     */
    public Integer getAccountType() {
        return accountType;
    }

    /**
     * 设置1/收入  2/支出
     *
     * @param accountType 1/收入  2/支出
     */
    public void setAccountType(Integer accountType) {
        this.accountType = accountType;
    }

    /**
     * Gets appmodel id.
     *
     * @return the appmodel id
     */
    public String getAppmodelId() {
        return appmodelId;
    }

    /**
     * Sets appmodel id.
     *
     * @param appmodelId the appmodel id
     */
    public void setAppmodelId(String appmodelId) {
        this.appmodelId = appmodelId;
    }

    /**
     * Gets order id.
     *
     * @return the order id
     */
    public Long getOrderId() {
        return orderId;
    }

    /**
     * Sets order id.
     *
     * @param orderId the order id
     */
    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    /**
     * The interface Constant.
     *
     * @author pavawi
     */
    public interface Constant {

        /**
         * The Constant STATUS_WAIT.
         */
        Integer STATUS_WAIT = 1;

        /**
         * The Constant STATUS_ENTERED.
         */
        Integer STATUS_ENTERED = 2;

        /**
         * The Constant STATUS_COMPLETE.
         */
        Integer STATUS_COMPLETE = 3;

        /**
         * The Constant TYPE_TRANSACTION.
         */
        Integer TYPE_TRANSACTION = 1;

        /**
         * The Constant TYPE_EXTRACT.
         */
        Integer TYPE_EXTRACT = 2;

        /**
         * The Constant ACCOUNT_TYPE_INCOME.
         */
        Integer ACCOUNT_TYPE_INCOME = 1;

        /**
         * The Constant ACCOUNT_TYPE_DISBURSEMENT.
         */
        Integer ACCOUNT_TYPE_DISBURSEMENT = 2;
    }
}