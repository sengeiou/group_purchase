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

import com.mds.group.purchase.shop.service.StatisticsService;
import com.mds.group.purchase.utils.GoodsAreaMappingUtil;
import com.mds.group.purchase.utils.GoodsUtil;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHandler;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

/**
 * The type Cache job handler.
 *
 * @author Administrator
 */
@JobHandler(value = "CacheJobHandler")
@Component
public class CacheJobHandler extends IJobHandler {

    @Resource
    private GoodsUtil goodsUtil;
    @Resource
    private StatisticsService statisticsService;
    @Resource
    private GoodsAreaMappingUtil goodsAreaMappingUtil;

    /**
     * 每隔一小时刷新一次缓存数据
     */
    @Override
    public ReturnT<String> execute(String param) {
        List<String> appmodelId = statisticsService.findAppmodelId();
        if (appmodelId != null) {
            for (String s : appmodelId) {
                goodsUtil.flushGoodsCache(s);
                goodsAreaMappingUtil.flushGoodsAreaMapping(s);
            }
        }
        return SUCCESS;
    }


}
