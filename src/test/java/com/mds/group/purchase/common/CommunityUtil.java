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

package com.mds.group.purchase.common;

import cn.hutool.core.util.RandomUtil;
import com.mds.group.purchase.logistics.controller.VO.ProvinceCityAreaStreetsVO;
import com.mds.group.purchase.logistics.model.Community;
import com.mds.group.purchase.logistics.model.Provinces;
import com.mds.group.purchase.logistics.model.Streets;
import com.mds.group.purchase.logistics.result.AreaResult;
import com.mds.group.purchase.logistics.result.CityResult;
import com.mds.group.purchase.logistics.result.LineResult;
import com.mds.group.purchase.logistics.result.StreetsResult;
import com.mds.group.purchase.logistics.service.*;
import com.mds.group.purchase.logistics.vo.CommunityGetVo;
import com.mds.group.purchase.logistics.vo.CommunityVo;
import com.mds.group.purchase.logistics.vo.LineGetVo;
import com.mds.group.purchase.logistics.vo.LineVo;
import org.junit.Assert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.RoundingMode;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class CommunityUtil {

    @Autowired
    private CommunityService communityService;

    @Autowired
    private StreetsService streetsService;

    @Autowired
    private CitiesService citiesService;

    @Autowired
    private AreasService areasService;

    @Autowired
    private ProvincesService provincesService;
    @Autowired
    private LineService lineService;


    public final static String appmodelId = "S00050001wx219007e82b660f17";

    public ProvinceCityAreaStreetsVO createLine() {
        ProvinceCityAreaStreetsVO pcasV = createComminuty();
        LineVo lineVo = new LineVo();
        lineVo.setDriverPhone("138" + RandomUtil.randomNumbers(8));
        lineVo.setDriverName("司机名" + RandomUtil.randomString(10));
        lineVo.setAppmodelId(appmodelId);
        lineVo.setLineName("线路" + RandomUtil.randomInt(10, 999));
        lineVo.setAreaId(pcasV.getAreaResult().getValue());
        lineVo.setCityId(pcasV.getCityResult().getValue());
        lineVo.setProvinceId(pcasV.getProvinces().getProvinceid());
        List<Community> communities = pcasV.getCommunities();
        List<Long> communitiesIds = communities.stream().map(obj -> obj.getCommunityId()).collect(Collectors.toList());
        lineVo.setCommunities(communitiesIds);
        lineService.saveLine(lineVo);
        LineGetVo lineGetVo = new LineGetVo();
        lineGetVo.setPage(1);
        lineGetVo.setSize(1);
        lineGetVo.setDriverName(lineVo.getDriverName());
        Map<String, Object> line1 = lineService.getLine(lineGetVo, appmodelId);
        List<LineResult> line = (List<LineResult>) line1.get("list");
        LineResult lineResult = line.get(0);
        Assert.assertEquals("司机名不一致", lineVo.getLineName(), lineResult.getLineName());
        pcasV.setLineResult(lineResult);
        return pcasV;
    }


    /**
     * 获取省市区街道
     * 并创建街道并获取其中某一个街道
     * @return
     */
    public ProvinceCityAreaStreetsVO addAndGetStreetsResult() {
        //获取所有省份信息
        List<Provinces> provinces = provincesService.findAll();
        Assert.assertNotEquals("数据为空", 0, provinces);
        Collections.shuffle(provinces);

        Provinces province = provinces.get(0);
        //获取对应省份的所有市信息
        List<CityResult> cityResults = citiesService.getCitiesByProvinceId(province.getProvinceid());
        int i = 0;
        while (cityResults.size() == 0) {
            i++;
            province = provinces.get(i);
            cityResults = citiesService.getCitiesByProvinceId(province.getProvinceid());
        }
        Collections.shuffle(cityResults);

        CityResult cityResult = cityResults.get(0);
        //获取对应市的所有县信息
        List<AreaResult> areaResults = areasService.getAreasByCityId(cityResult.getValue());
        i = 0;
        while (areaResults.size() == 0) {
            i++;
            cityResult = cityResults.get(i);
            areaResults = areasService.getAreasByCityId(cityResult.getValue());
        }
        Collections.shuffle(areaResults);
        AreaResult areaResult = areaResults.get(0);
        for (int j = 0; j < RandomUtil.randomInt(5, 10); j++) {
            Streets streets = new Streets();
            streets.setAppmodelId(appmodelId);
            streets.setAreaid(areaResult.getValue());
            streets.setStreet("街道名称".concat(RandomUtil.randomString(10)));
            streetsService.save(streets);
        }
        //获取对应县的所有街道信息
        List<StreetsResult> streetsResults = streetsService.getStreetsByAreaId(areaResult.getValue(), appmodelId);
        Assert.assertNotEquals("数据为空", 0, streetsResults.size());
        Collections.shuffle(streetsResults);

        ProvinceCityAreaStreetsVO pcas = new ProvinceCityAreaStreetsVO();
        pcas.setProvinces(province);
        pcas.setCityResult(cityResult);
        pcas.setAreaResult(areaResult);
        pcas.setStreetsResults(streetsResults);
        return pcas;
    }

    /**
     * 获取省市区街道
     * 并创建街道并获取其中某一个街道
     * @return
     */
    public ProvinceCityAreaStreetsVO addAndGetStreetsResultV119() {
        ProvinceCityAreaStreetsVO pcas = new ProvinceCityAreaStreetsVO();
        //获取所有省份信息
        List<Provinces> provinces = provincesService.findAll();
        Assert.assertNotEquals("数据为空", 0, provinces);
        Provinces province = provinces.get(0);
        //获取对应省份的所有市信息
        List<CityResult> cityResults = citiesService.getCitiesByProvinceId(province.getProvinceid());
        if (cityResults.isEmpty()) {
            return pcas;
        }
        CityResult cityResult = cityResults.get(0);
        List<AreaResult> areaResults = areasService.getAreasByCityId(cityResult.getValue());
        AreaResult areaResult = areaResults.get(0);
        for (int j = 0; j < RandomUtil.randomInt(5, 10); j++) {
            Streets streets = new Streets();
            streets.setAppmodelId(appmodelId);
            streets.setAreaid(areaResult.getValue());
            streets.setStreet("街道名称".concat(RandomUtil.randomString(10)));
            streetsService.save(streets);
        }
        //获取对应县的所有街道信息
        List<StreetsResult> streetsResults = streetsService.getStreetsByAreaId(areaResult.getValue(), appmodelId);
        Assert.assertNotEquals("数据为空", 0, streetsResults.size());

        pcas.setProvinces(province);
        pcas.setCityResult(cityResult);
        pcas.setAreaResult(areaResult);
        pcas.setStreetsResults(streetsResults);
        return pcas;
    }

    /**
     * 创建小区
     */
    public ProvinceCityAreaStreetsVO createComminuty() {
        ProvinceCityAreaStreetsVO pcasVO = addAndGetStreetsResultV119();
        CommunityVo communityVo = new CommunityVo();
        communityVo.setAppmodelId(appmodelId);
        communityVo.setProvinceId(pcasVO.getProvinces().getProvinceid());
        communityVo.setCityId(pcasVO.getCityResult().getValue());
        communityVo.setAreaId(pcasVO.getAreaResult().getValue());
        String provinceName = pcasVO.getProvinces().getProvince();
        String cityName = pcasVO.getCityResult().getLabel();
        String aredName = pcasVO.getAreaResult().getLabel();
        communityVo.setPcaAdr(provinceName.concat(cityName).concat(aredName));
        StreetsResult streetsResult = pcasVO.getStreetsResults().get(0);
        communityVo.setStreetId(streetsResult.getValue());
        communityVo.setStreetName(streetsResult.getLabel());
        communityVo.setCommunityName("小区名" + RandomUtil.randomString(10));
        String localtion = RandomUtil.randomDouble(0, 30, 9, RoundingMode.HALF_UP) + "," + RandomUtil
                .randomDouble(0, 30, 9, RoundingMode.HALF_UP);
        communityVo.setLocation(localtion);
//		ProvinceCityAreaStreetsVO line = createLine();
//		communityVo.setLineId(line.getLineResult().getLineId());
        communityService.saveCommunity(communityVo, appmodelId);
        //获取小区信息
        CommunityGetVo communityGetVo = new CommunityGetVo();
        communityGetVo.setPage(1);
        communityGetVo.setSize(10);
        communityGetVo.setPcaAdr(communityVo.getPcaAdr());
        List<Community> communities = communityService.getCommunities(communityGetVo, appmodelId);
        Assert.assertNotEquals("小区未正确添加", 0, communities.size());
        Community community = communities.get(0);
        community.setAreaName(aredName);
        pcasVO.setCommunity(community);
        pcasVO.setCommunities(communities);
        return pcasVO;
    }


}
