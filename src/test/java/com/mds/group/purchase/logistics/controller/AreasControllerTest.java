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

import com.mds.group.purchase.logistics.model.Provinces;
import com.mds.group.purchase.logistics.result.CityResult;
import com.mds.group.purchase.logistics.service.AreasService;
import com.mds.group.purchase.logistics.service.CitiesService;
import com.mds.group.purchase.logistics.service.ProvincesService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.assertNotNull;

@Rollback
@SpringBootTest
@Transactional
@RunWith(SpringRunner.class)
public class AreasControllerTest {

    @Autowired
    private AreasService areasService;
    @Autowired
    private CitiesService citiesService;
    @Autowired
    private ProvincesService provincesService;

    private static final String appmodelId = "S00050001wx17c66eb4da0ef6ab";

    @Test
    public void list() {
        List<Provinces> all = provincesService.findAll();
        if (all.isEmpty()) {
            return;
        }
        List<String> collect = all.stream().map(Provinces::getProvinceid).collect(Collectors.toList());
        collect.forEach(o->{
            List<CityResult> citiesByProvinceId = citiesService.getCitiesByProvinceId(o);
            assertNotNull(citiesByProvinceId);
        });
    }
}