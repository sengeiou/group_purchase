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

package com.mds.group.purchase.shop.controller;

import com.mds.group.purchase.configurer.WxConfiguration;
import com.mds.group.purchase.core.Result;
import com.mds.group.purchase.core.ResultGenerator;
import com.mds.group.purchase.shop.service.ManagerService;
import com.mds.group.purchase.shop.vo.UpdateManagerVO;
import io.swagger.annotations.ApiOperation;
import me.chanjar.weixin.common.error.WxErrorException;
import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * The type Manager controller.
 *
 * @author CodeGenerator
 * @date 2018 /11/27
 */
@RestController
@RequestMapping("/manager")
public class ManagerController {

    @Resource
    private ManagerService managerService;

    @Resource
    private WxConfiguration wxPayConfiguration;

    private Logger logger = LoggerFactory.getLogger(ManagerController.class);

    /**
     * Update manager result.
     *
     * @param updateManagerVO the update manager vo
     * @return the result
     */
    @PutMapping("/v1/updateManager")
    @ApiOperation(value = "更新商户秘钥/商户号/商户证书", tags = "更新接口")
    public Result updateManager(@RequestBody UpdateManagerVO updateManagerVO) {
        if (updateManagerVO.getAppid() == null) {
            return ResultGenerator.genFailResult("appId不能为空");
        }
        if (managerService.updateSecretKey(updateManagerVO.getAppid(), updateManagerVO.getCertificatePath(),
                updateManagerVO.getMchId(), updateManagerVO.getMchKey()) > 0) {
            return ResultGenerator.genSuccessResult();
        }
        return ResultGenerator.genFailResult("更新失败");
    }


    /**
     * Plat send mini program template message string.
     *
     * @param appmodelId   the appmodel id
     * @param forceRefresh the force refresh
     * @return the string
     * @throws WxErrorException the wx error exception
     */
    @GetMapping("/token")
    @ApiOperation(value = "获取access_token的接口", tags = "")
    @Deprecated
    public String platSendMiniProgramTemplateMessage(@RequestHeader String appmodelId,
                                                     @RequestParam boolean forceRefresh) throws WxErrorException {
        String authorizerAppid = appmodelId.substring(9);
        return wxPayConfiguration.getWxOpenService().getWxOpenComponentService()
                .getAuthorizerAccessToken(authorizerAppid, forceRefresh);

    }

    /**
     * Gets wxacode unlimited.
     *
     * @param appmodelId   the appmodel id
     * @param forceRefresh the force refresh
     * @param scene        the scene
     * @param page         the page
     * @return the wxacode unlimited
     * @throws WxErrorException the wx error exception
     */
    @GetMapping("getWxacode")
    public String getWxacodeUnlimited(@RequestHeader String appmodelId, @RequestParam boolean forceRefresh,
                                      @RequestParam String scene, @RequestParam String page) throws WxErrorException {
        RestTemplate restTemplate = new RestTemplate();
        String authorizerAppid = appmodelId.substring(9);
        String authorizerAccessToken = wxPayConfiguration.getWxOpenService().getWxOpenComponentService()
                .getAuthorizerAccessToken(authorizerAppid, forceRefresh);
        String url = "https://api.weixin.qq.com/wxa/getwxacodeunlimit?access_token=" + authorizerAccessToken;
        Map<String, Object> param = new HashMap<>(4);
        param.put("scene", scene);
        param.put("page", page);
        param.put("width", 430);
        param.put("auto_color", false);
        Map<String, Object> lineColor = new HashMap<>(4);
        lineColor.put("r", 0);
        lineColor.put("g", 0);
        lineColor.put("b", 0);
        param.put("line_color", lineColor);
        logger.info("调用生成微信URL接口传参:" + param);
        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        HttpEntity requestEntity = new HttpEntity<>(param, headers);
        ResponseEntity<byte[]> entity = restTemplate
                .exchange(url, HttpMethod.POST, requestEntity, byte[].class);
        byte[] result = entity.getBody();
        return Base64.encodeBase64String(result);
    }

}
