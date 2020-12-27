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

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * The type Group mall properties.
 *
 * @author shuke
 * @date 2019 /5/6
 */
@Component
public class GroupMallProperties {

    private static String orderNotify ;

    private static String orderNotifyH5 ;

    private static String redisPrefix ;

    private static String projectTokenPrefix ;

    private static String devPhone ;

    private static Integer orderNotifyNumber;

    /**
     * Gets order notify.
     *
     * @return the order notify
     */
    public static String getOrderNotify() {
        return orderNotify;
    }

    /**
     * Sets order notify.
     *
     * @param notify the notify
     */
    @Value("${groupmall.weixin.pay.notify.url}")
    public void setOrderNotify(String notify) {
        orderNotify = notify;
    }

    /**
     * Gets order notify h 5.
     *
     * @return the order notify h 5
     */
    public static String getOrderNotifyH5() {
        return orderNotifyH5;
    }

    /**
     * Sets order notify h 5.
     *
     * @param notifyH5 the notify h 5
     */
    @Value("${groupmall.weixin.pay.h5.notify.url}")
    public void setOrderNotifyH5(String notifyH5) {
        orderNotifyH5 = notifyH5;
    }

    /**
     * Gets redis prefix.
     *
     * @return the redis prefix
     */
    public static String getRedisPrefix() {
        return redisPrefix;
    }

    /**
     * Sets redis prefix.
     *
     * @param prefix the prefix
     */
    @Value("${groupmall.redis.project.prefix}")
    public void setRedisPrefix(String prefix) {
        redisPrefix = prefix;
    }

    /**
     * Gets project token prefix.
     *
     * @return the project token prefix
     */
    public static String getProjectTokenPrefix() {
        return projectTokenPrefix;
    }

    /**
     * Sets project token prefix.
     *
     * @param tokenPrefix the token prefix
     */
    @Value("${groupmall.redis.token.key}")
    public void setProjectTokenPrefix(String tokenPrefix) {
        projectTokenPrefix = tokenPrefix;
    }

    /**
     * Gets dev phone.
     *
     * @return the dev phone
     */
    public static String getDevPhone() {
        return devPhone;
    }

    /**
     * Sets dev phone.
     *
     * @param phone the phone
     */
    @Value("${groupmall.devphone}")
    public void setDevPhone(String phone) {
        devPhone = phone;
    }

    /**
     * Gets order notify number.
     *
     * @return the order notify number
     */
    public static Integer getOrderNotifyNumber() {
        return orderNotifyNumber;
    }

    /**
     * Sets order notify number.
     *
     * @param notifyNumber the notify number
     */
    @Value("${groupmall.order-notify-number}")
    public void setOrderNotifyNumber(Integer notifyNumber) {
        orderNotifyNumber = notifyNumber;
    }
}
