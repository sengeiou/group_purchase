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

import com.mds.group.purchase.order.model.Order;
import com.mds.group.purchase.order.model.OrderDetail;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 生成订单的参数对象类
 *
 * @author shuke
 * @date 2018 -12-8
 */
@Data
public class OrderSaveVo {

    @NotNull
    @ApiModelProperty("用户的id")
    private Long wxuserId;

    @NotNull
    @ApiModelProperty("活动商品id")
    private Long actGoodsId;

    @ApiModelProperty("订单类型,不需要传值")
    private Integer orderType;

//    @NotBlank
//    @ApiModelProperty("团主id")
//    private String groupId;

    @NotNull
    @ApiModelProperty("活动类型（1：秒杀 2拼团）")
    private Integer activityType;

//    @NotBlank
//    @ApiModelProperty("收货人名称")
//    private String  buyerName;
//
//    @NotBlank
//    @ApiModelProperty("收货人电话")
//    private String buyerPhone;
//
//    @NotBlank
//    @ApiModelProperty("收货人地址")
//    private String buyerAddress;

    @NotNull
    @ApiModelProperty("收货人信息id")
    private Long consigneeId;

//    @NotBlank
//    @ApiModelProperty("取货点地址")
//    private String pickupLocation;

    @ApiModelProperty("用户留言")
    private String userDesc;

    @NotNull
    @ApiModelProperty("商品数量")
    private Integer goodsNum;

    @NotBlank
    @ApiModelProperty("formId")
    private String formId;

    @ApiModelProperty("小程序模板id，此处可不填")
    private String appmodelId;

    /**
     * Instantiates a new Order save vo.
     */
    public OrderSaveVo() {
    }

    /**
     * Vo 2 order order.
     *
     * @return the order
     */
    public Order vo2Order() {
        Order order = new Order();
//        order.setBuyerName(buyerName);
//       order.setBuyerAddress(buyerAddress);
        order.setWxuserId(wxuserId);
        order.setUserDesc(userDesc);
//        order.setPickupLocation(pickupLocation);
//        order.setBuyerPhone(buyerPhone);
//       setGroupId(groupId);
        order.setAppmodelId(appmodelId);
        order.setFormId(formId);
        order.setOrderType(orderType);
        return order;
    }

    /**
     * Vo 2 order detail order detail.
     *
     * @return the order detail
     */
    public OrderDetail vo2OrderDetail() {
        OrderDetail od = new OrderDetail();
        od.setActGoodsId(actGoodsId);
        od.setWxuserId(wxuserId);
        od.setOrderType(orderType);
        od.setGoodsNum(goodsNum);
        od.setAppmodelId(appmodelId);
        return od;
    }

}
