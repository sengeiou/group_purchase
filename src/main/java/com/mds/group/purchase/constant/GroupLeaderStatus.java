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
 * The interface Group leader status.
 *
 * @author pavawi
 */
public interface GroupLeaderStatus {

    /**
     * 待审核
     */
    Integer STATUS_AWAIT = 0;

    /**
     * 正常
     */
    Integer STATUS_NORMAL = 1;

    /**
     * 拒绝
     */
    Integer STATUS_REPULSE = 2;

    /**
     * 禁用中
     */
    Integer STATUS_DISABLE = 3;
}
