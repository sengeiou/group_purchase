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

package com.mds.group.purchase.jobhandler.to;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

/**
 * The type Active mq task to.
 *
 * @author pavawi
 */
@Data
@Document(collection = "ActiveMqTaskTO")
public class ActiveMqTaskTO {

    @Id
    private String id;

    /**
     * 商家appmodelId
     */
    private String appmodelId;


    /**
     * activeMq 连接地址
     */
    private String activeBrokerUrl;

    /**
     * 队列创建时间
     */
    private Date createTime;

    /**
     * 发送指定的队列名
     */
    private String queueName;

    /**
     * 发送至队列的数据
     */
    private String jsonData;

    /**
     * 队列执行日期
     */
    private Date endTime;

    /**
     * 队列消费状态  true-执行成功  false-未执行成功
     */
    private Boolean state;

    /**
     *是否已经发送
     */
    private Boolean sendState;
    /**
     *发送次数
     */
    private Integer sendSum;
    /**
     *最后发送时间
     */
    private Date lastSendTime;

}
