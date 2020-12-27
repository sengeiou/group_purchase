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

import com.github.pagehelper.PageInfo;
import com.mds.group.purchase.core.Result;
import com.mds.group.purchase.core.ResultGenerator;
import com.mds.group.purchase.goods.vo.UpdateGoodsAreaVo;
import com.mds.group.purchase.logistics.result.GoodsAreaMappingLineResultV2;
import com.mds.group.purchase.logistics.result.GoodsAreaMappingResult;
import com.mds.group.purchase.logistics.service.GoodsAreaMappingService;
import com.mds.group.purchase.logistics.vo.GoodsAreaSearchVo;
import com.mds.group.purchase.logistics.vo.GoodsAreaVo;
import com.mds.group.purchase.utils.PageUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * The type Goods area mapping controller.
 *
 * @author CodeGenerator
 * @date 2018 /12/20
 */
@RestController
@RequestMapping("/goods/area/mapping")
@Api(tags = "所有接口")
public class GoodsAreaMappingController {

    @Resource
    private GoodsAreaMappingService goodsAreaMappingService;

    /**
     * 新建一个投放区域
     *
     * @param goodsAreaVo 投放区域参数
     * @param appmodelId  the appmodel id
     * @return 无 result
     */
    @ApiOperation(value = "新建一个投放区域", tags = "新增接口")
    @PostMapping("/v1/save")
    public Result add(@RequestBody GoodsAreaVo goodsAreaVo, @RequestHeader @NotBlank String appmodelId) {
        goodsAreaVo.setAppmodelId(appmodelId);
        goodsAreaMappingService.saveGoodsAreaAndFlushCache(goodsAreaVo);
        return Result.success();
    }

    /**
     * 得到投放区域
     *
     * @param appmodelId        the appmodel id
     * @param goodsAreaSearchVo the goods area search vo
     * @return List<GoodsAreaMappingResult>  result
     */
    @ApiOperation(value = "得到投放区域", tags = "查询接口")
    @PostMapping("/v1/list")
    public Result list(@ApiParam("小程序模板id") @RequestHeader @NotBlank String appmodelId,
                       @RequestBody @Valid GoodsAreaSearchVo goodsAreaSearchVo) {
        if (goodsAreaSearchVo == null) {
            goodsAreaSearchVo = new GoodsAreaSearchVo();
        }
        goodsAreaSearchVo.setAppmodelId(appmodelId);
        List<GoodsAreaMappingResult> list = goodsAreaMappingService.findBySearch(goodsAreaSearchVo);
        PageInfo pageInfo = PageUtil.pageUtil(goodsAreaSearchVo.getPage(), goodsAreaSearchVo.getSize(), list);
        return ResultGenerator.genSuccessResult(pageInfo);
    }

    /**
     * 删除投放区域
     *
     * @param appmodelId the appmodel id
     * @param goodsId    商品id
     * @return 无 result
     */
    @ApiOperation(value = "删除投放区域", tags = "删除接口")
    @DeleteMapping("/v1/delete")
    public Result delete(@RequestHeader @NotBlank String appmodelId, @RequestParam @NotBlank String goodsId) {
        goodsAreaMappingService.deletebyGoodsIds(goodsId, appmodelId);
        return Result.success();
    }

    /**
     * 更新一个投放区域
     *
     * @param goodsAreaVo 商品投放区域参数
     * @param appmodelId  the appmodel id
     * @return 无 result
     */
    @ApiOperation(value = "更新一个投放区域", tags = "更新接口")
    @PostMapping("/v1/update")
    public Result update(@RequestBody GoodsAreaVo goodsAreaVo, @RequestHeader @NotBlank String appmodelId) {
        goodsAreaVo.setAppmodelId(appmodelId);
        goodsAreaMappingService.updateGoodsAreaMapping(goodsAreaVo);
        return Result.success();
    }

    /**
     * 更新投放区域
     *
     * @param goodsAreaVo 商品投放区域参数
     * @param appmodelId  the appmodel id
     * @return 无 result
     * @since v1.2版本接口
     */
    @ApiOperation(value = "更新一个投放区域，v1.2版本接口", tags = "v1.2版本接口")
    @PostMapping("/v2/update")
    public Result updateV2(@RequestBody UpdateGoodsAreaVo goodsAreaVo, @RequestHeader @NotBlank String appmodelId) {
        goodsAreaVo.setAppmodelId(appmodelId);
        goodsAreaMappingService.updateGoodsAreaMappingBatch(goodsAreaVo);
        return Result.success();
    }

    /**
     * 获取单个商品的投放区域
     *
     * @param goodsId    商品id
     * @param appmodelId the appmodel id
     * @return GoodsAreaMappingLineResult2 by goods id v 2
     * @since v1.2版本
     */
    @ApiOperation(value = "获取单个商品的投放区域，v1.2版本接口", tags = "v1.2版本接口")
    @GetMapping("/v2/goodsId/get")
    public Result getByGoodsIdV2(@ApiParam("商品id") @RequestParam @NotNull Long goodsId,
                                 @ApiParam("小程序模板id") @RequestHeader @NotBlank String appmodelId) {
        List<GoodsAreaMappingLineResultV2> list = goodsAreaMappingService.findByGoodsId(goodsId, appmodelId);
        return Result.success(list);
    }

    /**
     * 新增商品设置投放区域和批量设置投放区域时获取可以选择投放区域
     *
     * @param appmodelId the appmodel id
     * @return GoodsAreaMappingLineResult2 result
     * @since v1.2版本
     */
    @ApiOperation(value = "获取单个商品的投放区域，v1.2版本接口", tags = "v1.2版本接口")
    @GetMapping("/v2/can/pick")
    public Result canPickV2(@ApiParam("小程序模板id") @RequestHeader @NotBlank String appmodelId) {
        List<GoodsAreaMappingLineResultV2> list = goodsAreaMappingService.canPick(appmodelId);
        return Result.success(list);
    }

}
