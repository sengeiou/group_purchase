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

import com.mds.group.purchase.constant.GoodsConstant;
import com.mds.group.purchase.goods.model.Goods;
import com.mds.group.purchase.goods.vo.SaveGoodsVo;
import com.mds.group.purchase.goods.vo.UpdateGoodsVo;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

import static org.junit.Assert.assertNotNull;
@Rollback
@SpringBootTest
@Transactional
@RunWith(SpringRunner.class)
public class GoodsServiceTest {

    @Resource
    private GoodsService goodsService;

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    private UpdateGoodsVo updateGoodsVo = new UpdateGoodsVo();

    private SaveGoodsVo saveGoodsVo = new SaveGoodsVo();

    private String appmodelId = "S00050001wx219007e82b660f17";

    public UpdateGoodsVo getUpdateGoodsVo() {
        updateGoodsVo.setStock(2);
        updateGoodsVo.setSalesVolume(2);
        updateGoodsVo.setGoodsId(10010L);
        updateGoodsVo.setAppmodelId("S00050001wx219007e82b660f17");
        updateGoodsVo.setCommissionType(2);
        updateGoodsVo.setExpirationDate("2018-12-25");
        Long[] longs = {1L, 3L, 4L, 5L};
        updateGoodsVo.setGoodsClassIds(longs);
        updateGoodsVo.setGoodsDelFlag(false);
        updateGoodsVo.setGoodsDesc("无");
        updateGoodsVo.setGoodsName("22");
        updateGoodsVo.setGoodsImg("url");
        updateGoodsVo.setGoodsProperty("属性");
        updateGoodsVo.setGoodsStatus(GoodsConstant.ON_SALE);
        updateGoodsVo.setGoodsTitle("标题");
        return updateGoodsVo;
    }

    public SaveGoodsVo getSaveGoodsVo() {
        saveGoodsVo.setStock(2);
        saveGoodsVo.setAppmodelId("S00050001wx219007e82b660f17");
        saveGoodsVo.setCommissionType(2);
        saveGoodsVo.setExpirationDate("2018-12-25");
        Long[] longs = {1L, 3L, 4L, 5L};
        saveGoodsVo.setGoodsClassIds(longs);
        saveGoodsVo.setGoodsDesc("无");
        saveGoodsVo.setGoodsName("22");
        saveGoodsVo.setGoodsImg("url");
        saveGoodsVo.setGoodsProperty("属性");
        saveGoodsVo.setGoodsStatus(GoodsConstant.ON_SALE);
        saveGoodsVo.setGoodsTitle("标题");
        return saveGoodsVo;
    }

    @Test
    public void add() {
        goodsService.add(getSaveGoodsVo());
    }

    @Test
    public void updateGoods() {
        goodsService.updateGoods(getUpdateGoodsVo());
    }

    @Test
    public void soldOut() {
        goodsService.soldOut("10010L", "S00050001wx219007e82b660f17");
        Goods goods = goodsService.findById(10010L);
        assert (goods.getGoodsStatus() == 1);
    }


    @Test
    public void delGoods() {
        goodsService.delGoods("10010", appmodelId);
        assert (goodsService.findById(10010L).getGoodsDelFlag());
    }

    @Test
    public void reduceStock() {
		/*assertFalse(goodsService.reduceStockAndaddVolume(10010L, 60));
		assert (goodsService.reduceStockAndaddVolume(10010L, 2));*/
    }

    @Test
    public void getGoodsCanAddToAct() {
        assertNotNull(goodsService.getGoodsCanAddToAct(appmodelId));
    }

    @Test
    public void findClassAndGoods() {
        assertNotNull(goodsService.findClassAndGoods(appmodelId));
    }

    @Test
    public void selectByClassId() {
        assertNotNull(goodsService.selectByClassId(1L));
    }
}