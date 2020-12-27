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

package com.mds.group.purchase.article.service;


import com.mds.group.purchase.article.model.ArticleCategory;

import java.util.List;

/**
 * The interface Article category service.
 *
 * @author Created by wx on 2018/06/07.
 */
public interface ArticleCategoryService {

    /***
     * 根据appmodelId查询文章分类
     *
     * @param appmodelId the appmodel id
     * @return List<ArticleCategory>  by appmodel id
     */
    List<ArticleCategory> getByAppmodelId(String appmodelId);

    /**
     * 根据分类ID查询分类
     *
     * @param categoryIds the category ids
     * @return the by category id
     */
    List<ArticleCategory> getByCategoryId(String categoryIds);

    /**
     * 根据AppmodelId和删除标记查询分类
     *
     * @param appmodelId  the appmodel id
     * @param deleteState the delete state
     * @return the article category
     */
    ArticleCategory findByAppmodelIdAndDeleteState(String appmodelId, Integer deleteState);

    /**
     * 删除指定分类
     *
     * @param categoryId the category id
     * @param appmodelId the appmodel id
     * @return the article category
     */
    ArticleCategory deleteCategory(String categoryId, String appmodelId);

    /**
     * 更新指定分类
     *
     * @param articleCategory the article category
     * @param type            the type
     * @return the article category
     */
    ArticleCategory savaOrUpdateCategory(ArticleCategory articleCategory,Integer type);
}
