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

import com.mds.group.purchase.configurer.WXPMServiceFactory;
import com.mds.group.purchase.core.Result;
import com.mds.group.purchase.shop.model.Wxmp;
import com.mds.group.purchase.shop.service.WxmpService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.constraints.NotBlank;
import java.util.Map;

/**
 * Created by CodeGenerator on 2019/05/25.
 *
 * @author pavawi
 */
@RestController
@RequestMapping("/wxmp")
public class WxmpController {
    @Resource
    private WxmpService wxmpService;
    @Resource
    private WXPMServiceFactory wxpmServiceFactory;


    /**
     * Sets wxmp info.
     *
     * @param appmodelId the appmodel id
     * @param param      the param
     * @return the wxmp info
     */
    @PostMapping("/setting")
    public Result setWXMPInfo(@RequestHeader @NotBlank String appmodelId, @RequestBody Map<String, String> param) {
        String mpAppid = param.get("mpAppid");
        String mpSecret = param.get("mpSecret");
        String mpAesKey = param.get("mpAesKey");
        String mpToken = param.get("mpToken");
        Wxmp wxmp = wxmpService.findBy("appmodelId", appmodelId);
        if (wxmp == null) {
            wxmp = new Wxmp();
            wxmp.setAppmodelId(appmodelId);
            wxmp.setCreateTime(System.currentTimeMillis());
            wxmp.setModifyTime(System.currentTimeMillis());
            wxmp.setMpAeskey(mpAesKey);
            wxmp.setMpToken(mpToken);
            wxmp.setMpAppid(mpAppid);
            wxmp.setMpSecret(mpSecret);
            wxmpService.save(wxmp);
        } else {
            wxmp.setModifyTime(System.currentTimeMillis());
            wxmp.setMpAeskey(mpAesKey);
            wxmp.setMpToken(mpToken);
            wxmp.setMpAppid(mpAppid);
            wxmp.setMpSecret(mpSecret);
            wxmpService.update(wxmp);
        }
        //刷新WxmpFactory
        wxpmServiceFactory.wxMpServiceMapFlush(appmodelId);
        return Result.success();
    }

    /**
     * Wxmp info result.
     *
     * @param appmodelId the appmodel id
     * @return the result
     */
    @GetMapping("/setting")
    public Result wxmpInfo(@RequestHeader @NotBlank String appmodelId) {
        Wxmp wxmp = wxmpService.findBy("appmodelId", appmodelId);
        return Result.success(wxmp);
    }

}
