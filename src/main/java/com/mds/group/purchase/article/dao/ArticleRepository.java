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

package com.mds.group.purchase.article.dao;

import com.mds.group.purchase.article.model.Article;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * The interface Article repository.
 *
 * @author Created by wx on 2018/06/07.
 */
@Repository
public interface ArticleRepository extends MongoRepository<Article, String> {


    /**
     * Gets article by article id in.
     *
     * @param articleIds the article ids
     * @return the article by article id in
     */
    List<Article> getArticleByArticleIdIn(String[] articleIds);


}
