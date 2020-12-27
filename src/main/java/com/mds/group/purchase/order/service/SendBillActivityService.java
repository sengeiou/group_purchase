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

package com.mds.group.purchase.order.service;

import com.mds.group.purchase.core.Service;
import com.mds.group.purchase.order.model.SendBillActivity;

import java.util.List;


/**
 * 活动页面设置发货单生成日期
 *
 * @author shuke
 * @date 2019 /02/19
 */
public interface SendBillActivityService extends Service<SendBillActivity> {

    /**
     * 用于活动创建/修改时设置发货单生成日期
     *
     * @param actId      活动id
     * @param date       设置的日期字符串
     * @param appmodelId 小程序模板id
     */
    void setSendBillGenerateDate(Long actId, String date, String appmodelId);

    /**
     * 根据活动id得到数据列表
     *
     * @param actIdList the act id list
     * @return by act ids
     */
    List<SendBillActivity> getByActIds(List<Long> actIdList);
}
