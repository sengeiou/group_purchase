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
import java.math.BigDecimal;
import java.util.Date;

/**
 * The type Refund bill.
 *
 * @author pavawi
 */
@Table(name = "t_refund_bill")
public class RefundBill {
    /**
     * 退款单id
     */
    @Id
    @Column(name = "refund_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long refundId;

    /**
     * 退款单号(自己生成的)
     */
    @Column(name = "pay_refund_id")
    private String payRefundId;

    /**
     * 外部退款单号(微信退款单号)
     */
    @Column(name = "out_refund_id")
    private String outRefundId;

    /**
     * 创建时间
     */
    @Column(name = "create_time")
    private Date createTime;

    /**
     * 原订单ID
     */
    @Column(name = "order_id")
    private Long orderId;

    /**
     * 金额
     */
    private BigDecimal fee;

    /**
     * 是否退款成功
     */
    private Boolean success;

    /**
     * 小程序模块id
     */
    @Column(name = "appmodel_id")
    private String appmodelId;

    /**
     * 获取退款单id
     *
     * @return refund_id - 退款单id
     */
    public Long getRefundId() {
        return refundId;
    }

    /**
     * 设置退款单id
     *
     * @param refundId 退款单id
     */
    public void setRefundId(Long refundId) {
        this.refundId = refundId;
    }

    /**
     * 获取退款单号(自己生成的)
     *
     * @return pay_refund_id - 退款单号(自己生成的)
     */
    public String getPayRefundId() {
        return payRefundId;
    }

    /**
     * 设置退款单号(自己生成的)
     *
     * @param payRefundId 退款单号(自己生成的)
     */
    public void setPayRefundId(String payRefundId) {
        this.payRefundId = payRefundId;
    }

    /**
     * 获取外部退款单号(微信退款单号)
     *
     * @return out_refund_id - 外部退款单号(微信退款单号)
     */
    public String getOutRefundId() {
        return outRefundId;
    }

    /**
     * 设置外部退款单号(微信退款单号)
     *
     * @param outRefundId 外部退款单号(微信退款单号)
     */
    public void setOutRefundId(String outRefundId) {
        this.outRefundId = outRefundId;
    }

    /**
     * 获取创建时间
     *
     * @return create_time - 创建时间
     */
    public Date getCreateTime() {
        return createTime;
    }

    /**
     * 设置创建时间
     *
     * @param createTime 创建时间
     */
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    /**
     * 获取原订单ID
     *
     * @return order_id - 原订单ID
     */
    public Long getOrderId() {
        return orderId;
    }

    /**
     * 设置原订单ID
     *
     * @param orderId 原订单ID
     */
    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    /**
     * 获取金额
     *
     * @return fee - 金额
     */
    public BigDecimal getFee() {
        return fee;
    }

    /**
     * 设置金额
     *
     * @param fee 金额
     */
    public void setFee(BigDecimal fee) {
        this.fee = fee;
    }

    /**
     * 获取是否退款成功
     *
     * @return success - 是否退款成功
     */
    public Boolean getSuccess() {
        return success;
    }

    /**
     * 设置是否退款成功
     *
     * @param success 是否退款成功
     */
    public void setSuccess(Boolean success) {
        this.success = success;
    }

    /**
     * 获取小程序模块id
     *
     * @return appmodel_id - 小程序模块id
     */
    public String getAppmodelId() {
        return appmodelId;
    }

    /**
     * 设置小程序模块id
     *
     * @param appmodelId 小程序模块id
     */
    public void setAppmodelId(String appmodelId) {
        this.appmodelId = appmodelId;
    }
}