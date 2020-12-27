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

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.BetweenFormater;
import cn.hutool.core.date.DateUtil;
import com.mds.group.purchase.solitaire.model.SolitaireRecordSetting;
import com.mds.group.purchase.solitaire.service.SolitaireRecordService;
import com.mds.group.purchase.solitaire.service.SolitaireRecordSettingService;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHandler;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * 自动删除接龙记录的定时任务
 * 基于xxljob
 *
 * @author shuke
 * @date 2019 /5/18
 */
@JobHandler(value = "AutoRemoveSolitaireRecordJobHandler")
@Component
public class AutoRemoveSolitaireRecordJobHandler extends IJobHandler {

    @Resource
    private SolitaireRecordSettingService solitaireRecordSettingService;
    @Resource
    private SolitaireRecordService solitaireRecordService;

    /**
     * 每天0时删除可以删除的接龙记录
     * @param param
     * @return
     * @throws Exception
     */
    @Override
    public ReturnT<String> execute(String param) throws Exception {
        List<SolitaireRecordSetting> autoDeleteRecord = solitaireRecordSettingService
                .findByList("autoDeleteRecord", true);
        List<String> canDeleteAppmodelIds = new ArrayList<>();
        autoDeleteRecord.stream().filter(o -> o.getRecordDeleteMethod() == 1).forEach(o -> {
            Long lastDeleteTime = o.getLastDeleteTime();
            String day = DateUtil.formatBetween(System.currentTimeMillis() - lastDeleteTime, BetweenFormater.Level.DAY);
            day = day.replace("天", "");
            if (Integer.valueOf(day) + 1 >= o.getDeleteTime()) {
                canDeleteAppmodelIds.add(o.getAppmodelId());
            }
        });
        if (CollectionUtil.isNotEmpty(canDeleteAppmodelIds)) {
            //删除所有appmodelId对应的记录
            solitaireRecordService.deleteByAppmodelIds(canDeleteAppmodelIds);
            //更新接龙设置的上一次删除时间
            solitaireRecordSettingService.updateBatchLastDeleteTime(canDeleteAppmodelIds,System.currentTimeMillis());
        }
        return ReturnT.SUCCESS;
    }
}
