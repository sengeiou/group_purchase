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
import com.mds.group.purchase.exception.ServiceException;
import com.mds.group.purchase.logistics.dao.StreetsMapper;
import com.mds.group.purchase.logistics.model.Streets;
import com.mds.group.purchase.logistics.result.Community4GroupApply;
import com.mds.group.purchase.logistics.result.StreetsResult;
import com.mds.group.purchase.logistics.service.CommunityService;
import com.mds.group.purchase.logistics.service.LineDetailService;
import com.mds.group.purchase.logistics.service.StreetsService;
import com.mds.group.purchase.logistics.vo.StreetsVo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


/**
 * The type Streets service.
 *
 * @author CodeGenerator
 * @date 2018 /11/27
 */
@Service
public class StreetsServiceImpl extends AbstractService<Streets> implements StreetsService {

    @Resource
    private StreetsMapper tStreetsMapper;
    @Resource
    private CommunityService communityService;
    @Resource
    private LineDetailService lineDetailService;

    @Override
    public List<StreetsResult> getStreetsByAreaId(String areaId, String appmodelId) {
        List<StreetsResult> list = tStreetsMapper.selectStreetsByAreaId(Integer.parseInt(areaId), appmodelId);
        if (list == null || list.isEmpty()) {
            return new ArrayList<>();
        }
        List<Long> streetIdList = list.stream().map(StreetsResult::getValue).collect(Collectors.toList());
        Map<Long, List<Community4GroupApply>> communitysByStreetIdCanAddToLine = communityService
                .getCommunitysByStreetIdCanAddToLine(streetIdList, appmodelId);

        Iterator<StreetsResult> iterator = list.iterator();
        List<StreetsResult> listRes = new ArrayList<>();
        while (iterator.hasNext()) {
            StreetsResult streetsResult = iterator.next();
            List<Community4GroupApply> community4GroupApply = communitysByStreetIdCanAddToLine
                    .get(streetsResult.getValue());
            streetsResult.setList(community4GroupApply);
            listRes.add(streetsResult);
        }
        return listRes;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateBatch(String appmodelId, List<StreetsVo> streetsVo) {
        streetsVo.forEach(o -> o.setAppmodelId(appmodelId));
        List<String> collect = streetsVo.stream().collect(Collectors.toMap(StreetsVo::getLabel, e -> 1, Integer::sum))
                .entrySet().stream().filter(entry -> entry.getValue() > 1).map(Map.Entry::getKey)
                .collect(Collectors.toList());
        if (collect.size() > 0) {
            String collect1 = String.join(",", collect);
            throw new ServiceException("不能添加重复的区域：" + collect1);
        }

        //1、提取新增的区域和要修改的区域
        List<Streets> addList = new ArrayList<>();
        List<Streets> updateList = new ArrayList<>();
        for (StreetsVo sv : streetsVo) {
            if (sv.getValue() == null || sv.getValue() == 0) {
                addList.add(sv.voToObj());
            } else {
                updateList.add(sv.voToObj());
            }
        }
        //2、更新数据
        if (addList.size() > 0) {
            tStreetsMapper.insertList(addList);
        }
        if (updateList.size() > 0) {
            for (Streets s : updateList) {
                tStreetsMapper.updateByPrimaryKey(s);
                //修改区域时同时修改community表和LineDetail表的street字段
                communityService.updateStreetNameByStreetId(s.getStreetid(), s.getStreet());
                lineDetailService.updateStreetNameByStreetId(s.getStreetid(), s.getStreet());
            }
        }
    }

    @Override
    public int countStreetsByAreaId(String value) {
        return tStreetsMapper.selectCountByAreaId(value);
    }

    @Override
    public List<StreetsResult> findByAppmodelId(String appmodelId) {
        return tStreetsMapper.selectByappmodelId(appmodelId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteStreet(Long streetId, String appmodelId) {
        if (streetId != -1) {
            //删除街道下的小区
            communityService.deleteCommunityByStreetId(streetId, appmodelId);
            deleteById(streetId);
        }
    }

    @Override
    public List<Streets> getStreetsByIdList(List<Long> streetIdList) {
        if (streetIdList == null) {
            return new ArrayList<>();
        }
        return tStreetsMapper.selectByStreetIdList(streetIdList);
    }
}
