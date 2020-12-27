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
import com.mds.group.purchase.article.model.Article;
import com.mds.group.purchase.article.model.ArticleCategory;
import com.mds.group.purchase.article.model.ArticleLaud;
import com.mds.group.purchase.article.service.ArticleCategoryService;
import com.mds.group.purchase.article.service.ArticleLaudService;
import com.mds.group.purchase.article.service.ArticleService;
import com.mds.group.purchase.article.service.LeaveWordService;
import com.mds.group.purchase.article.vo.ArticleVO;
import com.mds.group.purchase.exception.ServiceException;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * The type Article service.
 *
 * @author Created by wx on 2018/06/07.
 */
@Service
public class ArticleServiceImpl implements ArticleService {

    @Resource
    private MongoTemplate mongoTemplate;
    @Resource
    private LeaveWordService leaveWordService;
    @Resource
    private ArticleRepository articleRepository;
    @Resource
    private ArticleLaudService articleLaudService;
    @Resource
    private ArticleCategoryService articleCategoryService;

    @Override
    public Map<String, Object> findAll(Integer pageNum, Integer pageSize, String appmodelId) {
        Query query = new Query();
        if (!"".equals(appmodelId)) {
            query.addCriteria(Criteria.where("appmodelId").in(appmodelId));
        }
        // 查询总数
        Long count = mongoTemplate.count(query, Article.class);
        query.skip((pageNum - 1) * pageSize).limit(pageSize);
        //设置起始数 和 查询条数
        query.skip((pageNum - 1) * pageSize).limit(pageSize);
        query.with(new Sort(Sort.Direction.ASC, "sort"));
        List<Article> datas = mongoTemplate.find(query, Article.class);
        Map<String, Object> map = new HashMap<>(8);
        map.put("list", datas);
        map.put("totle", count);
        return map;
    }

    /**
     * 根据appmodelId查询全部文章，先按照顺序排序，在按照更新时间倒排
     */
    private List<Article> findAllAndSort(String appmodelId) {
        Query query = new Query();
        query.addCriteria(Criteria.where("appmodelId").is(appmodelId));
        List<Article> datas = mongoTemplate.find(query, Article.class);
        datas.sort(Comparator.comparing(Article::getSort)
                .thenComparing(Article::getUpdateTime, Comparator.reverseOrder()));
        return datas;
    }

    @Override
    public List<Article> findByAppmodelId(String appmodelId) {
        Query query = new Query();
        query.addCriteria(Criteria.where("appmodelId").is(appmodelId));
        return mongoTemplate.find(query, Article.class);
    }

    @Override
    public Map<String, Object> selectByCategoryId(Integer pageNum, Integer pageSize, String appmodelId, Long wxuserId,
                                                  String categoryId) {
        List<Article> articles;
        Long count;
        if (categoryId == null) {
            Map<String, Object> all = findAll(pageNum, pageSize, appmodelId);
            articles = (List<Article>) all.get("list");
            count = (Long) all.get("totle");
        } else {
            Query query = new Query(Criteria.where("appmodelId").is(appmodelId).and("categoryIds").regex(categoryId));
            query.with(new Sort(Sort.Direction.ASC, "sort"));
            //设置起始数
            query.skip((pageNum - 1) * pageSize);
            //设置查询条数
            query.limit(pageSize);
            articles = mongoTemplate.find(query, Article.class);
            count = mongoTemplate.count(new Query(Criteria.where("appmodelId").is(appmodelId)), Article.class);
        }
        List<String> collect = articles.stream().map(Article::getArticleId).collect(Collectors.toList());
        List<ArticleLaud> articleLaud = articleLaudService.getByWxuserIdAndArticleId(wxuserId, collect);
        List<ArticleVO> articleVOS = new ArrayList<>(articleLaud.size());
        BeanUtils.copyProperties(articles, articleVOS);
        for (ArticleVO article : articleVOS) {
            for (ArticleLaud laud : articleLaud) {
                if (article.getArticleId().equals(laud.getArticleId())) {
                    article.setLaudOrNot(laud.getLaudOrNot());
                }
            }
        }
        Map<String, Object> map = new HashMap<>();
        map.put("list", articles);
        map.put("totle", count);
        return map;
    }

    /**
     * 添加或更新文章
     * @param type    0-创建 1-更新
     */
    @Override
    public Article addOrUpdateArticle(Article article, Integer type) {
        if (type == 0) {
            article.setUpdateTime(DateUtil.now());
            article.setLookSum(0);
            article.setLaud(0);
            article.setDiscussSum(0);
            article.setSort(1);
            ArticleCategory articleCategory = articleCategoryService.getByAppmodelId(article.getAppmodelId()).stream()
                    .filter(obj -> obj.getCategoryType().equals(0)).collect(Collectors.toList()).get(0);
            article.setCategoryIds(articleCategory.getCategoryId());
            mongoTemplate.updateMulti(new Query(Criteria.where("appmodelId").is(article.getAppmodelId())),
                    new Update().inc("sort", 1), Article.class);
        } else {
            Article now = articleRepository.findById(article.getArticleId()).get();
            article.setDiscussSum(now.getDiscussSum());
            article.setLookSum(now.getLookSum());
            article.setLaud(now.getLaud());
        }
        updateCategory(article);
        return articleRepository.save(article);
    }

    private void updateCategory(Article article) {
        if (!"".equals(article.getCategoryIds())) {
            List<ArticleCategory> articleList = articleCategoryService.getByCategoryId(article.getCategoryIds());
            String categoryNames = articleList.stream().map(ArticleCategory::getCategoryName)
                    .collect(Collectors.joining(","));
            article.setCategoryNames(categoryNames);
        }
        if (article.getCategoryNames() == null || !article.getCategoryNames().contains("所有")) {
            ArticleCategory articleCategory = articleCategoryService
                    .findByAppmodelIdAndDeleteState(article.getAppmodelId(), 0);
            if (article.getCategoryIds().length() == 0) {
                article.setCategoryIds(articleCategory.getCategoryId());
                article.setCategoryNames(articleCategory.getCategoryName());
            } else {
                article.setCategoryIds(article.getCategoryIds() + "," + articleCategory.getCategoryId());
                article.setCategoryNames(article.getCategoryNames() + "," + articleCategory.getCategoryName());
            }

        }
    }


    /**
     * @param entirelyExcludeCategoryIds 完全排除的分类
     * @param entirelyIncludeCategoryIds 所有的文章有要的分类
     */
    @Override
    public int setCategory(String articleIds, String appmodelId, String entirelyExcludeCategoryIds,
                           String entirelyIncludeCategoryIds) {
        List<Article> pitchArticle = articleRepository.getArticleByArticleIdIn(articleIds.split(","));
        if (pitchArticle == null || pitchArticle.size() == 0) {
            return 0;
        }
        List<String> allCid = new ArrayList<>();
        for (Article article : pitchArticle) {
            //完全排除的分类
            String[] ids = article.getCategoryIds().split(",");
            List<String> list = new ArrayList<>();
            for (String id : ids) {
                if (entirelyExcludeCategoryIds.contains(id)) {
                    continue;
                }
                list.add(id);
            }
            if (entirelyIncludeCategoryIds.length() > 0 && list.size() > 0) {
                String[] split = entirelyIncludeCategoryIds.split(",");
                String entirelyInclude = list.toString();
                for (String cId : split) {
                    //已经包含这个分类id
                    if (entirelyInclude.contains(cId)) {
                        continue;
                    }
                    list.add(cId);
                }
            }
            if (entirelyIncludeCategoryIds.length() > 0 && list.size() == 0) {
                list = Arrays.stream(entirelyIncludeCategoryIds.split(",")).collect(Collectors.toList());
            }
            if (list.size() > 0) {
                allCid.addAll(list);
                String cid = String.join(",", list);
                article.setCategoryIds(cid);
            }
        }
        String cids = String.join(",", allCid);
        List<ArticleCategory> categoryIds = articleCategoryService.getByCategoryId(cids);
        if (categoryIds != null && categoryIds.size() > 0) {
            Map<String, String> names = categoryIds.stream()
                    .collect(Collectors.toMap(ArticleCategory::getCategoryId, ArticleCategory::getCategoryName));
            for (Article article : pitchArticle) {
                StringBuilder builder = new StringBuilder();
                String[] cIds = article.getCategoryIds().split(",");
                for (String cid : cIds) {
                    if (names.get(cid) != null) {
                        builder.append(names.get(cid)).append(",");
                    }
                }
                if (builder.length() > 0) {
                    article.setCategoryNames(builder.substring(0, builder.length() - 1));
                } else {
                    article.setCategoryNames(builder.toString());
                }
            }
        }
        articleRepository.saveAll(pitchArticle);
        return 1;
    }

    @Override
    public void updateArticles(List<Article> articles) {
        articleRepository.saveAll(articles);
    }


    @Override
    public Article findArticleByArticleId(String articleId) {
        Optional<Article> article = articleRepository.findById(articleId);
        return article.get();
    }

    @Override
    public void delete(String articleIds) {
        String[] articleId = articleIds.split(",");
        for (String articleid : articleId) {
            leaveWordService.deleteByArticleid(articleid);
            articleRepository.deleteById(articleid);
        }
    }

    @Override
    public Integer sort(String appmodelId, String articleId, Integer handleType) {
        Optional<Article> op = articleRepository.findById(articleId);
        Integer count = Math
                .toIntExact(mongoTemplate.count(new Query(Criteria.where("appmodelId").is(appmodelId)), Article.class));
        Article article = op.get();
        Integer currIndex = article.getSort();
        if ((currIndex == 1 && handleType == 1) || (currIndex.equals(count) && handleType == 4)) {
            return 0;
        }
        switch (handleType) {
            case 1:
                article.setSort(1);
                break;
            case 2:
                article.setSort(article.getSort() - 1);
                break;
            case 3:
                article.setSort(article.getSort() + 2);
                break;
            case 4:
                article.setSort(count + 1);
                break;
            default:
                break;
        }
        article.setUpdateTime(DateUtil.now());
        articleRepository.save(article);
        List<Article> articles = findAllAndSort(appmodelId);
        int index = 1;
        for (Article article1 : articles) {
            article1.setSort(index++);
        }
        articleRepository.saveAll(articles);
        return 1;
    }

    @Override
    public ArticleVO lookAriticle(Long wxuserId, String articleId) {
        Article article = findArticleByArticleId(articleId);
        if (article == null) {
            throw new ServiceException("文章不存在");
        }
        // 增加浏览量
        article.setLookSum(article.getLookSum() + 1);
        article = articleRepository.save(article);
        List<String> articleIds = new ArrayList<>(1);
        articleIds.add(articleId);
        ArticleVO articleVO = new ArticleVO();
        BeanUtils.copyProperties(article, articleVO);
        if (articleLaudService.getByWxuserIdAndArticleId(wxuserId, articleIds) != null
                && articleLaudService.getByWxuserIdAndArticleId(wxuserId, articleIds).size() > 0) {
            ArticleLaud articleLaud = articleLaudService.getByWxuserIdAndArticleId(wxuserId, articleIds).get(0);
            if (articleLaud == null) {
                articleVO.setLaudOrNot(false);
            } else {
                articleVO.setLaudOrNot(articleLaud.getLaudOrNot());
            }
        }
        return articleVO;
    }

    @Override
    public Map<String, Object> updateArticleLaud(Long wxuserId, String articleId, String appmodelId) {
        Article article = findArticleByArticleId(articleId);
        List<String> articleIds = new ArrayList<>(1);
        articleIds.add(articleId);
        if (articleLaudService.getByWxuserIdAndArticleId(wxuserId, articleIds) != null
                && articleLaudService.getByWxuserIdAndArticleId(wxuserId, articleIds).size() > 0) {
            ArticleLaud articleLaud = articleLaudService.getByWxuserIdAndArticleId(wxuserId, articleIds).get(0);
            if (!articleLaud.getLaudOrNot()) {
                articleLaud.setLaudOrNot(true);
                article.setLaud(article.getLaud() + 1);
            } else {
                articleLaud.setLaudOrNot(false);
                article.setLaud(article.getLaud() - 1);
            }
            articleLaudService.save(articleLaud);
        } else {
            ArticleLaud articleLaudNew = new ArticleLaud();
            articleLaudNew.setWxuserId(wxuserId);
            articleLaudNew.setArticleId(articleId);
            articleLaudNew.setLaudOrNot(true);
            articleLaudNew.setAppmodelId(appmodelId);
            articleLaudService.save(articleLaudNew);
            article.setLaud(article.getLaud() + 1);
        }
        article = articleRepository.save(article);
        Map<String, Object> map = new HashMap<>(8);
        List<String> list = new ArrayList<>(1);
        list.add(articleId);
        map.put("laudOrNot", articleLaudService.getByWxuserIdAndArticleId(wxuserId, list).get(0).getLaudOrNot());
        map.put("laud", article.getLaud());
        return map;
    }


}
