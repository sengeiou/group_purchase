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

import com.mds.group.purchase.configurer.GroupMallProperties;
import com.mds.group.purchase.core.AbstractService;
import com.mds.group.purchase.logistics.dao.ProvincesMapper;
import com.mds.group.purchase.logistics.model.*;
import com.mds.group.purchase.logistics.result.AreaResultF;
import com.mds.group.purchase.logistics.result.CityResultF;
import com.mds.group.purchase.logistics.result.PCAResult;
import com.mds.group.purchase.logistics.result.StreetsResultF;
import com.mds.group.purchase.logistics.service.*;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


/**
 * The type Provinces service.
 *
 * @author CodeGenerator
 * @date 2018 /11/27
 */
@Service
public class ProvincesServiceImpl extends AbstractService<Provinces> implements ProvincesService {

    @Resource
    private AreasService areasService;
    @Resource
    private RedisTemplate redisTemplate;
    @Resource
    private CitiesService citiesService;
    @Resource
    private StreetsService streetsService;
    @Resource
    private ProvincesMapper tProvincesMapper;
    @Resource
    private CommunityService communityService;

    @Override
    public List<PCAResult> getAll(String appmodelId) {
        String key = GroupMallProperties.getRedisPrefix() + appmodelId + ":pca";
        List<PCAResult> pcaResults = (List<PCAResult>) redisTemplate.opsForValue().get(key);
        if (pcaResults == null || pcaResults.size() == 0) {
            pcaResults = tProvincesMapper.getAll();
            redisTemplate.opsForValue().set(key, pcaResults);
        }
        return pcaResults;
    }

    @Override
    public List<PCAResult> haveCommunities(String appmodelId) {
        List<Community> byAppmodelId = communityService.findByAppmodelId(appmodelId);
        if (byAppmodelId.isEmpty()) {
            return new ArrayList<>();
        }
        //获取小区字段中包含的provincesId集合
        List<String> provinceIdList = byAppmodelId.stream().map(Community::getProvinceId).distinct()
                .collect(Collectors.toList());
        List<Provinces> provincesList = tProvincesMapper.selectByProvinceids(provinceIdList);
        //获取小区中包含的cityid集合
        List<String> cityIdList = byAppmodelId.stream().map(Community::getCityId).distinct()
                .collect(Collectors.toList());
        List<Cities> cityList = citiesService.getCitiesByCityIdList(cityIdList);
        //获取小区中包含的areaid集合
        List<String> areaIdList = byAppmodelId.stream().map(Community::getAreaId).distinct()
                .collect(Collectors.toList());
        List<Areas> areasList = areasService.getAreasByIds(areaIdList);
        //获取小区中包含的街道id集合
        List<Long> streetIdList = byAppmodelId.stream().map(Community::getStreetId).distinct()
                .collect(Collectors.toList());
        List<Streets> streetsList = streetsService.getStreetsByIdList(streetIdList);

        Map<String, List<StreetsResultF>> streetMap = new HashMap<>(16);
        streetsList.forEach(street -> {
            List<StreetsResultF> streetsResultFList = streetMap.get(street.getAreaid());
            if (streetsResultFList == null) {
                streetsResultFList = new ArrayList<>();
            }
            StreetsResultF streetsResultF = new StreetsResultF();
            streetsResultF.setAreaid(street.getAreaid());
            streetsResultF.setLabel(street.getStreet());
            streetsResultF.setValue(street.getStreetid());
            streetsResultFList.add(streetsResultF);
            streetMap.put(street.getAreaid(), streetsResultFList);
        });

        Map<String, List<AreaResultF>> areaMap = new HashMap<>(16);
        areasList.forEach(areas -> {
            List<AreaResultF> areaResultFS = areaMap.get(areas.getCityid());
            if (areaResultFS == null) {
                areaResultFS = new ArrayList<>();
            }
            AreaResultF areaResultF = new AreaResultF();
            areaResultF.setCityid(areas.getCityid());
            areaResultF.setLabel(areas.getArea());
            areaResultF.setValue(areas.getAreaid());
            areaResultF.setList(streetMap.get(areas.getAreaid()));
            areaResultFS.add(areaResultF);
            areaMap.put(areas.getCityid(), areaResultFS);
        });

        Map<String, List<CityResultF>> cityMap = new HashMap<>(16);
        cityList.forEach(cities -> {
            List<CityResultF> cityResultFS = cityMap.get(cities.getCityid());
            if (cityResultFS == null) {
                cityResultFS = new ArrayList<>();
            }
            CityResultF cityResultF = new CityResultF();
            cityResultF.setProvinceid(cities.getProvinceid());
            cityResultF.setLabel(cities.getCity());
            cityResultF.setValue(cities.getCityid());
            cityResultF.setList(areaMap.get(cities.getCityid()));
            cityResultFS.add(cityResultF);
            cityMap.put(cities.getProvinceid(), cityResultFS);
        });

        List<PCAResult> pcaResultList = new ArrayList<>();
        provincesList.forEach(provinces -> {
            PCAResult pcaResult = new PCAResult();
            pcaResult.setLabel(provinces.getProvince());
            pcaResult.setValue(provinces.getProvinceid());
            pcaResult.setList(cityMap.get(provinces.getProvinceid()));
            pcaResultList.add(pcaResult);
        });
        return pcaResultList;
    }

    @Override
    public String findIdLikeName(String province) {
        return tProvincesMapper.selectIdLikeName(province);
    }
}
