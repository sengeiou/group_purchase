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
import com.mds.group.purchase.goods.model.GoodsAutoAddArea;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 商品自动添加 映射类
 *
 * @author shuke
 */
public interface GoodsAutoAddAreaMapper extends Mapper<GoodsAutoAddArea> {
    /**
     * 删除by商品id
     *
     * @param longs 商品id，多个id之间用逗号分隔
     */
    void deleteByGoodsId(@Param("goodsIds") List<Long> longs);

    /**
     * 根据appmodelId查询
     * 且auto_add 为true
     *
     * @param appmodelId 小程序模板id
     * @return GoodsAutoAddArea list
     */
    List<GoodsAutoAddArea> selectByAppmodelId(@Param("appmodelId") String appmodelId);

    /**
     * 批量设置自动更新投放区域
     *
     * @param idList  商品id
     * @param autoAdd 自动投放
     */
    void updateBatch(@Param("idList") List<Long> idList, @Param("autoAdd") boolean autoAdd);

    /**
     * 更新商品的自动投放信息
     *
     * @param goodsAutoAddArea 商品自动投放设置信息
     * @return 更新个数 int
     */
    int updateByGoodsId(GoodsAutoAddArea goodsAutoAddArea);
}