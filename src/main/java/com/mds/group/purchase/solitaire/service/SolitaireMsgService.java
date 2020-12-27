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

package com.mds.group.purchase.solitaire.service;

import com.mds.group.purchase.wechatbot.model.WechatbotGroup;

import java.util.List;

/**
 * The interface Solitaire msg service.
 *
 * @author pavawi
 */
public interface SolitaireMsgService {

    /**
     * 向所有微信群发送接龙开始信息
     *
     * @param appmodelId the appmodel id
     */
    void sendStartSolitaireActMsg(String appmodelId);

    /**
     * 向指定微信群发送接龙开始信息
     *
     * @param appmodelId  the appmodel id
     * @param wxGroupList the wx group list
     */
    void sendStartSolitaireActMsgToGroups(String appmodelId, List<WechatbotGroup> wxGroupList);

    /**
     * 向指定微信群发送接龙开始信息
     *
     * @param appmodelId the appmodel id
     * @param wxGroups   the wx groups
     */
    void sendStartSolitaireActMsgToGroups(String appmodelId, String wxGroups);

    /**
     * 向团长的微信群发送接龙参与记录
     *
     * @param appmodelId    the appmodel id
     * @param groupLeaderId the group leader id
     */
    void sendSolitaireRecordActMsg(String appmodelId, String groupLeaderId);

    /**
     * 商品到货后发送@信息
     *
     * @param appmodelId      the appmodel id
     * @param groupLeaderId   the group leader id
     * @param buyerName       the buyer name
     * @param goodsName       the goods name
     * @param groupLeaderName the group leader name
     */
    void sendAtMsg(String appmodelId, String groupLeaderId, String buyerName, String goodsName, String groupLeaderName);
}
