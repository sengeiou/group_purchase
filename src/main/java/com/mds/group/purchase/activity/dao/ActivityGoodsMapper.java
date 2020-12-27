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

package com.mds.group.purchase.activity.dao;

import com.mds.group.purchase.activity.model.ActivityGoods;
import com.mds.group.purchase.core.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 活动商品数据操作接口
 *
 * @author shuke on 2018-12-4
 */
public interface ActivityGoodsMapper extends Mapper<ActivityGoods> {

    /**
     * 根据活动id删除属于该活动的活动商品
     *
     * @param activityId 活动id
     */
    void deleteByActivityId(Long activityId);

    /**
     * 根据活动商品id列表批量删除活动商品
     *
     * @param delGoodsIdList 需要删除的活动商品id
     */
    void deleteBatch(List<Long> delGoodsIdList);

    /**
     * 减少活动商品的库存
     *
     * @param activityGoodsId 活动商品id
     * @param goodsNum        商品数量
     * @return 操作成功后影响的行数 int
     */
    int reduceStockAndaddVolume(@Param("activityGoodsId") Long activityGoodsId, @Param("goodsNum") Integer goodsNum);

    /**
     * 根据活动id修改活动商品的预热状态
     *
     * @param activityId           活动id
     * @param activityGoodsPreheat 活动商品预热状态
     */
    void updatePreheatByActId(@Param("activityId") Long activityId,
                              @Param("activityGoodsPreheat") Integer activityGoodsPreheat);

    /**
     * 根据活动id查询首页活动商品
     *
     * @param actIds 活动id
     * @return 活动商品列表 list
     */
    List<ActivityGoods> selectIndexActGoodsByActIds(@Param("actIds") List<Long> actIds);

    /**
     * 根据活动id列表查询活动商品
     *
     * @param actIds 活动id
     * @return 活动商品列表 list
     */
    List<ActivityGoods> selectActGoodsByActIds(@Param("actIds") List<Long> actIds);

    /**
     * 根据活动id查询活动商品
     *
     * @param actId 活动id
     * @return 活动商品列表 list
     */
    List<ActivityGoods> selectActGoodsByActId(Long actId);

    /**
     * 根据商品id查询预热和开始活动的活动商品
     *
     * @param goodsId      商品id
     * @param activityType 活动类型 （1、秒杀活动 2、拼团活动）
     * @return 活动商品列表 list
     */
    List<ActivityGoods> selectByGoodsIdPreheatStart(@Param("goodsId") List<Long> goodsId,
                                                    @Param("activityType") Integer activityType);

    /**
     * 根据活动id获取活动商品数量
     *
     * @param activityId 活动id
     * @return 活动商品数量 integer
     */
    Integer countByActId(Long activityId);

    /**
     * 根据id删除
     *
     * @param activityId 活动id
     */
    void delById(Long activityId);

    /**
     * 根据普通商品id查询活动商品
     *
     * @param goodsId 普通商品id
     * @return 活动商品列表 list
     */
    List<ActivityGoods> selectActGoodsByGoodsId(@Param("goodsId") Long goodsId);

    /**
     * 查询存在未开始或已开始的活动中的活动中的指定商品
     *
     * @param goodsIdList 商品id列表
     * @param appmodelId  小程序模板id
     * @return 活动商品列表 list
     */
    List<ActivityGoods> selectExistsNoStartActivity(@Param("goodsIdList") List<Long> goodsIdList,
                                                    @Param("appmodelId") String appmodelId);

    /**
     * 根据活动商品id查询单个 活动商品
     *
     * @param actGoodsId 活动商品id
     * @return ActivityGoods activity goods
     */
    ActivityGoods selectByActGoodsId(@Param("actGoodsId") Long actGoodsId);

    /**
     * 根据活动商品id查询多个活动商品
     *
     * @param actGoodsIds 活动商品id
     * @return ActivityGoods list
     */
    List<ActivityGoods> selectByActGoodsIds(@Param("actGoodsIds") List<Long> actGoodsIds);

    /**
     * 批量下架活动商品
     *
     * @param actGoodsIds 活动商品id列表
     * @param soldOutFlag 下架标志 下架状态（0：未下架，1：下架）
     */
    void soldOutActGoods(@Param("actGoodsIds") List<Long> actGoodsIds, @Param("soldOutFlag") Integer soldOutFlag);

    /**
     * 根据商品id批量查询
     *
     * @param goodsIdList 商品id列表
     * @return List<ActivityGoods> list
     */
    List<ActivityGoods> selectByGoodsIds(@Param("goodsIdList") List<Long> goodsIdList);

    /**
     * 根据活动商品id批量修改参加接龙状态
     *
     * @param actGoodsIds     the act goods ids
     * @param isJoinSolitaire the is join solitaire
     */
    void updateJoinSolitaireByIds(@Param("actGoodsIds") String actGoodsIds,
                                  @Param("isJoinSolitaire") boolean isJoinSolitaire);
}