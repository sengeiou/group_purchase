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

import com.mds.group.purchase.article.model.ArticleCategory;
import com.mds.group.purchase.article.service.ArticleCategoryService;
import com.mds.group.purchase.core.Result;
import com.mds.group.purchase.core.ResultGenerator;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.constraints.NotBlank;
import java.util.List;

/**
 * 文章分类
 *
 * @author Created by wx on 2018/06/07.
 */
@Api(tags = "所有接口")
@Validated
@RestController
@RequestMapping("/information")
public class ArticleCategoryController {

    @Resource
    private ArticleCategoryService articleCategoryService;

    /**
     * Add category result.
     *
     * @param articleCategory the article category
     * @param appmodelId      the appmodel id
     * @return the result
     */
    @ApiOperation(value = "添加分类", tags = "添加接口")
    @PostMapping("/v1/category")
    public Result addCategory(@RequestBody ArticleCategory articleCategory, @RequestHeader String appmodelId) {
        articleCategory.setAppmodelId(appmodelId);
        ArticleCategory result = articleCategoryService.savaOrUpdateCategory(articleCategory, 0);
        if (result != null) {
            return ResultGenerator.genSuccessResult();
        } else {
            return ResultGenerator.genFailResult("保存失败");
        }
    }

    /**
     * Update category result.
     *
     * @param articleCategory the article category
     * @param appmodelId      the appmodel id
     * @return the result
     */
    @ApiOperation(value = "更新分类", tags = "更新接口")
    @PutMapping("/v1/category")
    public Result updateCategory(@RequestBody ArticleCategory articleCategory, @RequestHeader String appmodelId) {
        articleCategory.setAppmodelId(appmodelId);
        ArticleCategory result = articleCategoryService.savaOrUpdateCategory(articleCategory, 1);
        if (result != null) {
            return ResultGenerator.genSuccessResult();
        } else {
            return ResultGenerator.genFailResult("保存失败");
        }
    }

    /**
     * Delete category result.
     *
     * @param categoryId the category id
     * @param appmodelId the appmodel id
     * @return the result
     */
    @ApiOperation(value = "删除分类", tags = "删除接口")
    @DeleteMapping("/v1/category")
    public Result deleteCategory(@ApiParam(value = "分类id") @RequestParam @NotBlank String categoryId,
                                 @ApiParam(value = "模板id") @NotBlank @RequestHeader String appmodelId) {
        ArticleCategory articleCategory = articleCategoryService.deleteCategory(categoryId, appmodelId);
        if (articleCategory != null && articleCategory.getDeleteState().equals(1)) {
            return ResultGenerator.genSuccessResult();
        }
        return ResultGenerator.genFailResult("删除失败");
    }

    /**
     * Select category result.
     *
     * @param appmodelId the appmodel id
     * @return the result
     */
    @ApiOperation(value = "获取分类", tags = "查询接口")
    @GetMapping("/v1/category")
    public Result<List<ArticleCategory>> selectCategory(
            @ApiParam(value = "模板id") @NotBlank @RequestHeader String appmodelId) {
        List<ArticleCategory> articleCategories = articleCategoryService.getByAppmodelId(appmodelId);
        return ResultGenerator.genSuccessResult(articleCategories);
    }
}
