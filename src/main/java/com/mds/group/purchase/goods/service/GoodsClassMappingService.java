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
import com.mds.group.purchase.goods.model.GoodsClassMapping;

import java.util.List;


/**
 * 商品分类映射业务接口类
 *
 * @author shuke
 * @date 2018 /11/27
 */
public interface GoodsClassMappingService extends Service<GoodsClassMapping> {
    /**
     * 通用的添加商品分类映射的方法
     *
     * @param goodsIds      传入多个商品id可批量设置映射
     * @param goodsClassIds 商品分类id
     * @param appmodelId    模板id
     */
    void addGoodsClassMapping(Long[] goodsIds, Long[] goodsClassIds, String appmodelId);

    /**
     * 根据商品分类映射批量删除
     *
     * @param goodsClassIdList 商品分类id集合
     */
    void delGoodsClassMapping(List<Long> goodsClassIdList);

    /**
     * 根据商品id删除商品分类映射
     *
     * @param goodsId 商品id
     */
    void delGoodsClassMappingByGoodsId(Long goodsId);

    /**
     * 根据商品id查询
     *
     * @param goodsIds 商品id，多个id用逗号分隔
     * @return 商品分类映射集合 list
     */
    List<GoodsClassMapping> findByGoodsIds(String goodsIds);


    /**
     * 根据商品id删除商品分类映射
     *
     * @param goodsIds 商品id，多个id用逗号分隔
     */
    void deleteByGoodsIds(String goodsIds);
}
