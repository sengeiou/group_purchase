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

package com.mds.group.purchase.configurer;

import cn.hutool.core.collection.CollectionUtil;
import com.mds.group.purchase.shop.model.Wxmp;
import com.mds.group.purchase.shop.service.WxmpService;
import me.chanjar.weixin.mp.api.WxMpInRedisConfigStorage;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.api.impl.WxMpServiceImpl;
import org.springframework.stereotype.Component;
import redis.clients.jedis.JedisPool;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * The type Wxpm service factory.
 *
 * @author pavawi
 */
@Component
public class WXPMServiceFactory {

    @Resource
    private JedisPool jedisPool;
    @Resource
    private WxmpService wxmpService;

    private static Map<String, WxMpService> wxMpServiceMap= new ConcurrentHashMap<>();

    /**
     * Init.
     */
    public void init(){
        List<Wxmp> all = wxmpService.findAll();
        if (CollectionUtil.isNotEmpty(all)) {
            for (Wxmp wxmp : all) {
                WxMpInRedisConfigStorage wxMpConfigStorage = new WxMpInRedisConfigStorage(jedisPool);
                wxMpConfigStorage.setAppId(wxmp.getMpAppid());
                wxMpConfigStorage.setAesKey(wxmp.getMpAeskey());
                wxMpConfigStorage.setSecret(wxmp.getMpSecret());
                wxMpConfigStorage.setToken(wxmp.getMpToken());
                WxMpService wxMpService = new WxMpServiceImpl();
                wxMpService.setWxMpConfigStorage(wxMpConfigStorage);
                wxMpServiceMap.put(wxmp.getAppmodelId(), wxMpService);
            }
        }
    }

    /**
     * Wx mp service map flush.
     *
     * @param appmodelId the appmodel id
     */
    public void wxMpServiceMapFlush(String appmodelId){
        Wxmp wxmp = wxmpService.findBy("appmodelId", appmodelId);
        WxMpInRedisConfigStorage wxMpConfigStorage = new WxMpInRedisConfigStorage(jedisPool);
        wxMpConfigStorage.setAppId(wxmp.getMpAppid());
        wxMpConfigStorage.setAesKey(wxmp.getMpAeskey());
        wxMpConfigStorage.setSecret(wxmp.getMpSecret());
        wxMpConfigStorage.setToken(wxmp.getMpToken());
        WxMpService wxMpService = new WxMpServiceImpl();
        wxMpService.setWxMpConfigStorage(wxMpConfigStorage);
        wxMpServiceMap.put(appmodelId, wxMpService);
    }

    /**
     * Get wx mp service wx mp service.
     *
     * @param appmodelId the appmodel id
     * @return the wx mp service
     */
    public WxMpService getWxMpService(String appmodelId){
        if (wxMpServiceMap == null || wxMpServiceMap.size()==0) {
            init();
        }
        return wxMpServiceMap.get(appmodelId);
    }

}
