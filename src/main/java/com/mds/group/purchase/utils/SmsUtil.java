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

package com.mds.group.purchase.utils;

import com.github.qcloudsms.SmsSingleSender;
import com.github.qcloudsms.SmsSingleSenderResult;

import java.util.ArrayList;

/**
 * The type Sms util.
 *
 * @author pavawi
 */
public class SmsUtil {

    /**
     * Send msg int.
     *
     * @param userTel the user tel
     * @param content the content
     * @return the int
     * @throws Exception the exception
     */
    public static int sendMsg(String userTel, String content) throws Exception {
        // 短信appid
        int appid = 1400038028;
        // 短信秘钥
        String appkey = "040e0e068f9653eb7600b788f4499ef7";
        SmsSingleSender singleSender = new SmsSingleSender(appid, appkey);
        SmsSingleSenderResult singleSenderResult = new SmsSingleSenderResult();
        singleSenderResult.result = 1;
        ArrayList<String> params = new ArrayList<>();
        params.add(content);
        singleSenderResult = singleSender.sendWithParam("86", userTel, 318792, params, "", "", "");
        return singleSenderResult.result;
    }
}
