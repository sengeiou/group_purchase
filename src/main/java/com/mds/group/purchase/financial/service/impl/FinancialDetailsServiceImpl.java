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
import com.mds.group.purchase.financial.model.FinancialDetails;
import com.mds.group.purchase.financial.service.FinancialDetailsService;
import com.mds.group.purchase.order.model.Order;
import com.mds.group.purchase.user.model.GroupBpavawiceOrder;
import com.mds.group.purchase.user.model.Wxuser;
import com.mds.group.purchase.utils.GenSerialNumber;
import com.mds.group.purchase.utils.ParamUtil;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Condition;
import tk.mybatis.mapper.entity.Example;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


/**
 * The type Financial details service.
 *
 * @author shuke
 * @date 2019 /04/25
 */
@Service
public class FinancialDetailsServiceImpl extends AbstractService<FinancialDetails> implements FinancialDetailsService {


    @Override
    public List<FinancialDetails> findFinancialDetails(String startDate, String endDate, Integer type,
                                                       Integer accoutType, String appmodelId) {
        //若传入的开始结束时间为空，则对开始和结束时间初始化，开始时间为当前时间向过去偏移一个月
        startDate = ParamUtil.initDateString(startDate, -1);
        endDate = ParamUtil.initDateString(endDate, 0);
        Condition condition = new Condition(FinancialDetails.class);
        Example.Criteria criteria = condition.createCriteria();
        if (type != 0) {
            criteria.andEqualTo("type", type);
        }
        if (accoutType != 0) {
            criteria.andEqualTo("accountType", accoutType);
        }
        criteria.andEqualTo("appmodelId", appmodelId).andBetween("createdTime", startDate, endDate);
        condition.and(criteria);
        return findByCondition(condition);
    }

    @Override
    public String collectFinancial(String appmodelId, Integer type, Integer accoutType, String startDate,
                                   String endDate) {
        //若传入的开始结束时间为空，则对开始和结束时间初始化，开始时间为当前时间向过去偏移一个月
        startDate = ParamUtil.initDateString(startDate, -1);
        endDate = ParamUtil.initDateString(endDate, 0);
        String res;
        BigDecimal totalIncome = BigDecimal.ZERO.setScale(2, BigDecimal.ROUND_HALF_UP);
        BigDecimal totalDisbursement = BigDecimal.ZERO.setScale(2, BigDecimal.ROUND_HALF_UP);
        int totalIncomeNum = 0;
        int totalDisNum = 0;
        JSONObject jsonObject = new JSONObject();
        Condition condition = new Condition(FinancialDetails.class);
        Example.Criteria criteria = condition.createCriteria();

        if (type != 0) {
            criteria = condition.createCriteria().andEqualTo("type", type);
        }
        if (accoutType != 0) {
            criteria.andEqualTo("accountType", accoutType);
        }
        criteria.andEqualTo("appmodelId", appmodelId).andBetween("createdTime", startDate, endDate);
        condition.and(criteria);
        List<FinancialDetails> financialDetails = findByCondition(condition);
        if (CollectionUtil.isNotEmpty(financialDetails)) {
            Map<Integer, List<FinancialDetails>> map = financialDetails.stream()
                    .collect(Collectors.groupingBy(FinancialDetails::getAccountType));
            //收入
            List<FinancialDetails> financialDetailsIncome = map.get(FinancialDetails.Constant.ACCOUNT_TYPE_INCOME);
            if (CollectionUtil.isNotEmpty(financialDetailsIncome)) {
                totalIncome = financialDetailsIncome.stream().map(FinancialDetails::getAccount)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                totalIncomeNum = financialDetailsIncome.size();
            }
            //支出
            List<FinancialDetails> financialDetailsDisbursement = map
                    .get(FinancialDetails.Constant.ACCOUNT_TYPE_DISBURSEMENT);
            if (CollectionUtil.isNotEmpty(financialDetailsDisbursement)) {
                totalDisbursement = financialDetailsDisbursement.stream().map(FinancialDetails::getAccount)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                totalDisNum = financialDetailsDisbursement.size();
            }
        }
        //累计收入
        jsonObject.put("totalIncome", totalIncome);
        //收入单数量
        jsonObject.put("totalIncomNum", totalIncomeNum);
        //累计支出
        jsonObject.put("totalDisbursement", totalDisbursement);
        //支出单数量
        jsonObject.put("totalDisNum", totalDisNum);
        res = jsonObject.toJSONString();
        return res;
    }


    @Override
    public void save(@NotNull Order order, boolean payStatus) {
        FinancialDetails financialDetails = new FinancialDetails();
        financialDetails.setAccount(order.getPayFee());
        financialDetails.setOrderId(order.getOrderId());
        financialDetails.setCreatedTime(DateUtil.now());
        financialDetails.setGroupId(order.getGroupId());
        financialDetails.setModifyTime(DateUtil.now());
        financialDetails.setWxuserId(order.getWxuserId());
        financialDetails.setSerialNumber(
                GenSerialNumber.initGenSerialNumber(GenSerialNumber.FINANCIAL, order.getAppmodelId()).nextId());
        financialDetails.setAppmodelId(order.getAppmodelId());
        if (payStatus) {
            //付款成功后插入对账单记录
            financialDetails.setAccountType(FinancialDetails.Constant.ACCOUNT_TYPE_INCOME);
            financialDetails.setType(FinancialDetails.Constant.TYPE_TRANSACTION);
        } else {
            //付款回调处理失败后插入对账单记录
            financialDetails.setAccountType(FinancialDetails.Constant.ACCOUNT_TYPE_DISBURSEMENT);
            financialDetails.setType(FinancialDetails.Constant.TYPE_REFUND);
        }
        save(financialDetails);
    }

    @Override
    public void save(@NotNull GroupBpavawiceOrder groupBpavawiceOrder, @NotNull Wxuser wxuser) {
        if (groupBpavawiceOrder != null && wxuser != null) {
            //提现成功后插入对账单记录
            FinancialDetails financialDetails = new FinancialDetails();
            financialDetails.setAccount(groupBpavawiceOrder.getOutBpavawice());
            financialDetails.setAccountType(FinancialDetails.Constant.ACCOUNT_TYPE_DISBURSEMENT);
            financialDetails.setAppmodelId(groupBpavawiceOrder.getAppmodelId());
            financialDetails.setCreatedTime(DateUtil.now());
            financialDetails.setGroupId(groupBpavawiceOrder.getGroupLeaderId());
            financialDetails.setOrderId(groupBpavawiceOrder.getGroupBpavawiceOrderId());
            financialDetails.setModifyTime(DateUtil.now());
            financialDetails.setSerialNumber(
                    GenSerialNumber.initGenSerialNumber(GenSerialNumber.FINANCIAL, groupBpavawiceOrder.getAppmodelId())
                            .nextId());
            financialDetails.setWxuserId(wxuser.getWxuserId());
            financialDetails.setType(FinancialDetails.Constant.TYPE_EXTRACT);
            save(financialDetails);
        }
    }


}
