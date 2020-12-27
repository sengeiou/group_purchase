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
import com.mds.group.purchase.article.dao.ArticleRepository;
import com.mds.group.purchase.article.dao.LeaveWordRepository;
import com.mds.group.purchase.article.model.Article;
import com.mds.group.purchase.article.model.LeaveWord;
import com.mds.group.purchase.article.model.LeaveWordLaud;
import com.mds.group.purchase.article.service.ArticleService;
import com.mds.group.purchase.article.service.LeaveWordLaudService;
import com.mds.group.purchase.article.service.LeaveWordService;
import com.mds.group.purchase.article.vo.LeaveWordLaudVO;
import com.mds.group.purchase.article.vo.LeaveWordVO;
import com.mds.group.purchase.article.vo.ReplyVO;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * The type Leave word service.
 *
 * @author Created by wx on 2018/06/07.
 */
@Service
public class LeaveWordServiceImpl implements LeaveWordService {

    @Resource
    private MongoTemplate mongoTemplate;
    @Resource
    private ArticleService articleService;
    @Resource
    private ArticleRepository articleRepository;
    @Resource
    private LeaveWordRepository leaveWordRepository;
    @Resource
    private LeaveWordLaudService leaveWordLaudService;

    @Override
    public List<LeaveWord> findAll(String articleId) {
        Query query = new Query();
        query.addCriteria(Criteria.where("articleId").is(articleId));
        query.with(new Sort(Sort.Direction.DESC, "sortTime"));
        query.with(new Sort(Sort.Direction.DESC, "leaveTime"));
        return mongoTemplate.find(query, LeaveWord.class);
    }


    @Override
    public Page<LeaveWord> findByWxuserIdAndArticleId(Long wxuserId, String articleId, Integer pageNum,
                                                      Integer pageSize) {
        return leaveWordRepository.findByWxuserIdAndArticleId(wxuserId, articleId,
                PageRequest.of(pageNum - 1, pageSize, Sort.Direction.DESC, "leaveWordId"));
    }

    @Override
    public Page<LeaveWord> getByChoicenessType(Long wxuserId, String articleId, Integer pageNum, Integer pageSize) {
        Page<LeaveWord> leaveWords = leaveWordRepository.findByArticleIdAndChoiceness(articleId, 1,
                PageRequest
                        .of(pageNum - 1, pageSize, Sort.Direction.DESC, "sortTime"));
        if (leaveWords.getContent() != null && leaveWords.getContent().size() > 0) {
            for (LeaveWord leaveWord : leaveWords) {
                LeaveWordLaud leaveWordLaud = leaveWordLaudService
                        .getByWxuserIdAndLeaveWordId(wxuserId, leaveWord.getLeaveWordId());
                if (leaveWordLaud == null) {
                    leaveWord.setLaudOrNot(false);
                } else {
                    leaveWord.setLaudOrNot(leaveWordLaud.getLaudOrNot());
                }
            }
        }
        return leaveWords;
    }

    @Override
    public LeaveWord save(LeaveWord leaveWord) {
        leaveWord.setLeaveTime(DateUtil.now());
        leaveWord.setSortType(0);
        leaveWord.setSortTime(0L);
        leaveWord.setReplyType(0);
        leaveWord.setChoiceness(0);
        leaveWord.setLaud(0);
        LeaveWord save = leaveWordRepository.save(leaveWord);
        Article article = articleService.findArticleByArticleId(leaveWord.getArticleId());
        article.setDiscussSum(leaveWordRepository.countByArticleId(leaveWord.getArticleId()));
        articleRepository.save(article);
        return save;
    }

    @Override
    public void delete(String leaveWordId, String articleId) {
        leaveWordRepository.deleteById(leaveWordId);
        Article article = articleService.findArticleByArticleId(articleId);
        article.setDiscussSum(leaveWordRepository.countByArticleId(articleId));
        articleRepository.save(article);
    }

    @Override
    public LeaveWord updateToChoiceness(ReplyVO replyVO) {
        Integer choiceness = replyVO.getChoiceness();
        Optional<LeaveWord> op = leaveWordRepository.findById(replyVO.getLeaveWordId());
        LeaveWord leaveWord = op.get();
        LeaveWord save = null;
        // 设置是否精选
        if (choiceness != null) {
            if (choiceness == 0) {
                if (leaveWord.getSortType() == 1) {
                    leaveWord.setSortType(0);
                    leaveWord.setSortTime(0L);
                }
            }
            leaveWord.setChoiceness(choiceness);
            save = leaveWordRepository.save(leaveWord);
        }
        return save;
    }

    @Override
    public LeaveWord deleteChoiceness(String leaveWordId) {
        Optional<LeaveWord> op = leaveWordRepository.findById(leaveWordId);
        LeaveWord leaveWord = op.get();
        // 留言回复删除
        leaveWord.setReplyType(0);
        leaveWord.setReplyInfo(null);
        return leaveWordRepository.save(leaveWord);
    }

    @Override
    public LeaveWord replyChoiceness(String replyInfo, String leaveWordId) {
        Optional<LeaveWord> op = leaveWordRepository.findById(leaveWordId);
        LeaveWord leaveWord = op.get();
        LeaveWord save = null;
        // 新增留言回复或更新
        if (replyInfo != null) {
            leaveWord.setReplyInfo(replyInfo);
            leaveWord.setReplyTime(DateUtil.now());
            leaveWord.setReplyType(1);
            save = leaveWordRepository.save(leaveWord);
        }
        return save;
    }


    @Override
    public void sort(ReplyVO replyVO) {
        Integer sortType = replyVO.getSortType();
        Optional<LeaveWord> op = leaveWordRepository.findById(replyVO.getLeaveWordId());
        LeaveWord leaveWord = op.get();
        if (sortType == 1) {
            leaveWord.setChoiceness(1);
            leaveWord.setSortType(sortType);
            leaveWord.setSortTime(System.currentTimeMillis());
            leaveWordRepository.save(leaveWord);
        } else {
            leaveWord.setSortType(sortType);
            leaveWord.setSortTime(0L);
            leaveWordRepository.save(leaveWord);
        }
    }

    @Override
    public void updateLeaveWordLaud(LeaveWordLaudVO leaveWordLaudVO) {
        Long wxuserId = leaveWordLaudVO.getWxuserId();
        String leaveWordId = leaveWordLaudVO.getLeaveWordId();
        LeaveWord leaveWord = leaveWordRepository.findByLeaveWordId(leaveWordId);
        LeaveWordLaud leaveWordLaud = leaveWordLaudService.getByWxuserIdAndLeaveWordId(wxuserId, leaveWordId);
        if (leaveWordLaud == null) {
            LeaveWordLaud leaveWordLaudNew = new LeaveWordLaud();
            leaveWordLaudNew.setWxuserId(wxuserId);
            leaveWordLaudNew.setLeaveWordId(leaveWordId);
            leaveWordLaudNew.setLaudOrNot(true);
            leaveWordLaudService.save(leaveWordLaudNew);
            leaveWord.setLaud(leaveWord.getLaud() + 1);
            leaveWordRepository.save(leaveWord);
        } else {
            if (!leaveWordLaud.getLaudOrNot()) {
                leaveWordLaud.setLaudOrNot(true);
                leaveWordLaudService.save(leaveWordLaud);
                leaveWord.setLaud(leaveWord.getLaud() + 1);
                leaveWordRepository.save(leaveWord);
            } else {
                leaveWordLaud.setLaudOrNot(false);
                leaveWordLaudService.save(leaveWordLaud);
                leaveWord.setLaud(leaveWord.getLaud() - 1);
                leaveWordRepository.save(leaveWord);
            }
        }
    }

    @Override
    public void deleteByArticleid(String articleid) {
        leaveWordRepository.deleteByArticleId(articleid);
    }

    @Override
    public List<LeaveWordVO> findLeaveWord(String findWord, String appmodelId) {
        Criteria criterias = new Criteria();
        criterias.orOperator(Criteria.where("wxuserName").regex(".*?" + findWord + ".*"),
                Criteria.where("leaveInfo").regex(".*?" + findWord + ".*"));
        Query query = new Query(criterias);
        query.addCriteria(Criteria.where("appmodelId").is(appmodelId));
        query.with(new Sort(Sort.Direction.DESC, "leaveTime"));
        List<LeaveWord> leaveWords = mongoTemplate.find(query, LeaveWord.class);
        List<LeaveWordVO> leaveWordVOs = new ArrayList<>(leaveWords.size());
        for (LeaveWord leaveWord : leaveWords) {
            LeaveWordVO leaveWordVO = new LeaveWordVO();
            BeanUtils.copyProperties(leaveWord, leaveWordVO);
            leaveWordVOs.add(leaveWordVO);
        }
        return leaveWordVOs;
    }
}
