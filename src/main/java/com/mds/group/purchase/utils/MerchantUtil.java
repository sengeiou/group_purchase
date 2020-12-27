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


import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.mds.group.purchase.configurer.GroupMallProperties;
import com.mds.group.purchase.constant.RedisPrefix;
import com.mds.group.purchase.constant.Url;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.HashMap;

/**
 * The type Merchant util.
 *
 * @author pavawi
 */
@Component
public class MerchantUtil {

    @Resource
    private RedisTemplate<String, String> redisTemplate;


    /**
     * Gets mini logo.
     *
     * @param appmodelId the appmodel id
     * @return the mini logo
     */
    public String getMiniLogo(String appmodelId) {
        String redisKey = GroupMallProperties.getRedisPrefix() + appmodelId + RedisPrefix.MINI_LOGO;

        String logo = redisTemplate.opsForValue().get(redisKey);
        if (StringUtils.isBlank(logo)) {
            HashMap<String, Object> paramMap = new HashMap<>();
            paramMap.put("appmodelId", appmodelId);
            String result = HttpUtil.get(Url.GET_MINI_LOGO_URL, paramMap);
            JSONObject jsonObject = JSONUtil.parseObj(result);
            JSONObject object = new JSONObject(jsonObject.get("object"));
            logo = object.get("logo", String.class);
        }
        return logo;
    }

}
