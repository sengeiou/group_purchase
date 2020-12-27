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
import com.mds.group.purchase.goods.model.GoodsClass;
import com.mds.group.purchase.goods.result.GoodsClassResult;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 商品分类mapper
 *
 * @author shuke
 */
@Repository
public interface GoodsClassMapper extends Mapper<GoodsClass> {

    /**
     * 插入商品分类
     *
     * @param goodsClass 商品分类实体
     */
    void insertGoodsClass(GoodsClass goodsClass);

    /**
     * 查询商品分类的集合
     *
     * @param appmodelId 模板ID
     * @return List<GoodsClassResult> list
     */
    List<GoodsClassResult> selectGoodsClassesByAppmodelId(@Param("appmodelId") String appmodelId);

    /**
     * 根据商品父级id查询其下所属的下级分类集合
     *
     * @param fatherId 分类父id
     * @return 商品分类集合 list
     */
    List<GoodsClass> selectByFatherId(Long fatherId);

    /**
     * 查询改商品分类是否存在
     *
     * @param goodsClassId 商品分类id
     * @param appmodelId   模板ID
     * @return 该分类是否存在 1存在  0不存在
     */
    int isExists(@Param("goodsClassId") Long goodsClassId, @Param("appmodelId") String appmodelId);

    /**
     * 根据父类id删除改父分类下所属的所有下级分类
     * 逻辑删除 更新del_flag = 1
     *
     * @param goodsClassId 商品分类id
     */
    void delByFatherId(List<Long> goodsClassId);

    /**
     * 根据id删除
     * 逻辑删除 更新del_flag = 1
     *
     * @param goodsClassId 商品分类id集合
     */
    void delById(List<Long> goodsClassId);

    /**
     * 得到父分类下的下级分类id
     *
     * @param goodsClassId 商品分类id集合
     * @return 下级分类id集合 list
     */
    List<Long> selectIdByFatherId(List<Long> goodsClassId);

    /**
     * 得到该分类和其下级分类
     *
     * @param goodsClassId 商品分类id
     * @return 商品分类集合 list
     */
    List<GoodsClass> selectGoodsClassesAndUnderClass(Long goodsClassId);


    /**
     * 更新商品分类排序
     *
     * @param classId    分类id
     * @param sort       排序值
     * @param appmodelId the appmodel id
     */
    void updateSortById(@Param("classId") Long classId, @Param("sort") int sort,
                        @Param("appmodelId") String appmodelId);
}