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
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;

/**
 * 申请售后单
 *
 * @author shuke
 * @date 2018 -12-6
 */
@Data
public class BaseApplyAfterSaleOrderVO {

    @ApiModelProperty(value = "订单id")
    private List<Long> orderIds;

    @NotNull(message = "活动商品id不能为空")
    @ApiModelProperty(value = "活动商品id")
    private Long actGoodsId;


    @NotBlank(message = "formId不能为空")
    @ApiModelProperty(value = "formId")
    private String formId;

    @NotNull(message = "售后类型不能为空")
    @ApiModelProperty(value = "售后类型:(1.团长换货 2.团长退款 3.退款 4.换货 5.退货退款)")
    private Integer afterSaleType;

    @ApiModelProperty(value = "原因")
    private String reason;

    @ApiModelProperty(value = "说明")
    private String description;

    @ApiModelProperty(value = "图片")
    private String images;

    @ApiModelProperty(value = "退款金额")
    private BigDecimal refundFee;

    @ApiModelProperty(value = "申请数量")
    private Integer applicationNum;
}
