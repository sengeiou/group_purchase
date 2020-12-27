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

package com.mds.group.purchase.shop.service;

import com.github.pagehelper.PageInfo;
import com.mds.group.purchase.core.Service;
import com.mds.group.purchase.shop.model.BottomPoster;
import com.mds.group.purchase.shop.model.Poster;
import com.mds.group.purchase.shop.vo.ActivityInfoVO;
import com.mds.group.purchase.shop.vo.PosterVO;
import com.mds.group.purchase.shop.vo.SortVO;

import java.util.List;

/**
 * The interface Poster service.
 *
 * @author Created by wx on 2018/05/25.
 */
public interface PosterService extends Service<Poster> {

    /***
     * 根据appmodelId查询轮播图按sort值正序排序
     *
     * @param appmodelId the appmodel id
     * @return List<Poster>  list
     */
    List<PosterVO> findByAppmodelId(String appmodelId);

    /***
     * 批量删除轮播图
     *
     * @param posterId the poster id
     * @return int int
     */
    int batchDelete(String[] posterId);

    /**
     * 创建/更新轮播图
     *
     * @param poster the poster
     * @return int int
     */
    int saveOrUpdate(Poster poster);

    /**
     * 查询轮播图详情或所有轮播图
     *
     * @param appmodelId the appmodel id
     * @param posterId   the poster id
     * @return list list
     */
    List<Poster> infos(String appmodelId, Integer posterId);

    /**
     * 轮播图排序
     *
     * @param sortVO the sort vo
     * @return int int
     */
    int sort(SortVO sortVO);

    /**
     * 轮播图拖拽排序
     *
     * @param postIds    轮播图id，用逗号分隔
     * @param appmodelId 模板id
     * @since v1.2.2
     */
    void sort(String postIds, String appmodelId);

    /**
     * 查询进行中或未开始的拼团或秒杀活动
     *
     * @param appmodelId the appmodel id
     * @param searchType the search type
     * @param pageNum    the page num
     * @param pageSize   the page size
     * @return the optional poster activity goods
     */
    PageInfo<ActivityInfoVO> getOptionalPosterActivityGoods(String appmodelId, Integer searchType, Integer pageNum,
                                                            Integer pageSize);

    /**
     * 查询底部海报
     *
     * @param appmodelId the appmodel id
     * @return the bottom poster
     */
    BottomPoster findBottomPoster(String appmodelId);

    /**
     * 更新底部海报
     *
     * @param bottomPoster the bottom poster
     */
    void updateBottomPoster(BottomPoster bottomPoster);
}
