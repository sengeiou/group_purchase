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
 * @date 2018 -12-6
 */
@Data
public class SearchOrderVo {

    @ApiModelProperty(value = "商品名称，模糊搜索")
    private String goodsName;

    @ApiModelProperty(value = "买家昵称，模糊搜索")
    private String wxuserName;

    @ApiModelProperty(value = "收货人名称，模糊搜索")
    private String buyerName;

    @ApiModelProperty(value = "线路名称，模糊搜索")
    private String lineName;

    @ApiModelProperty(value = "订单编号，精确搜索")
    private String orderNo;

    @ApiModelProperty(value = "取货地点，模糊搜索")
    private String pickupLocation;

    @ApiModelProperty(value = "下单时间,开始")
    private String createOrderTimeStart;

    @ApiModelProperty(value = "下单时间,结束")
    private String createOrderTimeEnd;

    @ApiModelProperty(value = "订单状态 -1：所有订单 0.等待买家付款 1.买家已付款2.卖家已发货 3.待评价4.交易成功 5用户超时关闭订单,用户主动关闭订单,,7商家关闭")
    private Integer orderStatus;

    @ApiModelProperty(value = "线路id")
    private Long lineId;

    @ApiModelProperty(value = "区域id")
    private Long streetId;

    @ApiModelProperty(value = "活动id")
    private Long activityId;

    @ApiModelProperty(value = "用户id")
    private Long wxuserId;

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
     * Gets line id.
     *
     * @return the line id
     */
    public Long getLineId() {
        return lineId == 0 ? null : lineId;
    }

    /**
     * Gets street id.
     *
     * @return the street id
     */
    public Long getStreetId() {
        return streetId == 0 ? null : streetId;
    }

    /**
     * Gets activity id.
     *
     * @return the activity id
     */
    public Long getActivityId() {
        return activityId == 0 ? null : activityId;
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
     * Gets line name.
     *
     * @return the line name
     */
    public String getLineName() {
        return "".equalsIgnoreCase(lineName) ? null : lineName;
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
}
