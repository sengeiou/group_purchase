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
import com.mds.group.purchase.financial.model.FinancialDetails;
import com.mds.group.purchase.order.model.Order;
import com.mds.group.purchase.user.model.GroupBpavawiceOrder;
import com.mds.group.purchase.user.model.Wxuser;

import java.util.List;


/**
 * The interface Financial details service.
 *
 * @author shuke
 * @date 2019 /04/25
 */
public interface FinancialDetailsService extends Service<FinancialDetails> {
    /**
     * 获取对账单明细
     *
     * @param startDate  开始时间
     * @param endDate    结束时间
     * @param type       交易类型 1/商品交易 2/佣金提现 3/退款
     * @param accoutType 资金类型 1/收入 2/支出
     * @param appmodelId 模板id
     * @return 资金明细列表 list
     */
    List<FinancialDetails> findFinancialDetails(String startDate, String endDate, Integer type, Integer accoutType,
                                                String appmodelId);

    /**
     * 获取对账单资金汇总
     *
     * @param appmodelId 模板id
     * @param type       交易类型 1/商品交易 2/佣金提现 3/退款
     * @param accoutType 资金类型 1/收入 2/支出
     * @param startDate  开始时间
     * @param endDate    结束时间
     * @return 资金明细汇总记录 json字符串
     */
    String collectFinancial(String appmodelId, Integer type, Integer accoutType, String startDate, String endDate);

    /**
     * 用于支付回调处理插入对账单明细
     *
     * @param order     订单对象
     * @param payStatus 支付状态 true 支付成功  false 支付回调处理失败
     */
    void save(Order order, boolean payStatus);

    /**
     * 团长提现成功后插入对账单明细
     *
     * @param groupBpavawiceOrder 团长提现订单
     * @param wxuser            用户信息
     */
    void save(GroupBpavawiceOrder groupBpavawiceOrder, Wxuser wxuser);
}
