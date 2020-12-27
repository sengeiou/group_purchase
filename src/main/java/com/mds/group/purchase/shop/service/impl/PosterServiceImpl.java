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

package com.mds.group.purchase.shop.service.impl;

import cn.hutool.core.date.DateUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.mds.group.purchase.activity.model.ActivityGoods;
import com.mds.group.purchase.activity.service.ActivityGoodsService;
import com.mds.group.purchase.activity.service.ActivityService;
import com.mds.group.purchase.constant.Common;
import com.mds.group.purchase.constant.HandType;
import com.mds.group.purchase.core.AbstractService;
import com.mds.group.purchase.exception.ServiceException;
import com.mds.group.purchase.goods.model.Goods;
import com.mds.group.purchase.goods.service.GoodsService;
import com.mds.group.purchase.shop.dao.BottomPosterMapper;
import com.mds.group.purchase.shop.dao.PosterMapper;
import com.mds.group.purchase.shop.model.BottomPoster;
import com.mds.group.purchase.shop.model.Poster;
import com.mds.group.purchase.shop.service.PosterService;
import com.mds.group.purchase.shop.vo.ActivityInfoVO;
import com.mds.group.purchase.shop.vo.OptionalPosterActivityGoodsVO;
import com.mds.group.purchase.shop.vo.PosterVO;
import com.mds.group.purchase.shop.vo.SortVO;
import com.mds.group.purchase.utils.BeanMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Condition;

import javax.annotation.Resource;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


/**
 * The type Poster service.
 *
 * @author Created by wx on 2018/05/25.
 */
@Service
public class PosterServiceImpl extends AbstractService<Poster> implements PosterService {

    @Resource
    private GoodsService goodsService;
    @Resource
    private PosterMapper tPosterMapper;
    @Resource
    private ActivityService activityService;
    @Resource
    private BottomPosterMapper bottomPosterMapper;
    @Resource
    private ActivityGoodsService activityGoodsService;

    @Override
    public List<PosterVO> findByAppmodelId(String appmodelId) {
        List<Poster> posters = tPosterMapper.findByAppmodelId(appmodelId);
        List<PosterVO> posterVOS = BeanMapper.mapList(posters, PosterVO.class);
        //筛选出有链接商品的轮播图(把商品封装进去)
        List<Poster> haveProduct = posters.stream().filter(obj -> null != obj.getActivityGoodsId())
                .collect(Collectors.toList());
        if (haveProduct.size() > 0) {
            StringBuilder sb = new StringBuilder();
            for (Poster poster : haveProduct) {
                sb.append(poster.getPosterId()).append(",");
            }
            List<Goods> productList = goodsService.findByIds(sb.substring(0, sb.length() - 1));
            for (PosterVO poster : posterVOS) {
                for (Goods goods : productList) {
                    if (poster.getActivityGoodsId().equals(goods.getGoodsId())) {
                        poster.setGoodsInfo(goods);
                    }
                }
            }
        }
        return posterVOS;
    }

    @Override
    public int saveOrUpdate(Poster poster) {
        if (poster.getJumpType().equals(0)) {
            boolean b1 = poster.getArticleId() != null && poster.getArticleId().length() > 0;
            boolean b2 = poster.getActivityGoodsId() != null && poster.getActivityGoodsId() > 0;
            if (b1 || b2) {
                throw new ServiceException("当前为不转跳类型");
            }
        }
        if (poster.getJumpType().equals(1)) {
            if (StringUtils.isNotBlank(poster.getArticleId())) {
                throw new ServiceException("当前为关联商品");
            }
        }
        if (poster.getJumpType().equals(2)) {
            if (poster.getActivityGoodsId() != null) {
                throw new ServiceException("当前关联发现");
            }
        }
        int i;
        if (poster.getPosterId() == null) {
            poster.setCreateTime(DateUtil.date());
            List<Poster> posters = tPosterMapper.findByAppmodelId(poster.getAppmodelId());
            if (posters.size() >= 5) {
                throw new ServiceException("已达上限，无法添加");
            }
            if (posters.size() > 0) {
                for (Poster posterNew : posters) {
                    posterNew.setSort(posterNew.getSort() + 1);
                    tPosterMapper.updateByPrimaryKeySelective(posterNew);
                }
            }
            poster.setSort(1);
            i = tPosterMapper.insertSelective(poster);
        } else {
            i = tPosterMapper.updateByPrimaryKeySelective(poster);
        }
        return i;
    }

    @Override
    public List<Poster> infos(String appmodelId, Integer posterId) {
        Poster poster = new Poster();
        poster.setAppmodelId(appmodelId);
        if (posterId != null && posterId > 0) {
            poster.setPosterId(posterId);
        }
        List<Poster> posters = tPosterMapper.select(poster);
        if (posters.size() > 0) {
            posters.sort(Comparator.comparing(Poster::getSort));
        }
        return posters;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int sort(SortVO sortVO) {
        if (sortVO.getPosterId() == null || sortVO.getPosterId() <= 0) {
            throw new ServiceException("海报id不能为空");
        }
        if (sortVO.getHandleType().equals(HandType.TOP) || sortVO.getHandleType().equals(HandType.UP)) {
            List<Poster> posters = tPosterMapper.findByAppmodelIdDesc(sortVO.getAppmodelId());
            Poster poster = posters.stream().filter(obj -> obj.getPosterId().equals(sortVO.getPosterId()))
                    .collect(Collectors.toList()).get(0);
            if (posters.size() == 0) {
                throw new ServiceException("无数据可排序");
            }
            if (sortVO.getHandleType().equals(HandType.UP)) {
                for (int i = 0; i < posters.size(); i++) {
                    if (posters.get(i).getPosterId().equals(poster.getPosterId())) {
                        int index = i + 1;
                        //判断上一个数组是否存在,超出则表示已是置顶
                        if (index < 0) {
                            return sortVO.getHandleType();
                        }
                        Integer sort = posters.get(index).getSort();
                        posters.get(index).setSort(poster.getSort());
                        poster.setSort(sort);
                        tPosterMapper.updateByPrimaryKeySelective(posters.get(index));
                        tPosterMapper.updateByPrimaryKeySelective(poster);
                        return sortVO.getHandleType();
                    }
                }
            } else {
                for (Poster posterNew : posters) {
                    if (poster.getSort() > posterNew.getSort()) {
                        this.posterSort(poster, posterNew);
                    }
                }
            }
            return sortVO.getHandleType();
        }
        if (sortVO.getHandleType().equals(HandType.FOOT) || sortVO.getHandleType().equals(HandType.DOWN)) {
            List<Poster> posters = tPosterMapper.findByAppmodelId(sortVO.getAppmodelId());
            Poster poster = posters.stream().filter(obj -> obj.getPosterId().equals(sortVO.getPosterId()))
                    .collect(Collectors.toList()).get(0);
            if (posters.size() == 0) {
                throw new ServiceException("无数据可排序");
            }
            if (sortVO.getHandleType().equals(HandType.DOWN)) {
                for (int i = 0; i < posters.size(); i++) {
                    if (posters.get(i).getPosterId().equals(poster.getPosterId())) {
                        int index = i + 1;
                        //判断下一个数组是否存在,超出则表示没有
                        if (index > posters.size()) {
                            return sortVO.getHandleType();
                        }
                        Integer sort = posters.get(index).getSort();
                        posters.get(index).setSort(poster.getSort());
                        poster.setSort(sort);
                        tPosterMapper.updateByPrimaryKeySelective(posters.get(index));
                        tPosterMapper.updateByPrimaryKeySelective(poster);
                        return sortVO.getHandleType();
                    }
                }
            } else {
                for (Poster posterNew : posters) {
                    if (poster.getSort() < posterNew.getSort()) {
                        this.posterSort(poster, posterNew);
                    }
                }
            }
            return sortVO.getHandleType();
        }
        throw new ServiceException("非法操作");
    }

    @Override
    public void sort(String postIds, String appmodelId) {
        String[] split = postIds.split(Common.REGEX);
        for (int i = 0; i < split.length; i++) {
            tPosterMapper.updateSortById(Integer.valueOf(split[i]), i + 1, appmodelId);
        }
    }

    private void posterSort(Poster poster, Poster posterNew) {
        Integer sortNew = posterNew.getSort();
        posterNew.setSort(poster.getSort());
        tPosterMapper.updateByPrimaryKeySelective(posterNew);
        poster.setSort(sortNew);
        tPosterMapper.updateByPrimaryKeySelective(poster);
    }

    @Override
    public PageInfo<ActivityInfoVO> getOptionalPosterActivityGoods(String appmodelId, Integer searchType,
                                                                   Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<ActivityInfoVO> activityInfoVOS = activityService.findAssignActivitys(appmodelId, searchType);
        if (activityInfoVOS.size() > 0) {
            List<Long> activityLists = activityInfoVOS.stream().map(ActivityInfoVO::getActivityId)
                    .collect(Collectors.toList());
            Condition condition = new Condition(ActivityGoods.class);
            condition.createCriteria().andIn("activityId", activityLists).andEqualTo("delFlag", 0);
            List<ActivityGoods> activityGoodsList = activityGoodsService.findByCondition(condition);
            String goodsIds = activityGoodsList.stream().map(obj -> obj.getGoodsId().toString())
                    .collect(Collectors.joining(","));
            //根据活动id分组
            Map<Long, List<ActivityGoods>> activityGoodsListMap = activityGoodsList.stream()
                    .collect(Collectors.groupingBy(ActivityGoods::getActivityId));
            //查询所有商品
            Map<Long, Goods> goodsMap = goodsService.findByIds(goodsIds).stream()
                    .collect(Collectors.toMap(Goods::getGoodsId, v -> v));

            activityInfoVOS.forEach(obj -> {
                List<ActivityGoods> activityGoods = activityGoodsListMap.get(obj.getActivityId());
                List<OptionalPosterActivityGoodsVO> goodsList = new LinkedList<>();
                activityGoods.forEach(obi -> {
                    Goods goods = goodsMap.get(obi.getGoodsId());
                    if (goods.getGoodsDelFlag().equals(false) && goods.getGoodsStatus().equals(0)) {
                        OptionalPosterActivityGoodsVO vo = BeanMapper.map(goods, OptionalPosterActivityGoodsVO.class);
                        vo.setActivityGoodsId(obi.getActivityGoodsId());
                        goodsList.add(vo);
                    }
                });
                obj.setGoodsList(goodsList);
            });
        }
        return PageInfo.of(activityInfoVOS);
    }

    @Override
    public BottomPoster findBottomPoster(String appmodelId) {
        BottomPoster bottomPoster = bottomPosterMapper.selectByAppmodelId(appmodelId);
        if (bottomPoster == null) {
            //查询不到则初始化一个
            bottomPoster = new BottomPoster();
            bottomPoster.setStatus(1);
            bottomPoster.setPosterUrl("https://www.superprism.cn/resource/groupmall/zhaopintuanzhang2.jpg");
            bottomPoster.setPhone("18106661091");
            bottomPoster.setAppmodelId(appmodelId);
            bottomPosterMapper.insert(bottomPoster);
        }
        return bottomPoster;
    }

    @Override
    public void updateBottomPoster(BottomPoster bottomPoster) {
        bottomPosterMapper.updateByPrimaryKey(bottomPoster);
    }


    @Override
    public int batchDelete(String[] posterId) {
        return tPosterMapper.batchDelete(posterId);
    }
}
