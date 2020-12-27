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

package com.mds.group.purchase.solitaire.controller;

import com.mds.group.purchase.core.CodeMsg;
import com.mds.group.purchase.core.Result;
import com.mds.group.purchase.solitaire.service.SolitaireSettingService;
import com.mds.group.purchase.solitaire.vo.SolitaireSettingVo;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.constraints.NotBlank;

/**
 * 接龙设置
 *
 * @author shuke
 * @date 2019 /05/16
 */
@RestController
@RequestMapping("/solitaire/setting")
@Slf4j
public class SolitaireSettingController {
    @Resource
    private SolitaireSettingService solitaireSettingService;

    /**
     * 开启接龙
     *
     * @param appmodelId the appmodel id
     * @return the result
     */
    @ApiOperation(value = "开启接龙", tags = "更新接口")
    @PostMapping("/open")
    public Result openSplitaire(@RequestHeader @NotBlank @ApiParam("appmodelId") String appmodelId) {
        int res = solitaireSettingService.openSolitaire(appmodelId);
        return res != 0 ? Result.success("开启成功") : Result.error(CodeMsg.FAIL.fillArgs("开启失败"));
    }

    /**
     * 关闭接龙
     *
     * @param appmodelId the appmodel id
     * @return the result
     */
    @ApiOperation(value = "关闭接龙", tags = "更新接口")
    @PostMapping("/close")
    public Result closeSplitaire(@RequestHeader @ApiParam("appmodelId") String appmodelId) {
        int res = solitaireSettingService.closeSolitaire(appmodelId);
        return res != 0 ? Result.success("关闭成功") : Result.error(CodeMsg.FAIL.fillArgs("关闭失败"));
    }

    /**
     * 设置接龙参数
     *
     * @param appmodelId         the appmodel id
     * @param solitaireSettingVo the solitaire setting vo
     * @return the splitaire info
     */
    @ApiOperation(value = "设置接龙参数", tags = "更新接口")
    @PutMapping("/info")
    public Result setSplitaireInfo(@RequestHeader @ApiParam("appmodelId") String appmodelId,
                                   @RequestBody SolitaireSettingVo solitaireSettingVo) {
        solitaireSettingVo.setAppmodelId(appmodelId);
        solitaireSettingService.setSolitaireInfo(solitaireSettingVo);
        return Result.success("修改成功");
    }

    /**
     * 获取接龙设置
     *
     * @param appmodelId the appmodel id
     * @return the splitaire info
     */
    @ApiOperation(value = "获取接龙设置", tags = "查询接口")
    @ApiResponse(code = 200, message = "", response = SolitaireSettingVo.class)
    @GetMapping("/info")
    public Result getSplitaireInfo(@RequestHeader @ApiParam("appmodelId") String appmodelId) {
//		log.info("开始获取接龙设置appmodel"+appmodelId);
        SolitaireSettingVo res = solitaireSettingService.getSolitaireInfo(appmodelId);
//		log.info("获取接龙设置成功"+res.toString());
        return Result.success(res);
    }
}
