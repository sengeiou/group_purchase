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

package com.mds.group.purchase.activity.service;

import com.mds.group.purchase.activity.result.ActGoodsInfoResult;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertNotNull;

@Rollback
@SpringBootTest
@Transactional
@RunWith(SpringRunner.class)
public class ActivityGoodsServiceTest {

    @Autowired
    private ActivityGoodsService activityGoodsService;

    @Test
    public void deleteBatch() {
        List<Long> goodsIds = new ArrayList<>();
        goodsIds.add(10000L);
        activityGoodsService.deleteBatch(goodsIds);
    }

    @Test
    public void updateBatch() {

    }

	/*@OrderTest
	public void reduceStockAndaddVolume() {
		assertTrue(activityGoodsService.reduceStockAndaddVolume(10000L, 1));
	}

	*//**
     * 不能超过限购数
     *//*
	@OrderTest
	public void reduceStock2() {
		assertFalse(activityGoodsService.reduceStockAndaddVolume(10000L, 2));
	}

	*//**
     * 不能超过库存
     *//*
	@OrderTest
	public void reduceStock3() {
		assertFalse(activityGoodsService.reduceStockAndaddVolume(10001L, 3));
	}
*/
    @Test
    public void findByActId() {
        assertNotNull(activityGoodsService.findByActId(10000L));
    }

    @Test
    public void updatePreheatStatus() {
        activityGoodsService.updatePreheatStatus(10086L, 1);
        List<ActGoodsInfoResult> activityGoodsList = activityGoodsService.getActGoodsByActId4Pc(10086L);
        activityGoodsList.forEach(actGoodsInfoResult -> {
            assert (actGoodsInfoResult.getPreheatStatus() == 1);
        });
    }

    @Test
    public void getIndexActGoods() {
        List<ActGoodsInfoResult> activityGoodsList = activityGoodsService
                .getIndexGroupActGoods(1544169688415057L, "S00050001wx219007e82b660f17");
        assertNotNull(activityGoodsList);
    }

    /**
     * 查询商品分类下的活动商品
     */
    @Test
    public void getActGoodsByClass() {
        List<ActGoodsInfoResult> activityGoodsList = activityGoodsService
                .getActGoodsByClass("S00050001wx219007e82b660f17", 1L, 1544169688415057L);
        assertNotNull(activityGoodsList);
    }

    @Test
    public void countByActId() {
        assert (activityGoodsService.countByActId(10086L) > 0);
    }

    @Test
    public void getActGoodsByActId() {
        List<ActGoodsInfoResult> activityGoodsList = activityGoodsService.getActGoodsByActId4Pc(10086L);
        assertNotNull(activityGoodsList);
    }

    @Test
    public void getIndexSeckillGoods() {
        assertNotNull(activityGoodsService.getIndexSeckillGoods("S00050001wx219007e82b660f17", 1544169688415057L));
    }

    @Test
    public void getISecondSeckillGoods() {
        assertNotNull(activityGoodsService.getISecondSeckillGoods("S00050001wx219007e82b660f17", 1544169688415057L));
    }

    @Test
    public void deleteByActId() {
        activityGoodsService.deleteByActId(10086L,"123");
        List<ActGoodsInfoResult> list = activityGoodsService.getActGoodsByActId4Pc(10086L);
        assert(list==null||list.size()==0);
    }
}