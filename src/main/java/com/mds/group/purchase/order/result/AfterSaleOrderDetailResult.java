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

package com.mds.group.purchase.order.result;

import com.mds.group.purchase.order.model.AfterSaleOrder;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * The type After sale order detail result.
 *
 * @author pavawi
 */
@Data
@ApiModel("售后单详情")
@EqualsAndHashCode(callSuper = true)
public class AfterSaleOrderDetailResult extends AfterSaleOrder {

    /**
     * The History list.
     */
    @ApiModelProperty("之前的时间节点")
    List<AfterSaleOrderHistory> historyList;
    /**
     * The Order result.
     */
    @ApiModelProperty("生成该售后单的原始订单")
    OrderResult orderResult;
    /**
     * The Pickup location.
     */
    @ApiModelProperty("取货地点（团长的地址）")
    String pickupLocation;
    /**
     * The Group leader phone.
     */
    @ApiModelProperty("团长的电话")
    String groupLeaderPhone;
    /**
     * The Pay status.
     */
    @ApiModelProperty("订单的状态")
    Integer payStatus;

    /**
     * Sets history.
     *
     * @param afterSaleOrders the after sale orders
     */
    public void setHistory(List<AfterSaleOrder> afterSaleOrders) {
        List<AfterSaleOrderHistory> historyList = new ArrayList<>();
        for (AfterSaleOrder afterSaleOrder : afterSaleOrders) {
            AfterSaleOrderHistory afterSaleOrderHistory = new AfterSaleOrderHistory();
            afterSaleOrderHistory.setCloseTime(afterSaleOrder.getCloseTime());
            afterSaleOrderHistory.setCreateTime(afterSaleOrder.getCreateTime());
            afterSaleOrderHistory.setRefusalTime(afterSaleOrder.getRefusalTime());
            afterSaleOrderHistory.setSuccessTime(afterSaleOrder.getSuccessTime());
            historyList.add(afterSaleOrderHistory);
        }
        this.historyList = historyList;
    }

    /**
     * The type After sale order history.
     *
     * @author pavawi
     */
    @Getter
    @Setter
    class AfterSaleOrderHistory {
        /**
         * 申请时间
         */
        @ApiModelProperty("申请时间")
        private Date createTime;

        /**
         * 关闭时间
         */
        @ApiModelProperty("关闭时间")
        private Date closeTime;

        /**
         * 拒绝时间
         */
        @ApiModelProperty("拒绝时间")
        private Date refusalTime;

        /**
         * 成功时间
         */
        @ApiModelProperty("成功时间")
        private Date successTime;
    }
}

