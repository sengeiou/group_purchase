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
 * The type Order vo v 2.
 *
 * @author pavawi
 */
@Data
public class OrderVoV2 {

    @ApiModelProperty(value = "线路id")
    private Long lineId;

    @ApiModelProperty(value = "区域id")
    private Long streetId;

    @ApiModelProperty(value = "开始时间")
    private String startTime;

    @ApiModelProperty(value = "结束时间")
    private String endTime;

    @ApiModelProperty(value = "活动id")
    private Long activityId;

    @ApiModelProperty("用户id")
    private Long wxuserId;

    @ApiModelProperty("页码")
    private Integer page;

    @ApiModelProperty("页面大小")
    private Integer size;

    @ApiModelProperty(value = "订单状态 -1：所有订单 0.待付款 1.待发货 2配送中  3.待评价 4.已完成 5.已关闭 9.待提货  99.售后")
    private int orderStatus;

    /**
     * Gets start time.
     *
     * @return the start time
     */
    public String getStartTime() {
        return "".equalsIgnoreCase(startTime) ? null : startTime;
    }

    /**
     * Sets start time.
     *
     * @param startTime the start time
     */
    public void setStartTime(String startTime) {
        this.startTime = startTime;
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
     * Sets page.
     *
     * @param page the page
     */
    public void setPage(Integer page) {
        this.page = page;
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
     * Sets size.
     *
     * @param size the size
     */
    public void setSize(Integer size) {
        this.size = size;
    }

    /**
     * Gets end time.
     *
     * @return the end time
     */
    public String getEndTime() {
        return "".equalsIgnoreCase(endTime) ? null : endTime;
    }

    /**
     * Sets end time.
     *
     * @param endTime the end time
     */
    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    /**
     * Gets line id.
     *
     * @return the line id
     */
    public Long getLineId() {
        return lineId;
    }

    /**
     * Sets line id.
     *
     * @param lineId the line id
     */
    public void setLineId(Long lineId) {
        this.lineId = lineId;
    }

    /**
     * Gets street id.
     *
     * @return the street id
     */
    public Long getStreetId() {
        return streetId;
    }

    /**
     * Sets street id.
     *
     * @param streetId the street id
     */
    public void setStreetId(Long streetId) {
        this.streetId = streetId;
    }

    /**
     * Gets activity id.
     *
     * @return the activity id
     */
    public Long getActivityId() {
        return activityId;
    }

    /**
     * Sets activity id.
     *
     * @param activityId the activity id
     */
    public void setActivityId(Long activityId) {
        this.activityId = activityId;
    }

    /**
     * Gets wxuser id.
     *
     * @return the wxuser id
     */
    public Long getWxuserId() {
        return wxuserId;
    }

    /**
     * Sets wxuser id.
     *
     * @param wxuserId the wxuser id
     */
    public void setWxuserId(Long wxuserId) {
        this.wxuserId = wxuserId;
    }

    /**
     * Gets order status.
     *
     * @return the order status
     */
    public int getOrderStatus() {
        return orderStatus;
    }

    /**
     * Sets order status.
     *
     * @param orderStatus the order status
     */
    public void setOrderStatus(int orderStatus) {
        this.orderStatus = orderStatus;
    }
}
