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
 * 商品常量
 *
 * @author shuke
 */
public interface GoodsConstant {

    /**
     * 售卖中
     */
    int ON_SALE = 0;

    /**
     * 已售完
     */
    int SELL_OUT = 2;

    /**
     * 已下架
     */
    int SOLD_OUT = 1;

    /**
     * 默认销量
     */
    int DEFAULT_SALES_VOLUME = 0;

    /**
     * 已删除
     */
    Boolean IS_DEL = true;

    /**
     * 正常
     */
    Boolean NORMAL = false;

    /**
     * 12位商品id最小值
     */
    Long MIN_ID = 100000000000L;

    /**
     * 12位商品id最大值
     */
    Long MAX_ID = 999999999999L;

}
