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

package com.mds.group.purchase.goods.service;

import com.github.pagehelper.PageInfo;
import com.mds.group.purchase.core.Service;
import com.mds.group.purchase.goods.model.Goods;
import com.mds.group.purchase.goods.result.ClassAndGoodsResult;
import com.mds.group.purchase.goods.result.GoodsFuzzyResult;
import com.mds.group.purchase.goods.result.GoodsResult;
import com.mds.group.purchase.goods.result.GoodsResult4AddAct;
import com.mds.group.purchase.goods.vo.*;

import java.util.List;
import java.util.Map;


/**
 * 商品业务接口类
 *
 * @author shuke
 * @date 2018 /11/27
 */
public interface GoodsService extends Service<Goods> {

    /**
     * 创建一个商品
     *
     * @param goodsVo 保存商品参数类
     */
    void add(SaveGoodsVo goodsVo);

    /**
     * 创建一个商品
     *
     * @param goodsVo 保存商品参数类
     * @since v1.2
     */
    void addV2(SaveGoodsVo goodsVo);

    /***
     * v1.1.9版本添加商品的接口
     * @param goodsVo 保存商品参数类
     */
    void addV119(SaveGoodsV119Vo goodsVo);

    /**
     * 添加商品
     * 此版本新增了商品主图视频功能
     *
     * @param goodsVo 保存商品参数类
     * @since v1.2
     */
    void addV12(SaveGoodsV12Vo goodsVo);

    /**
     * 修改一件商品
     *
     * @param goodsVo 更新商品参数类
     */
    @Deprecated
    void updateGoods(UpdateGoodsVo goodsVo);

    /**
     * 修改一件商品
     * v1.1.9
     *
     * @param goodsVo 更新商品参数类
     */
    void updateGoodsV119(UpdateGoodsV119Vo goodsVo);

    /**
     * 修改一件商品
     *
     * @param goodsVo 更新商品参数类
     * @since v1.2
     */
    void updateGoodsV12(UpdateGoodsV12Vo goodsVo);

    /**
     * 商品是否存在
     *
     * @param goodsId    商品id
     * @param appmodelId 模板id
     * @return true 商品存在，false 商品不存在
     */
    boolean isExists(Long goodsId, String appmodelId);

    /**
     * 下架商品
     *
     * @param goodsIds   商品id，多个id之间用逗号分隔
     * @param appmodelId 模板id
     */
    void soldOut(String goodsIds, String appmodelId);


    /**
     * 批量删除商品
     *
     * @param goodsIds   商品id，多个id之间用逗号分隔
     * @param appmodelId 模板id
     */
    void delGoods(String goodsIds, String appmodelId);

    /**
     * 根据商品名称模糊查询
     *
     * @param appmodelId 模板id
     * @param goodsName  商品名称
     * @return 商品结果集合 goods like name
     */
    @Deprecated
    List<GoodsResult> getGoodsLikeName(String appmodelId, String goodsName);

    /**
     * 根据商品名称和状态模糊查询
     *
     * @param appmodelId  模板id
     * @param goodsName   商品名称
     * @param goodsStatus 商品状态
     * @return List<GoodsResult>  goods like name and status
     */
    @Deprecated
    List<GoodsResult> getGoodsLikeNameAndStatus(String appmodelId, String goodsName, Integer goodsStatus);

    /**
     * 批量判断商品是否存在
     *
     * @param goodsIds   商品id
     * @param appmodelId 模板id
     * @return true 商品存在，false 商品不存在
     */
    boolean goodsIsExists(Long[] goodsIds, String appmodelId);

    /**
     * 根据条件获取商品列表
     *
     * @param appmodelId 小程序模板id
     * @param getGoodsVo 获取商品列表的参数对象
     * @return goodsResult对象 goods
     */
    List<GoodsFuzzyResult> getGoods(String appmodelId, GetGoodsVo getGoodsVo);

    /**
     * 根据条件获取商品列表
     * v1.2
     *
     * @param appmodelId 小程序模板id
     * @param getGoodsVo 获取商品列表的参数对象
     * @return goodsResult对象 goods v 2
     */
    PageInfo<GoodsFuzzyResult> getGoodsV2(String appmodelId, GetGoodsVo getGoodsVo);

    /**
     * 批量操作
     *
     * @param appmodelId     模板id
     * @param branchUpdateVo 批量更新参数类
     */
    void branchUpdate(String appmodelId, BranchUpdateVo branchUpdateVo);

    /**
     * 查询单个商品
     *
     * @param appmodelId 模板id
     * @param goodsId    商品id
     * @return GoodsFuzzyResult goods by id
     */
    GoodsFuzzyResult getGoodsById(String appmodelId, Long goodsId);


    /**
     * 获取可以添加到活动的商品列表
     *
     * @param appmodelId 模板id
     * @return List<GoodsResult4AddAct>  goods can add to act
     */
    List<GoodsResult4AddAct> getGoodsCanAddToAct(String appmodelId);

    /**
     * 获取可以添加到活动的商品列表
     *
     * @param appmodelId the appmodel id
     * @return list list
     * @since v1.2.3 商品只能同时添加到一场活动中
     */
    List<GoodsResult4AddAct> goodsCanAddToAct(String appmodelId);

    /**
     * 获取商品分类和商品结果
     *
     * @param appmodelId 模板id
     * @return List<ClassAndGoodsResult>  list
     */
    List<ClassAndGoodsResult> findClassAndGoods(String appmodelId);

    /**
     * 根据分类查询商品
     *
     * @param goodsClassId 商品分类id
     * @return 商品集合 list
     */
    List<Goods> selectByClassId(Long goodsClassId);

    /**
     * 更新供应商商品状态
     * * @param map
     * providerId
     * providerIds
     * optionType 1-更新供应商商品为下架状态 2-更新供应商商品逻辑删除
     *
     * @param map the map
     */
    void updateProviderGoods(Map<String, Object> map);

    /**
     * 查询供应商上架的商品
     *
     * @param providerIds 供应商id集合
     * @param appmodelId  模板id
     * @return 商品名称集合 list
     */
    List<String> findByProviderPutawayOfGoods(List<String> providerIds, String appmodelId);

    /**
     * 查询未投放的商品
     *
     * @param appmodelId 模板id
     * @return 商品集合 list
     */
    List<Goods> findNoMappinGoods(String appmodelId);

    /**
     * id查询未删除商品
     *
     * @param goodsIds 商品id集合
     * @return 商品集合 list
     */
    List<Goods> findByIdListNotDel(List<Long> goodsIds);
}
