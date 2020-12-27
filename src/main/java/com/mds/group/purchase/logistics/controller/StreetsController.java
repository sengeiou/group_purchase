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
import com.mds.group.purchase.logistics.result.StreetsResult;
import com.mds.group.purchase.logistics.service.StreetsService;
import com.mds.group.purchase.logistics.vo.StreetsVo;
import io.swagger.annotations.*;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * The type Streets controller.
 *
 * @author shuke
 * @date 2018 /11/27
 */
@RestController
@RequestMapping("/streets")
@Api(tags = "所有接口")
@Validated
public class StreetsController {

    @Resource
    private StreetsService streetsService;

    /**
     * 获取对应县的所有街道信息
     *
     * @param appmodelId the appmodel id
     * @param areaId     the area id
     * @param page       the page
     * @param size       the size
     * @return the streets
     */
    @ApiOperation(value = "获取对应县的所有街道信息", tags = "查询接口")
    @ApiResponses({@ApiResponse(code = 200, message = "success", response = StreetsResult.class),})
    @GetMapping("/v1/areaId")
    public Result<PageInfo<StreetsResult>> getStreets(@RequestHeader @NotNull String appmodelId,
                                                      @ApiParam(value = "县id", required = true) @NotNull @RequestParam String areaId,
                                                      @ApiParam(value = "当前页码，默认为0") @RequestParam(defaultValue = "0") Integer page,
                                                      @ApiParam(value = "页面数据数量，默认为0查询所有") @RequestParam(defaultValue = "0") Integer size) {
        PageHelper.startPage(page, size);
        List<StreetsResult> streets = streetsService.getStreetsByAreaId(areaId, appmodelId);
        PageInfo<StreetsResult> pageInfo = new PageInfo<>(streets);
        return ResultGenerator.genSuccessResult(pageInfo);
    }

    /**
     * 批量更新区域信息
     *
     * @param appmodelId the appmodel id
     * @param streetsVo  the streets vo
     * @return the result
     */
    @ApiOperation(value = "批量更新区域信息", tags = "更新接口")
    @PutMapping("/v1/updateBatch")
    public Result updateStreets(@ApiParam(value = "小程序模板id", required = true) @NotNull @RequestHeader String appmodelId,
                                @ApiParam(value = "区域对象集合", required = true) @Valid @RequestBody List<StreetsVo> streetsVo) {
        streetsService.updateBatch(appmodelId, streetsVo);
        return Result.success(true);
    }

    /**
     * 获取当前小程序的所有（区域）街道
     *
     * @param appmodelId the appmodel id
     * @return the result
     */
    @ApiOperation(value = "获取当前小程序的所有（区域）街道", tags = "查询接口")
    @GetMapping("/v1/all")
    public Result updateStreets(
            @ApiParam(value = "小程序模板id", required = true) @NotNull @RequestHeader String appmodelId) {
        List<StreetsResult> list = streetsService.findByAppmodelId(appmodelId);
        return Result.success(list);
    }

    /**
     * 删除指定街道
     *
     * @param streetId   the street id
     * @param appmodelId the appmodel id
     * @return the result
     */
    @ApiOperation(value = "删除指定街道", tags = "删除接口")
    @DeleteMapping("/v1/delete")
    public Result delStreets(@ApiParam(value = "区域id") @NotNull @RequestParam Long streetId,
                             @RequestHeader @NotBlank String appmodelId) {
        streetsService.deleteStreet(streetId, appmodelId);
        return Result.success();
    }
}
