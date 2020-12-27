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

package com.mds.group.purchase.user.controller;

import com.github.pagehelper.PageInfo;
import com.mds.group.purchase.core.CodeMsg;
import com.mds.group.purchase.core.Result;
import com.mds.group.purchase.exception.ServiceException;
import com.mds.group.purchase.user.service.GroupBpavawiceOrderService;
import com.mds.group.purchase.user.service.GroupLeaderService;
import com.mds.group.purchase.user.vo.*;
import com.mds.group.purchase.utils.ResultPage;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

/**
 * The type Group leader controller.
 *
 * @author CodeGenerator
 * @date 2018 /11/27
 */
@RestController
@RequestMapping("/group/leader")
@Api(tags = {"所有接口"})
public class GroupLeaderController {

    @Resource
    private GroupLeaderService groupLeaderService;
    @Resource
    private GroupBpavawiceOrderService groupBpavawiceOrderService;

    /**
     * 申请成为团长|新增团长|修改
     *
     * @param groupLeaderApply the group leader apply
     * @param appmodelId       the appmodel id
     * @return the result
     */
    @PostMapping("/v1/apply/register")
    @ApiOperation(value = "申请成为团长|新增团长|修改", notes = "新增团长必须微信扫码获取到wxuserId", tags = "添加接口")
    public Result groupApplyRegister(@RequestBody @Valid GroupApplyForVO groupLeaderApply,
                                     @ApiParam(value = "小程序模板id", required = true) @NotNull @RequestHeader String appmodelId) {
        groupLeaderApply.setAppmodelId(appmodelId);
        int sum = groupLeaderService.groupApplyRegister(groupLeaderApply);
        if (sum > 0) {
            return Result.success();
        }
        return Result.error(new CodeMsg("申请失败"));
    }

    /**
     * v1.1.9版本申请成为团长
     *
     * @param groupLeaderApply the group leader apply
     * @param appmodelId       the appmodel id
     * @return the result
     */
    @PostMapping("/wx/v1.1.9/apply/register")
    @ApiOperation(value = "v1.1.9版本申请成为团长", tags = "添加接口")
    public Result groupApplyRegisterV119(@RequestBody @Valid GroupApplyRegisterV119VO groupLeaderApply,
                                         @ApiParam(value = "小程序模板id", required = true) @NotNull @RequestHeader String appmodelId) {
        groupLeaderApply.setAppmodelId(appmodelId);
        groupLeaderService.groupApplyRegisterV119(groupLeaderApply);
        return Result.success();
    }

    /**
     * v1.1.9版本编辑团长
     *
     * @param groupLeader the group leader
     * @param appmodelId  the appmodel id
     * @return the result
     */
    @PostMapping("/wx/v1.1.9/update")
    @ApiOperation(value = "v1.1.9版本编辑团长", tags = "添加接口")
    public Result groupUpdateV119(@RequestBody @Valid GroupUpdateV119VO groupLeader,
                                  @ApiParam(value = "小程序模板id", required = true) @NotNull @RequestHeader String appmodelId) {
        groupLeader.setAppmodelId(appmodelId);
        groupLeaderService.groupUpdateV119(groupLeader);
        return Result.success();
    }

    /**
     * v1.1.9版本同意/拒绝团长申请|禁用/启用团长
     *
     * @param groupApplyVO the group apply vo
     * @param appmodelId   the appmodel id
     * @return the result
     */
    @PutMapping("/v1.1.9/apply")
    @ApiOperation(value = "v1.1.9版本同意/拒绝团长申请|禁用/启用团长", tags = "更新接口")
    public Result groupApplyV119(@RequestBody @Valid GroupApplyV119VO groupApplyVO,
                                 @ApiParam(value = "小程序模板id", required = true) @NotNull @RequestHeader String appmodelId) {
        groupApplyVO.setAppmodelId(appmodelId);
        groupLeaderService.groupApplyV119(groupApplyVO);
        return Result.success();
    }

    /**
     * 同意/拒绝团长申请|禁用/启用团长
     *
     * @param groupApplyVO the group apply vo
     * @param appmodelId   the appmodel id
     * @return the result
     */
    @PutMapping("/v1/apply")
    @ApiOperation(value = "同意/拒绝团长申请|禁用/启用团长", tags = "更新接口")
    public Result groupApply(@RequestBody @Valid GroupApplyVO groupApplyVO,
                             @ApiParam(value = "小程序模板id", required = true) @NotNull @RequestHeader String appmodelId) {
        groupApplyVO.setAppmodelId(appmodelId);
        int sum = groupLeaderService.groupApply(groupApplyVO);
        if (sum > 0) {
            return Result.success();
        }
        throw new ServiceException("申请失败");
    }

    /**
     * 团长管理
     *
     * @param pageNum       the page num
     * @param pageSize      the page size
     * @param searchType    the search type
     * @param lineName      the line name
     * @param groupLeaderId the group leader id
     * @param communityName the community name
     * @param area          the area
     * @param appmodelId    the appmodel id
     * @return the result
     */
    @GetMapping("/v1/manager")
    @ApiOperation(value = "团长管理", tags = "查询接口")
    public Result<PageInfo<GroupManagerVO>> searchGroupManager(@RequestParam Integer pageNum,
                                                               @RequestParam Integer pageSize,
                                                               @RequestParam @ApiParam(value = "搜索类型  0-待审核 1-正常 2-拒绝" +
                                                                       " 3-禁用中") Integer searchType,
                                                               @RequestParam(required = false) @ApiParam(value =
                                                                       "线路名") String lineName,
                                                               @RequestParam(required = false) @ApiParam(value = "id") String groupLeaderId,
                                                               @RequestParam(required = false) @ApiParam(value =
                                                                       "小区名称") String communityName,
                                                               @RequestParam(required = false) @ApiParam(value = "地区") String area,
                                                               @RequestHeader @NotBlank(message = "appmodelId不能为空") @Size(max = 27, min = 27, message = "商品标示错误") String appmodelId) {
        List<GroupManagerVO> groupManagerVOS = groupLeaderService
                .searchGroupManager(pageNum, pageSize, searchType, lineName, groupLeaderId, communityName, area,
                        appmodelId);
        PageInfo<GroupManagerVO> pageInfo = new PageInfo<>(groupManagerVOS);
        return Result.success(pageInfo);
    }

    /**
     * 批量删除单个/多个团长|申请记录
     *
     * @param appmodelId the appmodel id
     * @param deleteVO   the delete vo
     * @return the result
     */
    @PutMapping("/v1/delete")
    @ApiOperation(value = "批量删除单个/多个团长|申请记录", tags = "更新接口")
    public Result groupDelete(
            @RequestHeader @NotBlank(message = "appmodelId不能为空") @Size(max = 27, min = 27, message = "商品标示错误") String appmodelId,
            @RequestBody @Valid DeleteVO deleteVO) {
        int sum = groupLeaderService.groupDelete(deleteVO);
        deleteVO.setAppmodelId(appmodelId);
        if (sum > 0) {
            return Result.success();
        }
        return Result.error(new CodeMsg("无数据删除"));
    }

    /**
     * Withdraw money apply result.
     *
     * @param appmodelId      the appmodel id
     * @param withdrawMoneyVO the withdraw money vo
     * @return the result
     */
    @PutMapping("/v1/withdraw/money/apply")
    @ApiOperation(value = "团长提现申请", tags = "更新接口")
    public Result withdrawMoneyApply(
            @RequestHeader @NotBlank(message = "appmodelId不能为空") @Size(max = 27, min = 27, message = "商品标示错误") String appmodelId,
            @RequestBody @Valid WithdrawMoneyApplyVO withdrawMoneyVO) {
        withdrawMoneyVO.setAppmodelId(appmodelId);
        int sum = groupLeaderService.withdrawMoneyApply(withdrawMoneyVO);
        if (sum > 0) {
            return Result.success();
        }
        return Result.error(new CodeMsg("无数据删除"));
    }

    /**
     * Withdraw money result.
     *
     * @param appmodelId      the appmodel id
     * @param withdrawMoneyVO the withdraw money vo
     * @return the result
     */
    @PutMapping("/v1/withdraw/money")
    @ApiOperation(value = "团长提现申请同意|拒绝", tags = "更新接口")
    public Result withdrawMoney(
            @RequestHeader @NotBlank(message = "appmodelId不能为空") @Size(max = 27, min = 27, message = "商品标示错误") String appmodelId,
            @RequestBody @Valid WithdrawMoneyVO withdrawMoneyVO) {
        withdrawMoneyVO.setAppmodelId(appmodelId);
        int sum = groupBpavawiceOrderService.withdrawMoney(withdrawMoneyVO);
        if (sum > 0) {
            return Result.success();
        }
        return Result.error(new CodeMsg("无数据删除"));
    }

    /**
     * Finance manager result.
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
     * @return the result
     */
    @GetMapping("/v1/finance/manager")
    @ApiOperation(value = "财务管理", tags = "查询接口")
    public Result<PageInfo<FinanceManagerVO>> financeManager(@RequestParam Integer pageNum,
                                                             @RequestParam Integer pageSize,
                                                             @RequestParam @ApiParam(value = "搜索类型  0-待审核  1-已到账  " +
                                                                     "2-已关闭 ", required = true) @NotNull Integer searchType,
                                                             @RequestParam(required = false) @ApiParam(value = "订单id") String groupBpavawiceOrderId,
                                                             @RequestParam(required = false) @ApiParam(value = "团长名称") String groupName,
                                                             @RequestParam(required = false) @ApiParam(value = "提交时间") String createTime,
                                                             @RequestParam(required = false) @ApiParam(value = "到账时间") String updateTime,
                                                             @RequestHeader @NotBlank(message = "appmodelId不能为空") @Size(max = 27, min = 27, message = "商品标示错误") String appmodelId,
                                                             HttpServletResponse response) {
        List<FinanceManagerVO> financeManagerVOS = groupBpavawiceOrderService
                .findanceManager(pageNum, pageSize, searchType, groupBpavawiceOrderId, groupName, createTime, updateTime,
                        appmodelId, response);
        PageInfo<FinanceManagerVO> financeManagerVOPageInfo = new PageInfo<>(financeManagerVOS);
        return Result.success(financeManagerVOPageInfo);
    }

    /**
     * Finance manager.
     *
     * @param groupBpavawiceOrderIds the group bpavawice order ids
     * @param response             the response
     */
    @PostMapping("/v1/finance/manager/export")
    @ApiOperation(value = "财务管理导出", tags = "查询接口")
    public void financeManager(@RequestBody @NotNull List<Long> groupBpavawiceOrderIds, HttpServletResponse response) {
        groupBpavawiceOrderService.financeManagerExport(groupBpavawiceOrderIds, response);
    }

    /**
     * Delete group bpavawice order result.
     *
     * @param groupBpavawiceOrderIds the group bpavawice order ids
     * @return the result
     */
    @PostMapping("/v1/del")
    @ApiOperation(value = "删除已关闭的提现申请记录", tags = "查询接口")
    public Result deleteGroupBpavawiceOrder(@RequestBody @NotNull List<Long> groupBpavawiceOrderIds) {
        groupBpavawiceOrderService.deleteGroupBpavawiceOrder(groupBpavawiceOrderIds);
        return Result.success();
    }


    /**
     * Withdraw money details result.
     *
     * @param pageNum       the page num
     * @param pageSize      the page size
     * @param searchType    the search type
     * @param groupLeaderId the group leader id
     * @param appmodelId    the appmodel id
     * @return the result
     */
    @GetMapping("/v1/withdraw/money/details")
    @ApiOperation(value = "提现记录查询", tags = "查询接口")
    public Result<ResultPage<List<WithdrawMoneyDetailsVO>>> withdrawMoneyDetails(@RequestParam Integer pageNum,
                                                                                 @RequestParam Integer pageSize,
                                                                                 @RequestParam @ApiParam(value =
                                                                                         "搜索类型  0-全部记录 1-到账记录 ",
                                                                                         required = true) @NotNull Integer searchType,
                                                                                 @RequestParam @ApiParam(value =
                                                                                         "团长id", required = true) String groupLeaderId,
                                                                                 @RequestHeader @NotBlank(message =
                                                                                         "appmodelId不能为空") @Size(max
                                                                                         = 27, min = 27, message =
                                                                                         "商品标示错误") String appmodelId) {
        ResultPage<List<WithdrawMoneyDetailsVO>> listResultPage = groupBpavawiceOrderService
                .withdrawMoneyDetails(pageNum, pageSize, searchType, groupLeaderId, appmodelId);
        return Result.success(listResultPage);
    }

    /**
     * Withdraw money detail result.
     *
     * @param groupBpavawiceOrderId the group bpavawice order id
     * @param appmodelId          the appmodel id
     * @return the result
     */
    @GetMapping("/v1/withdraw/money/detail")
    @ApiOperation(value = "提现记录详情", tags = "查询接口")
    public Result<WithdrawMoneyDetailVO> withdrawMoneyDetail(
            @RequestParam(required = false) @ApiParam(value = "审核id") Long groupBpavawiceOrderId,
            @RequestHeader @NotBlank(message = "appmodelId不能为空") @Size(max = 27, min = 27, message = "商品标示错误") String appmodelId) {
        WithdrawMoneyDetailVO withdrawMoneyDetailVO = groupBpavawiceOrderService
                .withdrawMoneyDetail(groupBpavawiceOrderId, appmodelId);
        return Result.success(withdrawMoneyDetailVO);
    }


    /**
     * Group backstage result.
     *
     * @param groupLeaderId the group leader id
     * @param searchType    the search type
     * @param pageNum       the page num
     * @param pageSize      the page size
     * @param appmodelId    the appmodel id
     * @return the result
     */
    @GetMapping("/v1/group/order")
    @ApiOperation(value = "团长后台", tags = "查询接口")
    public Result<ResultPage<List<GroupBackstageVO>>> groupBackstage(
            @RequestParam @ApiParam(value = "团长id", required = true) @NotBlank String groupLeaderId,
            @RequestParam @ApiParam(value = "搜索类型  0-待提货  1-历史订单", required = true) @NotBlank Integer searchType,
            @RequestParam Integer pageNum, @RequestParam Integer pageSize,
            @RequestHeader @NotBlank(message = "appmodelId不能为空") @Size(max = 27, min = 27, message = "商品标示错误") String appmodelId) {
        ResultPage<List<GroupBackstageVO>> resultPage = groupLeaderService
                .groupBackstage(groupLeaderId, searchType, appmodelId, pageNum, pageSize);
        return Result.success(resultPage);
    }

    /**
     * Group postil result.
     *
     * @param withdrawMoneyVO the withdraw money vo
     * @return the result
     */
    @PutMapping("/v1/postil")
    @ApiOperation(value = "订单批注", tags = "更新接口")
    public Result groupPostil(@RequestBody @Valid GroupPostilVO withdrawMoneyVO) {
        int sum = groupLeaderService.groupPostil(withdrawMoneyVO);
        if (sum > 0) {
            return Result.success();
        }
        return Result.error(new CodeMsg("无数据删除"));
    }

    /**
     * Withdraw money remark result.
     *
     * @param userRemarkVO the user remark vo
     * @return the result
     */
    @PutMapping("/v1/withdraw/money/remark")
    @ApiOperation(value = "团长提现记录备注", tags = "更新接口")
    public Result withdrawMoneyRemark(@RequestBody @Valid RemarkVO userRemarkVO) {
        int sum = groupBpavawiceOrderService.withdrawMoneyRemark(userRemarkVO);
        if (sum > 0) {
            return Result.success();
        }
        return Result.error(new CodeMsg("无数据删除"));
    }

}
