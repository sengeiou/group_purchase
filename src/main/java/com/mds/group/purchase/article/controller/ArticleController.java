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

package com.mds.group.purchase.article.controller;

import com.mds.group.purchase.activity.model.Activity;
import com.mds.group.purchase.activity.result.ActGoodsInfoResult;
import com.mds.group.purchase.activity.service.ActivityGoodsService;
import com.mds.group.purchase.activity.service.ActivityService;
import com.mds.group.purchase.article.model.Article;
import com.mds.group.purchase.article.service.ArticleService;
import com.mds.group.purchase.article.vo.ArticleLoudVO;
import com.mds.group.purchase.article.vo.ArticleVO;
import com.mds.group.purchase.article.vo.SetCategoryVO;
import com.mds.group.purchase.article.vo.SortVO;
import com.mds.group.purchase.core.Result;
import com.mds.group.purchase.core.ResultGenerator;
import io.swagger.annotations.*;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


/**
 * 文章
 *
 * @author Created by wx on 2018/06/07.
 */
@Api(tags = "所有接口")
@RestController
@Validated
@RequestMapping("/article")
public class ArticleController {

    @Resource
    private ArticleService articleService;
    @Resource
    private ActivityService activityService;
    @Resource
    private ActivityGoodsService activityGoodsService;

    /**
     * Add result.
     *
     * @param article    the article
     * @param appmodelId the appmodel id
     * @return the result
     */
    @ApiOperation(value = "文章添加", tags = "添加接口")
    @PostMapping("/v1/add")
    public Result add(@RequestBody @Valid Article article, @RequestHeader @NotNull String appmodelId) {
        article.setAppmodelId(appmodelId);
        Article newArticle = articleService.addOrUpdateArticle(article, 0);
        if (newArticle.getArticleId() != null) {
            return ResultGenerator.genSuccessResult();
        } else {
            return ResultGenerator.genFailResult("添加失败");
        }
    }

    /**
     * Update result.
     *
     * @param article the article
     * @return the result
     */
    @ApiOperation(value = "文章编辑更新", tags = "更新接口")
    @PutMapping("/v1/update")
    public Result update(@RequestBody Article article) {
        articleService.addOrUpdateArticle(article, 1);
        return ResultGenerator.genSuccessResult();
    }

    /**
     * Sets category.
     *
     * @param setCategoryVO the set category vo
     * @return the category
     */
    @ApiOperation(value = "批量设置分类", tags = "更新接口")
    @PutMapping("/v1/setCategory")
    public Result setCategory(@RequestBody SetCategoryVO setCategoryVO) {
        articleService.setCategory(setCategoryVO.getArticleIds(), setCategoryVO.getAppmodelId(),
                setCategoryVO.getEntirelyExcludeCategoryIds(), setCategoryVO.getEntirelyIncludeCategoryIds());
        return ResultGenerator.genSuccessResult();
    }

    /**
     * Delete result.
     *
     * @param articleIds the article ids
     * @return the result
     */
    @ApiOperation(value = "批量删除文章", tags = "删除接口")
    @DeleteMapping("/v1/delete")
    public Result delete(
            @ApiParam(value = "文章id字符串") @RequestParam @NotBlank(message = "articleIds不能不为空") String articleIds) {
        articleService.delete(articleIds);
        return ResultGenerator.genSuccessResult();
    }

    /**
     * Selete result.
     *
     * @param pageNum    the page num
     * @param pageSize   the page size
     * @param appmodelId the appmodel id
     * @return the result
     */
    @ApiOperation(value = "文章分页查询", tags = "查询接口")
    @GetMapping("/v1/select")
    public Result selete(
            @RequestParam @NotNull(message = "pageNum不能为空") @Min(value = 1, message = "pageNum不能小于1") Integer pageNum,
            @RequestParam @NotNull(message = "pageSize不能为空") @Min(value = 1, message = "pageSize不能小于1") Integer pageSize,
            @RequestHeader @NotNull(message = "appmodelId不能为空") @Size(max = 27, min = 27, message = "appmodelId错误") String appmodelId) {
        Map<String, Object> all = articleService.findAll(pageNum, pageSize, appmodelId);
        return ResultGenerator.genSuccessResult(all);
    }

    /**
     * Sort result.
     *
     * @param sortVO the sort vo
     * @return the result
     */
    @ApiOperation(value = "文章排序", tags = "更新接口")
    @PutMapping("/v1/sort")
    public Result sort(@RequestBody @Valid SortVO sortVO) {
        articleService.sort(sortVO.getAppmodelId(), sortVO.getArticleId(), sortVO.getHandleType());
        return ResultGenerator.genSuccessResult();
    }

    /**
     * Gets activity goods.
     *
     * @param appmodelId the appmodel id
     * @return the activity goods
     */
    @ApiOperation(value = "获取一开始和预热中的活动商品列表", response = ActGoodsInfoResult.class, tags = "查询接口")
    @GetMapping("/v2/activity/goods")
    public Result getActivityGoods(
            @RequestHeader @NotNull(message = "appmodelId不能为空") @Size(max = 27, min = 27, message = "appmodelId错误") String appmodelId) {
        List<Activity> activities = activityService.findActs(appmodelId);
        if (activities.isEmpty()) {
            return ResultGenerator.genSuccessResult();
        }
        List<ActGoodsInfoResult> actGoodsInfoResults =
                activityGoodsService.getActGoodsByActIds(activities.stream().map(Activity::getActivityId).collect(Collectors.toList()));
        return ResultGenerator.genSuccessResult(actGoodsInfoResults);
    }

    /**
     * Select by id result.
     *
     * @param wxuserId  the wxuser id
     * @param articleId the article id
     * @return the result
     */
    @ApiOperation(value = "根据Id查询文章", tags = "查询接口")
    @ApiResponses({
            @ApiResponse(code = 100, message = "success", response = Article.class, responseContainer = "Article"),})
    @GetMapping("/v1/selectById")
    public Result selectById(@ApiParam(value = "用户id") @NotNull Long wxuserId,
                             @ApiParam(value = "文章id") @NotBlank String articleId) {
        try {
            ArticleVO articleVO = articleService.lookAriticle(wxuserId, articleId);
            if (articleVO != null) {
                return ResultGenerator.genSuccessResult(articleVO);
            } else {
                return ResultGenerator.genFailResult("查询失败");
            }
        } catch (Exception e) {
            return Result.success("文章不存在");
        }
    }

    /**
     * Update article laud result.
     *
     * @param articleLoudVO the article loud vo
     * @return the result
     */
    @ApiOperation(value = "文章点赞", tags = "更新接口")
    @PutMapping("/v1/updateArticleLaud")
    public Result updateArticleLaud(@RequestBody @Valid ArticleLoudVO articleLoudVO) {
        Map<String, Object> map = articleService
                .updateArticleLaud(articleLoudVO.getWxuserId(), articleLoudVO.getArticleId(),
                        articleLoudVO.getAppmodelId());
        return ResultGenerator.genSuccessResult(map);
    }

    /**
     * Select by category id result.
     *
     * @param pageNum    the page num
     * @param pageSize   the page size
     * @param categoryId the category id
     * @param wxuserId   the wxuser id
     * @param appmodelId the appmodel id
     * @return the result
     */
    @ApiOperation(value = "根据分类查文章", tags = "查询接口")
    @GetMapping("/v1/selectByCategoryId")
    public Result selectByCategoryId(
            @RequestParam @NotNull(message = "pageNum不能为空") @Min(value = 1, message = "pageNum不能小于1") Integer pageNum,
            @RequestParam @NotNull(message = "pageSize不能为空") @Min(value = 1, message = "pageSize不能小于1") Integer pageSize,
            @RequestParam @NotNull(message = "分类id不能为空") String categoryId,
            @RequestParam @NotNull(message = "用户id不能为空") Long wxuserId,
            @RequestHeader @NotNull(message = "appmodelId不能为空") @Size(max = 27, min = 27, message = "appmodelId错误") String appmodelId) {
        Map<String, Object> map = articleService
                .selectByCategoryId(pageNum, pageSize, appmodelId, wxuserId, categoryId);
        return ResultGenerator.genSuccessResult(map);
    }

}
