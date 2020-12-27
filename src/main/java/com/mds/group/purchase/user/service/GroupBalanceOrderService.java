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

package com.mds.group.purchase.user.service;

import com.mds.group.purchase.core.Service;
import com.mds.group.purchase.user.model.GroupBpavawiceOrder;
import com.mds.group.purchase.user.vo.*;
import com.mds.group.purchase.utils.ResultPage;

import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.util.List;


/**
 * Created by CodeGenerator on 2018/11/27.
 *
 * @author pavawi
 */
public interface GroupBpavawiceOrderService extends Service<GroupBpavawiceOrder> {

    /**
     * 提现同意/拒绝
     *
     * @param withdrawMoneyVO the withdraw money vo
     * @return int int
     */
    int withdrawMoney(WithdrawMoneyVO withdrawMoneyVO);

    /**
     * 查询提现记录
     *
     * @param pageNum       the page num
     * @param pageSize      the page size
     * @param searchType    the search type
     * @param groupLeaderId the group leader id
     * @param appmodelId    the appmodel id
     * @return the result page
     */
    ResultPage<List<WithdrawMoneyDetailsVO>> withdrawMoneyDetails(Integer pageNum, Integer pageSize,
                                                                  Integer searchType, String groupLeaderId,
                                                                  String appmodelId);

    /**
     * 查询提现记录详情
     *
     * @param groupBpavawiceOrderId the group bpavawice order id
     * @param appmodelId          the appmodel id
     * @return withdraw money detail vo
     */
    WithdrawMoneyDetailVO withdrawMoneyDetail(Long groupBpavawiceOrderId, String appmodelId);


    /**
     * 团长提现记录备注
     *
     * @param userRemarkVO the user remark vo
     * @return int int
     */
    int withdrawMoneyRemark(RemarkVO userRemarkVO);

    /**
     * 财务管理
     *
     * @param pageNum             the page num
     * @param pageSize            the page size
     * @param searchType          the search type
     * @param groupBpavawiceOrderId the group bpavawice order id
     * @param groupName           the group name
     * @param createTime          the create time
     * @param updateTime          the update time
     * @param appmodelId          the appmodel id
     * @param response            the response
     * @return list list
     */
    List<FinanceManagerVO> findanceManager(Integer pageNum, Integer pageSize, Integer searchType,
                                           String groupBpavawiceOrderId, String groupName,
                                           String createTime, String updateTime, String appmodelId,
                                           HttpServletResponse response);

    /**
     * 查询团长申请中的提现
     *
     * @param groupLeaderId the group leader id
     * @return list list
     */
    List<GroupBpavawiceOrder> findByGroupLeaderId(String groupLeaderId);

    /**
     * Finance manager export.
     *
     * @param groupBpavawiceOrderIds the group bpavawice order ids
     * @param response             the response
     */
    void financeManagerExport(List<Long> groupBpavawiceOrderIds, HttpServletResponse response);

    /**
     * 删除已关闭的财务管理记录
     *
     * @param groupBpavawiceOrderIds the group bpavawice order ids
     */
    void deleteGroupBpavawiceOrder(List<Long> groupBpavawiceOrderIds);

    /**
     * Count cumulative cash withdrawal big decimal.
     *
     * @param groupLeaderId the group leader id
     * @return the big decimal
     */
    BigDecimal countCumulativeCashWithdrawal(String groupLeaderId);
}
