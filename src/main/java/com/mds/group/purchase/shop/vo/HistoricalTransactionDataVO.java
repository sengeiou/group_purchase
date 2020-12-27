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
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * The type Historical transaction data vo.
 *
 * @author pavawi
 */
@Data
public class HistoricalTransactionDataVO {

    @ApiModelProperty(value = "交易量")
    private Integer volumeOfBusinessNumber;
    @ApiModelProperty(value = "交易额")
    private BigDecimal volumeOfBusiness;
    @ApiModelProperty(value = "支付时间")
    private Date payTime;
}
