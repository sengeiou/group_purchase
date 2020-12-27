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

package com.mds.group.purchase.constant;

/**
 * 时间类型
 *
 * @author whh
 */
public interface TimeType {

    /**
     * 1 分钟
     */
    Long TEMPTIME = 1 * 60 * 1000L;

    /**
     * 5 分钟
     */
    Long FIVEMINUTES = 5 * 60 * 1000L;

    /**
     * 10 分钟
     */
    Long TENMINUTES = 10 * 60 * 1000L;

    /**
     * 30分钟
     */
    Long HALFHOUR = 30 * 60 * 1000L;

    /**
     * 24小时  (1天)
     */
    Long ONEDAY = 1 * 24 * 60 * 60 * 1000L;

    /**
     * 48小时  (2天)
     */
    Long NEXTDAY = 2 * 24 * 60 * 60 * 1000L;

    /**
     * 5天
     */
    Long FIVEDAY = 5 * 24 * 60 * 60 * 1000L;

    /**
     * 7天
     */
    Long SEVENDAY = 7 * 24 * 60 * 60 * 1000L;

    /**
     * 1小时
     */
    Long ONEHOUR = 60 * 60 * 1000L;

    /**
     * 1小时
     */
    Long SIX_HOURS = 6 * 60 * 60 * 1000L;

    /**
     * 19天
     */
    Long NINETEENDAYS = 19 * 24 * 60 * 60 * 1000L;
    /**
     * 20天
     */
    Long TWENTYDAY = 20 * 24 * 60 * 60 * 1000L;
}
