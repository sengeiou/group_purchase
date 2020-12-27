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

import com.mds.group.purchase.goods.model.GoodsClassMapping;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
@Rollback
@SpringBootTest
@Transactional
@RunWith(SpringRunner.class)
public class GoodsClassMappingServiceTest {

    @Autowired
    private GoodsClassMappingService goodsClassMappingService;
    private String appmodelId = "S00050001wx219007e82b660f17";

    @Test
    public void addGoodsClassMapping() {
        Long[] goodsId = {10010L};
        Long[] classId = {1L};
        goodsClassMappingService.addGoodsClassMapping(goodsId,classId,appmodelId);
        List<GoodsClassMapping> list = goodsClassMappingService.findByList("goodsId", 10010L);
        assert list.size() > 0;
    }

    @Test
    public void delGoodsClassMapping() {
        List<Long> list = new ArrayList<>();
        list.add(1L);
        goodsClassMappingService.delGoodsClassMapping(list);
        List<GoodsClassMapping> list1 = goodsClassMappingService.findByList("goodsClassId", 1L);
        assert list1.size() <= 0;
    }

    @Test
    public void delGoodsClassMappingByGoodsId() {
        goodsClassMappingService.delGoodsClassMappingByGoodsId(10010L);
        List<GoodsClassMapping> list = goodsClassMappingService.findByList("goodsId", 10010L);
        assert list.size() <= 0;
    }
}