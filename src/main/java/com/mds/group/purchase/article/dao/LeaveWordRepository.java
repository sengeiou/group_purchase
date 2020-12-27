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

import com.mds.group.purchase.article.model.LeaveWord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * The interface Leave word repository.
 *
 * @author Created by wx on 2018/06/07.
 */
@Repository
public interface LeaveWordRepository extends MongoRepository<LeaveWord, String> {

    /***
     * 根据留言id查询留言
     *
     * @param leaveWorleId the leave worle id
     * @return LeaveWord leave word
     */
    LeaveWord findByLeaveWordId(String leaveWorleId);

    /***
     * 根据文章id查询留言数量
     *
     * @param articleId the article id
     * @return List<LeaveWord> integer
     */
    Integer countByArticleId(String articleId);

    /***
     * 根据文章id删除留言
     *
     * @param articleId the article id
     * @return void
     */
    void deleteByArticleId(String articleId);

    /**
     * 查询用户自己留言
     *
     * @param wxuserId  the wxuser id
     * @param articleId the article id
     * @param pageable  the pageable
     * @return page
     */
    Page<LeaveWord> findByWxuserIdAndArticleId(Long wxuserId, String articleId, Pageable pageable);

    /**
     * 小程序端文章留言查询
     *
     * @param articleId  the article id
     * @param choiceness the choiceness
     * @param pageable   the pageable
     * @return page
     */
    Page<LeaveWord> findByArticleIdAndChoiceness(String articleId, Integer choiceness, Pageable pageable);


}
