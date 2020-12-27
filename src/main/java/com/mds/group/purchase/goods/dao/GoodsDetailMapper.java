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
import com.mds.group.purchase.goods.model.GoodsDetail;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

/**
 * 商品详情Mapper
 *
 * @author shuke
 */
@Repository
public interface GoodsDetailMapper extends Mapper<GoodsDetail> {

    /**
     * 插入商品详情
     *
     * @param record 商品详情实体
     * @return 详情id int
     */
    int insertGoodsDetail(GoodsDetail record);

    /**
     * 更新商品详情
     *
     * @param goodsDetail 商品详情实体
     */
    void updateSelective(GoodsDetail goodsDetail);

    /**
     * 根据商品id查询商品详情
     *
     * @param goodsId 商品id
     * @return 商品详情实体 goods detail
     */
    GoodsDetail selectByGoodsId(@Param("goodsId") Long goodsId);


}