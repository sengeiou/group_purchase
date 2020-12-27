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

package com.mds.group.purchase.financial.service;

import com.mds.group.purchase.core.Service;
import com.mds.group.purchase.financial.model.GroupBrokerage;
import com.mds.group.purchase.order.model.Order;
import com.mds.group.purchase.order.model.OrderDetail;
import com.mds.group.purchase.user.model.GroupBpavawiceOrder;

import java.util.List;


/**
 * The interface Group brokerage service.
 *
 * @author pavawi
 */
public interface GroupBrokerageService extends Service<GroupBrokerage> {

    /**
     * 更新佣金状态
     *
     * @param orderId       订单id
     * @param statusEntered 佣金状态
     */
    void updateStatusByOrderId(Long orderId, Integer statusEntered);

    /**
     * 获取团长的佣金汇总信息
     *
     * @param groupId    团长id
     * @param strartDate the strart date
     * @param endDate    the end date
     * @return 汇总信息 json字符串
     */
    String collectBrokerage(String groupId, String strartDate, String endDate);

    /**
     * 根据团长id 查询指定时间段内的佣金记录
     *
     * @param groupId   团长id
     * @param startDate 开始时间
     * @param endDate   结束时间
     * @return 佣金记录集合 list
     */
    List<GroupBrokerage> findList(String groupId, String startDate, String endDate);

    /**
     * Save.
     *
     * @param order  the order
     * @param detail the detail
     */
    void save(Order order, OrderDetail detail);

    /**
     * Save.
     *
     * @param bpavawiceOrder the bpavawice order
     */
    void save(GroupBpavawiceOrder bpavawiceOrder);
}
