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

package com.mds.group.purchase.shop.controller;

import cn.hutool.core.collection.CollectionUtil;
import com.github.pagehelper.PageInfo;
import com.mds.group.purchase.article.service.ArticleService;
import com.mds.group.purchase.core.CodeMsg;
import com.mds.group.purchase.core.Result;
import com.mds.group.purchase.exception.ServiceException;
import com.mds.group.purchase.shop.model.BottomPoster;
import com.mds.group.purchase.shop.model.Poster;
import com.mds.group.purchase.shop.service.PosterService;
import com.mds.group.purchase.shop.vo.ActivityInfoVO;
import com.mds.group.purchase.shop.vo.BottomPosterVo;
import com.mds.group.purchase.shop.vo.SortVO;
import com.mds.group.purchase.utils.PageUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * 轮播图
 *
 * @author Created by wx on 2018/05/25.
 */
@Api(tags = "所有接口")
@RequestMapping("/poster")
@RestController
public class PosterController {

    @Resource
    private PosterService posterService;
    @Resource
    private ArticleService articleService;

    /**
     * Save or update result.
     *
     * @param appmodelId the appmodel id
     * @param poster     the poster
     * @return the result
     */
    @ApiOperation(value = "新增轮播图/更新轮播图", tags = "添加接口")
    @PostMapping("/v1/save/update")
    public Result saveOrUpdate(
            @RequestHeader @NotBlank(message = "appmodelId不能为空") @Size(max = 27, min = 27, message = "商品标示错误") String appmodelId,
            @RequestBody Poster poster) {
        poster.setAppmodelId(appmodelId);
        int num = posterService.saveOrUpdate(poster);
        if (num > 0) {
            return Result.success("操作成功");
        } else {
            return Result.error(new CodeMsg("操作失败"));
        }
    }

    /**
     * Infos result.
     *
     * @param appmodelId the appmodel id
     * @param posterId   the poster id
     * @return the result
     */
    @ApiOperation(value = "查询轮播图详情/所有轮播图", tags = "查询接口")
    @GetMapping("/v1/infos")
    public Result<List<Poster>> infos(
            @RequestHeader @NotBlank(message = "appmodelId不能为空") @Size(max = 27, min = 27, message = "商品标示错误") String appmodelId,
            @ApiParam(value = "轮播图id,查询所有轮播图id传null") @RequestParam(required = false) Integer posterId) {
        List<Poster> poster = posterService.infos(appmodelId, posterId);
        return Result.success(poster);
    }

    /**
     * Gets optional poster activity goods.
     *
     * @param appmodelId the appmodel id
     * @param searchType the search type
     * @param pageNum    the page num
     * @param pageSize   the page size
     * @return the optional poster activity goods
     */
    @ApiOperation(value = "首页轮播图查询可关联的活动商品", tags = "查询接口")
    @GetMapping("/v1/optional/activity/goods")
    public Result<PageInfo> getOptionalPosterActivityGoods(
            @RequestHeader @NotBlank(message = "appmodelId不能为空") @Size(max = 27, min = 27, message = "商品标示错误") String appmodelId,
            @ApiParam(value = "搜索类型 1-秒杀 2-拼团 3-咨询文章") @RequestParam(required = false) @Min(1) @Max(3) Integer searchType,
            @ApiParam(value = "页数") @RequestParam(required = false) Integer pageNum,
            @ApiParam(value = "条数") @RequestParam(required = false) Integer pageSize) {
        if (searchType.equals(1) || searchType.equals(2)) {
            PageInfo<ActivityInfoVO> optionalPosterActivityGoods = posterService
                    .getOptionalPosterActivityGoods(appmodelId, searchType, pageNum, pageSize);
            return Result.success(optionalPosterActivityGoods);
        } else if (searchType.equals(3)) {
            Map<String, Object> all = articleService.findAll(pageNum, pageSize, appmodelId);
            PageInfo pageInfo = new PageInfo();
            if (CollectionUtil.isNotEmpty(all)) {
                List<Object> objects = new ArrayList<>(all.values());
                pageInfo = PageUtil.pageUtil(pageNum, pageSize, objects);
            }
            return Result.success(pageInfo);
        } else {
            throw new ServiceException("非法操作");
        }
    }

    /**
     * Batch delete result.
     *
     * @param posterIds the poster ids
     * @return the result
     */
    @ApiOperation(value = "删除轮播图(可批量)", tags = "删除接口")
    @DeleteMapping("/v1/batchDelete")
    public Result batchDelete(@ApiParam(value = "轮播图id字符串", required = true) @NotBlank @RequestParam String posterIds) {
        int result = posterService.batchDelete(posterIds.split(","));
        if (result > 0) {
            return Result.success("删除成功");
        } else {
            return Result.success("删除失败");
        }
    }

    /**
     * Sort result.
     *
     * @param appmodelId the appmodel id
     * @param sortVO     the sort vo
     * @return the result
     */
    @ApiOperation(value = "轮播图排序操作", tags = "更新接口")
    @PutMapping("/v1/sort")
    public Result sort(
            @RequestHeader @NotBlank(message = "appmodelId不能为空") @Size(max = 27, min = 27, message = "商品标示错误") String appmodelId,
            @RequestBody @Valid SortVO sortVO) {
        sortVO.setAppmodelId(appmodelId);
        int i = posterService.sort(sortVO);
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
                break;
        }
        return Result.error(new CodeMsg("操作没有生效"));
    }


    /**
     * Sort result.
     *
     * @param appmodelId the appmodel id
     * @param postIds    the post ids
     * @return the result
     */
    @ApiOperation(value = "轮播图排序操作", tags = "更新接口")
    @PutMapping("/v1.2.2/sort")
    public Result sort(
            @RequestHeader @NotBlank(message = "appmodelId不能为空") String appmodelId,
            @RequestParam @NotBlank String postIds) {
        posterService.sort(postIds, appmodelId);
        return Result.error(new CodeMsg("操作没有生效"));
    }

    /**
     * 获取底部海报
     *
     * @param appmodelId the appmodel id
     * @return the bottom poster
     */
    @ApiOperation(value = "获取底部海报", tags = "v1.2.1接口")
    @GetMapping("/v1.2.1/bottomPoster")
    public Result getBottomPoster(@RequestHeader @NotBlank String appmodelId) {
        BottomPoster bottomPoster = posterService.findBottomPoster(appmodelId);
        return Result.success(bottomPoster);
    }

    /**
     * 编辑底部海报
     *
     * @param appmodelId the appmodel id
     * @param vo         the vo
     * @return the result
     */
    @ApiOperation(value = "编辑底部海报", tags = "v1.2.1接口")
    @PutMapping("/v1.2.1/bottomPoster")
    public Result updateBottomPoster(@RequestHeader @NotBlank String appmodelId, @RequestBody BottomPosterVo vo) {
        vo.setAppmodelId(appmodelId);
        BottomPoster bottomPoster1 = vo.voToBottomPoster();
        posterService.updateBottomPoster(bottomPoster1);
        return Result.success();
    }


}
