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
 * The interface Wechat bot api url.
 *
 * @author pavawi
 */
public interface WechatBotApiURL {

    /**
     * The Constant BASEURL.
     */
    String BASEURL = "https://www.superprism.cn/wechatbot/wechat/boot/%s?appmodelId=%s";
//	String BASEURL = "http://192.168.1.114:8081/wechatbot/wechat/boot/%s?appmodelId=%s";

    /**
     * The Constant SENDMSG.
     */
//	String SENDMSG = "http://192.168.1.114:8081/wechatbot/wechat/boot/sendMsgByName?appmodelId=%s&groupNickName=%s&msg
//	=%s";
    String SENDMSG = "https://www.superprism.cn/wechatbot/wechat/boot/sendMsgByName?appmodelId=%s&groupNickName=%s" +
            "&msg=%s";
}
