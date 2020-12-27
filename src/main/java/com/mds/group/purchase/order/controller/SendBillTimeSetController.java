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

import com.mds.group.purchase.constant.SendBillConstant;
import com.mds.group.purchase.core.CodeMsg;
import com.mds.group.purchase.core.Result;
import com.mds.group.purchase.order.model.SendBillTimeSet;
import com.mds.group.purchase.order.service.SendBillTimeSetService;
import com.mds.group.purchase.utils.SendBillUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.constraints.NotBlank;
import java.util.Arrays;
import java.util.List;

/**
 * 发货单设置生成时间控制器类
 *
 * @author shuke
 * @date 2019 /02/18
 */
@RestController
@Validated
@Api(tags = "所有接口")
@RequestMapping("/send/bill/time/set")
public class SendBillTimeSetController {

    @Resource
    private SendBillTimeSetService sendBillTimeSetService;

    /**
     * 设置发货单生成时间
     *
     * @param appmodelId 小程序模板id
     * @param setTimes   设置的时间
     * @return null result
     * @since v1.2
     */
    @ApiOperation(value = "设置发货单生成时间", tags = "v1.2版本接口")
    @GetMapping("/v2/do")
    public Result updateTime(@RequestHeader @ApiParam("小程序模板id") @NotBlank String appmodelId,
                             @ApiParam("设置的发货单生成时间,多个时间之间用逗号（,）隔开，时间格式为 hh:mm") @NotBlank @RequestParam String setTimes) {
        String[] times = setTimes.split(",");
        List<String> setTimeList = Arrays.asList(times);
        if (setTimeList.size() > SendBillConstant.MAX_SEND_BILL_TIME_NUMBER) {
            return Result.error(CodeMsg.FAIL.fillArgs("发货单生成时间个数最多4个！"));
        }
        for (String time : setTimeList) {
            //1、对time进行格式验证
            Boolean timeVerify = SendBillUtil.setTimeVerify(time);
            if (!timeVerify) {
                return Result.error(CodeMsg.PARAMETER_ERROR.fillArgs("时间格式为 hh:mm,请查证参数"));
            }
        }
        sendBillTimeSetService.setTime(setTimeList, appmodelId);
        return Result.success();
    }

    /**
     * 获取商家已经设置的发货单生成时间
     *
     * @param appmodelId the appmodel id
     * @return the times
     * @since v1.2
     */
    @ApiOperation(value = "获取商家已经设置的发货单生成时间", tags = "v1.2版本接口")
    @GetMapping("/v2/times")
    public Result getTimes(@RequestHeader String appmodelId) {
        List<SendBillTimeSet> byAppmodelId = sendBillTimeSetService.getByAppmodelId(appmodelId);
        return Result.success(byAppmodelId);
    }
}
