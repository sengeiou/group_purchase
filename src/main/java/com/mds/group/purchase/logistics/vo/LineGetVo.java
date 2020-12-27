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

package com.mds.group.purchase.logistics.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;


/**
 * The type Line get vo.
 *
 * @author pavawi
 */
@Data
public class LineGetVo {

    @ApiModelProperty(value = "司机名称")
    private String driverName;

    @ApiModelProperty(value = "司机电话")
    private String driverPhone;

    @ApiModelProperty(value = "小区名称")
    private String communityName;

    @ApiModelProperty(value = "线路id")
    private Long lineId;

    @ApiModelProperty(value = "当前页码，默认为0")
    private Integer page;

    @ApiModelProperty(value = "页面数据数量，0未分组线路  1配送线路")
    private Integer size;

    @ApiModelProperty(value = "搜索类型0-分组线路,1-未默认线路")
    @NotNull
    private Integer type;

    /**
     * Gets driver name.
     *
     * @return the driver name
     */
    public String getDriverName() {
        return "".equals(driverName) ? null : driverName;
    }

    /**
     * Sets driver name.
     *
     * @param driverName the driver name
     */
    public void setDriverName(String driverName) {
        this.driverName = driverName;
    }

    /**
     * Gets driver phone.
     *
     * @return the driver phone
     */
    public String getDriverPhone() {
        return "".equals(driverPhone) ? null : driverPhone;
    }

    /**
     * Sets driver phone.
     *
     * @param driverPhone the driver phone
     */
    public void setDriverPhone(String driverPhone) {
        this.driverPhone = driverPhone;
    }

    /**
     * Gets community name.
     *
     * @return the community name
     */
    public String getCommunityName() {
        return "".equals(communityName) ? null : communityName;
    }

    /**
     * Sets community name.
     *
     * @param communityName the community name
     */
    public void setCommunityName(String communityName) {
        this.communityName = communityName;
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
}
