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

package com.mds.group.purchase.user.dao;

import com.mds.group.purchase.core.Mapper;
import com.mds.group.purchase.user.model.GroupBpavawiceOrder;
import com.mds.group.purchase.user.vo.FinanceManagerVO;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * The interface Group bpavawice order mapper.
 *
 * @author pavawi
 */
public interface GroupBpavawiceOrderMapper extends Mapper<GroupBpavawiceOrder> {

    /**
     * 查询某个团长提现记录
     *
     * @param searchType    the search type
     * @param groupLeaderId the group leader id
     * @param appmodelId    the appmodel id
     * @return list list
     */
    List<GroupBpavawiceOrder> selectWithdrawMoneyDetails(@Param("searchType") Integer searchType, @Param("groupLeaderId"
    ) String groupLeaderId, @Param("appmodelId") String appmodelId);

    /**
     * 财务管理
     *
     * @param paramMap the param map
     * @return list list
     */
    List<FinanceManagerVO> selectFinanceManager(Map<String, Object> paramMap);

    /**
     * 查询团长申请中的提现
     *
     * @param groupLeaderId the group leader id
     * @return list list
     */
    List<GroupBpavawiceOrder> selectByGroupLeaderId(@Param("groupLeaderId") String groupLeaderId);

    /**
     * 查询团长累计提现
     *
     * @param groupLeaderId the group leader id
     * @return big decimal
     */
    BigDecimal countCumulativeCashWithdrawal(@Param("groupLeaderId") String groupLeaderId);
}