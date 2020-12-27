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

package com.mds.group.purchase.websocket;

import com.alibaba.fastjson.JSONObject;
import com.mds.group.purchase.configurer.GroupMallProperties;
import com.mds.group.purchase.constant.ActiviMqQueueName;
import com.mds.group.purchase.constant.RedisPrefix;
import com.mds.group.purchase.utils.LimitQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * The type Websocket listener.
 *
 * @author pavawi
 */
@Component
public class WebsocketListener {

    @Resource
    private RedisTemplate redisTemplate;

    private Logger log = LoggerFactory.getLogger(WebsocketListener.class);

    /**
     * Order msg.
     *
     * @param jsonStr the json str
     */
    @JmsListener(destination = ActiviMqQueueName.ORDER_BROADCAST)
    public void orderMsg(String jsonStr) {
        log.info("=============>收到请求，开始发送弹幕");
        String appmodelId = JSONObject.parseObject(jsonStr).getString("appmodelId");
        OrderMsgController.broadcastToAll(jsonStr, appmodelId);
        log.info("=============>完成发送弹幕");
        String key = GroupMallProperties.getRedisPrefix().concat(appmodelId).concat(RedisPrefix.PAY_OK_NOTIFY_NEW);
        LimitQueue<String> lastNotify = (LimitQueue<String>) redisTemplate.opsForValue().get(key);
        if (lastNotify == null) {
            lastNotify = new LimitQueue<>(GroupMallProperties.getOrderNotifyNumber());
        }
        lastNotify.offer(jsonStr);
        redisTemplate.opsForValue().set(key, lastNotify);
    }


}
