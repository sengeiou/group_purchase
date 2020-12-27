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

/***
 * 排序操作
 *
 * @author Created by wx on 2018/09/01.
 */
public interface HandType {

    /**
     * 置顶
     */
    Integer TOP = 1;

    /**
     * 上移一位
     */
    Integer UP = 2;

    /**
     * 下移一位
     */
    Integer DOWN = 3;

    /**
     * 置底
     */
    Integer FOOT = 4;

}
