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

import com.mds.group.purchase.article.model.ArticleLaud;

import java.util.List;

/**
 * The interface Article laud service.
 *
 * @author Created by wx on 2018/06/07.
 */
public interface ArticleLaudService {

    /***
     * 根据用户id和文章id查询文章是否点赞
     *
     * @param wxuserId the wxuser id
     * @param articleId the article id
     * @return List<ArticleCategory>  by wxuser id and article id
     */
    List<ArticleLaud> getByWxuserIdAndArticleId(Long wxuserId, List<String> articleId);

    /**
     * 更新点赞
     *
     * @param articleLaud the article laud
     */
    void save(ArticleLaud articleLaud);
}
