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

package com.mds.group.purchase.shop.controller;

import cn.hutool.core.bean.BeanUtil;
import com.mds.group.purchase.GroupPurchaseApplicationTests;
import com.mds.group.purchase.constant.HandType;
import com.mds.group.purchase.exception.ServiceException;
import com.mds.group.purchase.shop.model.Poster;
import com.mds.group.purchase.shop.service.PosterService;
import com.mds.group.purchase.shop.vo.PosterVO;
import com.mds.group.purchase.shop.vo.SortVO;
import com.mds.group.purchase.utils.BeanMapper;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
public class PosterControllerTest extends GroupPurchaseApplicationTests {

    @Autowired
    private PosterService posterService;

    private static String appmodelId = "S00050001wx4c0a3c2e450c2c7d";

    @Test
    public void saveOrUpdate() {
        Poster poster = new Poster();
        poster.setAppmodelId(appmodelId);
        poster.setPosterImg("图片");
        poster.setJumpType(0);
        poster.setTargetUrl("跳转的地址");
        poster.setTargetName("连接名");
        poster.setActivityGoodsId(123L);
        try {
            //测试参数一致性
            posterService.saveOrUpdate(poster);
        } catch (ServiceException e) {
            poster.setActivityGoodsId(null);
        }
        List<Poster> infos = posterService.infos(appmodelId, null);
        if (infos.size() == 5) {
            try {
                //轮播图添加上限
                posterService.saveOrUpdate(poster);
            } catch (ServiceException e) {
                //更新
                Poster p = posterService.infos(appmodelId, null).get(0);
                BeanUtil.copyProperties(p,poster);
                poster.setPosterImg("图片修改" + e.getMessage());
                posterService.saveOrUpdate(poster);
                Poster posterId = posterService.infos(appmodelId, poster.getPosterId()).get(0);
                Assert.assertEquals(poster, posterId);
                return;
            }
        } else {
            posterService.saveOrUpdate(poster);
        }
        PosterVO posterVO = posterService.findByAppmodelId(appmodelId).get(0);
        Assert.assertNotNull("创建时间未正确添加", posterVO.getCreateTime());
        Poster copy = BeanMapper.map(posterVO, Poster.class);
        poster.setPosterId(posterVO.getPosterId());
        poster.setCreateTime(copy.getCreateTime());
        Assert.assertEquals(poster, copy);
        copy.setTargetUrl("连接地址修改");
        posterService.saveOrUpdate(copy);
        Poster updatePoster1 = posterService.infos(appmodelId, copy.getPosterId()).get(0);
        Assert.assertEquals(copy, updatePoster1);
    }

    @Test
    public void batchDelete() {
        saveOrUpdate();
        saveOrUpdate();
        List<Poster> infos = posterService.infos(appmodelId, null);
        String ids = infos.get(0).getPosterId() + "," + infos.get(1).getPosterId();
        posterService.batchDelete(ids.split(","));
        List<Poster> byIds = posterService.findByIds(ids);
        Assert.assertEquals("数组异常",0,byIds.size());
    }

    @Test
    public void sort() {
        saveOrUpdate();
        saveOrUpdate();
        saveOrUpdate();
        saveOrUpdate();
        List<Poster> infos = posterService.infos(appmodelId, null);
        Poster poster = infos.get(1);
        SortVO sortVO = new SortVO();
        sortVO.setPosterId(poster.getPosterId());
        sortVO.setAppmodelId(appmodelId);
        sortVO.setHandleType(HandType.TOP);
        int sort = posterService.sort(sortVO);
        Assert.assertEquals("置顶修改失败1",1,sort);
        poster.setSort(1);
        List<PosterVO> top = posterService.findByAppmodelId(PosterControllerTest.appmodelId);
        PosterVO posterVO = top.get(0);
        Poster p = BeanMapper.map(posterVO, Poster.class);
        Assert.assertEquals("置顶修改失败2",poster,p);

        //置底
        sortVO.setPosterId(posterVO.getPosterId());
        sortVO.setHandleType(HandType.FOOT);
        sort = posterService.sort(sortVO);
        Assert.assertEquals("置底修改失败1",4,sort);
        List<PosterVO> bottom = posterService.findByAppmodelId(PosterControllerTest.appmodelId);
        Assert.assertEquals("置底修改失败2", sortVO.getPosterId(),bottom.get(3).getPosterId());
    }
}