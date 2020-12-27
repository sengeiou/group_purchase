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

package com.mds.group.purchase.utils.bot;

import com.mds.group.purchase.configurer.GroupMallProperties;
import com.mds.group.purchase.constant.RedisPrefix;
import com.mds.group.purchase.wechatbot.model.WechatBot;
import com.mds.group.purchase.wechatbot.model.WechatbotGroup;
import com.mds.group.purchase.wechatbot.service.WechatBotService;
import com.mds.group.purchase.wechatbot.service.WechatbotGroupService;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import tk.mybatis.mapper.entity.Condition;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * The type Wechatbot group util.
 *
 * @author pavawi
 */
@Component
public class WechatbotGroupUtil {

    @Resource
    private RedisTemplate redisTemplate;
    @Resource
    private WechatBotService wechatBotService;
    @Resource
    private WechatbotGroupService wechatbotGroupService;


    /**
     * Flush binded group cache.
     *
     * @param appmodelId the appmodel id
     * @param botId      the bot id
     */
    public void flushBindedGroupCache(String appmodelId, Long botId) {
        //获取机器人uin
        WechatBot bot = wechatBotService.findBy("botId", botId);
        if (null == bot) {
            return;
        }
        String botUin = bot.getBotUin().toString();
        //获取机器人下所有已绑定的群
        Condition condition = new Condition(WechatbotGroup.class);
        condition.createCriteria().andEqualTo("appmodelId", appmodelId).andEqualTo("wechatbotId", botId);
        List<WechatbotGroup> groupList = wechatbotGroupService.findByCondition(condition);
        String redisKey = GroupMallProperties.getRedisPrefix().concat(appmodelId).concat(RedisPrefix.BOT_GROUP)
                .concat(botUin);
        Map<String, String> map = groupList.stream()
                .collect(Collectors.toMap(k -> k.getWechatGroupName(), v -> v.getGroupLeaderId(),(old, newV)->old));
        redisTemplate.opsForValue().set(redisKey,map);


    }
}
