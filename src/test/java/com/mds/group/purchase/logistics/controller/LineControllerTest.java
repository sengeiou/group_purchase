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

package com.mds.group.purchase.logistics.controller;

import com.mds.group.purchase.logistics.result.LineResult;
import com.mds.group.purchase.logistics.service.LineService;
import com.mds.group.purchase.logistics.vo.LineGetVo;
import com.mds.group.purchase.logistics.vo.LineVo;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Rollback
@SpringBootTest
@Transactional
@RunWith(SpringRunner.class)
public class LineControllerTest {

    @Autowired
    private LineService lineService;

    private static final String appmodelId = "S00050001wx17c66eb4da0ef6ab";

    @Test
    public void saveLine() {
        LineVo lineVo = new LineVo();
        lineVo.setAppmodelId(appmodelId);
        lineVo.setAreaId("110101");
        lineVo.setCityId("110100");
        lineVo.setProvinceId("110000");
        Long[] ids = {111199L,111201L};
        List<Long> cs = Arrays.asList(ids);
        lineVo.setCommunities(cs);
        lineVo.setDriverName("老王");
        lineVo.setDriverPhone("110");
        lineVo.setLineName("单元测试线路");
        lineService.saveLine(lineVo);
    }

    @Test
    public void getLine() {
        LineGetVo lineGetVo = new LineGetVo();
        lineGetVo.setDriverPhone("110");
        Map<String, Object> line = lineService.getLine(lineGetVo, appmodelId);
        line.forEach((k, v) -> {
            LineResult v1 = (LineResult) v;
            boolean b = "单元测试线路".equalsIgnoreCase(v1.getLineName());
            assert b;
        });
    }

    @Test
    public void modifyLine() {

    }

    @Test
    public void removeLine() {

    }

    @Test
    public void getLineStreetCommunity() {
    }


    @Test
    public void getLineStreetCommunity1() {
    }
}