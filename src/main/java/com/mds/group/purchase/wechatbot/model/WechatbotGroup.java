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

package com.mds.group.purchase.wechatbot.model;

import javax.persistence.*;
import java.util.Objects;

/**
 * The type Wechatbot group.
 *
 * @author pavawi
 */
@Table(name = "t_wechatbot_group")
public class WechatbotGroup {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 机器人id
     */
    @Column(name = "wechatbot_id")
    private Long wechatbotId;

    /**
     * 团长id
     */
    @Column(name = "group_leader_id")
    private String groupLeaderId;

    /**
     * 团长微信群名称
     */
    @Column(name = "wechat_group_name")
    private String wechatGroupName;

    /**
     * 小程序模板id
     */
    @Column(name = "appmodel_id")
    private String appmodelId;

    /**
     * 绑定时间
     */
    @Column(name = "create_time")
    private Long createTime;

    /**
     * 修改时间
     */
    @Column(name = "modify_time")
    private Long modifyTime;

    /**
     * Gets id.
     *
     * @return id id
     */
    public Long getId() {
        return id;
    }

    /**
     * Sets id.
     *
     * @param id the id
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * 获取机器人id
     *
     * @return wechatbot_id - 机器人id
     */
    public Long getWechatbotId() {
        return wechatbotId;
    }

    /**
     * 设置机器人id
     *
     * @param wechatbotId 机器人id
     */
    public void setWechatbotId(Long wechatbotId) {
        this.wechatbotId = wechatbotId;
    }

    /**
     * 获取团长id
     *
     * @return group_leader_id - 团长id
     */
    public String getGroupLeaderId() {
        return groupLeaderId;
    }

    /**
     * 设置团长id
     *
     * @param groupLeaderId 团长id
     */
    public void setGroupLeaderId(String groupLeaderId) {
        this.groupLeaderId = groupLeaderId;
    }

    /**
     * 获取团长微信群名称
     *
     * @return wechat_group_name - 团长微信群名称
     */
    public String getWechatGroupName() {
        return wechatGroupName;
    }

    /**
     * 设置团长微信群名称
     *
     * @param wechatGroupName 团长微信群名称
     */
    public void setWechatGroupName(String wechatGroupName) {
        this.wechatGroupName = wechatGroupName;
    }

    /**
     * 获取小程序模板id
     *
     * @return appmodel_id - 小程序模板id
     */
    public String getAppmodelId() {
        return appmodelId;
    }

    /**
     * 设置小程序模板id
     *
     * @param appmodelId 小程序模板id
     */
    public void setAppmodelId(String appmodelId) {
        this.appmodelId = appmodelId;
    }

    /**
     * 获取绑定时间
     *
     * @return create_time - 绑定时间
     */
    public Long getCreateTime() {
        return createTime;
    }

    /**
     * 设置绑定时间
     *
     * @param createTime 绑定时间
     */
    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }

    /**
     * 获取修改时间
     *
     * @return modify_time - 修改时间
     */
    public Long getModifyTime() {
        return modifyTime;
    }

    /**
     * 设置修改时间
     *
     * @param modifyTime 修改时间
     */
    public void setModifyTime(Long modifyTime) {
        this.modifyTime = modifyTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        WechatbotGroup group = (WechatbotGroup) o;
        return id.equals(group.id) && wechatbotId.equals(group.wechatbotId) && groupLeaderId.equals(group.groupLeaderId)
                && wechatGroupName.equals(group.wechatGroupName) && appmodelId.equals(group.appmodelId) && Objects
                .equals(createTime, group.createTime) && Objects.equals(modifyTime, group.modifyTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, wechatbotId, groupLeaderId, wechatGroupName, appmodelId);
    }
}