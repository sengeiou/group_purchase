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

package com.mds.group.purchase.shop.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * The type Shop function.
 *
 * @author pavawi
 */
@Table(name = "t_shop_function")
@Data
public class ShopFunction {

    @Id
    @Column(name = "shop_function_id")
    @ApiModelProperty(value = "店铺功能id")
    private Long shopFunctionId;


    @Column(name = "appmodel_id")
    @ApiModelProperty(value = "小程序模板id", hidden = true)
    private String appmodelId;


    @ApiModelProperty(value = "首页订单弹幕开关 0未开启  1-开启")
    @Column(name = "order_barrage_switch_index")
    @NotNull
    private Boolean orderBarrageSwitchIndex;

    @ApiModelProperty(value = "详情页订单弹幕开关 0未开启  1-开启")
    @Column(name = "order_barrage_switch_detail")
    @NotNull
    private Boolean orderBarrageSwitchDetail;

    //    @Column(name = "foot_mark")
    //    @NotNull
    //    @ApiModelProperty(value = "底部打标开关")
    //    private Boolean footMark;


    @Column(name = "forward_tips")
    @ApiModelProperty(value = "转发提示语")
    private String forwardTips;


    @Column(name = "activity_alert")
    @ApiModelProperty(value = "活动提醒时间")
    @NotNull
    @Min(5)
    @Max(60)
    private Integer activityAlert;


    @Column(name = "join_phone")
    @ApiModelProperty(value = "加盟热线")
    private String joinPhone;

    @Column(name = "shop_style_id")
    @NotNull
    @ApiModelProperty(value = "店铺风格id")
    private Integer shopStyleId;

    /**
     * 提现金额限制
     */
    @ApiModelProperty(value = "提现金额限制")
    @Column(name = "withdraw_limit")
    private BigDecimal withdrawLimit;

    /**
     * 团长申请协议
     */
    @ApiModelProperty(value = "团长申请协议")
    @NotBlank
    private String agreement;

}