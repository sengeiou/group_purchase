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
 * The type Order vo.
 *
 * @author pavawi
 */
@Data
public class OrderVo {

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

    @ApiModelProperty(value = "订单状态 -1：所有订单 0.待付款 1.待发货 2.待提货 3.待评价 4.已完成  5.售后 6 已关闭")
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
     * Gets end time.
     *
     * @return the end time
     */
    public String getEndTime() {
        return "".equalsIgnoreCase(endTime) ? null : endTime;
    }
}
