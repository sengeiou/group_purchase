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

package com.mds.group.purchase.user.vo;


import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * The type Group order vo.
 *
 * @author pavawi
 */
@Data
public class GroupOrderVO implements Serializable {

    private static final long serialVersionUID = 8826940005096163528L;
    @ApiModelProperty(value = "订单id")
    private Long orderId;

    @ApiModelProperty(value = "订单号")
    private String orderNo;

    @ApiModelProperty(value = "用户备注")
    private String userDesc;

    @ApiModelProperty(value = "批注")
    private String postil;

    @ApiModelProperty(value = "预计提货时间")
    private Date forecastReceiveTime;

    @ApiModelProperty(value = "订单状态 1.买家已付款2.卖家已发货")
    private Integer payStatus;

    @ApiModelProperty(value = "总金额")
    private BigDecimal totleFee;

    @ApiModelProperty(value = "买家地址")
    private String buyerAddress;

    @ApiModelProperty(value = "买家姓名")
    private String buyerName;

    @ApiModelProperty(value = "买家电话")
    private String buyerPhone;


    @ApiModelProperty(value = "订单详情")
    private List<GroupOrderDtailVO> groupOrderDtailVOList;

}
