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

package com.mds.group.purchase.wechatbot.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.mds.group.purchase.constant.WechatBotApiURL;
import com.mds.group.purchase.core.AbstractService;
import com.mds.group.purchase.core.CodeMsg;
import com.mds.group.purchase.core.Result;
import com.mds.group.purchase.utils.http.HttpClient;
import com.mds.group.purchase.utils.http.request.StringRequestAbstract;
import com.mds.group.purchase.utils.http.response.ApiResponse;
import com.mds.group.purchase.wechatbot.dao.WechatBotMapper;
import com.mds.group.purchase.wechatbot.model.WechatBot;
import com.mds.group.purchase.wechatbot.service.WechatBotService;
import com.mds.group.purchase.wechatbot.service.WechatbotGroupService;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMethod;
import tk.mybatis.mapper.entity.Condition;

import javax.annotation.Resource;
import java.util.Map;


/**
 * The type Wechat bot service.
 *
 * @author shuke
 * @date 2019 /05/15
 */
@Service
public class WechatBotServiceImpl extends AbstractService<WechatBot> implements WechatBotService {
    @Resource
    private WechatBotMapper tWechatBotMapper;
    @Resource
    private WechatbotGroupService wechatbotGroupService;

    private HttpClient httpClient = HttpClient.getClient();

    @Override
    public String requestWechatBot(String baseUrl, RequestMethod requestMethod,
                                   String... params) {
        String url = String.format(baseUrl, params);
        StringRequestAbstract request = new StringRequestAbstract(url);
        if (RequestMethod.POST.equals(requestMethod)) {
            request.post();
        }
        ApiResponse response = httpClient.send(request);
        return response.getRawBody();
    }

    @Override
    public Result checkLogin(String baseUrl, RequestMethod requestMethod, String requestSuffix, String appmodelId) {
        String res = requestWechatBot(baseUrl, requestMethod, requestSuffix, appmodelId);
        JSONObject jsonObject = JSONObject.parseObject(res);
        WechatBot bot = null;
        if (jsonObject != null && jsonObject.get("code").equals(200)) {
            //将所有机器人的在线状态改为离线
            tWechatBotMapper.logonByAppmodelId(appmodelId);
            Map<String, String> data = (Map<String, String>) jsonObject.get("data");
            Condition condition1 = new Condition(WechatBot.class);
            condition1.createCriteria().andEqualTo("appmodelId", appmodelId).andEqualTo("botUin",
                    Long.valueOf(data.get("uin")));
            bot = findByOneCondition(condition1);
            if (bot == null) {
                bot = new WechatBot();
                bot.setCreateDate(System.currentTimeMillis());
                bot.setBotNickName(data.get("nickName"));
                bot.setAppmodelId(appmodelId);
                bot.setBotUin(Long.valueOf(data.get("uin")));
                bot.setDelFlag(false);
                bot.setOnline(true);
                bot.setBotIcon(data.get("icon"));
                bot.setLastLoginDate(System.currentTimeMillis());
                save(bot);
            } else {
                bot.setBotNickName(data.get("nickName"));
                bot.setBotUin(Long.valueOf(data.get("uin")));
                bot.setOnline(true);
                bot.setBotIcon(data.get("icon"));
                bot.setLastLoginDate(System.currentTimeMillis());
                update(bot);
            }
            return Result.success(bot);
        } else if (jsonObject != null) {
            return jsonObject.toJavaObject(Result.class);
        } else {
            return Result.error(CodeMsg.FAIL);
        }
    }

    @Override
    public void removeBotByUin(String appmodelId, Long botUin) {
        Condition condition = new Condition(WechatBot.class);
        condition.createCriteria().andEqualTo("appmodelId", appmodelId).andEqualTo("botUin", botUin);
        WechatBot bot = findByOneCondition(condition);
        requestWechatBot(WechatBotApiURL.BASEURL, RequestMethod.POST, "logout", appmodelId);
        tWechatBotMapper.deleteByCondition(condition);
        wechatbotGroupService.deleteByBotId(appmodelId, bot.getBotId());
    }


}
