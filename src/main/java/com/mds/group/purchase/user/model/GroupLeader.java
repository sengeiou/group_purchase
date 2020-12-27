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

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.util.Date;

/**
 * The type Group leader.
 *
 * @author pavawi
 */
@Data
@Table(name = "t_group_leader")
public class GroupLeader {
    /**
     * 团长id
     */
    @Id
    @Column(name = "group_leader_id")
    private String groupLeaderId;

    /**
     * 团长的wxuserid
     */
    private Long wxuserId;

    /**
     * 团长姓名
     */
    @Column(name = "group_name")
    private String groupName;

    /**
     * 获得的佣金
     */
    private BigDecimal brokerage;

    /**
     * 累计获得的佣金
     */
    private BigDecimal totalBrokerage;


    /**
     * 团长电话
     */
    @Column(name = "group_phone")
    private String groupPhone;

    /**
     * 创建时间
     */
    @Column(name = "create_time")
    private Date createTime;

    /**
     * 团长所在小区
     */
    @Column(name = "community_id")
    private Long communityId;

    /**
     * 线路id
     */
    @Column(name = "line_id")
    private Long lineId;
    /**
     * 团长详细地址
     */
    private String address;

    /**
     * 团长状态 0-待审核 1-正常 2-拒绝 3-禁用中
     */
    private Integer status;
    /**
     * 小程序模板id
     */
    @Column(name = "appmodel_id")
    private String appmodelId;
    /**
     * formId
     */
    @Column(name = "form_id")
    private String formId;
    /**
     * 删除状态
     */
    @Column(name = "delete_state")
    private Boolean deleteState;
    /**
     * 团长申请的小区坐标
     */
    @Column(name = "group_location")
    private String groupLocation;
    /**
     * 团长申请的小区名称
     */
    @Column(name = "apply_community_name")
    private String applyCommunityName;

    /**
     * The interface Status.
     *
     * @author pavawi
     */
    public interface Status {
        //团长状态
        /**
         * 待审核
         */
        int WAITAUDIT = 0;
        /**
         * 正常
         */
        int NORMAL = 1;
        /**
         * 拒绝
         */
        int REFUSE = 2;
        /**
         * 禁用中
         */
        int LOCK = 3;
    }

}