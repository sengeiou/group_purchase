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

package com.mds.group.purchase.order.controller;

import com.github.pagehelper.PageInfo;
import com.mds.group.purchase.core.Result;
import com.mds.group.purchase.core.ResultGenerator;
import com.mds.group.purchase.order.result.*;
import com.mds.group.purchase.order.service.AfterSaleOrderService;
import com.mds.group.purchase.order.service.OrderService;
import com.mds.group.purchase.order.service.ReceiptBillDetailService;
import com.mds.group.purchase.order.service.ReceiptBillService;
import com.mds.group.purchase.order.vo.*;
import com.mds.group.purchase.user.model.Wxuser;
import com.mds.group.purchase.user.service.GroupLeaderService;
import com.mds.group.purchase.user.service.WxuserService;
import com.mds.group.purchase.utils.PageUtil;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The type Leader workbench controller.
 *
 * @author Administrator
 */
@RestController
@RequestMapping("group/leader/workbench/v1")
@Validated
@Api(tags = "团长工作台")
public class LeaderWorkbenchController {

    @Autowired
    private OrderService orderService;
    @Autowired
    private WxuserService wxuserService;
    @Autowired
    private ReceiptBillService receiptBillService;
    @Autowired
    private GroupLeaderService groupLeaderService;
    @Autowired
    private AfterSaleOrderService afterSaleOrderService;
    @Autowired
    private ReceiptBillDetailService receiptBillDetailService;

    /**
     * 获取首页信息
     *
     * @param wxuserId the wxuser id
     * @return the result
     */
    @ApiOperation(value = "获取首页信息")
    @ApiResponses({@ApiResponse(code = 200, message = "success", response = WorkbenchResult.class),})
    @GetMapping("/index")
    public Result<WorkbenchResult> workbenchSummary(@ApiParam("用户id") @RequestParam Long wxuserId) {
        WorkbenchResult workbenchResult = orderService.workbenchSummary(wxuserId);
        return Result.success(workbenchResult);
    }


    /**
     * 查询团长签收单
     *
     * @param searchReceiptBillVO the search receipt bill vo
     * @return the result
     */
    @ApiOperation(value = "查询团长签收单", response = ReceiptBillResult.class)
    @GetMapping("/receipt/bill/list")
    public Result<PageInfo> receiptBillList(@Validated SearchReceiptBillVO searchReceiptBillVO) {
        List<ReceiptBillResult> receiptBill = receiptBillService.searchReceiptBill(searchReceiptBillVO);
        PageInfo<ReceiptBillResult> pageInfo = PageUtil.pageUtil(searchReceiptBillVO.getPage(),
                searchReceiptBillVO.getSize(), receiptBill);
        return ResultGenerator.genSuccessResult(pageInfo);
    }

    /**
     * 团长签收单查看团员
     *
     * @param receiptBillInfoVO the receipt bill info vo
     * @return the result
     */
    @ApiOperation(value = "团长签收单查看团员", response = MyTeamMembersResult.class)
    @GetMapping("/receipt/bill/info")
    public Result receiptBill(@Validated ReceiptBillInfoVO receiptBillInfoVO) {
        List<MyTeamMembersResult> myTeamMembersResults = receiptBillDetailService.getInfo(receiptBillInfoVO);
        return ResultGenerator.genSuccessResult(myTeamMembersResults);
    }

    /**
     * 团长签收签收单
     *
     * @param billId the bill id
     * @return the result
     */
    @ApiOperation(value = "团长签收签收单")
    @PostMapping("/receipt/bill/receipt")
    public Result receiptBill(@RequestBody @NotNull Long billId) {
        receiptBillService.receiptByBillId(billId);
        return ResultGenerator.genSuccessResult();
    }

    /**
     * 团长签收商品
     *
     * @param receiptGoodsVO the receipt goods vo
     * @return the result
     */
    @ApiOperation(value = "团长签收商品")
    @PostMapping("/receipt/bill/goods/receipt")
    public Result receiptGoods(@RequestBody @Validated ReceiptGoodsVO receiptGoodsVO) {
        receiptBillService.receiptByOrderIds(receiptGoodsVO.getReceiptBillDetailId(), receiptGoodsVO.getOrderIds());
        return Result.success();
    }

    /**
     * 团长申请售后单
     *
     * @param batchApplyAfterSaleOrderVO the batch apply after sale order vo
     * @return the result
     */
    @ApiOperation(value = "团长申请售后单")
    @ApiResponses({@ApiResponse(code = 200, message = "success"),})
    @PostMapping("/after/sale/apply")
    public Result leaderApply(@RequestBody @Validated BatchApplyAfterSaleOrderVO batchApplyAfterSaleOrderVO) {
        afterSaleOrderService.leaderApply(batchApplyAfterSaleOrderVO);
        return Result.success();
    }

    /**
     * 团长取消申请
     *
     * @param applyAfterSaleOrderId the apply after sale order id
     * @return the result
     */
    @ApiOperation(value = "团长取消申请")
    @ApiResponses({@ApiResponse(code = 200, message = "success"),})
    @PostMapping("/after/sale/cancel")
    public Result leaderCancel(@RequestBody @Validated String applyAfterSaleOrderId) {
        afterSaleOrderService.cancel(applyAfterSaleOrderId, false, 2);
        return Result.success();
    }

    /**
     * 售后订单列表
     *
     * @param searchAfterSaleOrderVo the search after sale order vo
     * @return the result
     */
    @ApiOperation(value = "售后订单列表")
    @ApiResponses({@ApiResponse(code = 200, message = "success", response = OrderResult.class),})
    @GetMapping("/after/sale/list")
    public Result afterSaleList(@Validated SearchAfterSaleOrderVO searchAfterSaleOrderVo) {
        List<AfterSaleOrderResult> afterSaleOrderList =
                afterSaleOrderService.searchAfterSaleOrder(searchAfterSaleOrderVo);
        PageInfo pageInfo = PageUtil.pageUtil(searchAfterSaleOrderVo.getPage(), searchAfterSaleOrderVo.getSize(),
                afterSaleOrderList);
        return ResultGenerator.genSuccessResult(pageInfo);
    }


    /**
     * 团长确认是否收到退货
     *
     * @param returnConfirmVO the return confirm vo
     * @return the result
     */
    @ApiOperation(value = "团长确认是否收到退货")
    @ApiResponses({@ApiResponse(code = 200, message = "success")})
    @PostMapping("/after/sale/return")
    public Result afterSaleReturn(@RequestBody @Validated ReturnConfirmVO returnConfirmVO) {
        if (returnConfirmVO.getType().equals(0)) {
            afterSaleOrderService.leaderApprove(returnConfirmVO.getApplyAfterSaleOrderId(), false);
        } else {
            afterSaleOrderService.leaderRefusal(returnConfirmVO.getApplyAfterSaleOrderId());
        }
        return Result.success();
    }


    /**
     * 订单核销客户列表
     *
     * @param wxuserId the wxuser id
     * @param search   the search
     * @return the result
     */
    @ApiOperation(value = "订单核销客户列表")
    @ApiResponses({@ApiResponse(code = 200, message = "success", response = CustomerResult.class),})
    @GetMapping("/order/customer/list")
    public Result customerList(@RequestParam @NotNull Long wxuserId, @RequestParam(required = false) String search) {
        List<CustomerResult> customerList = afterSaleOrderService.customerList(wxuserId, search);
        return Result.success(customerList);
    }

    /**
     * 客户订单信息
     *
     * @param wxuserId      the wxuser id
     * @param groupLeaderId the group leader id
     * @param type          the type
     * @return the result
     */
    @ApiOperation(value = "客户订单信息")
    @ApiResponses({@ApiResponse(code = 200, message = "success", response = OrderResult.class),})
    @GetMapping("/order/info")
    public Result customerOrderInfo(@ApiParam("客户的wxuserId") @RequestParam("wxuserId") @NotNull Long wxuserId,
                                    @ApiParam("团长ID") @RequestParam("groupLeaderId") @NotBlank String groupLeaderId,
                                    @ApiParam("订单类型、0:待发货,1:待提货") @RequestParam("type") @NotNull Integer type) {
        Map<String, Object> result = new HashMap<>();
        Wxuser wxuser = wxuserService.findById(wxuserId);
        List<OrderResult> orderResults = afterSaleOrderService.customerOrderInfo(wxuserId, groupLeaderId, type);
        result.put("orderResults", orderResults);
        result.put("user", wxuser);
        return Result.success(result);
    }

    /**
     * 订单核销
     *
     * @param orderId the order id
     * @return the result
     */
    @ApiOperation(value = "订单核销")
    @ApiResponses({@ApiResponse(code = 200, message = "success"),})
    @GetMapping("/order/pickUp")
    public Result pickUpOrder(@RequestParam @NotNull Long orderId) {
        orderService.pickUp(orderId);
        return Result.success();
    }

    /**
     * 我的佣金首页
     *
     * @param wxuserId the wxuser id
     * @return the result
     */
    @ApiOperation(value = "我的佣金首页")
    @ApiResponses({@ApiResponse(code = 200, message = "success", response = MyCommissionResult.class),})
    @GetMapping("/commission/index")
    public Result commission(@ApiParam("团长wxuserId") @RequestParam("wxuserId") @NotNull Long wxuserId) {
        MyCommissionResult myCommissionResult = groupLeaderService.groupLeaderCommission(wxuserId);
        return Result.success(myCommissionResult);
    }


    /**
     * 待结算的订单
     *
     * @param wxuserId the wxuser id
     * @return the result
     */
    @ApiOperation(value = "待结算的订单")
    @ApiResponses({@ApiResponse(code = 200, message = "success", response = NotSettlementCommissionResult.class),})
    @GetMapping("/commission/order/notSettlement/")
    public Result notSettlement(@ApiParam("团长wxuserId") @RequestParam("wxuserId") @NotNull Long wxuserId) {
        List<NotSettlementCommissionResult> notSettlementCommissionResults =
                orderService.findGroupLeaderNotSettlementOrder(wxuserId);
        return Result.success(notSettlementCommissionResults);
    }
}
