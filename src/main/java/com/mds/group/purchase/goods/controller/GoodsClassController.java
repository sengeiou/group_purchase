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

package com.mds.group.purchase.goods.controller;

import com.github.pagehelper.PageInfo;
import com.mds.group.purchase.core.Result;
import com.mds.group.purchase.goods.model.GoodsClass;
import com.mds.group.purchase.goods.result.GoodsClassResult;
import com.mds.group.purchase.goods.service.GoodsClassService;
import com.mds.group.purchase.goods.vo.GoodsClassVo;
import com.mds.group.purchase.shop.vo.SortVO;
import com.mds.group.purchase.utils.PageUtil;
import io.swagger.annotations.*;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

/**
 * The type Goods class controller.
 *
 * @author shuke
 * @date 2018 /11/27
 */
@RestController
@Validated
@RequestMapping("/goods/class")
@Api(value = "GoodsClassController|商品分类相关接口", tags = "所有接口")
public class GoodsClassController {

    @Resource
    private GoodsClassService goodsClassService;

    /**
     * 获取所有商品分类信息
     *
     * @param appmodelId 小程序模板ID
     * @param page       the page
     * @param size       the size
     * @return 商品分类结果对象列表 goods classes
     */
    @ApiOperation(value = "获取所有商品分类信息", tags = "查询接口")
    @ApiResponses({@ApiResponse(code = 200, message = "success", response = GoodsClassResult.class),})
    @GetMapping("/v1/all")
    public Result getGoodsClasses(@RequestHeader @NotBlank String appmodelId, @RequestParam int page,
                                  @RequestParam int size) {
        List<GoodsClassResult> goodsClasses = goodsClassService.getGoodsClasses(appmodelId);
        PageInfo pageInfo = PageUtil.pageUtil(page, size, goodsClasses);
        return Result.success(pageInfo);
    }

    /**
     * 商品分类排序
     *
     * @param appmodelId the appmodel id
     * @param sortVO     the sort vo
     * @return the result
     */
    @ApiOperation(value = "商品分类排序", tags = "更新接口")
    @ApiResponses({@ApiResponse(code = 200, message = "success")})
    @PutMapping("/v1/sort")
    public Result sort(
            @RequestHeader @NotBlank(message = "appmodelId不能为空") @Size(max = 27, min = 27, message = "商品标示错误") String appmodelId,
            @RequestBody @Valid SortVO sortVO) {
        sortVO.setAppmodelId(appmodelId);
        int i = goodsClassService.sort(sortVO.getHandleType(),sortVO.getGoodsClassId(),sortVO.getAppmodelId());
        switch (i) {
            case 1:
                return Result.success("置顶成功");
            case 2:
                return Result.success("上移成功");
            case 3:
                return Result.success("下移成功");
            case 4:
                return Result.success("置底成功");
            default:
                return null;
        }
    }

    /**
     * 商品分类排序
     *
     * @param appmodelId the appmodel id
     * @param classIds   the class ids
     * @return the result
     */
    @ApiOperation(value = "商品分类排序", tags = "更新接口")
    @ApiResponses({@ApiResponse(code = 200, message = "success")})
    @PutMapping("/v1.2.2/sort")
    public Result sort(
            @RequestHeader @NotBlank(message = "appmodelId不能为空") String appmodelId,
            @RequestBody String classIds) {
        goodsClassService.sortNew(classIds,appmodelId);
        return Result.success();
    }

    /**
     * 增加goodsClass商品分类数据
     * 返回这条数据的完整数据
     *
     * @param appmodelId 小程序模板id
     * @param classVo    the class vo
     * @return GoodsClass result
     */
    @PostMapping("/v1/save")
    @ApiOperation(value = "增加goodsClass商品分类数据", tags = "新增接口")
    @ApiResponses({@ApiResponse(code = 200, message = "success", response = GoodsClass.class),})
    public Result<GoodsClass> addGoodsClass(
            @ApiParam(value = "小程序模板id", required = true) @RequestHeader @NotNull String appmodelId,
            @RequestBody @Valid GoodsClassVo classVo) {
        goodsClassService.putGoodsClass(appmodelId, classVo);
        return Result.success();
    }

    /**
     * 删除商品分类
     *
     * @param goodsClassId the goods class id
     * @return the result
     */
    @DeleteMapping("/v1/delete")
    @ApiOperation(value = "删除商品分类", notes = "删除分类会同时删除该分类下级分类，解除商品与分类的映射关系", tags = "删除接口")
    @ApiResponses({@ApiResponse(code = 200, message = "success", response = Boolean.class),})
    public Result delGoodsClass(
            @ApiParam(value = "分类id", required = true) @RequestParam @NotBlank String goodsClassId) {
        goodsClassService.delById(goodsClassId);
        return Result.success(true);
    }
}
