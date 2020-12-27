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

package com.mds.group.purchase.financial.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSONObject;
import com.mds.group.purchase.core.AbstractService;
import com.mds.group.purchase.financial.dao.GroupBrokerageMapper;
import com.mds.group.purchase.financial.model.GroupBrokerage;
import com.mds.group.purchase.financial.service.GroupBrokerageService;
import com.mds.group.purchase.order.model.Order;
import com.mds.group.purchase.order.model.OrderDetail;
import com.mds.group.purchase.user.model.GroupBpavawiceOrder;
import com.mds.group.purchase.utils.GenSerialNumber;
import com.mds.group.purchase.utils.ParamUtil;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Condition;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


/**
 * The type Group brokerage service.
 *
 * @author shuke
 * @date 2019 /04/23
 */
@Service
public class GroupBrokerageServiceImpl extends AbstractService<GroupBrokerage> implements GroupBrokerageService {

    @Resource
    private GroupBrokerageMapper tGroupBrokerageMapper;

    @Override
    public void updateStatusByOrderId(Long orderId, Integer statusEntered) {
        tGroupBrokerageMapper.updateStatusByOrderId(orderId, statusEntered, DateUtil.now());
    }

    @Override
    public String collectBrokerage(String groupId, String startDate, String endDate) {
        //若传入的开始结束时间为空，则对开始和结束时间初始化，开始时间为当前时间向过去偏移一个月
        startDate = ParamUtil.initDateString(startDate, -1);
        endDate = ParamUtil.initDateString(endDate, 0);
        String res;
        BigDecimal totalWithdraw = BigDecimal.ZERO.setScale(2, BigDecimal.ROUND_HALF_UP);
        BigDecimal waitEnter = BigDecimal.ZERO.setScale(2, BigDecimal.ROUND_HALF_UP);
        BigDecimal waitWithdraw = BigDecimal.ZERO.setScale(2, BigDecimal.ROUND_HALF_UP);
        JSONObject jsonObject = new JSONObject();
        Condition condition = new Condition(GroupBrokerage.class);
        condition.createCriteria().andEqualTo("groupId", groupId).andBetween("createdTime", startDate, endDate);
        List<GroupBrokerage> groupBrokerages = findByCondition(condition);
        if (CollectionUtil.isNotEmpty(groupBrokerages)) {
            //收入的佣金
            Map<Integer, List<GroupBrokerage>> groupBrokeragesIncome = groupBrokerages.stream()
                    .filter(o -> o.getAccountType().equals(GroupBrokerage.Constant.ACCOUNT_TYPE_INCOME))
                    .collect(Collectors.groupingBy(GroupBrokerage::getStatus));
            //提现的佣金
            List<GroupBrokerage> groupBrokeragesDisbursement = groupBrokerages.stream()
                    .filter(o -> o.getAccountType().equals(GroupBrokerage.Constant.ACCOUNT_TYPE_DISBURSEMENT))
                    .collect(Collectors.toList());
            //计算累计提现金额
            totalWithdraw = groupBrokeragesDisbursement.stream().map(GroupBrokerage::getAccount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            //计算待结算佣金
            List<GroupBrokerage> waitEnters = groupBrokeragesIncome.get(GroupBrokerage.Constant.STATUS_WAIT);
            if (CollectionUtil.isNotEmpty(waitEnters)) {
                waitEnter = waitEnters.stream().map(GroupBrokerage::getAccount)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
            }
            //计算待提现佣金  已结算佣金减去已提现
            List<GroupBrokerage> waitWithdraws = groupBrokeragesIncome.get(GroupBrokerage.Constant.STATUS_ENTERED);
            if (CollectionUtil.isNotEmpty(waitWithdraws)) {
                waitWithdraw = waitWithdraws.stream().map(GroupBrokerage::getAccount)
                        .reduce(BigDecimal.ZERO, BigDecimal::add).subtract(totalWithdraw);
            }
        }
        //累计提现
        jsonObject.put("totalWithdraw", totalWithdraw);
        //待结算
        jsonObject.put("waitEnter", waitEnter);
        //待提现
        jsonObject.put("waitWithdraw", waitWithdraw);
        res = jsonObject.toJSONString();
        return res;
    }

    @Override
    public List<GroupBrokerage> findList(String groupId, String startDate, String endDate) {
        //若传入的开始结束时间为空，则对开始和结束时间初始化，开始时间为当前时间向过去偏移一个月
        startDate = ParamUtil.initDateString(startDate, -1);
        endDate = ParamUtil.initDateString(endDate, 0);
        Condition condition = new Condition(GroupBrokerage.class);
        condition.createCriteria().andEqualTo("groupId", groupId).andBetween("createdTime", startDate, endDate);
        return findByCondition(condition);
    }

    @Override
    public void save(Order order, OrderDetail orderDetail) {
        GroupBrokerage groupBrokerage = new GroupBrokerage();
        groupBrokerage.setAccount(orderDetail.getGroupLeaderCommission());
        groupBrokerage.setAccountType(GroupBrokerage.Constant.ACCOUNT_TYPE_INCOME);
        groupBrokerage.setCreatedTime(DateUtil.now());
        groupBrokerage.setGroupId(order.getGroupId());
        groupBrokerage.setModifyTime(DateUtil.now());
        groupBrokerage.setOrderId(order.getOrderId());
        groupBrokerage.setStatus(GroupBrokerage.Constant.STATUS_WAIT);
        groupBrokerage.setType(GroupBrokerage.Constant.TYPE_TRANSACTION);
        String serialNumber = GenSerialNumber.initGenSerialNumber(GenSerialNumber.GROUPBROKERAGE, order.getAppmodelId())
                .nextId();
        groupBrokerage.setSerialNumber(serialNumber);
        groupBrokerage.setAppmodelId(order.getAppmodelId());
        save(groupBrokerage);
    }

    @Override
    public void save(GroupBpavawiceOrder bpavawiceOrder) {
        GroupBrokerage groupBrokerage = new GroupBrokerage();
        groupBrokerage.setAccount(bpavawiceOrder.getOutBpavawice());
        groupBrokerage.setAccountType(GroupBrokerage.Constant.ACCOUNT_TYPE_DISBURSEMENT);
        groupBrokerage.setCreatedTime(DateUtil.now());
        groupBrokerage.setGroupId(bpavawiceOrder.getGroupLeaderId());
        groupBrokerage.setModifyTime(DateUtil.now());
        groupBrokerage.setOrderId(bpavawiceOrder.getGroupBpavawiceOrderId());
        groupBrokerage.setStatus(GroupBrokerage.Constant.STATUS_COMPLETE);
        groupBrokerage.setType(GroupBrokerage.Constant.TYPE_EXTRACT);
        String serialNumber = GenSerialNumber
                .initGenSerialNumber(GenSerialNumber.GROUPBROKERAGE, bpavawiceOrder.getAppmodelId()).nextId();
        groupBrokerage.setSerialNumber(serialNumber);
        groupBrokerage.setAppmodelId(bpavawiceOrder.getAppmodelId());
        save(groupBrokerage);
    }
}
