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

package com.mds.group.purchase.shop.service.impl;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import com.mds.group.purchase.core.AbstractService;
import com.mds.group.purchase.shop.dao.StatisticsMapper;
import com.mds.group.purchase.shop.model.Statistics;
import com.mds.group.purchase.shop.service.StatisticsService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;


/**
 * The type Statistics service.
 *
 * @author CodeGenerator
 * @date 2018 /12/25
 */
@Service
public class StatisticsServiceImpl extends AbstractService<Statistics> implements StatisticsService {

    @Resource
    private StatisticsMapper tStatisticsMapper;

    @Override
    public Statistics findTodayStatisticData(String appmodelId) {
        DateTime dateTime = DateUtil.offsetDay(DateUtil.parseDate(DateUtil.today()), -1);
        Statistics statistics = new Statistics();
        statistics.setAppmodelId(appmodelId);
        statistics.setCreateTime(dateTime);
        return tStatisticsMapper.selectOne(statistics);
    }

    @Override
    public Statistics findSevenDaysStatisticData(String appmodelId, String currentDate, String lastWeek) {
        Statistics statistics = tStatisticsMapper.selectSevenDaysStatisticData(appmodelId, currentDate, lastWeek);
        if (statistics == null) {
            return null;
        }
        if (statistics.getPageview() == null) {
            return null;
        }
        return statistics;
    }

    @Override
    public List<String> findAppmodelId() {
        return tStatisticsMapper.selectAppmodelId();
    }
}
