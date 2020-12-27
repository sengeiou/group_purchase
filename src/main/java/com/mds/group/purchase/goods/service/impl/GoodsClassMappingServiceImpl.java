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

package com.mds.group.purchase.goods.service.impl;

import com.mds.group.purchase.core.AbstractService;
import com.mds.group.purchase.core.CodeMsg;
import com.mds.group.purchase.exception.GlobalException;
import com.mds.group.purchase.goods.dao.GoodsClassMappingMapper;
import com.mds.group.purchase.goods.model.GoodsClassMapping;
import com.mds.group.purchase.goods.service.GoodsClassMappingService;
import com.mds.group.purchase.goods.service.GoodsClassService;
import com.mds.group.purchase.goods.service.GoodsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


/**
 * The type Goods class mapping service.
 *
 * @author CodeGenerator
 * @date 2018 /11/27
 */
@Service
public class GoodsClassMappingServiceImpl extends AbstractService<GoodsClassMapping> implements GoodsClassMappingService {

    /**
     * The Goods service.
     */
    @Resource
    GoodsService goodsService;
    /**
     * The Goods class service.
     */
    @Resource
    GoodsClassService goodsClassService;
    @Resource
    private GoodsClassMappingMapper goodsClassMappingDao;


    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addGoodsClassMapping(Long[] goodsIds, Long[] goodsClassIds, String appmodelId) {
        //校验goodsId是否存在
        if (!goodsService.goodsIsExists(goodsIds, appmodelId)) {
            throw new GlobalException(CodeMsg.PARAMETER_ERROR.fillArgs("无效的商品ID"));
        }
        //校验goodsClasses是否存在
        if (!goodsClassService.goodsClassIsExists(goodsClassIds, appmodelId)) {
            throw new GlobalException(CodeMsg.PARAMETER_ERROR.fillArgs("商品分类不能为空"));
        }

        //1.遍历商品id数组
        for (Long goodsId : goodsIds) {
            //2.删除原有记录，插入新纪录
            goodsClassMappingDao.deleteByGoodsId(goodsId);
            for (Long goodsClassId : goodsClassIds) {
                GoodsClassMapping goodsClassMapping1 = new GoodsClassMapping();
                goodsClassMapping1.setAppmodelId(appmodelId);
                goodsClassMapping1.setGoodsClassId(goodsClassId);
                goodsClassMapping1.setGoodsId(goodsId);
                goodsClassMappingDao.insertSelective(goodsClassMapping1);
            }
        }
    }

    @Override
    public void delGoodsClassMapping(List<Long> goodsClassIds) {
        if (goodsClassIds.size() <= 0) {
            goodsClassIds.add(-1L);
        }
        goodsClassMappingDao.deleteBatchByGoodsClassId(goodsClassIds);
    }

    @Override
    public void delGoodsClassMappingByGoodsId(Long goodsId) {
        goodsClassMappingDao.deleteByGoodsId(goodsId);
    }

    @Override
    public List<GoodsClassMapping> findByGoodsIds(String goodsIds) {
        String[] strings = goodsIds.split(",");
        List<String> stringList = Arrays.asList(strings);
        List<Long> idList = stringList.parallelStream().map(Long::valueOf).collect(Collectors.toList());
        return goodsClassMappingDao.selectByGoodsIds(idList);
    }


    @Override
    public void deleteByGoodsIds(String goodsIds) {
        List<String> stringList = Arrays.asList(goodsIds.split(","));
        List<Long> goodsIdList = stringList.stream().map(Long::valueOf).collect(Collectors.toList());
        goodsClassMappingDao.deleteByGoodsIdList(goodsIdList);
    }


}
