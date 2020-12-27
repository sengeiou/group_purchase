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
import com.mds.group.purchase.logistics.result.CityResult;
import com.mds.group.purchase.logistics.result.CityResultHaveGroup;
import com.mds.group.purchase.logistics.service.AreasService;
import com.mds.group.purchase.logistics.service.CitiesService;
import com.mds.group.purchase.utils.GeoCodeUtil;
import io.swagger.annotations.*;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;

/**
 * The type Cities controller.
 *
 * @author CodeGenerator
 * @date 2018 /11/27
 */
@RestController
@Validated
@RequestMapping("/cities")
@Api(tags = "所有接口")
public class CitiesController {

    @Resource
    private AreasService areasService;
    @Resource
    private CitiesService citiesService;

    /**
     * 获取对应省份的所有市信息
     *
     * @param provinceId the province id
     * @param page       the page
     * @param size       the size
     * @return the cities by province id
     */
    @ApiOperation(value = "获取对应省份的所有市信息", tags = "查询接口")
    @ApiResponses({
            @ApiResponse(code = 200, message = "success", response = CityResult.class),
    })
    @GetMapping("/v1/provinceId")
    public Result getCitiesByProvinceId(@ApiParam(value = "省id", required = true) @NotNull @RequestParam("provinceId") String provinceId,
                                        @ApiParam(value = "当前页码，默认为0") @RequestParam(defaultValue = "0") Integer page,
                                        @ApiParam(value = "页面数据数量，默认为0查询所有") @RequestParam(defaultValue = "0") Integer size) {
        PageHelper.startPage(page, size);
        List<CityResult> cities = citiesService.getCitiesByProvinceId(provinceId);
        for (CityResult cr : cities) {
            int areasCount = areasService.countAreasByCityId(cr.getValue());
            if (areasCount <= 0) {
                cr.setIsNode(false);
            } else {
                cr.setIsNode(true);
            }
        }
        PageInfo<CityResult> pageInfo = new PageInfo<>(cities);
        return Result.success(pageInfo);
    }


    /**
     * 获取包含团长的小区的所有城市
     *
     * @param appmodelId the appmodel id
     * @return the cities have group
     */
    @ApiOperation(value = "获取包含团长的小区的所有城市", tags = "查询接口")
    @ApiResponses({
            @ApiResponse(code = 200, message = "success", response = CityResultHaveGroup.class),
    })
    @GetMapping("/wx/v1/haveGroup")
    public Result getCitiesHaveGroup(@ApiParam(value = "小程序模板id") @NotBlank @RequestHeader String appmodelId) {
        Map<String, List<CityResultHaveGroup>> citiesHaveGroup = citiesService.getCitiesHaveGroup(appmodelId);
        return Result.success(citiesHaveGroup);
    }

    /**
     * 根据城市名获取经纬度
     *
     * @param cityName the city name
     * @return the location by city name
     */
    @GetMapping("/location/cityName")
    public Result getLocationByCityName(@RequestParam("cityName") String cityName) {
        String longitudeAndLatitude = GeoCodeUtil.getLongitudeAndLatitude(cityName);
        return Result.success(longitudeAndLatitude);
    }


}
