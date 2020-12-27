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

package com.mds.group.purchase.shop.service;

import com.mds.group.purchase.core.Service;
import com.mds.group.purchase.shop.model.Statistics;

import java.util.List;


/**
 * Created by CodeGenerator on 2018/12/25.
 *
 * @author pavawi
 */
public interface StatisticsService extends Service<Statistics> {


    /**
     * 查询今日统计
     *
     * @param appmodelId the appmodel id
     * @return the statistics
     */
    Statistics findTodayStatisticData(String appmodelId);

    /**
     * 查询7日统计
     *
     * @param appmodelId  the appmodel id
     * @param currentDate the current date
     * @param lastWeek    the last week
     * @return the statistics
     */
    Statistics findSevenDaysStatisticData(String appmodelId, String currentDate, String lastWeek);

    /**
     * 查询已有appmodelId
     *
     * @return the list
     */
    List<String> findAppmodelId();
}
