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

import com.github.binarywang.wxpay.config.WxPayConfig;
import com.github.binarywang.wxpay.service.WxPayService;
import com.github.binarywang.wxpay.service.impl.WxPayServiceImpl;
import com.mds.group.purchase.exception.ServiceException;
import com.mds.group.purchase.shop.model.Manager;
import com.mds.group.purchase.shop.model.Wxmp;
import com.mds.group.purchase.shop.service.ManagerService;
import com.mds.group.purchase.shop.service.WxmpService;
import com.mongodb.MongoClientOptions;
import me.chanjar.weixin.open.api.WxOpenMaService;
import me.chanjar.weixin.open.api.WxOpenService;
import me.chanjar.weixin.open.api.impl.WxOpenInRedisConfigStorage;
import me.chanjar.weixin.open.api.impl.WxOpenServiceImpl;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import redis.clients.jedis.JedisPool;

import javax.annotation.Resource;

/**
 * The type Wx configuration.
 *
 * @author Binary Wang
 */
@Component
@EnableConfigurationProperties({WxComponentProperties.class})
public class WxConfiguration {

    @Resource
    private JedisPool jedisPool;
    @Resource
    private ManagerService managerService;
    @Resource
    private WxComponentProperties wxComponentProperties;

    /**
     * Mongo options mongo client options.
     *
     * @return the mongo client options
     */
    @Bean
    public MongoClientOptions mongoOptions() {
        return MongoClientOptions.builder().maxConnectionIdleTime(60000).build();
    }

    /**
     * Gets wx open service.
     *
     * @return the wx open service
     */
    @Bean
    public WxOpenService getWxOpenService() {
        WxOpenInRedisConfigStorage open = new WxOpenInRedisConfigStorage(this.jedisPool);
        open.setComponentAppId(this.wxComponentProperties.getComponentAppId());
        open.setComponentAppSecret(this.wxComponentProperties.getComponentSecret());
        open.setComponentToken(this.wxComponentProperties.getComponentToken());
        open.setComponentAesKey(this.wxComponentProperties.getComponentAesKey());
        WxOpenService wxOpenService = new WxOpenServiceImpl();
        wxOpenService.setWxOpenConfigStorage(open);
        return wxOpenService;
    }


    /**
     * Init wx pay service.
     *
     * @param appmodelId the appmodel id
     * @return the wx pay service
     */
    public WxPayService init(String appmodelId) {
        WxPayService wxPayService = new WxPayServiceImpl();
        Manager manager = managerService.findBy("appmodelId", appmodelId);
        WxPayConfig payConfig = new WxPayConfig();
        payConfig.setAppId(manager.getAppId());
        payConfig.setMchId(manager.getMchId().trim());
        payConfig.setMchKey(manager.getMchKey());
        if (manager.getCertificatePath() == null) {
            throw new ServiceException("商家支付证书不存在");
        }
        String path = this.wxComponentProperties.getKeyPath().replace("APPMODELID", manager.getAppmodelId())
                .concat(manager.getCertificatePath());
        payConfig.setKeyPath(path);
        wxPayService.setConfig(payConfig);

        return wxPayService;
    }

    @Resource
    private WxmpService wxmpService;

    /**
     * Init h 5 wx pay service.
     *
     * @param appmodelId the appmodel id
     * @return the wx pay service
     */
    public WxPayService initH5(String appmodelId) {
        WxPayService wxPayService = new WxPayServiceImpl();
        Manager manager = managerService.findBy("appmodelId", appmodelId);
        Wxmp wxmp = wxmpService.findBy("appmodelId", appmodelId);
        WxPayConfig payConfig = new WxPayConfig();
        payConfig.setAppId(wxmp.getMpAppid());
        payConfig.setMchId(manager.getMchId().trim());
        payConfig.setMchKey(manager.getMchKey());
        if (manager.getCertificatePath() == null) {
            throw new ServiceException("商家支付证书不存在");
        }
        String path = this.wxComponentProperties.getKeyPath().replace("APPMODELID", manager.getAppmodelId())
                .concat(manager.getCertificatePath());
        payConfig.setKeyPath(path);
        wxPayService.setConfig(payConfig);

        return wxPayService;
    }

    /**
     * Init wx open service wx open ma service.
     *
     * @param appmodelId the appmodel id
     * @return the wx open ma service
     */
    public WxOpenMaService initWxOpenService(String appmodelId) {
        Manager manager = managerService.findBy("appmodelId", appmodelId);
        return getWxOpenService().getWxOpenComponentService().getWxMaServiceByAppid(manager.getAppId());
    }
}
