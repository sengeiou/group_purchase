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

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.mds.group.purchase.core.Result;
import com.mds.group.purchase.core.ResultGenerator;
import com.mds.group.purchase.logistics.model.Provinces;
import com.mds.group.purchase.logistics.result.PCAResult;
import com.mds.group.purchase.logistics.result.ProvincesResult;
import com.mds.group.purchase.logistics.service.CitiesService;
import com.mds.group.purchase.logistics.service.ProvincesService;
import io.swagger.annotations.*;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.List;


/**
 * The type Provinces controller.
 *
 * @author shuke
 * @date 2018 /11/27
 */
@RestController
@RequestMapping("/provinces")
@Api(tags = "所有接口")
public class ProvincesController {

    @Resource
    private CitiesService citiesService;
    @Resource
    private ProvincesService provincesService;

    /**
     * 获取所有省份信息的json字符串
     *
     * @param appmodelId the appmodel id
     * @return the result
     */
    @ApiOperation(value = "获取所有省份信息的json字符串", tags = "查询接口")
    @ApiResponses({
            @ApiResponse(code = 200, message = "success", response = PCAResult.class),
    })
    @GetMapping("/v1/all")
    public Result list(@RequestHeader @NotBlank String appmodelId) {
        List<PCAResult> list = provincesService.getAll(appmodelId);
        return ResultGenerator.genSuccessResult(JSON.toJSONString(list));
    }

    /**
     * 获取所有省份信息
     *
     * @param page the page
     * @param size the size
     * @return the result
     */
    @ApiOperation(value = "获取所有省份信息", tags = "查询接口")
    @ApiResponses({
            @ApiResponse(code = 200, message = "success", response = Provinces.class),
    })
    @GetMapping("/v1/allProvince")
    public Result allProvince(@ApiParam(value = "当前页码，默认为0") @RequestParam(defaultValue = "0") Integer page,
                              @ApiParam(value = "页面数据数量，默认为0查询所有") @RequestParam(defaultValue = "0") Integer size) {
        PageHelper.startPage(page, size);
        List<Provinces> list = provincesService.findAll();
        List<ProvincesResult> results = new ArrayList<>();
        for (Provinces p : list) {
            ProvincesResult pr = new ProvincesResult(p);
            int citiesCount = citiesService.countCitiesByProvinceId(p.getProvinceid());
            if (citiesCount <= 0) {
                pr.setIsNode(false);
            } else {
                pr.setIsNode(true);
            }
            results.add(pr);
        }
        PageInfo<ProvincesResult> pageInfo = new PageInfo<>(results);
        return Result.success(pageInfo);
    }

    /**
     * 小程序端申请团长时，获取包含小区的省份信息
     *
     * @param appmodelId 模板id
     * @return 省集合 result
     */
    @ApiOperation(value = "小程序端申请团长时，获取包含小区的省份信息", tags = "v1.2版本接口")
    @GetMapping("/wx/v2/group")
    public Result haveCommunities(@RequestHeader @NotBlank String appmodelId) {
        List<PCAResult> pcaResults = provincesService.haveCommunities(appmodelId);
        return Result.success(pcaResults);
    }

}
