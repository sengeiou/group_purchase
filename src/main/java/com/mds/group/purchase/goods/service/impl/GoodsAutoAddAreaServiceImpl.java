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
import com.mds.group.purchase.goods.dao.GoodsAutoAddAreaMapper;
import com.mds.group.purchase.goods.model.GoodsAutoAddArea;
import com.mds.group.purchase.goods.service.GoodsAutoAddAreaService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


/**
 * The type Goods auto add area service.
 *
 * @author shuke
 * @date 2019 /02/23
 */
@Service
public class GoodsAutoAddAreaServiceImpl extends AbstractService<GoodsAutoAddArea> implements GoodsAutoAddAreaService {

    @Resource
    private GoodsAutoAddAreaMapper tGoodsAutoAddAreaMapper;

    @Override
    public int updateByGoodsId(GoodsAutoAddArea goodsAutoAddArea) {
        return tGoodsAutoAddAreaMapper.updateByGoodsId(goodsAutoAddArea);
    }

    @Override
    public void deleteByGoodsId(String goodsIds) {
        String[] strings = goodsIds.split(",");
        List<String> goodsIdList = Arrays.asList(strings);
        List<Long> longs = goodsIdList.stream().map(Long::valueOf).collect(Collectors.toList());
        tGoodsAutoAddAreaMapper.deleteByGoodsId(longs);
    }

    @Override
    public List<GoodsAutoAddArea> findByAppmodelId(String appmodelId) {
        return tGoodsAutoAddAreaMapper.selectByAppmodelId(appmodelId);
    }

    @Override
    public void updateBatch(String goodsIds, boolean autoAdd, String appmodelId) {
        List<String> ids = Arrays.asList(goodsIds.split(","));
        List<Long> idList = ids.stream().map(Long::valueOf).collect(Collectors.toList());
        tGoodsAutoAddAreaMapper.deleteByGoodsId(idList);
        List<GoodsAutoAddArea> list = new ArrayList<>();
        idList.forEach(o -> {
            GoodsAutoAddArea goodsAutoAddArea = new GoodsAutoAddArea();
            goodsAutoAddArea.setAutoAdd(autoAdd);
            goodsAutoAddArea.setAppmodelId(appmodelId);
            goodsAutoAddArea.setGoodsId(o);
            list.add(goodsAutoAddArea);
        });
        tGoodsAutoAddAreaMapper.insertList(list);
    }

}
