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

package com.mds.group.purchase.shop.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * The type Order data vo.
 *
 * @author pavawi
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderDataVO {

    @ApiModelProperty("等待发货订单数")
    private Long waitSendGoods;
    @ApiModelProperty("等待支付订单数")
    private Long waitPayOrder;
    @ApiModelProperty("等待取货订单数")
    private Long waitPick;
    @ApiModelProperty("成功的订单")
    private Long payOkOrder;
}
