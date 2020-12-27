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

package com.mds.group.purchase.logistics.service;

import com.mds.group.purchase.core.Service;
import com.mds.group.purchase.goods.vo.UpdateGoodsAreaVo;
import com.mds.group.purchase.logistics.model.GoodsAreaMapping;
import com.mds.group.purchase.logistics.result.GoodsAreaMappingLineResultV2;
import com.mds.group.purchase.logistics.result.GoodsAreaMappingResult;
import com.mds.group.purchase.logistics.vo.GoodsAreaSearchVo;
import com.mds.group.purchase.logistics.vo.GoodsAreaVo;

import java.util.List;


/**
 * The interface Goods area mapping service.
 *
 * @author pavawi
 */
public interface GoodsAreaMappingService extends Service<GoodsAreaMapping> {

    /**
     * 添加投放区域
     *
     * @param goodsAreaVo the goods area vo
     */
    void saveGoodsAreaAndFlushCache(GoodsAreaVo goodsAreaVo);

    /**
     * 查询投放区域
     *
     * @param goodsAreaSearchVo the goods area search vo
     * @return list list
     */
    List<GoodsAreaMappingResult> findBySearch(GoodsAreaSearchVo goodsAreaSearchVo);


    /**
     * 根据商品id删除投放区域
     *
     * @param goodsIds   the goods ids
     * @param appmodelId the appmodel id
     */
    void deletebyGoodsIds(String goodsIds, String appmodelId);

    /**
     * 根据社区id删除投放区域
     *
     * @param communityIds the community ids
     * @param appmodelId   the appmodel id
     */
    void deletebyCommunityIds(String communityIds, String appmodelId);

    /**
     * 更新投放区域
     *
     * @param goodsAreaVo the goods area vo
     */
    void updateGoodsAreaMapping(GoodsAreaVo goodsAreaVo);

    /**
     * 批量更新投放区域
     *
     * @param goodsAreaVo the goods area vo
     */
    void updateGoodsAreaMappingBatch(UpdateGoodsAreaVo goodsAreaVo);

    /**
     * 根据商品id获取投放区域
     *
     * @param goodsId    the goods id
     * @param appmodelId the appmodel id
     * @return list list
     */
    List<GoodsAreaMappingLineResultV2> findByGoodsId(Long goodsId, String appmodelId);

    /**
     * 获取可以投放的区域
     *
     * @param appmodelId the appmodel id
     * @return list list
     */
    List<GoodsAreaMappingLineResultV2> canPick(String appmodelId);

    /**
     * 自动添加投放区域
     *
     * @param goodsId     the goods id
     * @param communityId the community id
     * @param appmodelId  the appmodel id
     */
    void autoAdd(List<Long> goodsId, List<Long> communityId, String appmodelId);
}
