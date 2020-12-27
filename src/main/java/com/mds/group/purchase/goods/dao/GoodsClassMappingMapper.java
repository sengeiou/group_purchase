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

package com.mds.group.purchase.goods.dao;

import com.mds.group.purchase.core.Mapper;
import com.mds.group.purchase.goods.model.GoodsClassMapping;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 商品分类映射Mapper
 *
 * @author shuke
 */
@Repository
public interface GoodsClassMappingMapper extends Mapper<GoodsClassMapping> {

    /**
     * 根据商品id删除商品分类映射
     *
     * @param goodsId 商品id
     */
    void deleteByGoodsId(@Param("goodsId") Long goodsId);

    /**
     * 根据商品分类id集合批量删除商品分类映射
     *
     * @param goodsClassIds 商品id集合
     */
    void deleteBatchByGoodsClassId(@Param("goodsClassIds") List<Long> goodsClassIds);

    /**
     * 根据商品id批量查询商品分类映射
     *
     * @param goodsIds 商品id集合
     * @return List<GoodsClassMapping> list
     */
    List<GoodsClassMapping> selectByGoodsIds(@Param("goodsIds") List<Long> goodsIds);

    /**
     * 根据商品id删除分类映射
     *
     * @param goodsIdList 商品id集合
     */
    void deleteByGoodsIdList(@Param("goodsIdList") List<Long> goodsIdList);
}