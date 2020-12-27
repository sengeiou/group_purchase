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

import com.mds.group.purchase.core.Result;
import com.mds.group.purchase.shop.model.Footer;
import com.mds.group.purchase.shop.service.FooterService;
import com.mds.group.purchase.shop.vo.FooterInfoVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.List;


/**
 * 底部导航
 *
 * @author Created by wx on 2018/06/04.
 */
@Api(tags = "所有接口")
@RequestMapping("/footer")
@RestController
public class FooterController {

    @Resource
    private FooterService footerService;

    /**
     * Find by appmodel id result.
     *
     * @param appmodelId the appmodel id
     * @return the result
     */
    @ApiOperation(value = "查询底部导航", tags = "查询接口")
    @GetMapping("/v1/info")
    public Result<List<FooterInfoVO>> findByAppmodelId(
            @RequestHeader @NotBlank(message = "appmodelId不能为空") @Size(max = 27, min = 27, message = "商品标示错误") String appmodelId) {
        List<FooterInfoVO> footers = footerService.findByAppmoedelId(appmodelId);
        return Result.success(footers);
    }

    /**
     * Update result.
     *
     * @param footer the footer
     * @return the result
     */
    @ApiOperation(value = "编辑/开启关闭底部导航", tags = "更新接口")
    @PutMapping("/v1/update")
    public Result update(@RequestBody Footer footer) {
        int result = footerService.update(footer);
        if (result > 0) {
            return Result.success("更新成功");
        } else {
            return Result.success("更新失败");
        }
    }

    /**
     * Update sort result.
     *
     * @param footerIds the footer ids
     * @return the result
     */
    @ApiOperation(value = "编辑底部导航排序", tags = "更新接口")
    @PutMapping("/v1/update/sort")
    public Result updateSort(@RequestBody String footerIds) {
        int result = footerService.updateSort(footerIds);
        if (result > 0) {
            return Result.success("更新成功");
        } else {
            return Result.success("请确认推荐和我的栏目顺序");
        }
    }
}
