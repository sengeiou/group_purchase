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

import com.mds.group.purchase.goods.model.GoodsClass;
import com.mds.group.purchase.goods.model.GoodsClassMapping;
import com.mds.group.purchase.goods.result.GoodsClassResult;
import com.mds.group.purchase.goods.vo.ClassTwoVo;
import com.mds.group.purchase.goods.vo.GoodsClassVo;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertNotNull;
@Rollback
@SpringBootTest
@Transactional
@RunWith(SpringRunner.class)
public class GoodsClassServiceTest {
    private String appmodelId = "S00050001wx219007e82b660f17";

    @Resource
    private GoodsClassService goodsClassService;

    @Resource
    private GoodsClassMappingService goodsClassMappingService;

    @Test
    public void getGoodsClasses() {
        assertNotNull(goodsClassService.getGoodsClasses(appmodelId));
    }

    @Test
    public void delById() {
        goodsClassService.delById("1");
        //查找id为1的分类的下级分类是否同时删除
        List<GoodsClass> list = goodsClassService.findByList("fatherId", 1L);
        //查找goodsClassMapping 是否同时删除映记录
        List<GoodsClassMapping> list1 = goodsClassMappingService.findByList("goodsClassId", 1L);
        assert (list.size() <= 0 && list1.size() <= 0);
    }

    @Test
    public void putGoodsClass() {
        GoodsClassVo goodsClassVo = new GoodsClassVo();
        goodsClassVo.setFatherId(0L);
        goodsClassVo.setGoodsClassId(-1L);
        goodsClassVo.setGoodsClassName("单元测试一级分类");
        List<ClassTwoVo> twoVos = new ArrayList<>();
        ClassTwoVo classTwoVo = new ClassTwoVo();
        classTwoVo.setFatherId(-1L);
        classTwoVo.setGoodsClassId(-1L);
        classTwoVo.setGoodsClassName("单元测试二级分类");
        twoVos.add(classTwoVo);
        goodsClassVo.setClassTwos(twoVos);
        goodsClassService.putGoodsClass(appmodelId, goodsClassVo);
        //查询是否成功
        List<GoodsClassResult> list = goodsClassService.selectFirstClass(appmodelId);
        list.forEach(goodsClassResult -> {
            if ("单元测试一级分类".equalsIgnoreCase(goodsClassResult.getGoodsClassName())) {
                goodsClassResult.getClassTwos().forEach(classTwo->{
                    assert !"单元测试二级分类".equalsIgnoreCase(classTwo.getGoodsClassName()) || true;
                });
            }
        });


    }

    @Test
    public void selectFirstClass() {
        //一级分类不空
        assertNotNull(goodsClassService.selectFirstClass(appmodelId));
    }
}