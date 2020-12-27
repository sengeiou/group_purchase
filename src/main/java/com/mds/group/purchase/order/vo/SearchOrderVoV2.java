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

/**
 * 搜索订单参数类
 *
 * @author shuke
 * @version 1.2
 * @date 2018 -12-6
 */
@Data
public class SearchOrderVoV2 {

    @ApiModelProperty(value = "商品名称，模糊搜索")
    private String goodsName;

    @ApiModelProperty(value = "买家昵称，模糊搜索")
    private String wxuserName;

    @ApiModelProperty(value = "收货人名称，模糊搜索")
    private String buyerName;

    @ApiModelProperty(value = "团长姓名，模糊搜索")
    private String groupName;

    @ApiModelProperty(value = "订单编号，精确搜索")
    private String orderNo;

    @ApiModelProperty(value = "订单id，精确搜索")
    private Long orderId;

    @ApiModelProperty(value = "取货地点，模糊搜索")
    private String pickupLocation;

    @ApiModelProperty(value = "下单时间,开始")
    private String createOrderTimeStart;

    @ApiModelProperty(value = "下单时间,结束")
    private String createOrderTimeEnd;

    @ApiModelProperty(value = "用于进行时间快速筛选，近一个月：1  近三个月：2  全部：0   默认为近一个月")
    private Integer quicklyDate;

    @ApiModelProperty(value = "发货单id  不限发货单是0  未生成发货单的订单是-1 ")
    private Long sendBillId;
    @ApiModelProperty(value = "订单状态 -1：所有订单 0.等待买家付款 1.买家已付款2.配送中（2.卖家已发货） 3.已评价4.交易成功 5.已关闭（5.用户超时关闭订单,6.用户主动关闭订单," +
            "7商家关闭）99.售后")
    private Integer orderStatus;

    @ApiModelProperty(value = "线路id")
    private Long lineId;

    @ApiModelProperty(value = "当前页码，默认为0")
    private Integer page;

    @ApiModelProperty(value = "页面数据数量，默认为0查询所有")
    private Integer size;

    /**
     * Gets order status.
     *
     * @return the order status
     */
    public Integer getOrderStatus() {
        return orderStatus == null ? -1 : orderStatus;
    }

    /**
     * Gets page.
     *
     * @return the page
     */
    public Integer getPage() {
        return page == null ? 0 : page;
    }

    /**
     * Gets size.
     *
     * @return the size
     */
    public Integer getSize() {
        return size == null ? 0 : size;
    }

    /**
     * Gets goods name.
     *
     * @return the goods name
     */
    public String getGoodsName() {
        return "".equalsIgnoreCase(goodsName) ? null : goodsName;
    }

    /**
     * Gets wxuser name.
     *
     * @return the wxuser name
     */
    public String getWxuserName() {
        return "".equalsIgnoreCase(wxuserName) ? null : wxuserName;
    }

    /**
     * Gets buyer name.
     *
     * @return the buyer name
     */
    public String getBuyerName() {
        return "".equalsIgnoreCase(buyerName) ? null : buyerName;
    }

    /**
     * Gets order no.
     *
     * @return the order no
     */
    public String getOrderNo() {
        return "".equalsIgnoreCase(orderNo) ? null : orderNo;
    }

    /**
     * Gets pickup location.
     *
     * @return the pickup location
     */
    public String getPickupLocation() {
        return "".equalsIgnoreCase(pickupLocation) ? null : pickupLocation;
    }

    /**
     * Gets create order time start.
     *
     * @return the create order time start
     */
    public String getCreateOrderTimeStart() {
        return "".equalsIgnoreCase(createOrderTimeStart) ? null : createOrderTimeStart;
    }

    /**
     * Gets create order time end.
     *
     * @return the create order time end
     */
    public String getCreateOrderTimeEnd() {
        return "".equalsIgnoreCase(createOrderTimeEnd) ? null : createOrderTimeEnd;
    }

    /**
     * Gets group name.
     *
     * @return the group name
     */
    public String getGroupName() {
        return "".equalsIgnoreCase(groupName) ? null : groupName;
    }

    /**
     * Gets send bill id.
     *
     * @return the send bill id
     */
    public Long getSendBillId() {
        return sendBillId == null ? 0 : sendBillId;
    }

    /**
     * Gets quickly date.
     *
     * @return the quickly date
     */
    public Integer getQuicklyDate() {
        return quicklyDate == null ? 0 : quicklyDate;
    }

    /**
     * Gets line id.
     *
     * @return the line id
     */
    public Long getLineId() {
        return lineId == null ? 0 : lineId;
    }
}
