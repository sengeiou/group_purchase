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

import com.mds.group.purchase.core.Service;
import com.mds.group.purchase.goods.model.GoodsClass;
import com.mds.group.purchase.goods.result.GoodsClassResult;
import com.mds.group.purchase.goods.vo.GoodsClassVo;

import java.util.List;


/**
 * 商品分类业务接口类
 *
 * @author shuke
 * @date 2018 /11/27
 */
public interface GoodsClassService extends Service<GoodsClass> {

    /**
     * 得到所有分类
     *
     * @param appmodelId 模板id
     * @return 商品分类结果集合 goods classes
     */
    List<GoodsClassResult> getGoodsClasses(String appmodelId);

    /**
     * 判断商品分类是否否存在
     *
     * @param goodsClassId 商品分类id
     * @param appmodelId   模板id
     * @return 分类是否存在 boolean
     */
    boolean isExists(Long goodsClassId, String appmodelId);

    /**
     * 批量判断商品分类是否否存在
     *
     * @param goodsClassIds 商品分类id
     * @param appmodelId    模板id
     * @return 分类是否存在 boolean
     */
    boolean goodsClassIsExists(Long[] goodsClassIds, String appmodelId);

    /**
     * 根据id删除分类
     * 同时删除分类下的子分类
     * 删除所有属于该分类的商品的goodsclassMapping
     *
     * @param goodsClassId 商品分类id
     */
    void delById(String goodsClassId);

    /**
     * 增加或修改一个一级分类及所属二级分类
     *
     * @param appmodelId 模板id
     * @param classVo    商品分类参数类
     */
    void putGoodsClass(String appmodelId, GoodsClassVo classVo);

    /**
     * 查询一级分类
     * 已弃用
     *
     * @param appmodelId 模板id
     * @return List<GoodsClassResult>  list
     */
    @Deprecated
    List<GoodsClassResult> selectFirstClass(String appmodelId);

    /**
     * 得到该分类和其下级分类
     *
     * @param goodsClassId 商品分类id
     * @return 商品分类集合 goods classes and under class
     */
    List<GoodsClass> getGoodsClassesAndUnderClass(Long goodsClassId);

    /**
     * 商品分类排序
     *
     * @param handleType   排序操作类型 1 置顶 2 上移一位 3 下移一位  4置底
     * @param goodsClassId 商品分类id
     * @param appmodelId   模板id
     * @return 排序操作类型 1 置顶 2 上移一位 3 下移一位  4置底
     */
    int sort(Integer handleType, Long goodsClassId, String appmodelId);

    /**
     * 实现拖拽排序的功能
     *
     * @param classIds   分类id
     * @param appmodelId 模板id
     * @since v1.2.2
     */
    void sortNew(String classIds, String appmodelId);
}
