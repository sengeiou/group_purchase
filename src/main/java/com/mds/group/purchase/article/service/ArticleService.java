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

import com.mds.group.purchase.article.model.Article;
import com.mds.group.purchase.article.vo.ArticleVO;

import java.util.List;
import java.util.Map;

/**
 * The interface Article service.
 *
 * @author Created by wx on 2018/06/07.
 */
public interface ArticleService {

    /***
     * 分页查询店铺所有文章
     *
     * @param pageNum the page num
     * @param pageSize the page size
     * @param appmodelId the appmodel id
     * @return the map
     */
    Map<String, Object> findAll(Integer pageNum, Integer pageSize, String appmodelId);

    /***
     * 根据appmodelId查询店铺所有文章
     *
     * @param appmodelId the appmodel id
     * @return List<Article>  list
     */
    List<Article> findByAppmodelId(String appmodelId);

    /**
     * 据categoryId查询分页查询文章
     *
     * @param pageNum    the page num
     * @param pageSize   the page size
     * @param appmodelId the appmodel id
     * @param wxuserId   the wxuser id
     * @param categoryId the category id
     * @return map map
     */
    Map<String, Object> selectByCategoryId(Integer pageNum, Integer pageSize, String appmodelId, Long wxuserId,
                                           String categoryId);

    /**
     * 添加文章
     *
     * @param article the article
     * @param type    0-创建 1-更新
     * @return article article
     */
    Article addOrUpdateArticle(Article article, Integer type);

    /**
     * 设置文章分类
     *
     * @param articleIds                 the article ids
     * @param appmodelId                 the appmodel id
     * @param entirelyExcludeCategoryIds the entirely exclude category ids
     * @param entirelyIncludeCategoryIds the entirely include category ids
     * @return category category
     */
    int setCategory(String articleIds, String appmodelId, String entirelyExcludeCategoryIds,
                    String entirelyIncludeCategoryIds);

    /**
     * 批量更新文章
     *
     * @param articles the articles
     */
    void updateArticles(List<Article> articles);

    /**
     * 根据文章id查询文章
     *
     * @param articleId the article id
     * @return article article
     */
    Article findArticleByArticleId(String articleId);

    /**
     * 删除文章
     *
     * @param articleIds the article ids
     */
    void delete(String articleIds);

    /**
     * 文章排序
     *
     * @param appmodelId the appmodel id
     * @param articleId  the article id
     * @param handleType the handle type
     * @return the integer
     */
    Integer sort(String appmodelId, String articleId, Integer handleType);

    /**
     * 用户查询文章
     *
     * @param wxuserId  the wxuser id
     * @param articleId the article id
     * @return article vo
     */
    ArticleVO lookAriticle(Long wxuserId, String articleId);

    /**
     * 文章点赞
     *
     * @param wxuserId   the wxuser id
     * @param articleId  the article id
     * @param appmodelId the appmodel id
     * @return the map
     */
    Map<String, Object> updateArticleLaud(Long wxuserId, String articleId, String appmodelId);
}
