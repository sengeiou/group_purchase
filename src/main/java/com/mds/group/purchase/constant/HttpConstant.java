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
 * The interface Http Constant.
 *
 * @author pavawi
 */
public interface HttpConstant {

    /**
     * The Constant VERSION.
     */
    String VERSION = "1.0.5";
    /**
     * The Constant BASE_URL.
     */
    String BASE_URL = "https://login.weixin.qq.com";
    /**
     * The Constant GET.
     */
    String GET = "GET";
    /**
     * The Constant GROUP_BR.
     */
    String GROUP_BR = ":<br/>";
    /**
     * The Constant GROUP_IDENTIFY.
     */
    String GROUP_IDENTIFY = "@@";
    /**
     * The Constant LOCATION_IDENTIFY.
     */
    String LOCATION_IDENTIFY = "/cgi-bin/mmwebwx-bin/webwxgetpubliclinkimg?url=";

    /**
     * The Constant USER_AGENT.
     */
    String USER_AGENT = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_13_2) AppleWebKit/537.36 (KHTML, like Gecko) " +
            "Chrome/63.0.3239.132 Safari/537.36";

}
