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

import cn.hutool.core.collection.CollectionUtil;
import com.google.common.collect.Maps;
import com.mds.group.purchase.core.AbstractService;
import com.mds.group.purchase.core.CodeMsg;
import com.mds.group.purchase.exception.GlobalException;
import com.mds.group.purchase.logistics.dao.CitiesMapper;
import com.mds.group.purchase.logistics.model.Cities;
import com.mds.group.purchase.logistics.model.Community;
import com.mds.group.purchase.logistics.model.Provinces;
import com.mds.group.purchase.logistics.result.CityResult;
import com.mds.group.purchase.logistics.result.CityResultHaveGroup;
import com.mds.group.purchase.logistics.service.CitiesService;
import com.mds.group.purchase.logistics.service.CommunityService;
import com.mds.group.purchase.logistics.service.ProvincesService;
import com.mds.group.purchase.utils.PinYinUtil;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Condition;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


/**
 * The type Cities service.
 *
 * @author CodeGenerator
 * @date 2018 /11/27
 */
@Service
public class CitiesServiceImpl extends AbstractService<Cities> implements CitiesService {

    @Resource
    private CitiesMapper tCitiesMapper;
    @Resource
    private CommunityService communityService;
    @Resource
    private ProvincesService provincesService;

    @Override
    public List<CityResult> getCitiesByProvinceId(String provinceId) {
        return tCitiesMapper.selectCitiesByProvinceId(Integer.parseInt(provinceId));
    }

    @Override
    public int countCitiesByProvinceId(String provinceid) {
        Cities cities = new Cities();
        cities.setProvinceid(provinceid);
        return tCitiesMapper.selectCount(cities);
    }

    @Override
    public List<Cities> findByCityIds(List<String> cityIdsList) {
        Condition condition = new Condition(Cities.class);
        condition.createCriteria().andIn("cityid", cityIdsList);
        return tCitiesMapper.selectByCondition(condition);
    }

    @Override
    public Map<String, List<CityResultHaveGroup>> getCitiesHaveGroup(String appmodelId) {
        List<Community> communities = communityService.userCanPickCommunities(appmodelId, null);
        if (communities == null || communities.isEmpty()) {
            return Maps.newHashMap();
        }
        List<String> collect = communities.parallelStream().map(Community::getCityId).distinct()
                .collect(Collectors.toList());
        List<Cities> byCityIds = findByCityIds(collect);
        Map<String, List<CityResultHaveGroup>> cityResultHaveGroups = Maps.newHashMapWithExpectedSize(8);
        try {
            List<Cities> municipal = byCityIds.stream()
                    .filter(obj -> "110000".equals(obj.getProvinceid()) || "120000".equals(obj.getProvinceid()) ||
                            "310000"
                                    .equals(obj.getProvinceid()) || "500000".equals(obj.getProvinceid()))
                    .collect(Collectors.toList());
            Map<String, String> municipalMap = Maps.newHashMapWithExpectedSize(8);
            if (CollectionUtil.isNotEmpty(municipal)) {
                List<String> provinceid = municipal.stream().map(Cities::getProvinceid).collect(Collectors.toList());
                Condition condition = new Condition(Provinces.class);
                condition.createCriteria().andIn("provinceid", provinceid);
                List<Provinces> provinces = provincesService.findByCondition(condition);
                municipalMap = provinces.stream()
                        .collect(Collectors.toMap(Provinces::getProvinceid, Provinces::getProvince));
            }
            for (Cities obj : byCityIds) {
                CityResultHaveGroup cityResultHaveGroup = new CityResultHaveGroup();
                cityResultHaveGroup.setCityId(obj.getCityid());
                if ("市辖区".equals(obj.getCity())) {
                    cityResultHaveGroup.setCityName(municipalMap.get(obj.getProvinceid()));
                } else {
                    cityResultHaveGroup.setCityName(obj.getCity());
                }
                String py = PinYinUtil.getPingYin(cityResultHaveGroup.getCityName().substring(0, 1));
                cityResultHaveGroup.setCityPingYin(py);
                List<CityResultHaveGroup> resultHaveGroups = cityResultHaveGroups.get(py);
                if (resultHaveGroups == null || resultHaveGroups.isEmpty()) {
                    resultHaveGroups = new ArrayList<>();
                }
                resultHaveGroups.add(cityResultHaveGroup);
                cityResultHaveGroups.put(py, resultHaveGroups);
            }
        } catch (BadHanyuPinyinOutputFormatCombination e) {
            throw new GlobalException(CodeMsg.SERVER_ERROR);
        }
        return cityResultHaveGroups;
    }

    @Override
    public List<Cities> getCitiesByCityIdList(List<String> cityIdList) {
        return tCitiesMapper.selectByCityIdList(cityIdList);
    }

    @Override
    public String findIdLikeName(String provinceId, String cityName) {
        return tCitiesMapper.selectIdLikeName(provinceId, cityName);
    }
}
