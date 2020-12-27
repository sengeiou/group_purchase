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
import com.mds.group.purchase.order.model.SendBill;
import com.mds.group.purchase.order.result.OrderResult;
import com.mds.group.purchase.order.result.SendBillResult;
import com.mds.group.purchase.order.service.SendBillService;
import com.mds.group.purchase.order.vo.SendBillFilterVo;
import com.mds.group.purchase.utils.PageUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 发货单控制器类
 *
 * @author shuke
 * @date 2019 /02/18
 */
@Api(tags = "所有接口")
@RestController
@RequestMapping("/send/bill")
public class SendBillController {

    @Resource
    private SendBillService sendBillService;


    /**
     * 发货单的发货功能
     * 改变属于该发货单的所有订单状态为 已发货状态
     *
     * @param appmodelId 小程序模板id
     * @param sendBillId 发货单id
     * @return null result
     * @since v1.2
     */
    @ApiOperation(value = "发货单的发货功能", tags = "v1.2版本接口")
    @GetMapping("/v2/send")
    public Result send(@RequestHeader @NotBlank String appmodelId,
                       @ApiParam("发货单id") @RequestParam @NotNull Long sendBillId) {
        sendBillService.doSend(appmodelId, sendBillId);
        return Result.success();
    }

    /**
     * 发货单的查看详情功能
     *
     * @param appmodelId 小程序模板id
     * @param sendBillId 发货单id
     * @param page       the page
     * @param size       the size
     * @return OrderResult result
     * @since v1.2
     */
    @ApiOperation(value = "查看发货单详情", tags = "v1.2版本接口")
    @GetMapping("/v2/detail")
    public Result<PageInfo<OrderResult>> detail(@RequestHeader @NotBlank String appmodelId,
                                                @ApiParam("发货单id") @RequestParam @NotNull Long sendBillId,
                                                @ApiParam("分页页码，传入0查询所有") @RequestParam Integer page,
                                                @ApiParam("分页数据量，传入0查询所有") @RequestParam Integer size) {
        List<OrderResult> detail = sendBillService.getDetail(appmodelId, sendBillId);
        PageInfo<OrderResult> pageInfo = PageUtil.pageUtil(page, size, detail);
        return Result.success(pageInfo);
    }

    /**
     * 根据PC端传入的时间和发货单状态筛选出发货单
     *
     * @param appmodelId       小程序模板id
     * @param sendBillFilterVo 发货单筛选参数对象
     * @return SendBill result
     * @since v1.2
     */
    @ApiOperation(value = "根据PC端传入的时间和发货单状态筛选出发货单", tags = "v1.2版本接口")
    @PostMapping("v2/filter")
    public Result<PageInfo<SendBillResult>> filter(@RequestHeader @NotBlank String appmodelId,
                                                   @RequestBody SendBillFilterVo sendBillFilterVo) {
        List<SendBillResult> filter = sendBillService.filter(appmodelId, sendBillFilterVo);
        PageInfo<SendBillResult> pageInfo = PageUtil.pageUtil(sendBillFilterVo.getPage(), sendBillFilterVo.getSize(),
                filter);
        return Result.success(pageInfo);
    }

    /**
     * 根据PC端传入的时间查询发货单
     *
     * @param appmodelId   小程序模板id
     * @param generateDate 生成时间
     * @return SendBill result
     * @since v1.2
     */
    @ApiOperation(value = "根据PC端传入的时间查询出发货单", tags = "v1.2版本接口")
    @PostMapping("v2/byDate")
    public Result<List<SendBill>> history(@RequestHeader @NotBlank String appmodelId,
                                          @RequestParam @ApiParam("发货单生成时间，格式为yyyy-MM-dd或yyyy/MM/dd") @NotBlank String generateDate) {
        List<SendBill> filter = sendBillService.getByDate(appmodelId, generateDate);
        return Result.success(filter);
    }

    /**
     * 导出发货单里的订单
     *
     * @param response   the response
     * @param appmodelId 小程序模板id
     * @param sendBillId 发货单id
     * @since v1.2
     */
    @ApiOperation(value = "导出发货单里的订单", tags = "v1.2版本接口")
    @PostMapping("/v2/export")
    public void downloadAllClassmate(HttpServletResponse response, @RequestHeader @NotBlank String appmodelId,
                                     @ApiParam("发货单id") @RequestParam @NotNull Long sendBillId) {
        sendBillService.downloadSendBill(response, appmodelId, sendBillId);
    }
}
