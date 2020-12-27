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

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageInfo;
import com.mds.group.purchase.activity.result.ActGoodsInfoResult;
import com.mds.group.purchase.activity.result.IndexSeckillResult;
import com.mds.group.purchase.activity.service.ActivityGoodsService;
import com.mds.group.purchase.configurer.GroupMallProperties;
import com.mds.group.purchase.constant.ActiviMqQueueName;
import com.mds.group.purchase.constant.ActivityConstant;
import com.mds.group.purchase.constant.Common;
import com.mds.group.purchase.core.Result;
import com.mds.group.purchase.goods.model.GoodsClass;
import com.mds.group.purchase.jobhandler.ActiveDelaySendJobHandler;
import com.mds.group.purchase.shop.service.ShopFunctionService;
import com.mds.group.purchase.shop.vo.ShopCreateUpdateVO;
import com.mds.group.purchase.utils.PageUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 活动商品控制器
 *
 * @author shuke
 * @date 2018 -12-17
 */
@RestController
@RequestMapping("/activity/goods")
@Validated
@Api(tags = "所有接口")
public class ActivityGoodsController {

    @Resource
    private RedisTemplate<String, List<GoodsClass>> redisTemplate;
    @Resource
    private ActivityGoodsService activityGoodsService;
    @Resource
    private ActiveDelaySendJobHandler activeDelaySendJobHandler;
    @Resource
    private ShopFunctionService shopFunctionService;

    /**
     * 获取首页拼团活动商品
     *
     * @param appmodelId the appmodel id
     * @param wxuserId   the wxuser id
     * @param page       the page
     * @param size       the size
     * @return the result
     */
    @ApiOperation(value = "获取首页拼团活动商品", tags = "查询接口")
    @GetMapping("/wx/v1/group/index")
    public Result actGoodsInfoIndexList(@RequestHeader @NotBlank String appmodelId,
                                        @RequestParam @NotNull @ApiParam("用户id") Long wxuserId, @RequestParam @NotNull @ApiParam("页码") Integer page,
                                        @ApiParam("页面显示数量") @RequestParam @NotNull Integer size) {
        List<ActGoodsInfoResult> resultList = activityGoodsService.getIndexGroupActGoods(wxuserId, appmodelId);
        PageInfo pageInfo = PageUtil.pageUtil(page, size, resultList);
        return Result.success(pageInfo);
    }

    /**
     * 根据分类id获取拼团商品
     *
     * @param appmodelId   the appmodel id
     * @param wxuserId     the wxuser id
     * @param goodsClassId the goods class id
     * @param page         the page
     * @param size         the size
     * @return the result
     */
    @ApiOperation(value = "根据分类id获取拼团商品", tags = "查询接口")
    @GetMapping("/wx/v1/info/class")
    public Result actGoodsInfoByClass(@RequestHeader @NotBlank String appmodelId,
                                      @RequestParam @NotNull @ApiParam("用户id") Long wxuserId,
                                      @RequestParam @NotNull @ApiParam("商品分类id，传-1查询所有") Long goodsClassId,
                                      @RequestParam @NotNull @ApiParam("页码") Integer page,
                                      @ApiParam("页面显示数量") @RequestParam @NotNull Integer size) {
        List<ActGoodsInfoResult> actGoodsInfoResultList = activityGoodsService
                .getActGoodsByClass(appmodelId, goodsClassId, wxuserId);
        PageInfo pageInfo = PageUtil.pageUtil(page, size, actGoodsInfoResultList);
        return Result.success(pageInfo);
    }

    /**
     * 根据活动商品id得到商品详情
     *
     * @param actGoodsId the act goods id
     * @param appmodelId the appmodel id
     * @return the result
     */
    @ApiOperation(value = "根据活动商品id得到商品详情", tags = "查询接口")
    @ApiResponse(code = 200, message = "success", response = ActGoodsInfoResult.class)
    @GetMapping("/wx/v1/info/actGoodsId")
    public Result actGoodsInfoByActGoodsId(@RequestParam @ApiParam("活动商品id") Long actGoodsId,
                                           @RequestHeader String appmodelId) {
        ActGoodsInfoResult actGoodsById = activityGoodsService.getActGoodsById(actGoodsId, appmodelId);
        return Result.success(actGoodsById);
    }

    /**
     * 根据活动商品id得到商品详情
     *
     * @param actGoodsId the act goods id
     * @param appmodelId the appmodel id
     * @return the result
     */
    @ApiOperation(value = "根据活动商品id得到商品详情", tags = "v1.1.9版本接口")
    @ApiResponse(code = 200, message = "success", response = ActGoodsInfoResult.class)
    @GetMapping("/wx/v1.1.9/info/actGoodsId")
    public Result actGoodsInfoByActGoodsIdV19(@RequestParam @ApiParam("活动商品id") Long actGoodsId,
                                              @RequestHeader String appmodelId) {
        ActGoodsInfoResult actGoodsById = activityGoodsService.getActGoodsById(actGoodsId, appmodelId);
        return Result.success(actGoodsById);
    }

    /**
     * 获取用户可见的拼团分类列表
     *
     * @param appmodelId the appmodel id
     * @param wxuserId   the wxuser id
     * @return the result
     */
    @ApiOperation(value = "获取用户可见的拼团分类列表", tags = "查询接口")
    @GetMapping("/wx/v1/user/class")
    public Result<List<GoodsClass>> actGoodsInfoByClass(@RequestHeader @NotBlank String appmodelId,
                                                        @RequestParam @NotNull @ApiParam("用户id") Long wxuserId) {
        String redisKey = GroupMallProperties.getRedisPrefix() + appmodelId + ":class:" + wxuserId;
        List<GoodsClass> result = redisTemplate.opsForValue().get(redisKey);
        if (result == null) {
            result = activityGoodsService.getGoodsClass(appmodelId, wxuserId);
            if (result != null && !result.isEmpty()) {
                redisTemplate.opsForValue().set(redisKey, result, ActivityConstant.ACTIVITY_GOODS_TIMEOUT_HALF_MINIUT,
                        TimeUnit.MILLISECONDS);
            }
        }
        return Result.success(result);
    }

    /**
     * 获取首页秒杀活动商品
     *
     * @param wxuserId   the wxuser id
     * @param appmodelId the appmodel id
     * @param page       the page
     * @param size       the size
     * @return the result
     */
    @ApiOperation(value = "获取首页秒杀活动商品", tags = "查询接口")
    @Deprecated
    @GetMapping("/wx/v1/seckill/index")
    public Result<PageInfo<IndexSeckillResult>> seckillGoodsInfoIndexList(
            @RequestParam @NotNull @ApiParam("用户id") Long wxuserId, @RequestHeader @NotBlank String appmodelId,
            @RequestParam(defaultValue = "0") @ApiParam("页码") Integer page,
            @ApiParam("页面显示数量") @RequestParam(defaultValue = "0") Integer size) {
        List<IndexSeckillResult> resultList = activityGoodsService.getIndexSeckillGoods(appmodelId, wxuserId);
        if (CollectionUtil.isNotEmpty(resultList)) {
            Collections.sort(resultList);
            if (resultList.size() > Common.INDEX_GOODS_SIZE) {
                resultList = resultList.subList(0, Common.INDEX_GOODS_SIZE);
            }
        }
        PageInfo<IndexSeckillResult> indexSeckillResultPageInfo = PageUtil.pageUtil(page, size, resultList);
        return Result.success(indexSeckillResultPageInfo);
    }

    /**
     * 获取首页秒杀活动商品
     *
     * @param wxuserId   the wxuser id
     * @param appmodelId the appmodel id
     * @return the result
     */
    @ApiOperation(value = "获取首页秒杀活动商品", tags = "查询接口")
    @GetMapping("/wx/v2/seckill/index")
    public Result<List<IndexSeckillResult>> seckillGoodsInfoIndexList(
            @RequestParam @NotNull @ApiParam("用户id") Long wxuserId, @RequestHeader @NotBlank String appmodelId) {
        List<IndexSeckillResult> resultList = activityGoodsService.getIndexSeckillGoods(appmodelId, wxuserId);
        Collections.sort(resultList);
        if (CollectionUtil.isNotEmpty(resultList)) {
            Collections.sort(resultList);
        }
        return Result.success(resultList);
    }

    /**
     * 获取用户小区是否有该商品
     *
     * @param wxuserId   the wxuser id
     * @param appmodelId the appmodel id
     * @param actGoodsId the act goods id
     * @return the result
     */
    @ApiOperation(value = "获取用户小区是否有该商品", tags = "查询接口")
    @GetMapping("/wx/v1/exist/actgoods")
    public Result<Boolean> ifExistActGoods(@RequestParam @NotNull @ApiParam("用户id") Long wxuserId,
                                           @RequestHeader @NotBlank String appmodelId, @RequestParam @NotNull @ApiParam("活动商品id") Long actGoodsId) {
        return Result.success(activityGoodsService.ifExistActGoods(appmodelId, wxuserId, actGoodsId));
    }

    /**
     * 获取秒杀活动二级页面商品
     *
     * @param appmodelId the appmodel id
     * @param wxuserId   the wxuser id
     * @param page       the page
     * @param size       the size
     * @return the result
     */
    @ApiOperation(value = "获取秒杀活动二级页面商品", tags = "查询接口")
    @GetMapping("/wx/v1/seckill/list")
    public Result<PageInfo<IndexSeckillResult>> seckillGoodsInfoSecondList(@RequestHeader @NotBlank String appmodelId,
                                                                           @RequestParam @NotNull @ApiParam("用户id") Long wxuserId,
                                                                           @RequestParam(defaultValue = "0") @NotNull @ApiParam("页码") Integer page,
                                                                           @ApiParam("页面显示数量") @RequestParam(defaultValue = "0") @NotNull Integer size) {
        List<IndexSeckillResult> resultList = activityGoodsService.getISecondSeckillGoods(appmodelId, wxuserId);
        resultList.sort(IndexSeckillResult::compareTo);
        PageInfo<IndexSeckillResult> pageInfo = PageUtil.pageUtil(page, size, resultList);
        return Result.success(pageInfo);
    }

    /**
     * 根据活动id获取当前活动商品
     *
     * @param activityId the activity id
     * @param page       the page
     * @param size       the size
     * @return the result
     */
    @ApiOperation(value = "获取当前活动商品", tags = "查询接口")
    @GetMapping("/v1/by/activity")
    public Result<PageInfo<ActGoodsInfoResult>> actGoodsInfoIndexListByActId(@RequestParam @NotNull Long activityId,
                                                                             @RequestParam @NotNull @ApiParam("页码") Integer page,
                                                                             @ApiParam("页面显示数量") @RequestParam @NotNull Integer size) {
        List<ActGoodsInfoResult> resultList = activityGoodsService.getActGoodsByActId4Pc(activityId);
        Collections.sort(resultList);
        PageInfo<ActGoodsInfoResult> pageInfo = PageUtil.pageUtil(page, size, resultList);
        return Result.success(pageInfo);
    }

    /**
     * 用户设置活动预约消息提醒
     *
     * @param appmodelId the appmodel id
     * @param wxuserId   the wxuser id
     * @param actGoodsId the act goods id
     * @param formId     the form id
     * @return the template msg
     */
    @ApiOperation(value = "用户设置活动预约消息提醒", tags = "所有接口")
    @GetMapping("/wx/v1/msg/set")
    public Result setTemplateMsg(@RequestHeader @NotBlank String appmodelId, @RequestParam @NotNull Long wxuserId,
                                 @RequestParam @NotNull Long actGoodsId, @RequestParam @NotBlank String formId) {
        ActGoodsInfoResult actGoodsById = activityGoodsService.getActGoodsById(actGoodsId, appmodelId);
        ShopCreateUpdateVO shop = shopFunctionService.findByAppmodelId(appmodelId);
        long actAlert = shop.getActivityAlert() == null ?
                ActivityConstant.ACT_SUBSCRIBE_TIME :
                shop.getActivityAlert() * 1000 * 60L;
        String startTime = actGoodsById.getActStartTDate();
        long sendMili = DateUtil.parse(startTime).getTime() - actAlert;
        long mili = sendMili - System.currentTimeMillis();
        if (mili < 0) {
            mili = 0L;
        }
        Map<String, Object> map = new HashMap<>(16);
        map.put("appmodelId", appmodelId);
        map.put("wxuserId", wxuserId);
        map.put("actGoodsId", actGoodsId);
        map.put("formId", formId);
        map.put("goodsName", actGoodsById.getGoodsName());
        map.put("type", 1);
        activeDelaySendJobHandler
                .savaTask(JSON.toJSONString(map), ActiviMqQueueName.ORDER_MINIPROGRAM_TEMPLATE_MESSAGE, mili,
                        appmodelId, false);
        return Result.success();
    }

    /**
     * 用户评论页面点击进入商品详情
     *
     * @param appmodelId the appmodel id
     * @param actGoodsId the act goods id
     * @return the result
     */
    @ApiOperation(value = "用户评论页面点击进入商品详情", tags = "查询接口")
    @GetMapping
    public Result actGoodsInfo4Comment(@RequestHeader @NotBlank String appmodelId,
                                       @RequestParam @NotNull Long actGoodsId) {
        ActGoodsInfoResult actGoodsById = activityGoodsService.getActGoodsById(actGoodsId, appmodelId);
        return Result.success(actGoodsById);
    }
}
