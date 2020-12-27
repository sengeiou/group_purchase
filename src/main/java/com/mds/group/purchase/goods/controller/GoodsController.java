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

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.mds.group.purchase.configurer.GroupMallProperties;
import com.mds.group.purchase.constant.ActiviMqQueueName;
import com.mds.group.purchase.core.CodeMsg;
import com.mds.group.purchase.core.Result;
import com.mds.group.purchase.goods.model.Goods;
import com.mds.group.purchase.goods.result.ClassAndGoodsResult;
import com.mds.group.purchase.goods.result.GoodsFuzzyResult;
import com.mds.group.purchase.goods.service.GoodsService;
import com.mds.group.purchase.goods.vo.*;
import com.mds.group.purchase.jobhandler.ActiveDelaySendJobHandler;
import com.mds.group.purchase.utils.GoodsAreaMappingUtil;
import com.mds.group.purchase.utils.PageUtil;
import io.swagger.annotations.*;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * The type Goods controller.
 *
 * @author shuke
 * @date 2018 /11/27
 */
@RestController
@Validated
@RequestMapping("/goods")
@Api(value = "GoodsController|商品相关接口", tags = "所有接口")
public class GoodsController {

    @Resource
    private GoodsService goodsService;
    @Resource
    private RedisTemplate<String,List<ClassAndGoodsResult>> redisTemplate;
    @Resource
    private RedisTemplate<String,Integer> redisTemplate4Page;
    @Resource
    private GoodsAreaMappingUtil goodsAreaMappingUtil;
    @Resource
    private ActiveDelaySendJobHandler activeDelaySendJobHandler;

    /**
     * 添加一件商品
     *
     * @param goodsVo    页面请求参数
     * @param appmodelId the appmodel id
     * @return true result
     */
    @ApiOperation(value = "新增一个商品", tags = "新增接口")
    @PostMapping("/v1/save")
    public Result addGoods(@RequestBody @Valid SaveGoodsVo goodsVo, @RequestHeader @NotBlank String appmodelId) {
        goodsVo.setAppmodelId(appmodelId);
        goodsService.add(goodsVo);
        return Result.success(true);
    }

    /**
     * 添加一件商品
     *
     * @param goodsVo    页面请求参数
     * @param appmodelId the appmodel id
     * @return true result
     * @since v1.2版本
     */
    @ApiOperation(value = "新增一个商品,v1.2版本", tags = "v1.2版本接口")
    @PostMapping("/v2/save")
    public Result addGoodsV2(@ApiParam("添加商品时的参数对象") @RequestBody @Valid SaveGoodsVo goodsVo,
                             @ApiParam("小程序模板id") @RequestHeader @NotBlank String appmodelId) {
        goodsVo.setAppmodelId(appmodelId);
        goodsService.addV2(goodsVo);
        return Result.success(true);
    }

    /**
     * 添加一件商品
     *
     * @param goodsVo    页面请求参数
     * @param appmodelId the appmodel id
     * @return true result
     * @since v1.1.9版本
     */
    @ApiOperation(value = "新增一个商品,v1.1.9版本", tags = "v1.1.9版本接口")
    @PostMapping("/v1.1.9/save")
    public Result addGoodsV119(@ApiParam("添加商品时的参数对象") @RequestBody @Valid SaveGoodsV119Vo goodsVo,
                               @ApiParam("小程序模板id") @RequestHeader @NotBlank String appmodelId) {
        goodsVo.setAppmodelId(appmodelId);
        goodsService.addV119(goodsVo);
        return Result.success(true);
    }

    /**
     * 添加一件商品
     *
     * @param goodsVo    页面请求参数
     * @param appmodelId the appmodel id
     * @return true result
     * @since v1.2版本
     */
    @ApiOperation(value = "新增一个商品,v1.2版本", tags = "v1.2接口")
    @PostMapping("/v1.2/save")
    public Result addGoodsV12(@ApiParam("添加商品时的参数对象") @RequestBody @Valid SaveGoodsV12Vo goodsVo,
                              @ApiParam("小程序模板id") @RequestHeader @NotBlank String appmodelId) {
        goodsVo.setAppmodelId(appmodelId);
        goodsService.addV12(goodsVo);
        return Result.success(true);
    }

    /**
     * 修改商品信息
     *
     * @param goodsVo 需要修改的参数
     * @return 修改成功 result
     */
    @ApiOperation(value = "修改商品信息", tags = "更新接口")
    @PutMapping(value = "/v1/update")
    @Deprecated
    public Result updateGoods(@RequestBody @Valid UpdateGoodsVo goodsVo) {
        //刷新商品缓存
        if (goodsVo.getStock() != null && goodsVo.getStock() < 0) {
            goodsVo.setGoodsStatus(2);
        }
        goodsService.updateGoods(goodsVo);
        return Result.success(true);
    }

    /**
     * 修改商品信息
     *
     * @param goodsVo 需要修改的参数
     * @return 修改成功 result
     * @since v1.1.9
     */
    @ApiOperation(value = "修改商品信息", tags = "v1.1.9版本接口")
    @PutMapping(value = "/v1.1.9/update")
    @Deprecated
    public Result updateGoodsV119(@ApiParam("修改商品信息参数对象") @RequestBody @Valid UpdateGoodsV119Vo goodsVo) {
        //刷新商品缓存
        if (goodsVo.getStock() != null && goodsVo.getStock() < 0) {
            goodsVo.setGoodsStatus(2);
        }
        goodsService.updateGoodsV119(goodsVo);
        return Result.success(true);
    }

    /**
     * 修改商品信息
     *
     * @param goodsVo 需要修改的参数
     * @return 修改成功 result
     * @since v1.2
     */
    @ApiOperation(value = "修改商品信息", tags = "v1.2接口")
    @PutMapping(value = "/v1.2/update")
    public Result updateGoodsV12(@ApiParam("修改商品信息参数对象") @RequestBody @Valid UpdateGoodsV12Vo goodsVo) {
        //刷新商品缓存
        if (goodsVo.getStock() != null && goodsVo.getStock() < 0) {
            goodsVo.setGoodsStatus(2);
        }
        goodsService.updateGoodsV12(goodsVo);
        return Result.success(true);
    }

    /**
     * 获取商品列表和模糊查询商品的接口
     *
     * @param appmodelId 小程序模板id
     * @param getGoodsVo 获取商品的参数对象
     * @return GoodsFuzzyResult 商品结果对象
     */
    @ApiOperation(value = "获取商品列表和模糊查询商品的接口")
    @PostMapping("/v1/fuzzy")
    public Result<PageInfo<GoodsFuzzyResult>> searchGoodsLikeName(
            @ApiParam(value = "小程序模板id", required = true) @RequestHeader String appmodelId,
            @RequestBody GetGoodsVo getGoodsVo) {
        int page = getGoodsVo.getPage();
        int size = getGoodsVo.getSize();
        List<GoodsFuzzyResult> list = goodsService.getGoods(appmodelId, getGoodsVo);
        PageInfo<GoodsFuzzyResult> pageInfo = PageUtil.pageUtil(page, size, list);
        return Result.success(pageInfo);
    }

    /**
     * 获取商品列表和模糊查询商品的接口
     *
     * @param appmodelId 小程序模板id
     * @param getGoodsVo 获取商品的参数对象
     * @return GoodsFuzzyResult 商品结果对象
     * @since v1.2版本接口
     */
    @ApiOperation(value = "获取商品列表和模糊查询商品的接口", tags = "v1.2版本接口")
    @PostMapping("/v2/fuzzy")
    public Result<PageInfo<GoodsFuzzyResult>> searchGoodsLikeNameV2(
            @ApiParam(value = "小程序模板id", required = true) @RequestHeader String appmodelId,
            @ApiParam(value = "获取商品的参数对象", required = true) @RequestBody GetGoodsVo getGoodsVo) {
        PageInfo<GoodsFuzzyResult> list = goodsService.getGoodsV2(appmodelId, getGoodsVo);
        return Result.success(list);
    }


    /**
     * 新增投放区域时查询未投放的小区
     *
     * @param pageSize   当前页码
     * @param pageNum    页面显示数据条数
     * @param appmodelId 小程序模板id
     * @return 商品对象列表 no mappin goods
     */
    @ApiOperation(value = "新增投放区域时查询未投放的小区", tags = "新增接口")
    @GetMapping("/v1/no/mapping")
    public Result<PageInfo<Goods>> getNoMappinGoods(@RequestParam Integer pageSize, @RequestParam Integer pageNum,
                                                    @RequestHeader @NotBlank String appmodelId) {
        PageHelper.startPage(pageNum, pageSize);
        List<Goods> list = goodsService.findNoMappinGoods(appmodelId);
        return Result.success(new PageInfo<>(list));
    }

    /**
     * 批量操作商品
     *
     * @param appmodelId     小程序模板id
     * @param branchUpdateVo 批量更新参数对象
     * @return 更新成功 result
     */
    @ApiOperation(value = "批量操作商品", tags = "更新接口")
    @ApiResponses({@ApiResponse(code = 200, message = "success", response = Boolean.class),
            @ApiResponse(code = 100101, message = "参数校验异常"),})
    @PutMapping("/v1/batch")
    public Result updateGoodsClassBatch(
            @ApiParam(value = "小程序模板id", required = true) @RequestHeader @NotNull String appmodelId,
            @ApiParam("批量操作参数类") @Valid @RequestBody BranchUpdateVo branchUpdateVo) {
        //将String类型数组转化传Long类型
        goodsService.branchUpdate(appmodelId, branchUpdateVo);
        //刷新商品缓存
        activeDelaySendJobHandler.savaTask(appmodelId, ActiviMqQueueName.GOODS_INFO_CACHE, 0L, appmodelId, false);
        return Result.success(true);
    }

    /**
     * 查询单个商品
     *
     * @param appmodelId 小程序模板id
     * @param goodsId    商品id
     * @return GoodsFuzzyResult 商品结果对象
     */
    @ApiOperation(value = "查询单个商品")
    @GetMapping("/v1/one")
    public Result<GoodsFuzzyResult> searchGoodsLikeName(
            @ApiParam(value = "小程序模板id", required = true) @RequestHeader String appmodelId,
            @ApiParam("商品id") @RequestParam Long goodsId) {
        GoodsFuzzyResult goods = goodsService.getGoodsById(appmodelId, goodsId);
        return Result.success(goods);
    }

    /**
     * 查询商品分类（一级分类和二级分类）以及分类中的商品
     *
     * @param appmodelId 小程序模板id
     * @param page       当前页码
     * @param size       页面显示数据条数
     * @return ClassAndGoodsResult result
     */
    @ApiOperation(value = "查询商品分类（一级分类和二级分类）以及分类中的商品", tags = "查询接口")
    @ApiResponses({
            @ApiResponse(code = 100, message = "success", response = ClassAndGoodsResult.class, responseContainer = "List"),})
    @GetMapping("/v1/class/goods")
    @Deprecated
    public Result findClassAndGoods(@RequestHeader @NotBlank String appmodelId,
                                    @RequestParam(defaultValue = "0") Integer page, @RequestParam(defaultValue = "0") Integer size) {
        if (page < 0 || size < 0) {
            return Result.error(CodeMsg.BIND_ERROR.fillArgs("很遗憾,输入数据有误"));
        }
        String redisKey = GroupMallProperties.getRedisPrefix() + appmodelId + ":goodsCache4Poster";
        List<ClassAndGoodsResult> result = redisTemplate.opsForValue().get(redisKey);
        if (result == null) {
            result = goodsService.findClassAndGoods(appmodelId);
            redisTemplate.opsForValue().set(redisKey, result);
        }
        if (result != null && result.size() > 0) {
            if (result.size() < (page - 1) * size) {
                return Result.error(CodeMsg.BIND_ERROR.fillArgs("很遗憾,没有更多数据了0.0"));
            }
            // 开始
            int fromIndex = (page - 1) * size;
            // 结束
            int toIndex = page * size;
            if (toIndex > result.size()) {
                toIndex = result.size();
            }
            List<ClassAndGoodsResult> resultList = result.subList(fromIndex, toIndex);
            Result<List<ClassAndGoodsResult>> result1 = new Result<>();
            result1.setCode(200);
            result1.setMsg(result.size() + "");
            result1.setData(resultList);
            return result1;
        }
        return Result.error(CodeMsg.BIND_ERROR.fillArgs("很遗憾,没有更多数据了0.0"));
    }

    /**
     * 设置分页显示条数
     *
     * @param appmodelId 小程序模板id
     * @param pageSize   用户设置的数据显示量
     * @return null page size
     */
    @ApiOperation(value = "设置分页显示条数")
    @GetMapping("/v1/setPageSize")
    public Result setPageSize(@ApiParam(value = "小程序模板id", required = true) @RequestHeader String appmodelId,
                              @ApiParam(value = "用户设置的数据显示量", required = true) @RequestParam Integer pageSize) {
        String key = GroupMallProperties.getRedisPrefix() + appmodelId + ":pageSize";
        redisTemplate4Page.opsForValue().set(key, pageSize);
        return Result.success();
    }

    /**
     * Flush goods area mapping cache.
     */
    @GetMapping("/flush")
    public void flushGoodsAreaMappingCache(){
        goodsAreaMappingUtil.flushGoodsAreaMapping("S00050001wx17c66eb4da0ef6ab");
    }

}
