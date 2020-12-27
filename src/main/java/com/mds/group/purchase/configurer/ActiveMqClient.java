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

package com.mds.group.purchase.configurer;

import org.apache.activemq.ScheduledMessage;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.support.JmsUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.jms.*;

/**
 * 发送消息类
 *
 * @author whh
 */
@Component
public class ActiveMqClient {

    @Resource
    private JmsTemplate jmsTemplate;

    /**
     * Send.
     *
     * @param message   the message
     * @param queueName the queue name
     * @desc 立即发送
     */
    public void send(String message, String queueName) {
        jmsTemplate.convertAndSend(queueName, message);
    }

    /**
     * Send 2.
     *
     * @param text      the text
     * @param queueName the queue name
     * @desc
     */
    public void send2(String text, String queueName) {
        Connection connection = null;
        Session session = null;
        MessageProducer producer = null;
        try {
            //获取连接工厂
            ConnectionFactory connectionFactory = this.jmsTemplate.getConnectionFactory();
            connectionFactory.createConnection();
            //获取连接
            connection = connectionFactory.createConnection();
            connection.start();
            //获取session
            session = connection.createSession(Boolean.TRUE, Session.AUTO_ACKNOWLEDGE);
            // 创建一个消息队列
            Topic topic = session.createTopic(queueName);
            producer = session.createProducer(topic);
            producer.setDeliveryMode(DeliveryMode.PERSISTENT);
            TextMessage message = session.createTextMessage(text);
            //发送
            producer.send(message);
            session.commit();
        } catch (JMSException e) {
            e.printStackTrace();
        } finally {
            JmsUtils.closeMessageProducer(producer);
            JmsUtils.closeSession(session);
            JmsUtils.closeConnection(connection);
        }
    }


    /**
     * 延时发送
     *
     * @param text      文本内容
     * @param queueName 队列名称
     * @param delayTime 延迟时间(毫秒值)
     */
    public void delaySend(String text, String queueName, Long delayTime) {

        Connection connection = null;
        Session session = null;
        MessageProducer producer = null;
        try {
            //获取连接工厂
            ConnectionFactory connectionFactory = this.jmsTemplate.getConnectionFactory();
            connectionFactory.createConnection();
            //获取连接
            connection = connectionFactory.createConnection();
            connection.start();
            //获取session
            session = connection.createSession(Boolean.TRUE, Session.AUTO_ACKNOWLEDGE);
            // 创建一个消息队列
            Destination destination = session.createQueue(queueName);
            producer = session.createProducer(destination);
            producer.setDeliveryMode(DeliveryMode.PERSISTENT);
            TextMessage message = session.createTextMessage(text);
            //设置延迟时间
            message.setLongProperty(ScheduledMessage.AMQ_SCHEDULED_DELAY, delayTime);
            //发送
            producer.send(message);
            session.commit();
        } catch (JMSException e) {
            e.printStackTrace();
        } finally {
            JmsUtils.closeMessageProducer(producer);
            JmsUtils.closeSession(session);
            JmsUtils.closeConnection(connection);
        }
    }

    /**
     * Delete all schedule message.
     */
    public void deleteAllScheduleMessage() {
        ConnectionFactory connectionFactory = this.jmsTemplate.getConnectionFactory();
        try {
            Connection conn = connectionFactory.createConnection();
            Session session = conn.createSession(false, Session.AUTO_ACKNOWLEDGE);
            Destination management = session.createTopic(ScheduledMessage.AMQ_SCHEDULER_MANAGEMENT_DESTINATION);
            MessageProducer producer = session.createProducer(management);
            Message request = session.createMessage();
            request.setStringProperty(ScheduledMessage.AMQ_SCHEDULER_ACTION,
                    ScheduledMessage.AMQ_SCHEDULER_ACTION_REMOVEALL);
            producer.send(request);
        } catch (JMSException e) {
            e.printStackTrace();
        }

    }
}
