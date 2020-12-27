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
import com.mds.group.purchase.goods.dao.GoodsDetailMapper;
import com.mds.group.purchase.goods.model.GoodsDetail;
import com.mds.group.purchase.goods.service.GoodsDetailService;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Condition;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


/**
 * The type Goods detail service.
 *
 * @author CodeGenerator
 * @date 2018 /11/27
 */
@Service
public class GoodsDetailServiceImpl extends AbstractService<GoodsDetail> implements GoodsDetailService {

    @Resource
    private GoodsDetailMapper goodsDetailDao;

    @Override
    public void addDetail(GoodsDetail goodsDetail) {
        goodsDetailDao.insertGoodsDetail(goodsDetail);
    }

    @Override
    public void updateGoodsDetail(GoodsDetail goodsDetail) {
        goodsDetailDao.updateSelective(goodsDetail);
    }

    @Override
    public GoodsDetail getGoodsDetailByGoodsId(Long goodsId) {
        return goodsDetailDao.selectByGoodsId(goodsId);
    }

    @Override
    public List<GoodsDetail> findByGoodsIds(String goodsIds) {
        if ("".equalsIgnoreCase(goodsIds)) {
            return new ArrayList<>();
        }
        Condition condition = new Condition(GoodsDetail.class);
        List<Long> goodsList = Arrays.stream(goodsIds.split(",")).map(Long::valueOf).collect(Collectors.toList());
        condition.createCriteria().andIn("goodsId", goodsList);
        return goodsDetailDao.selectByCondition(condition);
    }

    @Override
    public void updateList(List<GoodsDetail> goodsDetailList) {
        goodsDetailList.forEach(obj -> goodsDetailDao.updateSelective(obj));
    }

}
