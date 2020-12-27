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

package com.mds.group.purchase.activity.service;

import com.mds.group.purchase.activity.model.ActivityGoods;
import com.mds.group.purchase.activity.result.ActGoodsInfoResult;
import com.mds.group.purchase.activity.result.IndexSeckillResult;
import com.mds.group.purchase.core.Service;
import com.mds.group.purchase.goods.model.GoodsClass;

import java.util.List;


/**
 * 活动商品业务接口类
 *
 * @author CodeGenerator
 * @date 2018 /12/03
 */
public interface ActivityGoodsService extends Service<ActivityGoods> {

    /**
     * 批量删除活动商品
     *
     * @param delGoodsIdList 需要添加的活动商品id列表
     */
    void deleteBatch(List<Long> delGoodsIdList);

    /**
     * 批量更新活动商品
     *
     * @param goodsList 需要修改的活动商品列表
     */
    void updateBatch(List<ActivityGoods> goodsList);

    /**
     * 减少活动商品的库存,同时减少商品库存
     *
     * @param activityGoodsId 活动商品id
     * @param goodsNum        商品数量
     */
    @Deprecated
    void reduceStockAndaddVolume(Long activityGoodsId, Integer goodsNum);

    /**
     * 根据活动id获取活动商品
     *
     * @param activityId 活动id
     * @return 活动商品列表 list
     */
    List<ActivityGoods> findByActId(Long activityId);

    /**
     * 根据活动id获取活动商品
     *
     * @param activityIds 活动id
     * @return 活动商品列表 list
     */
    List<ActivityGoods> findByActIds(List<Long> activityIds);

    /**
     * 根据活动id修改活动商品的预热状态
     *
     * @param activityId           活动id
     * @param activityGoodsPreheat 活动商品的状态（0:不能购买，不能展示 1:预热状态 2:活动开始可购买 3:已售罄）
     */
    void updatePreheatStatus(Long activityId, Integer activityGoodsPreheat);

    /**
     * 根据活动商品id修改活动商品的状态
     *
     * @param actGoodsId           活动商品id
     * @param activityGoodsPreheat 活动商品的状态（0:不能购买，不能展示 1:预热状态 2:活动开始可购买 3:已售罄）
     */
    void updateActGoodsPreheatStatusById(Long actGoodsId, Integer activityGoodsPreheat);

    /**
     * 获取活动商品详情信息
     *
     * @param actGoodsId 活动商品id
     * @return 活动商品详情结果对象 act goods info
     */
    @Deprecated
    ActGoodsInfoResult getActGoodsInfo(Long actGoodsId);

    /**
     * 获取小程序端首页展示的拼团商品
     * 活动按后台最新新增活动显示（首页拼团只显示一场状态（进行中/未开始））
     *
     * @param wxuserId   用户id
     * @param appmodelId 小程序模板id
     * @return 活动商品详情结果对象列表 index group act goods
     */
    List<ActGoodsInfoResult> getIndexGroupActGoods(Long wxuserId,String appmodelId);

    /**
     * 获取不同商品分类下的拼团商品
     *
     * @param appmodelId   小程序模板id
     * @param goodsClassId 商品分类id 传入-1 查询所有分类的商品
     * @param wxuserId     用户id
     * @return 活动商品详情结果对象列表 act goods by class
     */
    List<ActGoodsInfoResult> getActGoodsByClass(String appmodelId,Long goodsClassId,Long wxuserId);

    /**
     * 获取所有商品分类下的拼团商品
     *
     * @param appmodelId 小程序模板id
     * @param wxuserId   用户id
     * @return 商品分类 goods class
     */
    List<GoodsClass> getGoodsClass(String appmodelId,Long wxuserId);

    /**
     * 根据活动id获取活动商品数量
     *
     * @param activityId 活动id
     * @return 商品数量 integer
     */
    Integer countByActId(Long activityId);

    /**
     * 根据活动id获取当前活动的活动商品
     *
     * @param activityId 活动id
     * @return 活动商品详情结果对象列表 act goods by act id 4 pc
     */
    List<ActGoodsInfoResult> getActGoodsByActId4Pc(Long activityId);

    /**
     * 根据活动id获取当前活动的活动商品
     *
     * @param activityIds 活动id
     * @return 活动商品详情结果对象列表 act goods by act ids
     */
    List<ActGoodsInfoResult> getActGoodsByActIds(List<Long> activityIds);

    /**
     * 获取小程序端首页展示的秒杀商品
     *
     * @param appmodelId 小程序模板id
     * @param wxuserId   用户id
     * @return 首页秒杀商品结果对象列表 index seckill goods
     */
    List<IndexSeckillResult> getIndexSeckillGoods(String appmodelId,Long wxuserId);

    /**
     * 获取小程序端二级页面展示的秒杀商品
     *
     * @param appmodelId 小程序模板id
     * @param wxuserId   用户id
     * @return 首页秒杀商品结果对象列表 i second seckill goods
     */
    List<IndexSeckillResult> getISecondSeckillGoods(String appmodelId,Long wxuserId);

    /**
     * 根据活动id删除活动商品
     *
     * @param activityId 活动id
     * @param appmodelId 小程序模板id
     */
    void deleteByActId(Long activityId,String appmodelId);

    /**
     * 根据活动商品id查询活动商品详情
     *
     * @param actGoodsId 活动商品id
     * @param appmodelId 小程序模板id
     * @return 活动商品结果对象 act goods by id
     */
    ActGoodsInfoResult getActGoodsById(Long actGoodsId,String appmodelId);

    /**
     * 新增活动商品
     *
     * @param activityGoodsListAdd 要新增的活动商品列表
     * @return 新增的记录条数 int
     */
    int saveActGoods(List<ActivityGoods> activityGoodsListAdd);

    /**
     * 根据普通商品id查询活动商品
     * 活动商品状态为未删除
     *
     * @param goodsId 普通商品id
     * @return 活动商品对象列表 act goods by goods id
     */
    List<ActivityGoods> getActGoodsByGoodsId(Long goodsId);

    /**
     * 删除存在未开始或已开始的活动中的活动中的指定商品
     *
     * @param goodsIdList 普通商品id数组
     * @param appmodelId  小程序模板id
     */
    void removeExistsNoStartActivityGoods(List<Long> goodsIdList, String appmodelId);

    /**
     * 根据商品id批量上架活动商品
     *
     * @param goodsIdList 商品id列表
     * @param appmodelId  模板id
     */
    void putAwayActivityGoods(List<Long> goodsIdList,String appmodelId);

    /**
     * 获取用户小区是否有该商品
     *
     * @param appmodelId 小程序模板id
     * @param wxuserId   用户id
     * @param actGoodsId 活动商品id
     * @return 小区是否有该商品 boolean
     */
    Boolean ifExistActGoods(String appmodelId, Long wxuserId, Long actGoodsId);

    /**
     * 根据活动id获取当前活动的活动商品
     *
     * @param actGoodsId 活动商品id
     * @return 活动商品对象列表 act goods by act id list
     */
    List<ActivityGoods> getActGoodsByActIdList(List<Long> actGoodsId);

    /**
     * 更新活动商品的排序值
     *
     * @param actGoodsList 活动商品
     * @since v1.2
     */
    void updateActGoodsSortList(List<ActivityGoods> actGoodsList);

    /**
     * 更新活动商品是否参加接龙活动
     *
     * @param actGoodsList 活动商品
     * @since v1.2.3
     */
    void updateActGoodsJoinSolitaire(List<ActivityGoods> actGoodsList);

    /**
     * 根据活动商品id查询多个活动商品
     *
     * @param actGoodsIds the act goods ids
     * @return list list
     */
    List<ActivityGoods> selectByActGoodsIds(List<Long> actGoodsIds);
}
