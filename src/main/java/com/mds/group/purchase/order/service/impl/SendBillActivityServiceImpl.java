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
import com.mds.group.purchase.order.dao.SendBillActivityMapper;
import com.mds.group.purchase.order.model.SendBillActivity;
import com.mds.group.purchase.order.service.SendBillActivityService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;


/**
 * 活动页面设置发货单生成日期
 *
 * @author shuke
 * @date 2019 /02/19
 */
@Service
public class SendBillActivityServiceImpl extends AbstractService<SendBillActivity> implements SendBillActivityService {

    @Resource
    private SendBillActivityMapper tSendBillActivityMapper;

    @Override
    public void setSendBillGenerateDate(Long actId, String date, String appmodelId) {
        SendBillActivity sendBillActivity = tSendBillActivityMapper.selectByActId(actId);
        if (sendBillActivity == null) {
            sendBillActivity = new SendBillActivity();
            sendBillActivity.setSendBillGenerateDate(date);
            sendBillActivity.setActivityId(actId);
            sendBillActivity.setDelFlag(Common.DEL_FLAG_FALSE);
            sendBillActivity.setAppmodelId(appmodelId);
            tSendBillActivityMapper.insert(sendBillActivity);
        } else {
            sendBillActivity.setSendBillGenerateDate(date);
            tSendBillActivityMapper.updateByPrimaryKeySelective(sendBillActivity);
        }
    }

    @Override
    public List<SendBillActivity> getByActIds(List<Long> actIdList) {
        if (actIdList.isEmpty()) {
            return Collections.emptyList();
        }
        return tSendBillActivityMapper.selectByActIds(actIdList);
    }
}
