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
 * 通用常量
 *
 * @author shuke
 */
public interface Common {

    /**
     * 是否超出访问次数限制
     */
    Boolean REQUEST_OVER_LIMIT = true;
    /**
     * The Constant REQUEST_IN_LIMIT.
     */
    Boolean REQUEST_IN_LIMIT = false;

    /**
     * The Constant REQUEST_LIMT.
     */
    String REQUEST_LIMT = "isOverLimit";


    /**
     * The Constant DEL_MSG.
     */
    String DEL_MSG = "删除成功";

    /**
     * 删除成功
     */
    Integer DEL_FLAG_TRUE = 1;

    /**
     * 删除失败
     */
    Integer DEL_FLAG_FALSE = 0;

    /**
     * 首页秒杀活动商品显示数量
     */
    Integer INDEX_GOODS_SIZE = 4;

    /**
     * 逗号分隔符
     */
    String REGEX = ",";
}
