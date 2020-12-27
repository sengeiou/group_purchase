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

import com.mds.group.purchase.article.model.ArticleCategory;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * The interface Article category repository.
 *
 * @author Created by wx on 2018/06/07.
 */
@Repository
public interface ArticleCategoryRepository extends MongoRepository<ArticleCategory, String> {

    /**
     * Find by appmodel id is and category type is article category.
     *
     * @param appmodelId   the appmodel id
     * @param categoryType the category type
     * @return the article category
     */
    ArticleCategory findByAppmodelIdIsAndCategoryTypeIs(String appmodelId, Integer categoryType);
}
