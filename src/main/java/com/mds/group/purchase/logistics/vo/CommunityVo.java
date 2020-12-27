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
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;

/**
 * The type Community vo.
 *
 * @author pavawi
 */
@Data
public class CommunityVo {

    @ApiModelProperty(value = "小区id")
    private Long communityId;

    @ApiModelProperty(value = "小区名称")
    @NotNull
    @Length(min = 1, max = 28, message = "小区名不能大于28个字符")
    private String communityName;

    @ApiModelProperty(value = "街道id")
    @NotNull
    private Long streetId;

    @ApiModelProperty(value = "小程序模板id")
    private String appmodelId;

    @ApiModelProperty(value = "县区id")
    @NotNull
    private String areaId;

    @ApiModelProperty(value = "市id")
    @NotNull
    private String cityId;

    @ApiModelProperty(value = "省id")
    @NotNull
    private String provinceId;

    @ApiModelProperty(value = "所属的省市县名称")
    private String pcaAdr;

    @ApiModelProperty(value = "所属的区域名称")
    private String streetName;

    @ApiModelProperty(value = "经纬度")
    private String location;

    @ApiModelProperty(value = "线路id   更新小区线路时,取消分配的线路,传0")
    private Long lineId;

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
        community.setAppmodelId(this.appmodelId);
        community.setAreaId(this.areaId);
        community.setStreetId(this.streetId);
        community.setCityId(this.cityId);
        community.setProvinceId(this.provinceId);
        community.setCommunityName(this.communityName);
        community.setStreetName(this.streetName);
        community.setLocation(this.location);
        community.setPcaAdr(this.pcaAdr);
        return community;
    }
}
