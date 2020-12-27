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

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * The type Wechat bot.
 *
 * @author pavawi
 */
@Table(name = "t_wechat_bot")
public class WechatBot {
    /**
     * 机器人id
     */
    @Id
    @Column(name = "bot_id")
    private Long botId;

    /**
     * 机器人昵称
     */
    @Column(name = "bot_nick_name")
    private String botNickName;

    /**
     * 机器人头像
     */
    @Column(name = "bot_icon")
    private String botIcon;

    /**
     * 唯一标识
     */
    @Column(name = "bot_uin")
    private Long botUin;

    /**
     * 在线状态
     */
    private Boolean online;

    /**
     * 机器人对应的小程序模板id
     */
    @Column(name = "appmodel_id")
    private String appmodelId;

    /**
     * 删除状态
     */
    @Column(name = "del_flag")
    private Boolean delFlag;

    /**
     * 创建时间
     */
    @Column(name = "create_date")
    private Long createDate;

    /**
     * 删除时间
     */
    @Column(name = "del_date")
    private Long delDate;

    /**
     * 上次登录时间
     */
    @Column(name = "last_login_date")
    private Long lastLoginDate;

    /**
     * 上次登出时间
     */
    @Column(name = "last_logout_date")
    private Long lastLogoutDate;

    /**
     * 获取机器人id
     *
     * @return bot_id - 机器人id
     */
    public Long getBotId() {
        return botId;
    }

    /**
     * 设置机器人id
     *
     * @param botId 机器人id
     */
    public void setBotId(Long botId) {
        this.botId = botId;
    }

    /**
     * 获取机器人昵称
     *
     * @return bot_nick_name - 机器人昵称
     */
    public String getBotNickName() {
        return botNickName;
    }

    /**
     * 设置机器人昵称
     *
     * @param botNickName 机器人昵称
     */
    public void setBotNickName(String botNickName) {
        this.botNickName = botNickName;
    }

    /**
     * 获取唯一标识
     *
     * @return bot_uin - 唯一标识
     */
    public Long getBotUin() {
        return botUin;
    }

    /**
     * 设置唯一标识
     *
     * @param botUin 唯一标识
     */
    public void setBotUin(Long botUin) {
        this.botUin = botUin;
    }

    /**
     * 获取在线状态
     *
     * @return online - 在线状态
     */
    public Boolean getOnline() {
        return online;
    }

    /**
     * 设置在线状态
     *
     * @param online 在线状态
     */
    public void setOnline(Boolean online) {
        this.online = online;
    }

    /**
     * 获取机器人对应的小程序模板id
     *
     * @return appmodel_id - 机器人对应的小程序模板id
     */
    public String getAppmodelId() {
        return appmodelId;
    }

    /**
     * 设置机器人对应的小程序模板id
     *
     * @param appmodelId 机器人对应的小程序模板id
     */
    public void setAppmodelId(String appmodelId) {
        this.appmodelId = appmodelId;
    }

    /**
     * 获取删除状态
     *
     * @return del_flag - 删除状态
     */
    public Boolean getDelFlag() {
        return delFlag;
    }

    /**
     * 设置删除状态
     *
     * @param delFlag 删除状态
     */
    public void setDelFlag(Boolean delFlag) {
        this.delFlag = delFlag;
    }

    /**
     * 获取创建时间
     *
     * @return create_date - 创建时间
     */
    public Long getCreateDate() {
        return createDate;
    }

    /**
     * 设置创建时间
     *
     * @param createDate 创建时间
     */
    public void setCreateDate(Long createDate) {
        this.createDate = createDate;
    }

    /**
     * 获取删除时间
     *
     * @return del_date - 删除时间
     */
    public Long getDelDate() {
        return delDate;
    }

    /**
     * 设置删除时间
     *
     * @param delDate 删除时间
     */
    public void setDelDate(Long delDate) {
        this.delDate = delDate;
    }

    /**
     * 获取上次登录时间
     *
     * @return last_login_date - 上次登录时间
     */
    public Long getLastLoginDate() {
        return lastLoginDate;
    }

    /**
     * 设置上次登录时间
     *
     * @param lastLoginDate 上次登录时间
     */
    public void setLastLoginDate(Long lastLoginDate) {
        this.lastLoginDate = lastLoginDate;
    }

    /**
     * 获取上次登出时间
     *
     * @return last_logout_date - 上次登出时间
     */
    public Long getLastLogoutDate() {
        return lastLogoutDate;
    }

    /**
     * 设置上次登出时间
     *
     * @param lastLogoutDate 上次登出时间
     */
    public void setLastLogoutDate(Long lastLogoutDate) {
        this.lastLogoutDate = lastLogoutDate;
    }

    /**
     * Gets bot icon.
     *
     * @return the bot icon
     */
    public String getBotIcon() {
        return botIcon;
    }

    /**
     * Sets bot icon.
     *
     * @param botIcon the bot icon
     */
    public void setBotIcon(String botIcon) {
        this.botIcon = botIcon;
    }
}