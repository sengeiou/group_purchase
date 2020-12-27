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
import com.mds.group.purchase.goods.model.GoodsDetail;

import java.util.List;


/**
 * 商品详情业务接口类
 *
 * @author shuke
 * @date 2018 /11/27
 */
public interface GoodsDetailService extends Service<GoodsDetail> {

    /**
     * 增加商品详情
     *
     * @param goodsDetail 商品详情实体
     */
    void addDetail(GoodsDetail goodsDetail);

    /**
     * 修改商品详情
     * 已弃用
     *
     * @param goodsDetail 商品详情实体
     */
    @Deprecated
    void updateGoodsDetail(GoodsDetail goodsDetail);

    /**
     * 根据商品id获取商品详情
     *
     * @param goodsId 商品id
     * @return 商品详情 goods detail by goods id
     */
    GoodsDetail getGoodsDetailByGoodsId(Long goodsId);

    /**
     * 根据商品id列表批量查询
     *
     * @param goodsIds 商品id，多个id之间用逗号分隔
     * @return 商品详情集合 list
     */
    List<GoodsDetail> findByGoodsIds(String goodsIds);

    /**
     * 批量更新
     *
     * @param goodsDetailList 商品详情集合
     */
    void updateList(List<GoodsDetail> goodsDetailList);
}
