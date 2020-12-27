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


import com.mds.group.purchase.article.dao.ArticleLaudRepository;
import com.mds.group.purchase.article.model.ArticleLaud;
import com.mds.group.purchase.article.service.ArticleLaudService;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * The type Article laud service.
 *
 * @author Created by wx on 2018/06/07.
 */
@Service
public class ArticleLaudServiceImpl implements ArticleLaudService {

    @Resource
    private MongoTemplate mongoTemplate;
    @Resource
    private ArticleLaudRepository articleLaudRepository;

    @Override
    public List<ArticleLaud> getByWxuserIdAndArticleId(Long wxuserId, List<String> articleId) {
        Query query = new Query();
        query.addCriteria(Criteria.where("wxuserId").is(wxuserId).and("articleId").in(articleId));
        return mongoTemplate.find(query, ArticleLaud.class);
    }

    @Override
    public void save(ArticleLaud articleLaud) {
        articleLaudRepository.save(articleLaud);
    }
}
