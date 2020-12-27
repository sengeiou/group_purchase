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

package com.mds.group.purchase.logistics.dao;

import com.mds.group.purchase.core.Mapper;
import com.mds.group.purchase.logistics.model.GoodsAreaMapping;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * The interface Goods area mapping mapper.
 *
 * @author pavawi
 */
public interface GoodsAreaMappingMapper extends Mapper<GoodsAreaMapping> {
    /***
     * 根据条件查询投放区域
     * @param goodsAreaMapping the goods area mapping
     * @return list list
     */
    List<GoodsAreaMapping> selectByFuzzy(@Param("goodsAreaMapping") GoodsAreaMapping goodsAreaMapping);

    /**
     * Delete by goods ids.
     *
     * @param goodsIds the goods ids
     */
    void deleteByGoodsIds(List<Long> goodsIds);

    /**
     * 根据商品id查询投放区域
     *
     * @param goodsIdList the goods id list
     * @return list list
     */
    List<GoodsAreaMapping> selectByGoodsIds(List<Long> goodsIdList);


    /**
     * Select by goods id list.
     *
     * @param goodsId the goods id
     * @return the list
     */
    List<GoodsAreaMapping> selectByGoodsId(Long goodsId);

    /**
     * Delete by community ids.
     *
     * @param longs the longs
     */
    void deleteByCommunityIds(List<Long> longs);
}