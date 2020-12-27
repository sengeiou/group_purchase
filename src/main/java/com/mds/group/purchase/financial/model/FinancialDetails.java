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

import javax.persistence.*;
import java.math.BigDecimal;

/**
 * The type Financial details.
 *
 * @author pavawi
 */
@Table(name = "t_financial_details")
public class FinancialDetails {
    /**
     * 资金明细id
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 流水单号
     */
    @Column(name = "serial_number")
    private String serialNumber;

    /**
     * 订单id
     */
    @Column(name = "order_id")
    private Long orderId;
    /**
     * 交易时间
     */
    @Column(name = "created_time")
    private String createdTime;

    /**
     * 用户id
     */
    @Column(name = "wxuser_id")
    private Long wxuserId;

    /**
     * 团长id
     */
    @Column(name = "group_id")
    private String groupId;

    /**
     * 交易类型 1/商品交易 2/佣金提现
     */
    private Integer type;

    /**
     * 1/收入 2/支出
     */
    @Column(name = "account_type")
    private Integer accountType;

    /**
     * 更新时间
     */
    @Column(name = "modify_time")
    private String modifyTime;

    /**
     * 资金额
     */
    private BigDecimal account;

    /**
     * 模板id
     */
    @Column(name = "appmodel_id")
    private String appmodelId;

    /**
     * 获取资金明细id
     *
     * @return id - 资金明细id
     */
    public Long getId() {
        return id;
    }

    /**
     * 设置资金明细id
     *
     * @param id 资金明细id
     */
    public void setId(Long id) {
        this.id = id;
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
     * 获取流水单号
     *
     * @return serial_number - 流水单号
     */
    public String getSerialNumber() {
        return serialNumber;
    }

    /**
     * 设置流水单号
     *
     * @param serialNumber 流水单号
     */
    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
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
     * 获取用户id
     *
     * @return wxuser_id - 用户id
     */
    public Long getWxuserId() {
        return wxuserId;
    }

    /**
     * 设置用户id
     *
     * @param wxuserId 用户id
     */
    public void setWxuserId(Long wxuserId) {
        this.wxuserId = wxuserId;
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
     * 获取交易类型 1/商品交易 2/佣金提现
     *
     * @return type - 交易类型 1/商品交易 2/佣金提现
     */
    public Integer getType() {
        return type;
    }

    /**
     * 设置交易类型 1/商品交易 2/佣金提现
     *
     * @param type 交易类型 1/商品交易 2/佣金提现
     */
    public void setType(Integer type) {
        this.type = type;
    }

    /**
     * 获取1/收入 2/支出
     *
     * @return account_type - 1/收入 2/支出
     */
    public Integer getAccountType() {
        return accountType;
    }

    /**
     * 设置1/收入 2/支出
     *
     * @param accountType 1/收入 2/支出
     */
    public void setAccountType(Integer accountType) {
        this.accountType = accountType;
    }

    /**
     * 获取更新时间
     *
     * @return modify_time - 更新时间
     */
    public String getModifyTime() {
        return modifyTime;
    }

    /**
     * 设置更新时间
     *
     * @param modifyTime 更新时间
     */
    public void setModifyTime(String modifyTime) {
        this.modifyTime = modifyTime;
    }

    /**
     * 获取资金额
     *
     * @return account - 资金额
     */
    public BigDecimal getAccount() {
        return account;
    }

    /**
     * 设置资金额
     *
     * @param account 资金额
     */
    public void setAccount(BigDecimal account) {
        this.account = account;
    }

    /**
     * 获取模板id
     *
     * @return appmodel_id - 模板id
     */
    public String getAppmodelId() {
        return appmodelId;
    }

    /**
     * 设置模板id
     *
     * @param appmodelId 模板id
     */
    public void setAppmodelId(String appmodelId) {
        this.appmodelId = appmodelId;
    }

    /**
     * The interface Constant.
     *
     * @author pavawi
     */
    public interface Constant {

        /**
         * The Constant TYPE_TRANSACTION.
         */
        Integer TYPE_TRANSACTION = 1;

        /**
         * The Constant TYPE_EXTRACT.
         */
        Integer TYPE_EXTRACT = 2;

        /**
         * The Constant TYPE_REFUND.
         */
        Integer TYPE_REFUND = 3;

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