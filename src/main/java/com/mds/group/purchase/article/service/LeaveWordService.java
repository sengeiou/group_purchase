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

import com.mds.group.purchase.article.model.LeaveWord;
import com.mds.group.purchase.article.vo.LeaveWordLaudVO;
import com.mds.group.purchase.article.vo.LeaveWordVO;
import com.mds.group.purchase.article.vo.ReplyVO;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * The interface Leave word service.
 *
 * @author Created by wx on 2018/06/07.
 */
public interface LeaveWordService {

    /***
     * 根据文章id查询所有留言后台
     *
     * @param articleId the article id
     * @return List<LeaveWord>  list
     */
    List<LeaveWord> findAll(String articleId);

    /***
     * 根据输入条件查询留言
     *
     * @param findWord the find word
     * @param appmodelId the appmodel id
     * @return List<LeaveWord>  list
     */
    List<LeaveWordVO> findLeaveWord(String findWord, String appmodelId);


    /***
     * 分页查询用户自己留言小程序端
     *
     * @param wxuserId the wxuser id
     * @param articleId the article id
     * @param pageNum the page num
     * @param pageSize the page size
     * @return the page
     */
    Page<LeaveWord> findByWxuserIdAndArticleId(Long wxuserId, String articleId, Integer pageNum, Integer pageSize);

    /***
     * 分页查询小程序端文章精选留言
     *
     * @param wxuserId the wxuser id
     * @param articleId the article id
     * @param pageSize the page size
     * @param pageNum the page num
     * @return the by choiceness type
     */
    Page<LeaveWord> getByChoicenessType(Long wxuserId, String articleId, Integer pageSize, Integer pageNum);


    /**
     * 留言添加
     *
     * @param leaveWord the leave word
     * @return leave word
     */
    LeaveWord save(LeaveWord leaveWord);

    /**
     * 删除某条留言
     *
     * @param leaveWordId the leave word id
     * @param articleId   the article id
     * @return
     */
    void delete(String leaveWordId, String articleId);

    /**
     * 设置留言是否精选
     *
     * @param replyVO the reply vo
     * @return the leave word
     */
    LeaveWord updateToChoiceness(ReplyVO replyVO);

    /**
     * 删除回复
     *
     * @param leaveWordId the leave word id
     * @return the leave word
     */
    LeaveWord deleteChoiceness(String leaveWordId);


    /**
     * 回复留言
     *
     * @param replyInfo   the reply info
     * @param leaveWordId the leave word id
     * @return the leave word
     */
    LeaveWord replyChoiceness(String replyInfo, String leaveWordId);


    /**
     * 留言置顶/取消
     *
     * @param replyVO the reply vo
     */
    void sort(ReplyVO replyVO);

    /**
     * 留言点赞
     *
     * @param leaveWordLaudVO the leave word laud vo
     */
    void updateLeaveWordLaud(LeaveWordLaudVO leaveWordLaudVO);

    /**
     * 删除文章所有留言
     *
     * @param articleid the articleid
     */
    void deleteByArticleid(String articleid);

}
