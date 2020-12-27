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

import com.mds.group.purchase.constant.enums.AfterSaleOrderStatus;
import com.mds.group.purchase.constant.enums.AfterSaleOrderType;
import com.mds.group.purchase.order.model.AfterSaleOrder;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * The type After sale order result.
 *
 * @author pavawi
 */
@Data
@ApiModel("售后单")
@EqualsAndHashCode(callSuper = true)
public class AfterSaleOrderResult extends AfterSaleOrder {
    /**
     * 买家头像
     */
    @ApiModelProperty(value = "买家头像")
    private String wxuserIcon;

    /**
     * 买家昵称
     */
    @ApiModelProperty(value = "买家昵称")
    private String wxuserName;

    /**
     * 买家姓名
     */
    @ApiModelProperty(value = "收货人姓名")
    private String buyerName;

    /**
     * 买家电话
     */
    @ApiModelProperty(value = "收货人电话")
    private String buyerPhone;

    /**
     * 买家地址
     */
    @ApiModelProperty(value = "收货人地址")
    private String buyerAddress;


    @ApiModelProperty(name = "售后单状态")
    private String afterSaleStatusText;

    @ApiModelProperty(name = "订单状态")
    private PcOrderResult orderResult;

    /**
     * Gets status text.
     *
     * @param afterSaleType   the after sale type
     * @param afterSaleStatus the after sale status
     * @return the status text
     */
    public static String getStatusText(Integer afterSaleType, Integer afterSaleStatus) {
        StringBuffer text = new StringBuffer();
        if (afterSaleStatus == 5 || afterSaleStatus == 6) {
            text.append("商家同意");
        } else {
            text.append("申请");
        }
        text.append(AfterSaleOrderType.getDesc(afterSaleType));
        text.append(",");
        text.append(AfterSaleOrderStatus.getDesc(afterSaleStatus));
        return text.toString();
    }


}
