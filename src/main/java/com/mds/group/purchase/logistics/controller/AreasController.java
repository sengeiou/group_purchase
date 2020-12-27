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

package com.mds.group.purchase.logistics.controller;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.mds.group.purchase.core.Result;
import com.mds.group.purchase.core.ResultGenerator;
import com.mds.group.purchase.logistics.result.AreaResult;
import com.mds.group.purchase.logistics.service.AreasService;
import com.mds.group.purchase.logistics.service.StreetsService;
import io.swagger.annotations.*;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.constraints.NotNull;
import java.util.List;


/**
 * The type Areas controller.
 *
 * @author shuke
 * @date 2018 /11/27
 */
@RestController
@RequestMapping("/areas")
@Api(tags = "所有接口")
@Validated
public class AreasController {

    @Resource
    private AreasService areasService;
    @Resource
    private StreetsService streetsService;

    /**
     * 获取对应市的所有县信息
     *
     * @param cityId the city id
     * @param page   the page
     * @param size   the size
     * @return the result
     */
    @ApiOperation(value = "获取对应市的所有县信息", tags = "查询接口")
    @ApiResponses({
            @ApiResponse(code = 200, message = "success", response = AreaResult.class),
    })
    @GetMapping("/v1/cityId")
    public Result list(@ApiParam(value = "市id", required = true) @NotNull @RequestParam String cityId,
                       @ApiParam(value = "当前页码，默认为0") @RequestParam(defaultValue = "0") Integer page,
                       @ApiParam(value = "页面数据数量，默认为0查询所有") @RequestParam(defaultValue = "0") Integer size) {
        PageHelper.startPage(page, size);
        List<AreaResult> list = areasService.getAreasByCityId(cityId);
        for (AreaResult ar : list) {
            int streetsCount = streetsService.countStreetsByAreaId(ar.getValue());
            if (streetsCount <= 0) {
                ar.setIsNode(false);
            } else {
                ar.setIsNode(true);
            }
        }
        PageInfo<AreaResult> pageInfo = new PageInfo<>(list);
        return ResultGenerator.genSuccessResult(pageInfo);
    }
}
