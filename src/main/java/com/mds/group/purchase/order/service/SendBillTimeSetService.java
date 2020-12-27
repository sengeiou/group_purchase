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
import com.mds.group.purchase.order.model.SendBillTimeSet;

import java.util.List;


/**
 * 发货单生成时间业务接口类
 *
 * @author shuke
 * @date 2019 /02/18
 */
public interface SendBillTimeSetService extends Service<SendBillTimeSet> {

    /**
     * 新增一个发货单生成时间
     *
     * @param setTime    the set time
     * @param appmodelId the appmodel id
     * @return 新增的记录条数 integer
     */
    @Deprecated
    Integer addNewSetTime(String setTime, String appmodelId);

    /**
     * 根据appmodelId来查询商户已经设置发货单生成时间的个数
     *
     * @param appmodelId the appmodel id
     * @return set time number by appmodel id
     */
    @Deprecated
    Integer getSetTimeNumberByAppmodelId(String appmodelId);

    /**
     * 发货单生成时间设置
     * 调用该方法会删除之前设置的时间，然后重新插入新的数据
     *
     * @param setTimeList the set time list
     * @param appmodelId  the appmodel id
     * @return time time
     */
    Integer setTime(List<String> setTimeList, String appmodelId);

    /**
     * 获取所有的设置记录
     *
     * @return all all
     */
    List<SendBillTimeSet> getAll();

    /**
     * 获取当前商家的设置时间
     *
     * @param appmodelId the appmodel id
     * @return by appmodel id
     */
    List<SendBillTimeSet> getByAppmodelId(String appmodelId);
}
