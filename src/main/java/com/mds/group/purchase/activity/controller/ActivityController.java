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

package com.mds.group.purchase.activity.controller;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.mds.group.purchase.activity.model.Activity;
import com.mds.group.purchase.activity.service.ActivityService;
import com.mds.group.purchase.activity.vo.ActivityV123Vo;
import com.mds.group.purchase.activity.vo.ActivityV12Vo;
import com.mds.group.purchase.activity.vo.ActivityVo;
import com.mds.group.purchase.core.Result;
import com.mds.group.purchase.goods.result.GoodsResult;
import com.mds.group.purchase.goods.result.GoodsResult4AddAct;
import com.mds.group.purchase.goods.service.GoodsService;
import com.mds.group.purchase.utils.ActivityUtil;
import com.mds.group.purchase.utils.GoodsUtil;
import com.mds.group.purchase.utils.PageUtil;
import io.swagger.annotations.*;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 营销活动
 *
 * @author shuke
 * @date 2018 /12/03
 */
@RestController
@RequestMapping("/activity")
@Validated
@Api(tags = "所有接口")
public class ActivityController {

    @Resource
    private GoodsUtil goodsUtil;
    @Resource
    private GoodsService goodsService;
    @Resource
    private ActivityUtil activityUtil;
    @Resource
    private ActivityService activityService;

    /**
     * 未传入活动id时创建活动，当传入活动id时修改活动
     *
     * @param appmodelId the appmodel id
     * @param activityVo the activity vo
     * @return the result
     */
    @ApiOperation(value = "未传入活动id时创建活动，当传入活动id时修改活动", tags = "v1.2版本接口")
    @PostMapping("/v1/do")
    @Deprecated
    public Result doActivity(@ApiParam("小程序模板id") @RequestHeader @NotBlank String appmodelId,
                             @ApiParam("活动参数对象") @Valid @RequestBody ActivityVo activityVo) {
        activityVo.setAppmodelId(appmodelId);
        //判断设置的活动价是否小于团长佣金
        activityUtil.actPriceAssert(activityVo);
        if (activityVo.getActivityId() == null) {
            activityService.createActivity(activityVo);
        } else {
            activityService.modifyActivity(activityVo);
        }
        //刷新活动和商品缓存 (将更新缓存和发送队列放到方法外,防止事物未提交,mq异步处理,拿到的是旧数据)
        goodsUtil.flushGoodsCache(activityVo.getAppmodelId());
        //创建和更新活动后设置活动更新状态为true
        activityUtil.setActivityUpdate(appmodelId);
        return Result.success(true);
    }

    /**
     * 未传入活动id时创建活动，当传入活动id时修改活动,此版本接口增加手动排序和自动按照销量排序功能，且活动开始后也能设置排序
     *
     * @param appmodelId 小程序模板id
     * @param activityVo the activity vo
     * @return the result
     * @since v1.2
     */
    @ApiOperation(value = "未传入活动id时创建活动，当传入活动id时修改活动,此版本接口增加手动排序和自动按照销量排序功能，且活动开始后也能设置排序", tags = "v1.2接口")
    @PostMapping("/v1.2/do")
    public Result doActivityV12(@ApiParam("小程序模板id") @RequestHeader @NotBlank String appmodelId,
                                @ApiParam("活动参数对象") @Valid @RequestBody ActivityV12Vo activityVo) {
        activityVo.setAppmodelId(appmodelId);
        doAct(activityVo);
        return Result.success(true);
    }

    /**
     * 未传入活动id时创建活动，当传入活动id时修改活动,此版本接口增加手动排序和自动按照销量排序功能，且活动开始后也能设置排序
     *
     * @param appmodelId 小程序模板id
     * @param activityVo the activity vo
     * @return the result
     * @since v1.2
     */
    @ApiOperation(value = "未传入活动id时创建活动，当传入活动id时修改活动,此版本接口增加手动排序和自动按照销量排序功能，且活动开始后也能设置排序", tags = "v1.2接口")
    @PostMapping("/v1.2.3/do")
    public Result doActivityV123(@ApiParam("小程序模板id") @RequestHeader @NotBlank String appmodelId,
                                 @ApiParam("活动参数对象") @Valid @RequestBody ActivityV123Vo activityVo) {
        activityVo.setAppmodelId(appmodelId);
        doAct(activityVo);
        return Result.success(true);
    }

    private void doAct(ActivityVo activityVo) {
        //判断设置的活动价是否小于团长佣金,是否小于商品原价
        activityUtil.actPriceAssertV12(activityVo);
        if (activityVo.getActivityId() == null) {
            //新建活动
            activityService.createActivity(activityVo);
        } else {
            //更新活动内容
            activityService.modifyActivity(activityVo);
        }
        //刷新活动和商品缓存 (将更新缓存和发送队列放到方法外,防止事物未提交,mq异步处理,拿到的是旧数据)
        goodsUtil.flushGoodsCache(activityVo.getAppmodelId());
        //创建和更新活动后设置活动更新状态为true
        activityUtil.setActivityUpdate(activityVo.getAppmodelId());
    }

    /**
     * 活动已经开始则结束活动
     * 其他状态则删除活动，
     * 删除已结束的活动，如果有未发货订单，则给出提示且不能删除
     *
     * @param activityIds 活动id，多个id之间用逗号分隔
     * @param appmodelId  the appmodel id
     * @return Result result
     */
    @ApiOperation(value = "删除活动", tags = "删除接口")
    @ApiResponses({@ApiResponse(code = 100, message = "success", response = boolean.class)})
    @DeleteMapping("/v2/delete")
    public Result deleteActivityV2(@NotBlank @RequestParam String activityIds,
                                   @NotBlank @RequestHeader String appmodelId) {
        activityService.deleteOrCloseActivity(activityIds, appmodelId);
        return Result.success(true);
    }

    /**
     * 获取活动列表
     *
     * @param appmodelId 小程序模板id
     * @param actType    the act type
     * @param page       当前页码
     * @param size       当前页面显示数据条数
     * @return List<Activity>   活动列表
     */
    @ApiOperation(value = "查看活动列表", tags = "查询接口")
    @ApiResponses({@ApiResponse(code = 100, message = "success", response = Activity.class)})
    @GetMapping("/v1/all")
    public Result getActivityAll(@NotNull @RequestHeader String appmodelId,
                                 @ApiParam("actType 1:秒杀 2：平团") @RequestParam Integer actType,
                                 @ApiParam(value = "当前页码") @RequestParam(defaultValue = "0") Integer page,
                                 @ApiParam(value = "页面数据量") @RequestParam(defaultValue = "0") Integer size) {
        PageHelper.startPage(page, size, "status asc,start_time desc ");
        List<Activity> activities = activityService.findAllAct(appmodelId, actType);
        PageInfo<Activity> pageInfo = new PageInfo<>(activities);
        return Result.success(pageInfo);
    }

    /**
     * 获取可以添加到活动的商品接口
     *
     * @param appmodelId the appmodel id
     * @param page       the page
     * @param size       the size
     * @return List<GoodsResult4AddAct>  goods can add to act
     */
    @ApiOperation(value = "获取可以添加到活动的商品接口", tags = "查询接口")
    @ApiResponses({@ApiResponse(code = 100, message = "success", response = GoodsResult.class)})
    @GetMapping("/v1/goods/can/add")
    public Result getGoodsCanAddToAct(@RequestHeader @ApiParam("小程序模板id") @NotBlank String appmodelId,
                                      @ApiParam(value = "当前页码") @RequestParam(defaultValue = "0") Integer page,
                                      @ApiParam(value = "页面数据量") @RequestParam(defaultValue = "0") Integer size) {
        List<GoodsResult4AddAct> list = goodsService.getGoodsCanAddToAct(appmodelId);
        PageInfo pageInfo = PageUtil.pageUtil(page, size, list);
        return Result.success(pageInfo);
    }

    /**
     * 获取可以添加到活动的商品接口
     *
     * @param appmodelId the appmodel id
     * @param page       the page
     * @param size       the size
     * @return List<GoodsResult4AddAct>  result
     * @since v1.2.3 商品只能同时添加到一场活动中
     */
    @ApiOperation(value = "获取可以添加到活动的商品接口", tags = "查询接口")
    @ApiResponses({@ApiResponse(code = 100, message = "success", response = GoodsResult.class)})
    @GetMapping("/v1.2.3/goods/can/add")
    public Result goodsCanAddToAct(@RequestHeader @ApiParam("小程序模板id") @NotBlank String appmodelId,
                                   @ApiParam(value = "当前页码") @RequestParam(defaultValue = "0") Integer page,
                                   @ApiParam(value = "页面数据量") @RequestParam(defaultValue = "0") Integer size) {
        List<GoodsResult4AddAct> list = goodsService.goodsCanAddToAct(appmodelId);
        PageInfo pageInfo = PageUtil.pageUtil(page, size, list);
        return Result.success(pageInfo);
    }

    /**
     * 小程序端获取一开始和预热中的活动列表
     *
     * @param appmodelId 小程序模板id
     * @param actType    the act type
     * @return List<Activity>  活动列表
     */
    @ApiOperation(value = "小程序端获取一开始和预热中的活动列表", tags = "查询接口")
    @ApiResponses({@ApiResponse(code = 100, message = "success", response = Activity.class)})
    @GetMapping("/wx/v1/ready/start/list")
    public Result getActivityAll(@NotNull @RequestHeader String appmodelId,
                                 @ApiParam("actType 1:秒杀 2：拼团") @RequestParam Integer actType) {
        PageHelper.startPage(0, 0, "status asc,start_time asc ");
        List<Activity> activities = activityService.findAct(appmodelId, actType);
        PageInfo<Activity> pageInfo = new PageInfo<>(activities);
        return Result.success(pageInfo);
    }

    /**
     * 小程序端获取一开始和预热中的活动列表
     *
     * @return currentTimeMillis 当前时间的时间戳
     */
    @ApiOperation(value = "获取系统时间", tags = "查询接口")
    @GetMapping("/v1/system/info")
    public Result getSystemInfo() {
        return Result.success(System.currentTimeMillis());
    }
}
