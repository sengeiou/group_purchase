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

package com.mds.group.purchase.listener;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.mds.group.purchase.configurer.GroupMallProperties;
import com.mds.group.purchase.constant.ActiviMqQueueName;
import com.mds.group.purchase.constant.RedisPrefix;
import com.mds.group.purchase.solitaire.service.SolitaireMsgService;
import com.mds.group.purchase.solitaire.service.SolitaireRecordService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * The type Solitaire listener.
 *
 * @author pavawi
 */
@Component
public class SolitaireListener {

    @Resource
    private SolitaireMsgService solitaireMsgService;
    @Resource
    private SolitaireRecordService solitaireRecordService;
    @Resource
    private RedisTemplate redisTemplate;

    private Logger log = LoggerFactory.getLogger(SolitaireListener.class);

    /**
     * Generate solitaire record listener.
     *
     * @param orderNo the order no
     */
    @JmsListener(destination = ActiviMqQueueName.GENERATE_SOLITAIRE_RECORD)
    public void generateSolitaireRecordListener(String orderNo) {
        //生成接龙记录
        solitaireRecordService.addRecord(orderNo);
    }

    /**
     * Send solitaire act start msg listener.
     *
     * @param appmodelId the appmodel id
     */
    @JmsListener(destination = ActiviMqQueueName.SOLITAIRE_ACT_START_MSG)
    public void sendSolitaireActStartMsgListener(String appmodelId) {
        solitaireMsgService.sendStartSolitaireActMsg(appmodelId);
    }

    /**
     * Send solitaire record msg listener.
     *
     * @param jsonData the json data
     */
    @JmsListener(destination = ActiviMqQueueName.SOLITAIRE_RECORD_MSG)
    public void sendSolitaireRecordMsgListener(String jsonData) {
        log.info("---" + jsonData);
        JSONObject jsonObject = JSON.parseObject(jsonData);
        String appmodelId = jsonObject.getString("appmodelId");
        String groupLeaderId = jsonObject.getString("id");
        solitaireMsgService.sendSolitaireRecordActMsg(appmodelId, groupLeaderId);
    }


    /**
     * Send at msg listener.
     *
     * @param jsonData the json data
     */
    @JmsListener(destination = ActiviMqQueueName.SOLITAIRE_AT_MSG)
    public void sendAtMsgListener(String jsonData) {
        JSONObject jsonObject = JSON.parseObject(jsonData);
        String appmodelId = jsonObject.getString("appmodelId");
        String groupLeaderId = jsonObject.getString("groupLeaderId");
        String buyerName = jsonObject.getString("buyerName");
        String goodsName = jsonObject.getString("goodsName");
        String groupLeaderName = jsonObject.getString("groupLeaderName");
        solitaireMsgService.sendAtMsg(appmodelId, groupLeaderId, buyerName, goodsName, groupLeaderName);
    }

    /**
     * H 5 page view count.
     *
     * @param appmodelId the appmodel id
     */
    @JmsListener(destination = ActiviMqQueueName.H5_PAGE_VIEW)
    public void h5PageViewCount(String appmodelId) {
        String redisKey = GroupMallProperties.getRedisPrefix() + appmodelId + RedisPrefix.H5_PAGE_VIEW;
        redisTemplate.opsForValue().increment(redisKey, 1L);
    }

    /**
     * Update h 5 payed user count.
     *
     * @param jsonData the json data
     */
    @JmsListener(destination = ActiviMqQueueName.UPDATE_PAYED_USER_NUM)
    public void updateH5PayedUserCount(String jsonData) {
        JSONObject jsonObject = JSON.parseObject(jsonData);
        String appmodelId = jsonObject.getString("appmodelId");
        String wxuserId = jsonObject.getString("id");
        String redisKey = GroupMallProperties.getRedisPrefix() + appmodelId + RedisPrefix.H5_PAYED_USER_COUNT;
        redisTemplate.opsForSet().add(redisKey, wxuserId);
    }

    /**
     * New person join group send m sg.
     *
     * @param jsonData the json data
     */
    @JmsListener(destination = ActiviMqQueueName.NEW_PERSON_JOIN_GROUP_SENDMSG)
    public void newPersonJoinGroupSendMSg(String jsonData) {
        JSONObject jsonObject = JSON.parseObject(jsonData);
        String appmodelId = jsonObject.getString("appmodelId");
        String wxUin = jsonObject.getString("wxUin");
        String groupName = jsonObject.getString("groupName");
        solitaireMsgService.sendStartSolitaireActMsgToGroups(appmodelId, groupName);
    }
}
