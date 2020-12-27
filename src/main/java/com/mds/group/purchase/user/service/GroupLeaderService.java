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
import com.mds.group.purchase.order.result.MyCommissionResult;
import com.mds.group.purchase.user.model.GroupLeader;
import com.mds.group.purchase.user.vo.*;
import com.mds.group.purchase.utils.ResultPage;

import java.util.List;


/**
 * The interface Group leader service.
 *
 * @author CodeGenerator
 * @date 2018 /11/27
 */
public interface GroupLeaderService extends Service<GroupLeader> {

    /**
     * 团长注册
     *
     * @param groupLeaderApply the group leader apply
     * @return the int
     */
    int groupApplyRegister(GroupApplyForVO groupLeaderApply);

    /**
     * 同意或禁用
     *
     * @param groupApplyVO the group apply vo
     * @return the int
     */
    int groupApply(GroupApplyVO groupApplyVO);

    /**
     * 批量删除
     *
     * @param deleteVO the delete vo
     * @return the int
     */
    int groupDelete(DeleteVO deleteVO);

    /**
     * 查询团长
     *
     * @param pageNum       页码
     * @param pageSize      页面大小
     * @param searchType    搜索类型  0-待提货  1-历史订单
     * @param lineName      线路名称
     * @param groupLeaderId 团长id
     * @param communityName 小区名称
     * @param area          地区
     * @param appmodelId    模板id
     * @return list list
     */
    List<GroupManagerVO> searchGroupManager(Integer pageNum, Integer pageSize, Integer searchType, String lineName,
                                            String groupLeaderId,
                                            String communityName, String area, String appmodelId);

    /**
     * 团长提现申请
     *
     * @param withdrawMoneyVO the withdraw money vo
     * @return the int
     */
    int withdrawMoneyApply(WithdrawMoneyApplyVO withdrawMoneyVO);


    /**
     * 团长后台查询
     *
     * @param groupLeaderId 团长id
     * @param searchType    搜索类型  0-待提货  1-历史订单
     * @param appmodelId    模板id
     * @param pageNum       页码
     * @param pageSize      页面大小
     * @return the result page
     */
    ResultPage<List<GroupBackstageVO>> groupBackstage(String groupLeaderId, Integer searchType, String appmodelId,
                                                      Integer pageNum, Integer pageSize);

    /**
     * 团长批注订单
     *
     * @param withdrawMoneyVO the withdraw money vo
     * @return the int
     */
    int groupPostil(GroupPostilVO withdrawMoneyVO);

    /**
     * 根据团长id查询团长信息
     *
     * @param groupleaderIdList 团长id集合
     * @return the list
     */
    List<GroupLeader> findByGroupleaderIds(List<String> groupleaderIdList);

    /**
     * 根据app modelid查询未删除的团长信息
     *
     * @param appmodelId the appmodel id
     * @return the list
     */
    List<GroupLeader> findByAppmodelId(String appmodelId);

    /**
     * 查询用户团长信息
     *
     * @param wxuserId 用户id
     * @return the group leader
     */
    GroupLeader findByWxuserId(Long wxuserId);

    /**
     * 查询各种状态的团长
     *
     * @param status     团长状态 0-待审核 1-正常 2-拒绝 3-禁用中
     * @param appmodelId 模板id
     * @return the list
     */
    List<GroupLeader> findByStatusAppmodelId(Integer status, String appmodelId);

    /**
     * 获取未删除的团长
     *
     * @param communityId 小区id
     * @return the group leader
     */
    GroupLeader findBySoleGroupLeader(Long communityId);

    /**
     * 版本申请团长
     *
     * @param groupLeaderApply the group leader apply
     * @since v1.1.9
     */
    void groupApplyRegisterV119(GroupApplyRegisterV119VO groupLeaderApply);

    /**
     * 商家审核操作团长
     *
     * @param groupApplyVO the group apply vo
     * @since v1.1.9
     */
    void groupApplyV119(GroupApplyV119VO groupApplyVO);

    /**
     * 更新团长信息
     *
     * @param groupLeader the group leader
     * @since v1.1.9
     */
    void groupUpdateV119(GroupUpdateV119VO groupLeader);

    /**
     * 查询我的客户数量
     *
     * @param wxuserId the wxuser id
     * @return integer integer
     */
    Integer findCustomersByWxUserId(Long wxuserId);

    /**
     * 查询我的佣金
     *
     * @param groupLeaderId the group leader id
     * @return my commission result
     */
    MyCommissionResult groupLeaderCommission(Long groupLeaderId);
}
