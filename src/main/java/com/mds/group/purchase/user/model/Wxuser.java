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

package com.mds.group.purchase.user.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

/**
 * The type Wxuser.
 *
 * @author pavawi
 */
@Data
@Table(name = "t_wxuser")
public class Wxuser implements Serializable {

    private static final long serialVersionUID = -7348286426841442198L;

    @Id
    @Column(name = "wxuser_id")
    @ApiModelProperty(value = "用户id")
    private Long wxuserId;

    @Column(name = "wxuser_name")
    @ApiModelProperty(value = "用户昵称")
    private String wxuserName;

    @Column(name = "wxuser_desc")
    @ApiModelProperty(value = "用户简介")
    private String wxuserDesc;

    @ApiModelProperty(value = "微信联合id")
    @Column(name = "union_id")
    private String unionId;

    @ApiModelProperty(value = "公司自定义联合id")
    @Column(name = "mds_union_id")
    private String mdsUnionId;

    @ApiModelProperty(value = "备注")
    private String remark;

    @ApiModelProperty(value = "微信小程序openid")
    @Column(name = "mini_open_id")
    private String miniOpenId;

    @ApiModelProperty(value = "微信小程序openid")
    @Column(name = "pc_open_id")
    private String pcOpenId;

    @ApiModelProperty(value = "公众号openid")
    @Column(name = "mp_openid")
    private String mpOpenid;

    @ApiModelProperty(value = "用户手机号")
    private String phone;

    @Column(name = "session_key")
    @ApiModelProperty(value = "解密sessionkey")
    private String sessionKey;

    @ApiModelProperty(value = "用户头像")
    private String icon;

    @Column(name = "create_time")
    @ApiModelProperty(value = "注册时间")
    private Date createTime;

    @Column(name = "community_id")
    @ApiModelProperty(value = "所在小区id")
    private Long communityId;

    @ApiModelProperty(value = "0禁用  1:正常  2：团长")
    @Column(name = "user_status")
    private Integer userStatus;
    @ApiModelProperty(value = "是否删除  1正常 2-逻辑删除")
    @Column(name = "delete_statu")
    private Integer deleteStatu;
    @ApiModelProperty(value = "小程序模板id")
    @Column(name = "appmodel_id")
    private String appmodelId;

    /**
     * The interface User status.
     *
     * @author pavawi
     */
    public interface UserStatus {
        //用户状态
        /**
         * 禁用
         */
        int LOCK = 0;
        /**
         * 正常
         */
        int NORMAL = 1;
        /**
         * 团长
         */
        int GROUPLEADER = 2;
    }


}