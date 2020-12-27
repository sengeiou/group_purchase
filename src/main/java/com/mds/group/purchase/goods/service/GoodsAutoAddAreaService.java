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
import com.mds.group.purchase.goods.model.GoodsAutoAddArea;

import java.util.List;


/**
 * 商品自动投放业务类
 *
 * @author shuke
 * @date 2019 /02/23
 */
public interface GoodsAutoAddAreaService extends Service<GoodsAutoAddArea> {

    /**
     * 根据商品id修改
     *
     * @param goodsAutoAddArea 商品自动投放实体
     * @return 修改记录数 int
     */
    int updateByGoodsId(GoodsAutoAddArea goodsAutoAddArea);

    /**
     * 根据商品id删除
     *
     * @param goodsIds 商品id，多个id用逗号分隔
     */
    void deleteByGoodsId(String goodsIds);

    /**
     * 根据appmodelId查询
     *
     * @param appmodelId 模板id
     * @return List<GoodsAutoAddArea>  list
     */
    List<GoodsAutoAddArea> findByAppmodelId(String appmodelId);


    /**
     * 批量设置是否自动投放状态
     *
     * @param goodsIds   商品id，多个id用逗号分隔
     * @param autoAdd    商品是否自动加入新建小区（0：不加人 1：加入）
     * @param appmodelId 模板id
     */
    void updateBatch(String goodsIds, boolean autoAdd, String appmodelId);

}
