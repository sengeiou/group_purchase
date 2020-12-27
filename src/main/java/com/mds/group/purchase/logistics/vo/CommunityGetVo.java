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

import com.mds.group.purchase.logistics.model.Community;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * The type Community get vo.
 *
 * @author pavawi
 */
@Data
public class
CommunityGetVo {

    @ApiModelProperty(value = "所属的省市县名称")
    private String pcaAdr;

    @ApiModelProperty(value = "所属的区域名称")
    private String streetName;

    @ApiModelProperty(value = "小区名称")
    private String communityName;

    @ApiModelProperty(value = "小区id")
    private Long communityId;

    @ApiModelProperty(value = "当前页码，默认为0")
    private Integer page;

    @ApiModelProperty(value = "页面数据数量，默认为0查询所有")
    private Integer size;

    /**
     * Vo to community community.
     *
     * @return the community
     */
    public Community voToCommunity() {
        Community community = new Community();
        if (this.communityId != null) {
            community.setCommunityId(this.communityId);
        }
        if (this.communityName != null && !"".equals(this.communityName)) {
            community.setCommunityName(this.communityName);
        }
        if (this.streetName != null && !"".equals(this.streetName)) {
            community.setStreetName(this.streetName);
        }
        if (this.pcaAdr != null && !"".equals(this.pcaAdr)) {
            community.setPcaAdr(this.pcaAdr);
        }
        return community;
    }
}
