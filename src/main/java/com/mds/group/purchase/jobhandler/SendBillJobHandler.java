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
import com.mds.group.purchase.constant.ActiviMqQueueName;
import com.mds.group.purchase.order.model.SendBillTimeSet;
import com.mds.group.purchase.order.service.SendBillTimeSetService;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHandler;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

/**
 * 发货单自动生成
 *
 * @author shuke
 * @date 2019 -2-19
 */
@JobHandler(value = "SendBillJobHandler")
@Component
public class SendBillJobHandler extends IJobHandler {

    @Resource
    private SendBillTimeSetService sendBillTimeSetService;
    @Resource
    private ActiveDelaySendJobHandler activeDelaySendJobHandler;

    /**
     * 每十分钟查询可生成发货单时间
     */
    @Override
    public ReturnT<String> execute(String s) {
        List<SendBillTimeSet> all = sendBillTimeSetService.getAll();
        DateUtil.thisHour(true);
        DateUtil.thisMinute();
        for (SendBillTimeSet sendBillTimeSet : all) {
            String time = sendBillTimeSet.getSetTime() + ":00";
            this.time(time, sendBillTimeSet.getAppmodelId());
        }
        return SUCCESS;
    }

    /**
     * 传入的时间在当前时间以后10分钟之内，发送消息队列生成发货单
     *
     * @param setTimeStr the set time str
     * @param appmodelId the appmodel id
     */
    public void time(String setTimeStr, String appmodelId) {
        DateTime now = DateUtil.date();
        DateTime dateTime = DateUtil.parseTimeToday(setTimeStr);
        long time1 = now.getTime();
        long time2 = dateTime.getTime();
        long tenMin = 1000 * 60 * 10L;
        long timeTemp = time2 - time1;
        if (timeTemp >= 0 && timeTemp < tenMin) {
            activeDelaySendJobHandler
                    .savaTask(appmodelId, ActiviMqQueueName.GENERATE_SENDBILL, timeTemp, appmodelId, false);
        }
    }


}
