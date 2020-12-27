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
 * 活动相关常量
 *
 * @author shuke
 */
public interface ActivityConstant {

    /**
     * 活动状态（0:未开始 1：预热中 2：进行中 3：已经结束）
     */
    Integer ACTIVITY_STATUS_DNS = 0;

    /**
     * The Constant ACTIVITY_STATUS_READY.
     */
    Integer ACTIVITY_STATUS_READY = 1;

    /**
     * The Constant ACTIVITY_STATUS_START.
     */
    Integer ACTIVITY_STATUS_START = 2;

    /**
     * The Constant ACTIVITY_STATUS_END.
     */
    Integer ACTIVITY_STATUS_END = 3;

    /**
     * 活动商品的状态（0:不能购买，不能展示 1:预热状态 2:活动开始可购买 3:已售罄）
     */
    Integer ACTIVITY_GOODS_DONT_SHOW = 0;

    /**
     * The Constant ACTIVITY_GOODS_PREHEAT.
     */
    Integer ACTIVITY_GOODS_PREHEAT = 1;

    /**
     * The Constant ACTIVITY_GOODS_SEAL.
     */
    Integer ACTIVITY_GOODS_SEAL = 2;

    /**
     * The Constant ACTIVITY_GOODS_OVER.
     */
    Integer ACTIVITY_GOODS_OVER = 3;

    /**
     * 活动类型 1、秒杀  2、拼团
     */
    Integer ACTIVITY_SECKILL = 1;

    /**
     * The Constant ACTIVITY_GROUP.
     */
    Integer ACTIVITY_GROUP = 2;

    /**
     * 首页活动商品缓存时间
     */
    Long ACTIVITY_GOODS_TIMEOUT_HALF_MINIUT = 30 * 1000L;

    /**
     * 活动商品预约提醒 时间
     */
    Long ACT_SUBSCRIBE_TIME = 5 * 1000 * 60L;


}
