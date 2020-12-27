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

package com.mds.group.purchase.article.service.impl;


import cn.hutool.core.date.DateUtil;
import com.mds.group.purchase.article.dao.ArticleCategoryRepository;
import com.mds.group.purchase.article.model.Article;
import com.mds.group.purchase.article.model.ArticleCategory;
import com.mds.group.purchase.article.service.ArticleCategoryService;
import com.mds.group.purchase.article.service.ArticleService;
import org.springframework.beans.BeanUtils;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;

/**
 * The type Article category service.
 *
 * @author Created by wx on 2018/06/07.
 */
@Service
public class ArticleCategoryServiceImpl implements ArticleCategoryService {

    @Resource
    private MongoTemplate mongoTemplate;
    @Resource
    private ArticleService articleService;
    @Resource
    private ArticleCategoryRepository articleCategoryRepository;

    @Override
    public List<ArticleCategory> getByAppmodelId(String appmodelId) {
        Query query = new Query();
        query.addCriteria(Criteria.where("deleteState").is(0));
        query.addCriteria(Criteria.where("appmodelId").is(appmodelId));
        List<ArticleCategory> articleCategories = mongoTemplate.find(query, ArticleCategory.class);
        if (articleCategories.size() > 0) {
            return articleCategories;
        } else {
            ArticleCategory articleCategoryNew = new ArticleCategory();
            articleCategoryNew.setCategoryName("所有");
            articleCategoryNew.setCategoryType(0);
            articleCategoryNew.setDeleteState(0);
            articleCategoryNew.setAppmodelId(appmodelId);
            articleCategoryRepository.save(articleCategoryNew);
            articleCategories.add(articleCategoryNew);
            return articleCategories;
        }
    }

    @Override
    public List<ArticleCategory> getByCategoryId(String categoryIds) {
        List<String> ids = Arrays.asList(categoryIds.split(","));
        return mongoTemplate
                .find(new Query(Criteria.where("categoryId").in(ids)), ArticleCategory.class);
    }

    @Override
    public ArticleCategory findByAppmodelIdAndDeleteState(String appmodelId, Integer deleteState) {
        return articleCategoryRepository
                .findByAppmodelIdIsAndCategoryTypeIs(appmodelId, deleteState);
    }

    @Override
    public ArticleCategory deleteCategory(String categoryId, String appmodelId) {
        ArticleCategory articleCategoryDelete = articleCategoryRepository.findById(categoryId).get();
        articleCategoryDelete.setDeleteState(1);
        List<Article> articles = mongoTemplate
                .find(new Query(Criteria.where("categoryIds").regex(categoryId).and("appmodelId").is(appmodelId)),
                        Article.class);
        for (Article article : articles) {
            String[] split = article.getCategoryIds().split(",");
            StringBuilder stringBuilder = new StringBuilder();
            for (String s : split) {
                if (!s.equals(categoryId)) {
                    stringBuilder.append(s).append(",");
                }
            }
            if (stringBuilder.length() > 0) {
                article.setCategoryIds(stringBuilder.substring(0, stringBuilder.length() - 1));
                String[] split1 = article.getCategoryNames().split(",");
                StringBuilder names = new StringBuilder();
                for (String s : split1) {
                    if (!s.equals(articleCategoryDelete.getCategoryName())) {
                        names.append(s);
                    }
                }
                if (names.length() > 0) {
                    article.setCategoryNames(names.substring(0, names.length() - 1));
                } else {
                    article.setCategoryNames("");
                }
            } else {
                article.setCategoryIds(stringBuilder.toString());
                article.setCategoryNames("");
            }
            articleService.addOrUpdateArticle(article,1);
        }
        return articleCategoryRepository.save(articleCategoryDelete);
    }


    /**
     * @param articleCategory 文章分类
     * @param type  0-保存  1-更新
     */
    @Override
    public ArticleCategory savaOrUpdateCategory(ArticleCategory articleCategory, Integer type) {
        if (type == 0) {
            articleCategory.setDeleteState(0);
            articleCategory.setCategoryType(1);
        }
        if (type == 1) {
            ArticleCategory oldArticleCategory = articleCategoryRepository.findById(articleCategory.getCategoryId())
                    .get();
            List<Article> articles = mongoTemplate.find(new Query(
                    Criteria.where("categoryNames").regex(oldArticleCategory.getCategoryName()).and("appmodelId")
                            .is(articleCategory.getAppmodelId())), Article.class);
            String newTime = DateUtil.now();
            for (Article article : articles) {
                String name = article.getCategoryNames()
                        .replace(oldArticleCategory.getCategoryName(), articleCategory.getCategoryName());
                article.setCategoryNames(name);
                article.setUpdateTime(newTime);
            }
            articleService.updateArticles(articles);
            oldArticleCategory.setCategoryName(articleCategory.getCategoryName());
            BeanUtils.copyProperties(articleCategory, oldArticleCategory);
        }
        return articleCategoryRepository.save(articleCategory);
    }

}

