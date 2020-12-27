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

import com.github.pagehelper.Page;
import com.github.pagehelper.PageInfo;
import com.mds.group.purchase.core.Result;
import com.mds.group.purchase.logistics.model.Line;
import com.mds.group.purchase.logistics.result.LineResult;
import com.mds.group.purchase.logistics.service.LineService;
import com.mds.group.purchase.logistics.vo.LineAddCommunityV12Vo;
import com.mds.group.purchase.logistics.vo.LineGetVo;
import com.mds.group.purchase.logistics.vo.LineV12Vo;
import com.mds.group.purchase.logistics.vo.LineVo;
import io.swagger.annotations.*;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.util.List;
import java.util.Map;

/**
 * The type Line controller.
 *
 * @author shuke
 * @date 2018 /11/27
 */
@RestController
@RequestMapping("/line")
@Validated
@Api(tags = "所有接口")
public class LineController {

    @Resource
    private LineService lineService;

    /**
     * 新建线路
     *
     * @param lineVo     线路新建参数
     * @param appmodelId the appmodel id
     * @return 无 result
     */
    @ApiOperation(value = "新建线路", tags = "新增接口")
    @ApiResponses({@ApiResponse(code = 200, message = "success"),})
    @PostMapping(value = "/v1/save")
    public Result saveLine(@Valid @RequestBody LineVo lineVo, @RequestHeader String appmodelId) {
        lineVo.setAppmodelId(appmodelId);
        lineService.saveLine(lineVo);
        return Result.success(true);
    }

    /**
     * 新建线路
     *
     * @param lineVo     线路新建参数
     * @param appmodelId 模板id
     * @return 无 result
     * @since v1.2
     */
    @ApiOperation(value = "新建线路", tags = "v1.2接口")
    @ApiResponses({@ApiResponse(code = 200, message = "success"),})
    @PostMapping(value = "/v1.2/save")
    public Result saveLineV12(@Valid @RequestBody LineV12Vo lineVo, @RequestHeader String appmodelId) {
        lineVo.setAppmodelId(appmodelId);
        lineService.saveLineV12(lineVo);
        return Result.success(true);
    }

    /**
     * 线路中的小区 添加/更改
     *
     * @param lineVo     线路新建参数
     * @param appmodelId 模板id
     * @return 无 result
     * @since v1.2
     */
    @ApiOperation(value = "为线路添加/更改小区", tags = "v1.2接口")
    @ApiResponses({@ApiResponse(code = 200, message = "success"),})
    @PostMapping(value = "/v1.2/add/community")
    public Result lineAddCommunity(@Valid @RequestBody LineAddCommunityV12Vo lineVo, @RequestHeader String appmodelId) {
        lineVo.setAppmodelId(appmodelId);
        lineService.lineAddCommunity(lineVo);
        return Result.success(true);
    }

    /**
     * 获取线路信息
     *
     * @param lineGetVo  the line get vo
     * @param appmodelId the appmodel id
     * @return the line
     */
    @ApiOperation(value = "获取线路信息", tags = "查询接口")
    @ApiResponses({@ApiResponse(code = 200, message = "success", response = LineResult.class),})
    @PostMapping(value = "/v1/condition")
    public Result getLine(@ApiParam(value = "线路查询参数") @RequestBody LineGetVo lineGetVo,
                          @RequestHeader @NotBlank String appmodelId) {
        Map<String, Object> map = lineService.getLine(lineGetVo, appmodelId);
        Page page = (Page) map.get("page");
        List<LineResult> line = (List<LineResult>) map.get("list");
        PageInfo pageInfo = new PageInfo(page);
        pageInfo.setList(line);
        return Result.success(pageInfo);
    }

    /**
     * 获取线路信息
     *
     * @param lineGetVo  the line get vo
     * @param appmodelId the appmodel id
     * @return the line v 12
     */
    @ApiOperation(value = "获取线路信息", tags = "v1.2接口")
    @ApiResponses({@ApiResponse(code = 200, message = "success", response = LineResult.class),})
    @PostMapping(value = "/v1.2/condition")
    public Result getLineV12(@ApiParam(value = "线路查询参数") @RequestBody LineGetVo lineGetVo,
                             @RequestHeader @NotBlank String appmodelId) {
        Map<String, Object> map = lineService.getLineV12(lineGetVo, appmodelId);
        Page page = (Page) map.get("page");
        List<LineResult> line = (List<LineResult>) map.get("list");
        PageInfo pageInfo = new PageInfo(page);
        pageInfo.setList(line);
        return Result.success(pageInfo);
    }

    /**
     * 修改一条线路信息
     *
     * @param lineVo the line vo
     * @return the result
     */
    @ApiOperation(value = "修改一条线路信息", tags = "更新接口")
    @ApiResponses({@ApiResponse(code = 200, message = "success"),})
    @PutMapping(value = "/v1/update")
    public Result modifyLine(@RequestBody @Valid LineVo lineVo) {
        lineService.updateLine(lineVo);
        return Result.success(true);
    }

    /**
     * 修改一条线路信息
     *
     * @param lineVo the line vo
     * @return the result
     */
    @ApiOperation(value = "修改一条线路信息", tags = "v1.2接口")
    @ApiResponses({@ApiResponse(code = 200, message = "success"),})
    @PutMapping(value = "/v1.2/update")
    public Result modifyLineV12(@RequestBody @Valid LineV12Vo lineVo) {
        lineService.updateLineV12(lineVo);
        return Result.success(true);
    }

    /**
     * 删除指定的线路信息
     *
     * @param lineIds    the line ids
     * @param appmodelId the appmodel id
     * @return the result
     */
    @ApiOperation(value = "删除指定的线路信息", tags = "删除接口")
    @ApiResponses({@ApiResponse(code = 200, message = "success"),})
    @DeleteMapping(value = "/v1/delete")
    public Result removeLine(@ApiParam(value = "线路id", required = true) @RequestParam @NotBlank String lineIds,
                             @RequestHeader String appmodelId) {
        lineService.deleteLineByIds(lineIds, appmodelId);
        return Result.success(true);
    }

    /**
     * 查询所有的线路和线路下的区域
     *
     * @param appmodelId the appmodel id
     * @return the line street community
     */
    @ApiOperation(value = "查询所有的线路和线路下的区域", tags = "查询接口")
    @ApiResponses({@ApiResponse(code = 200, message = "success"),})
    @GetMapping(value = "/v1/line/street/community")
    public Result getLineStreetCommunity(@RequestHeader @NotBlank String appmodelId) {
        return Result.success(lineService.getLineStreetCommunity(appmodelId));
    }

    /**
     * 查询所有的线路
     *
     * @param appmodelId the appmodel id
     * @return the lines
     */
    @ApiOperation(value = "查询所有的线路", tags = "v1.2版本接口")
    @ApiResponses({@ApiResponse(code = 200, message = "success"),})
    @GetMapping(value = "/v2/all")
    public Result getLines(@RequestHeader @NotBlank String appmodelId) {
        return Result.success(lineService.getAll(appmodelId));
    }

    /**
     * 查询指定区域的线路
     *
     * @param areaid     the areaid
     * @param appmodelId the appmodel id
     * @return the line street community
     */
    @ApiOperation(value = "查询指定区域的线路", tags = "查询接口")
    @ApiResponses({@ApiResponse(code = 200, message = "success"),})
    @GetMapping(value = "/v1/line/areaid")
    public Result<List<Line>> getLineStreetCommunity(@ApiParam(value = "区域id") String areaid,
                                                     @RequestHeader @NotBlank String appmodelId) {
        List<Line> lines = lineService.findByAreaid(areaid, appmodelId);
        return Result.success(lines);
    }
}
