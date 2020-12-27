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
import com.mds.group.purchase.order.result.ReceiptBillResult;
import com.mds.group.purchase.order.service.GoodsSortingOrderService;
import com.mds.group.purchase.order.service.LineSortingOrderService;
import com.mds.group.purchase.order.service.ReceiptBillService;
import com.mds.group.purchase.order.service.SendBillService;
import com.mds.group.purchase.order.vo.GoodsSortingOrderViewVO;
import com.mds.group.purchase.order.vo.LineSortingOrderViewVo;
import com.mds.group.purchase.order.vo.SendbillFindVO;
import com.mds.group.purchase.utils.PageUtil;
import com.mds.group.purchase.utils.ResultPage;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * The type Sorting order controller.
 *
 * @author Administrator
 */
@RestController
@Validated
@Api(tags = "所有接口")
public class SortingOrderController {

    @Resource
    private SendBillService sendBillService;
    @Resource
    private ReceiptBillService receiptBillService;
    @Resource
    private LineSortingOrderService lineSortingOrderService;
    @Resource
    private GoodsSortingOrderService goodsSortingOrderService;


    /**
     * 查询线路分拣单
     *
     * @param appmodelId the appmodel id
     * @param pageNum    the page num
     * @param pageSize   the page size
     * @param sendBillId the send bill id
     * @return the result
     * @since v1.2
     */
    @ApiOperation(value = "查询线路分拣单", tags = "v1.2版本接口")
    @GetMapping("line/sorting/order/v2/find")
    public Result<ResultPage<List<LineSortingOrderViewVo>>> lineSortingOrder(@RequestHeader @NotBlank String appmodelId,
                                                                             @RequestParam @NotNull @Min(1) @ApiParam(value = "页数") Integer pageNum,
                                                                             @RequestParam @NotNull @Min(1) @ApiParam(value = "条数") Integer pageSize,
                                                                             @RequestParam(defaultValue = "0") @NotNull @ApiParam(value = "发货单id,为0查询距离当前时间最近的分拣单") Long sendBillId) {
        List<LineSortingOrderViewVo> lineSortingOrderViewVos = lineSortingOrderService
                .sortingOrder(appmodelId, sendBillId);
        PageInfo<LineSortingOrderViewVo> pageInfo = PageUtil.pageUtil(pageNum, pageSize, lineSortingOrderViewVos);
        ResultPage<List<LineSortingOrderViewVo>> resultPage = new ResultPage<>();
        resultPage.setTotle(pageInfo.getTotal());
        resultPage.setList(pageInfo.getList());
        return Result.success(resultPage);
    }

    /**
     * 查询商品分拣单
     *
     * @param appmodelId the appmodel id
     * @param pageNum    the page num
     * @param pageSize   the page size
     * @param sendBillId the send bill id
     * @return the result
     * @since v1.2
     */
    @ApiOperation(value = "查询商品分拣单", tags = "v1.2版本接口")
    @GetMapping("goods/sorting/order/v2/find")
    public Result<ResultPage<List<GoodsSortingOrderViewVO>>> goodsSortingOrder(
            @RequestHeader @NotBlank String appmodelId,
            @RequestParam @NotNull @Min(1) @ApiParam(value = "页数") Integer pageNum,
            @RequestParam @NotNull @Min(1) @ApiParam(value = "条数") Integer pageSize,
            @RequestParam(defaultValue = "0") @NotNull @ApiParam(value = "发货单id,为0查询距离当前时间最近的分拣单") Long sendBillId) {
        List<GoodsSortingOrderViewVO> goodsSortingOrderViewVOS = goodsSortingOrderService
                .goodsSortingOrder(appmodelId, sendBillId);
        PageInfo<GoodsSortingOrderViewVO> pageInfo = PageUtil.pageUtil(pageNum, pageSize, goodsSortingOrderViewVOS);
        ResultPage<List<GoodsSortingOrderViewVO>> resultPage = new ResultPage<>();
        resultPage.setTotle(pageInfo.getTotal());
        resultPage.setList(pageInfo.getList());
        return ResultGenerator.genSuccessResult(resultPage);
    }

    /**
     * 分拣单导出(商品分拣单|线路分拣单|团长签收单)
     *
     * @param appmodelId the appmodel id
     * @param type       the type
     * @param pageNum    the page num
     * @param pageSize   the page size
     * @param lineId     the line id
     * @param sendBillId the send bill id
     * @param response   the response
     * @since v1.2
     */
    @ApiOperation(value = "分拣单导出(商品分拣单|线路分拣单|团长签收单)", tags = "v1.2版本接口")
    @GetMapping("sorting/order/v2/export")
    public void export(@RequestHeader @NotBlank String appmodelId,
                       @RequestParam @ApiParam(value = "导出类型 1-商品分拣单  2-线路分拣单 3-团长签收单") Integer type,
                       @RequestParam @NotNull @Min(1) @ApiParam(value = "指定导出页面") Integer pageNum,
                       @RequestParam @NotNull @Min(1) @ApiParam(value = "导出条数条数") Integer pageSize,
                       @RequestParam(defaultValue = "0") @ApiParam(value = "线路id") Long lineId,
                       @RequestParam @ApiParam(value = "发货单id") @NotNull Long sendBillId,
                       HttpServletResponse response) {
        lineSortingOrderService.export(appmodelId, type, pageNum, pageSize, sendBillId, lineId, response);
    }

    /**
     * 查询团长签收单
     *
     * @param appmodelId the appmodel id
     * @param pageNum    the page num
     * @param pageSize   the page size
     * @param sendBillId the send bill id
     * @param lineId     the line id
     * @return the result
     * @since v1.2
     */
    @ApiOperation(value = "查询团长签收单", tags = "v1.2版本接口")
    @GetMapping("receiptform/v2/find")
    public Result<ResultPage<List<ReceiptBillResult>>> list(
            @RequestHeader @NotBlank @ApiParam("小程序模板id") String appmodelId,
            @RequestParam @NotNull @Min(1) @ApiParam(value = "页数") Integer pageNum,
            @RequestParam @NotNull @Min(1) @ApiParam(value = "条数") Integer pageSize,
            @RequestParam(defaultValue = "0") @ApiParam(value = "发货单id,为0查询距离当前时间最近的分拣单") Long sendBillId,
            @RequestParam(defaultValue = "0") @ApiParam(value = "线路id，为0查询全部线路") Long lineId) {
        List<ReceiptBillResult> receiptBill = receiptBillService.getReceiptBill(appmodelId, sendBillId, lineId);
        PageInfo<ReceiptBillResult> pageInfo = PageUtil.pageUtil(pageNum, pageSize, receiptBill);
        ResultPage<List<ReceiptBillResult>> resultPage = new ResultPage<>();
        resultPage.setList(pageInfo.getList());
        resultPage.setTotle(pageInfo.getTotal());
        return ResultGenerator.genSuccessResult(resultPage);
    }

    /**
     * 分拣单|签收单查询 发货单数据
     *
     * @param appmodelId the appmodel id
     * @param type       the type
     * @return the result
     * @since v1.2
     */
    @ApiOperation(value = "分拣单|签收单查询 发货单数据", tags = "v1.2版本接口")
    @GetMapping("sendbill/v2/find")
    public Result<List<SendbillFindVO>> sendbillFind(@RequestHeader @NotBlank @ApiParam("小程序模板id") String appmodelId,
                                                     @RequestParam @NotNull @Min(1) @Max(2) @ApiParam(value = "type " +
                                                             "1=分拣单查询  2=团长签收单查询") Integer type) {
        List<SendbillFindVO> vos = sendBillService.sendbillFind(appmodelId, type);
        return ResultGenerator.genSuccessResult(vos);
    }


}
