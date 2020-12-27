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

import com.alibaba.fastjson.JSON;
import com.mds.group.purchase.configurer.GroupMallProperties;
import com.mds.group.purchase.constant.RedisPrefix;
import com.mds.group.purchase.utils.LimitQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The type Order msg controller.
 *
 * @author shuke
 */
@ServerEndpoint(value = "/websocket/{appmodelId}", configurator = WebSocketConfig.class)
@Component
@Scope("prototype")
public class OrderMsgController {

    @Resource
    private RedisTemplate redisTemplate;

    private Session session;
    private static Logger logger = LoggerFactory.getLogger(OrderMsgController.class);

    /**
     * 连接集合
     */
    private static final Map<String, List<OrderMsgController>> CONNECTIONS = new HashMap<>(16);

    /**
     * 广播给所有用户
     *
     * @param jsonMsg    the json msg
     * @param appmodelId the appmodel id
     */
    public static void broadcastToAll(String jsonMsg, String appmodelId) {
        List<OrderMsgController> orderMsgControllers = getOrderMsgControllers(appmodelId);
        for (OrderMsgController client : orderMsgControllers) {
            client.call(jsonMsg, appmodelId);
        }
    }

    /**
     * websocket连接建立后触发
     *
     * @param session    the session
     * @param appmodelId the appmodel id
     */
    @OnOpen
    public void onOpen(Session session, @PathParam(value = "appmodelId") String appmodelId) {
        //设置websocket连接的session
        this.setSession(session);
        List<OrderMsgController> orderMsgControllers = getOrderMsgControllers(appmodelId);
        orderMsgControllers.add(this);
        getConnections().put(appmodelId, orderMsgControllers);
        logger.info("WEBSOCKET连接打开" + appmodelId);
        //将最近50条订单记录发送到客户端
        String key = GroupMallProperties.getRedisPrefix().concat(appmodelId).concat(RedisPrefix.PAY_OK_NOTIFY_NEW);
        LimitQueue<String> lastNotify = (LimitQueue<String>) redisTemplate.opsForValue().get(key);
        if (lastNotify != null && lastNotify.size() > 0) {
            logger.info("WEBSOCKET发送历史弹幕");
            call(JSON.toJSONString(lastNotify), appmodelId);
        }
    }

    /**
     * websocket连接断开后触发
     *
     * @param appmodelId the appmodel id
     */
    @OnClose
    public void onClose(@PathParam(value = "appmodelId") String appmodelId) {
        //从连接集合中移除
        List<OrderMsgController> orderMsgControllers = getOrderMsgControllers(appmodelId);
        orderMsgControllers.remove(this);
        getConnections().put(appmodelId, orderMsgControllers);
    }

    /**
     * On error.
     *
     * @param t the t
     */
    @OnError
    public void onError(Throwable t) {
        logger.error("Chat Error: " + t.toString());
    }

    private void call(String jsonMsg, String appmodelId) {
        try {
            synchronized (this) {
                if (this.getSession().isOpen()) {
                    this.getSession().getBasicRemote().sendText(jsonMsg);
                } else {
                    logger.error(this.getSession().toString() + "没有开启状态");
                    logger.error(getConnections().toString());
                }
            }
        } catch (IOException e) {
            try {
                //断开连接
                this.getSession().close();
            } catch (IOException e1) {
                e.printStackTrace();
            }
            onClose(appmodelId);
        }
    }

    private void setSession(Session session) {
        this.session = session;
    }

    private Session getSession() {
        return this.session;
    }

    private static Map<String, List<OrderMsgController>> getConnections() {
        return CONNECTIONS;
    }

    private static List<OrderMsgController> getOrderMsgControllers(String appmodelId) {
        List<OrderMsgController> orderMsgControllers = getConnections().get(appmodelId);
        if (orderMsgControllers==null) {
            orderMsgControllers = new ArrayList<>();
        }
        return orderMsgControllers;
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }
}
