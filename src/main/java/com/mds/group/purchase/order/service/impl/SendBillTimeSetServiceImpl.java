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

package com.mds.group.purchase.order.service.impl;

import com.mds.group.purchase.constant.Common;
import com.mds.group.purchase.core.AbstractService;
import com.mds.group.purchase.order.dao.SendBillTimeSetMapper;
import com.mds.group.purchase.order.model.SendBillTimeSet;
import com.mds.group.purchase.order.service.SendBillTimeSetService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;


/**
 * 发货单生产时间业务实现类
 *
 * @author shuke
 * @date 2019 /02/18
 */
@Service
public class SendBillTimeSetServiceImpl extends AbstractService<SendBillTimeSet> implements SendBillTimeSetService {

    @Resource
    private SendBillTimeSetMapper tSendBillTimeSetMapper;

    @Override
    public Integer addNewSetTime(String setTime, String appmodelId) {
        SendBillTimeSet sendBillTimeSet = new SendBillTimeSet();
        sendBillTimeSet.setAppmodelId(appmodelId);
        sendBillTimeSet.setDelFlag(Common.DEL_FLAG_TRUE);
        sendBillTimeSet.setSetTime(setTime);
        return tSendBillTimeSetMapper.insert(sendBillTimeSet);
    }

    @Override
    public Integer getSetTimeNumberByAppmodelId(String appmodelId) {
        return tSendBillTimeSetMapper.selectSetTimeNumberByAppmodelId(appmodelId);
    }

    @Override
    public Integer setTime(List<String> setTimeList, String appmodelId) {
        //1、删除之前设置的时间记录
        tSendBillTimeSetMapper.deleteByAppmodelId(appmodelId);
        //2、插入新的时间设置纪录
        List<SendBillTimeSet> sendBillTimeSetList = new ArrayList<>();
        setTimeList.forEach(time -> {
            SendBillTimeSet sendBillTimeSet = new SendBillTimeSet();
            sendBillTimeSet.setAppmodelId(appmodelId);
            sendBillTimeSet.setDelFlag(Common.DEL_FLAG_FALSE);
            sendBillTimeSet.setSetTime(time);
            sendBillTimeSetList.add(sendBillTimeSet);
        });
        return tSendBillTimeSetMapper.insertList(sendBillTimeSetList);
    }

    @Override
    public List<SendBillTimeSet> getAll() {
        return tSendBillTimeSetMapper.selectAll();
    }

    @Override
    public List<SendBillTimeSet> getByAppmodelId(String appmodelId) {
        //如果商家还没有设置时间，则默认添加一个时间21:00
        List<SendBillTimeSet> sendBillTimeSets = tSendBillTimeSetMapper.selectSetTimeByAppmodelId(appmodelId);
        if (sendBillTimeSets.isEmpty()) {
            SendBillTimeSet sendBillTimeSet = new SendBillTimeSet();
            sendBillTimeSet.setAppmodelId(appmodelId);
            sendBillTimeSet.setDelFlag(Common.DEL_FLAG_FALSE);
            sendBillTimeSet.setSetTime("21:30");
            tSendBillTimeSetMapper.insert(sendBillTimeSet);
        }
        return tSendBillTimeSetMapper.selectSetTimeByAppmodelId(appmodelId);
    }
}
