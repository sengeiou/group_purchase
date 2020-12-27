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

package com.mds.group.purchase.wechatbot.service;

import com.mds.group.purchase.core.Result;
import com.mds.group.purchase.core.Service;
import com.mds.group.purchase.wechatbot.model.WechatbotGroup;
import com.mds.group.purchase.wechatbot.vo.BindingWechatGroupVo;

import java.util.List;


/**
 * The interface Wechatbot group service.
 *
 * @author shuke
 * @date 2019 /05/15
 */
public interface WechatbotGroupService extends Service<WechatbotGroup> {

    /**
     * 绑定团长和群聊
     *
     * @param appmodelId          小程序模板id
     * @param botUin              the bot uin
     * @param groupLeaderId       团长id
     * @param wechatGroupNickName 微信群聊名称
     * @return 绑定结果 result
     */
    Result bindingGroupLeaderToBot(String appmodelId, Long botUin, String groupLeaderId, String wechatGroupNickName);

    /**
     * 已绑定的群聊列表
     *
     * @param appmodelId 小程序模板id
     * @param botUin     the bot uin
     * @return list list
     */
    List<BindingWechatGroupVo> findBindingList(String appmodelId, Long botUin);


    /**
     * 根据GroupId删除
     *
     * @param appmodelId    the appmodel id
     * @param groupLeaderId the group leader id
     * @param botUin        the bot uin
     * @return the boolean
     */
    boolean deleteByGroupId(String appmodelId, String groupLeaderId, Long botUin);

    /**
     * 根据BotId删除
     *
     * @param appmodelId the appmodel id
     * @param botId      the bot id
     */
    void deleteByBotId(String appmodelId, Long botId);

    /**
     * 根据GroupIds删除
     *
     * @param wechatbotGroupIds the wechatbot group ids
     * @param appmodelId        the appmodel id
     * @return the int
     */
    int deleteByGroupIds(String wechatbotGroupIds, String appmodelId);

    /**
     * 根据Uin查询
     *
     * @param appmodelId the appmodel id
     * @param botUin     the bot uin
     * @return the int
     */
    int findBindGroupCount(String appmodelId, Long botUin);
}
