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
import com.mds.group.purchase.goods.model.Goods;
import com.mds.group.purchase.goods.result.GoodsResult;
import com.mds.group.purchase.goods.vo.GetGoodsVo;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * 商品Mapper
 *
 * @author shuke
 */
@Repository
public interface GoodsMapper extends Mapper<Goods> {

    /**
     * 插入一条商品记录
     *
     * @param record 商品实体
     */
    void insertGoods(Goods record);

    /**
     * 更新商品
     *
     * @param goods 商品
     */
    void updateSelective(Goods goods);

    /**
     * 根据商品id查询商品是否存在
     *
     * @param goodsId    商品id
     * @param appmodelId 模板id
     * @return 1存在 0不存在
     */
    int isExists(@Param("goodsId") Long goodsId, @Param("appmodelId") String appmodelId);

    /**
     * 查询投放中的商品
     *
     * @param appmodelId 模板id
     * @return List<GoodsResult> list
     */
    List<GoodsResult> selectInPutInGoods(@Param("appmodelId") String appmodelId);

    /**
     * 根据商品名称模糊查询
     *
     * @param appmodelId 模板id
     * @param goodsName  商品名称
     * @return List<GoodsResult> list
     */
    List<GoodsResult> selectLikeName(@Param("appmodelId") String appmodelId, @Param("goodsName") String goodsName);

    /**
     * 根据商品状态和商品名称模糊查询
     * 状态(默认上架，0--上架，1--下架（仓库中），2--已售完)
     *
     * @param appmodelId  模板id
     * @param goodsName   商品名称
     * @param goodsStatus 商品状态
     * @return List<GoodsResult> list
     */
    List<GoodsResult> selectByStatusLikeName(@Param("appmodelId") String appmodelId,
                                             @Param("goodsName") String goodsName,
                                             @Param("goodsStatus") Integer goodsStatus);

    /**
     * 根据条件动态查询商品列表
     * 查询了3张表
     * t_goods , t_goods_detail , t_goods_class_mapping
     *
     * @param appmodelId    小程序模板id
     * @param getGoodsVo    获取商品列表的参数对象
     * @param goodsClassIds 商品分类id集合
     * @return goodsResult对象 list
     */
    List<GoodsResult> selectGoods(@Param("appmodelId") String appmodelId, @Param("getGoodsVo") GetGoodsVo getGoodsVo,
                                  @Param("goodsClassIds") List<Long> goodsClassIds);

    /**
     * 根据动态条件计数商品
     *
     * @param appmodelId    小程序模板id
     * @param getGoodsVo    获取商品列表的参数对象
     * @param goodsClassIds 商品分类id集合
     * @return 商品个数 int
     */
    int selectCountGoods(@Param("appmodelId") String appmodelId, @Param("getGoodsVo") GetGoodsVo getGoodsVo,
                         @Param("goodsClassIds") List<Long> goodsClassIds);

    /**
     * 根据商品id查询在售，未删除的商品
     *
     * @param goodsIds 多个商品id，中间用逗号（，）分隔 示例：12,55,56
     * @return List<Goods> list
     */
    List<Goods> selectByGoodsIds(String goodsIds);

    /**
     * 更新供应商商品的上架状态为下架
     * providerId
     * providerIds
     * optionType 1-更新供应商商品为下架状态 2-更新供应商商品逻辑删除
     *
     * @param map 参数map
     */
    void updateProviderGoods(Map<String, Object> map);

    /**
     * 查询所有商品
     *
     * @param appmodelId 模板id
     * @return List<GoodsResult> list
     */
    List<GoodsResult> selectAllGoods(String appmodelId);

    /**
     * 根据供应商id查询供应商名称集合
     *
     * @param providerIds 供应商id集合
     * @param appmodelId  模板id
     * @return 供应商名称集合 list
     */
    List<String> selectByProviderPutawayOfgoods(@Param("providerIds") List<String> providerIds,
                                                @Param("appmodelId") String appmodelId);

    /**
     * 查询未投放的商品
     *
     * @param appmodelId 模板id
     * @return 商品集合 list
     */
    List<Goods> selectByNoMappinGoods(@Param("appmodelId") String appmodelId);

    /**
     * 根据id查询未删除商品
     *
     * @param goodsIds 商品id集合
     * @return 商品集合 list
     */
    List<Goods> selectByIdListNotDel(@Param("goodsIds") List<Long> goodsIds);

    /**
     * 批量设置商品状态为下架
     *
     * @param goodsIdList 商品id集合
     */
    void updateGoodsStatusBatch(@Param("goodsIdList") List<Long> goodsIdList);

    /**
     * 批量删除
     *
     * @param goodsIdList 商品id集合
     */
    void deleteBatch(@Param("goodsIdList") List<Long> goodsIdList);

    /**
     * 根据id查询状态为上架的未删除商品
     *
     * @param goodsIdList 商品id集合
     * @return 商品集合 list
     */
    List<Goods> selectByIdListOnSaleNotDel(@Param("goodsIdList") List<Long> goodsIdList);


}