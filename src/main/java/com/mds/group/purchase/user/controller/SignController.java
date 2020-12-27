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

package com.mds.group.purchase.user.controller;

import com.mds.group.purchase.configurer.WXPMServiceFactory;
import com.mds.group.purchase.core.CodeMsg;
import com.mds.group.purchase.core.Result;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * The type Sign controller.
 *
 * @author pavawi
 */
@RestController
@RequestMapping("/h5/sign")
public class SignController {

    private Logger logger = LoggerFactory.getLogger(SignController.class);

    @Resource
    private WXPMServiceFactory wxpmServiceFactory;

    /**
     * 构建js-sdk所需配置
     *
     * @param url        the url
     * @param appmodelId the appmodel id
     * @return sign sign
     */
    @GetMapping(value = "js-api-signature")
    public Result getSign(@RequestParam String url, @RequestHeader String appmodelId) {
        try {
            WxMpService wxMpService = wxpmServiceFactory.getWxMpService(appmodelId);
            return Result.success(wxMpService.createJsapiSignature(url));
        } catch (WxErrorException e) {
            logger.error(e.toString());
            return Result.error(CodeMsg.FAIL.fillArgs("获取签名失败"));
        }
    }

}
