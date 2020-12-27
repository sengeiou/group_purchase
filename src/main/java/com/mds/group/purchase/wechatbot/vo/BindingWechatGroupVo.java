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

package com.mds.group.purchase.wechatbot.vo;

import com.mds.group.purchase.user.model.GroupLeader;
import com.mds.group.purchase.wechatbot.model.WechatbotGroup;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * The type Binding wechat group vo.
 *
 * @author pavawi
 */
@Data
public class BindingWechatGroupVo {

    private String groupLeaderId;

    private String groupLeaderName;

    private String groupIcon;

    private String groupPhone;

    private String communityName;

    private List<String> wechatGroupName;

    private String lineName;

    /**
     * Instantiates a new Binding wechat group vo.
     */
    public BindingWechatGroupVo() {

    }

    /**
     * Instantiates a new Binding wechat group vo.
     *
     * @param wechatbotGroups the wechatbot groups
     * @param groupLeader     the group leader
     * @param lineName        the line name
     * @param groupIcon       the group icon
     * @param communityName   the community name
     */
    public BindingWechatGroupVo(List<WechatbotGroup> wechatbotGroups, GroupLeader groupLeader, String lineName,
                                String groupIcon, String communityName) {
        wechatGroupName = new ArrayList<>();
        wechatbotGroups.forEach(o -> wechatGroupName.add(o.getWechatGroupName()));
        groupLeaderId = groupLeader.getGroupLeaderId();
        groupLeaderName = groupLeader.getGroupName();
        this.groupIcon = groupIcon;
        groupPhone = groupLeader.getGroupPhone();
        this.communityName = communityName;
        this.lineName = lineName;
    }
}
