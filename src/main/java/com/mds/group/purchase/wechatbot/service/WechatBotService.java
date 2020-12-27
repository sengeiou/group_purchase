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

package com.mds.group.purchase.wechatbot.service;

import com.mds.group.purchase.core.Result;
import com.mds.group.purchase.core.Service;
import com.mds.group.purchase.wechatbot.model.WechatBot;
import org.springframework.web.bind.annotation.RequestMethod;


/**
 * Created by CodeGenerator on 2019/05/15.
 *
 * @author pavawi
 */
public interface WechatBotService extends Service<WechatBot> {

    /**
     * 请求微信机器人项目
     *
     * @param baseUrl       请求地址
     * @param requestMethod 请求方法
     * @param params        参数
     * @return string string
     */
    String requestWechatBot(String baseUrl, RequestMethod requestMethod, String... params);

    /**
     * 检测登录
     *
     * @param baseUrl       the base url
     * @param requestMethod the request method
     * @param requestSuffix the request suffix
     * @param params        the params
     * @return the result
     */
    Result checkLogin(String baseUrl, RequestMethod requestMethod, String requestSuffix, String params);


    /**
     * 根据Uin删除机器人
     *
     * @param appmodelId the appmodel id
     * @param botUin     the bot uin
     */
    void removeBotByUin(String appmodelId, Long botUin);
}
