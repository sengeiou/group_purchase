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

package com.mds.group.purchase.jobhandler;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import com.mds.group.purchase.configurer.GroupMallProperties;
import com.mds.group.purchase.constant.RedisPrefix;
import com.mds.group.purchase.shop.model.Manager;
import com.mds.group.purchase.shop.model.Statistics;
import com.mds.group.purchase.shop.service.ManagerService;
import com.mds.group.purchase.shop.service.StatisticsService;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHandler;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

/**
 * The type Statistics job handler.
 *
 * @author Administrator
 */
@JobHandler(value = "StatisticsJobHandler")
@Component
public class StatisticsJobHandler extends IJobHandler {

    @Resource
    private RedisTemplate redisTemplate;
    @Resource
    private ManagerService managerService;
    @Resource
    private StatisticsService statisticsService;

    /**
     * 每天凌晨12点时存入当天的数据
     */
    @Override
    public ReturnT<String> execute(String param) {
        List<Manager> managers = managerService.findAll();
        for (Manager manager : managers) {
            String appmodelId = manager.getAppmodelId();
            //今日浏览量统计
            Integer pageview = (Integer) redisTemplate.opsForValue()
                    .get(GroupMallProperties.getRedisPrefix().concat(appmodelId).concat(RedisPrefix.MANAGER_STATISTICS_PAGEVIEW));
            //今日访客量统计
            Integer visitorsum = (Integer) redisTemplate.opsForValue()
                    .get(GroupMallProperties.getRedisPrefix().concat(appmodelId).concat(RedisPrefix.MANAGER_STATISTICS_VISITORSUM));
            //存入数据库
            DateTime parse = DateUtil.parse(DateUtil.today());
            Statistics statistics = new Statistics();
            statistics.setAppmodelId(appmodelId);
            statistics.setPageview(pageview);
            statistics.setVisitorsum(visitorsum);
            statistics.setCreateTime(parse);
            statisticsService.save(statistics);
            //删除键值
            redisTemplate.opsForValue().set(GroupMallProperties.getRedisPrefix().concat(appmodelId).concat(RedisPrefix.MANAGER_STATISTICS_PAGEVIEW),0);
            redisTemplate.opsForValue().set(GroupMallProperties.getRedisPrefix().concat(appmodelId).concat(RedisPrefix.MANAGER_STATISTICS_VISITORSUM),0);
        }
        return SUCCESS;
    }


}
