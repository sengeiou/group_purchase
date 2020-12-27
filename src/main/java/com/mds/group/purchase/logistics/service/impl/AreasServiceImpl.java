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

package com.mds.group.purchase.logistics.service.impl;

import com.mds.group.purchase.core.AbstractService;
import com.mds.group.purchase.logistics.dao.AreasMapper;
import com.mds.group.purchase.logistics.model.Areas;
import com.mds.group.purchase.logistics.result.AreaResult;
import com.mds.group.purchase.logistics.service.AreasService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;


/**
 * The type Areas service.
 *
 * @author CodeGenerator
 * @date 2018 /11/27
 */
@Service
public class AreasServiceImpl extends AbstractService<Areas> implements AreasService {

    @Resource
    private AreasMapper tAreasMapper;

    @Override
    public List<AreaResult> getAreasByCityId(String cityId) {
        return tAreasMapper.selectAreasByCityId(Integer.parseInt(cityId));
    }

    @Override
    public int countAreasByCityId(String value) {
        Areas areas = new Areas();
        areas.setCityid(value);
        return tAreasMapper.selectCount(areas);
    }

    @Override
    public List<Areas> getAreasByIds(List<String> areaIdList) {
        return tAreasMapper.selectByAreaidList(areaIdList);
    }

    @Override
    public String findIdLikeName(String cityId, String areas) {
        return tAreasMapper.selectIdLikeName(cityId, areas);
    }
}
