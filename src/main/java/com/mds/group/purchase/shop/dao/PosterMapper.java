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

package com.mds.group.purchase.shop.dao;

import com.mds.group.purchase.core.Mapper;
import com.mds.group.purchase.shop.model.Poster;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * The interface Poster mapper.
 *
 * @author Created by wx on 2018/05/25.
 */
public interface PosterMapper extends Mapper<Poster> {

    /**
     * 根据appmodelId查找商品展示区根据sort值正序排序
     *
     * @param appmodelId the appmodel id
     * @return List<Poster>  list
     */
    List<Poster> findByAppmodelId(String appmodelId);

    /**
     * 根据appmodelId查找商品展示区根据sort值倒序排序
     *
     * @param appmodelId the appmodel id
     * @return List<Poster>  list
     */
    List<Poster> findByAppmodelIdDesc(String appmodelId);

    /**
     * 批量删除轮播图
     *
     * @param posterId 轮播图id
     * @return int int
     */
    int batchDelete(String[] posterId);

    /**
     * 拖拽排序轮播图
     *
     * @param posterId   轮播图id
     * @param sort       the sort
     * @param appmodelId 模板id
     * @since v1.2.2
     */
    void updateSortById(@Param("posterId") Integer posterId, @Param("sort") Integer sort,
                        @Param("appmodelId") String appmodelId);
}