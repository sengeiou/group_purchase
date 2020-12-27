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

import com.mds.group.purchase.core.Result;
import com.mds.group.purchase.core.ResultGenerator;
import com.mds.group.purchase.order.result.AfterSaleApplyNumberResult;
import com.mds.group.purchase.order.result.AfterSaleOrderDetailResult;
import com.mds.group.purchase.order.result.MembersResult;
import com.mds.group.purchase.order.service.AfterSaleOrderService;
import com.mds.group.purchase.order.service.ReceiptBillDetailService;
import com.mds.group.purchase.order.vo.AfterSaleOrderUpdateVO;
import com.mds.group.purchase.order.vo.ApplyAfterSaleOrderVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotBlank;
import java.util.List;

/**
 * The type After sale order controller.
 *
 * @author Administrator
 */
@RestController
@RequestMapping("order/after/sale/v1")
@Validated
@Api(tags = "订单售后")
public class AfterSaleOrderController {
    @Autowired
    private AfterSaleOrderService afterSaleOrderService;
    @Autowired
    private ReceiptBillDetailService receiptBillDetailService;

    /**
     * 用户申请售后单
     *
     * @param applyAfterSaleOrderVO the apply after sale order vo
     * @return the result
     */
    @ApiOperation(value = "用户申请售后单")
    @ApiResponses({@ApiResponse(code = 200, message = "success"),})
    @PostMapping("/user/apply")
    public Result userApply(@RequestBody @Validated ApplyAfterSaleOrderVO applyAfterSaleOrderVO) {
        afterSaleOrderService.userApply(applyAfterSaleOrderVO);
        return Result.success();
    }

    /**
     * 售后单详情
     *
     * @param applyAfterSaleOrderId the apply after sale order id
     * @return the info
     */
    @ApiOperation(value = "售后单详情")
    @ApiResponses({@ApiResponse(code = 200, message = "success", response = AfterSaleOrderDetailResult.class),})
    @GetMapping("/info")
    public Result getInfo(@RequestParam @NotBlank String applyAfterSaleOrderId) {
        AfterSaleOrderDetailResult afterSaleOrder = afterSaleOrderService.findDetailById(applyAfterSaleOrderId);
        return Result.success(afterSaleOrder);
    }

    /**
     * 售后单申请次数
     *
     * @param orderId the order id
     * @return the result
     */
    @ApiOperation(value = "售后单申请次数")
    @ApiResponses({@ApiResponse(code = 200, message = "success", response = AfterSaleApplyNumberResult.class),})
    @GetMapping("/apply/number")
    public Result applyNumber(@RequestParam @Validated Long orderId) {
        AfterSaleApplyNumberResult afterSaleApplyNumberResult = afterSaleOrderService.applyNumber(orderId);
        return Result.success(afterSaleApplyNumberResult);
    }

    /**
     * 用户取消申请
     *
     * @param applyAfterSaleOrderId the apply after sale order id
     * @return the result
     */
    @ApiOperation(value = "用户取消申请")
    @ApiResponses({@ApiResponse(code = 200, message = "success"),})
    @PostMapping("/user/cancel")
    public Result userCancel(@RequestBody @Validated String applyAfterSaleOrderId) {
        afterSaleOrderService.cancel(applyAfterSaleOrderId, false, 1);
        return Result.success();
    }

    /**
     * 用户确认退货
     *
     * @param applyAfterSaleOrderId the apply after sale order id
     * @return the result
     */
    @ApiOperation(value = "用户确认退货")
    @ApiResponses({@ApiResponse(code = 200, message = "success"),})
    @PostMapping("/user/return")
    public Result userReturn(@RequestBody @Validated String applyAfterSaleOrderId) {
        afterSaleOrderService.userReturn(applyAfterSaleOrderId);
        return Result.success();
    }

    /**
     * 售后订单查看团员
     *
     * @param applyAfterSaleOrderId the apply after sale order id
     * @return the result
     */
    @ApiOperation(value = "售后订单查看团员")
    @ApiResponses({@ApiResponse(code = 200, message = "success", response = MembersResult.class),})
    @GetMapping("/member/list")
    public Result memberList(@RequestParam @NotBlank String applyAfterSaleOrderId) {
        List<MembersResult> membersResults = afterSaleOrderService.getMemberListById(applyAfterSaleOrderId);
        return ResultGenerator.genSuccessResult(membersResults);
    }

    /**
     * 商家拒绝申请
     *
     * @param applyAfterSaleOrderVO the apply after sale order vo
     * @return the result
     */
    @ApiOperation(value = "商家拒绝申请")
    @ApiResponses({@ApiResponse(code = 200, message = "success")})
    @PostMapping("/seller/refusal")
    public Result sellerRefusal(@RequestBody @Validated AfterSaleOrderUpdateVO applyAfterSaleOrderVO) {
        afterSaleOrderService.sellerRefusal(applyAfterSaleOrderVO);
        return Result.success();
    }

    /**
     * 商家同意申请
     *
     * @param applyAfterSaleOrderVO the apply after sale order vo
     * @return the result
     */
    @ApiOperation(value = "商家同意申请")
    @ApiResponses({@ApiResponse(code = 200, message = "success"),})
    @PostMapping("/seller/approve")
    public Result sellerApprove(@RequestBody @Validated AfterSaleOrderUpdateVO applyAfterSaleOrderVO) {
        afterSaleOrderService.sellerApprove(applyAfterSaleOrderVO, false);
        return Result.success();
    }

    /**
     * 更新团长佣金
     *
     * @return the result
     */
    @ApiOperation(value = "更新团长佣金")
    @ApiResponses({@ApiResponse(code = 200, message = "success"),})
    @PostMapping("/commission/update")
    public Result updateGroupLeaderCommission() {
        receiptBillDetailService.updateGroupLeaderCommission();
        return Result.success();
    }

}
