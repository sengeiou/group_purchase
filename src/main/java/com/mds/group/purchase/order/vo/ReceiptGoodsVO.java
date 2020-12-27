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

package com.mds.group.purchase.order.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * The type Receipt goods vo.
 *
 * @author pavawi
 */
@Data
public class ReceiptGoodsVO {
    @NotBlank(message = "订单ID不能为空")
    @ApiModelProperty(value = "需要签收的订单ID")
    private String orderIds;

    @NotBlank(message = "签收单详情ID不能为空")
    @ApiModelProperty(value = "签收单详情")
    private String receiptBillDetailId;
}
