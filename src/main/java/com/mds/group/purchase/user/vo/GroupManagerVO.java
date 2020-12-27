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

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * The type Group manager vo.
 *
 * @author pavawi
 */
@Data
public class GroupManagerVO implements Serializable {

    /**
     * 等待审核
     */
    public final static int WAITAUDIT = 0;
    //团长状态
    /**
     * 正常
     */
    public final static int NORMAL = 1;
    /**
     * 拒绝
     */
    public final static int REFUSE = 2;
    /**
     * 禁用
     */
    public final static int LOCK = 3;
    private static final long serialVersionUID = 6929681239679463908L;
    @ApiModelProperty(value = "团长id")
    private String groupLeaderId;

    @ApiModelProperty(value = "用户id")
    private Long wxuserId;

    @ApiModelProperty(value = "微信头像")
    private String headImage;

    @ApiModelProperty(value = "团长姓名")
    private String groupName;

    @ApiModelProperty(value = "团长电话")
    private String groupPhone;

    @ApiModelProperty(value = "团长状态 0-待审核 1-正常 2-拒绝 3-禁用中")
    private Integer status;

    @ApiModelProperty(value = "申请时间")
    private Date createTime;

    @ApiModelProperty(value = "团长详细地址")
    private String address;

    @ApiModelProperty(value = "小区id")
    private Long communityId;

    @ApiModelProperty(value = "小区名")
    private String communityName;

    @ApiModelProperty(value = "地区")
    private String area;

    @ApiModelProperty(value = "线路名")
    private String lineName;

    @ApiModelProperty(value = "线路id")
    private Long lineId;

    @ApiModelProperty(value = "评分")
    private String grade;

    @ApiModelProperty(value = "申请的小区坐标")
    private String groupLocation;

    @ApiModelProperty(value = "申请的小区名称")
    private String applyCommunityName;

    private BigDecimal brokerage;

    private String appmodelId;

    private Boolean deleteState;

    private String formId;

    @ApiModelProperty(value = "街道id")
    private Long streetId;

    @ApiModelProperty(value = "县区id")
    private String areaId;

    @ApiModelProperty(value = "市id")
    private String cityId;

    @ApiModelProperty(value = "省id")
    private String provinceId;

    @ApiModelProperty(value = "所属的区域名称")
    private String streetName;

    @ApiModelProperty(value = "经纬度")
    private String location;

}
