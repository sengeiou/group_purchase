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

package com.mds.group.purchase.user.vo;

import com.mds.group.purchase.logistics.vo.CommunityVo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * The type Group apply v 119 vo.
 *
 * @author Administrator
 */
@Data
public class GroupApplyV119VO {

    @ApiModelProperty(value = "id")
    private String id;

    @ApiModelProperty(value = "操作类型 0-拒绝 1-同意 2-禁用 3-开启")
    @NotNull
    private Integer optionType;
    @ApiModelProperty(value = "团长姓名")
    private String groupName;
    @ApiModelProperty(value = "团长电话")
    private String groupPhone;
    @ApiModelProperty(value = "取货地址")
    private String pickUpAddr;
    @ApiModelProperty(hidden = true)
    private String appmodelId;
    @ApiModelProperty(value = "小区名称")
    private String communityName;
    @ApiModelProperty(value = "街道id")
    private Long streetId;
    @ApiModelProperty(value = "县区id")
    private String areaId;
    @ApiModelProperty(value = "市id")
    private String cityId;
    @ApiModelProperty(value = "省id")
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
     * Vo to community vo community vo.
     *
     * @return the community vo
     */
    public CommunityVo voToCommunityVo() {
        CommunityVo community = new CommunityVo();
        community.setLineId(this.lineId);
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

    /**
     * The interface Option type.
     *
     * @author pavawi
     */
    public interface OptionType {
        //操作类型
        /**
         * 拒绝
         */
        int REFUSE = 0;
        /**
         * 同意
         */
        int PASS = 1;
        /**
         * 禁用
         */
        int LOCK = 2;
        /**
         * 开启
         */
        int OPEN = 3;
    }
}
