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

package com.mds.group.purchase.financial.controller;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.mds.group.purchase.core.Result;
import com.mds.group.purchase.core.ResultGenerator;
import com.mds.group.purchase.financial.model.FinancialDetails;
import com.mds.group.purchase.financial.service.FinancialDetailsService;
import com.mds.group.purchase.utils.FinancialUtil;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.constraints.NotBlank;
import java.util.List;

/**
 * The type Financial details controller.
 *
 * @author shuke
 * @date 2019 /04/25
 */
@RestController
@RequestMapping("/financial/details")
public class FinancialDetailsController {
    @Resource
    private FinancialDetailsService financialDetailsService;
    @Resource
    private FinancialUtil financialUtil;

    /**
     * List result.
     *
     * @param page       the page
     * @param size       the size
     * @param startDate  the start date
     * @param endDate    the end date
     * @param type       the type
     * @param accoutType the accout type
     * @param appmodelId the appmodel id
     * @return the result
     */
    @GetMapping
    public Result list(@RequestParam(defaultValue = "0") Integer page, @RequestParam(defaultValue = "0") Integer size,
                       String startDate, String endDate, @RequestParam(defaultValue = "0") Integer type,
                       @RequestParam(defaultValue = "0") Integer accoutType,
                       @RequestHeader @NotBlank String appmodelId) {
        PageHelper.startPage(page, size);
        PageHelper.orderBy("created_time desc");
        List<FinancialDetails> list = financialDetailsService
                .findFinancialDetails(startDate, endDate, type, accoutType, appmodelId);
        PageInfo pageInfo = new PageInfo<>(list);
        pageInfo.setList(financialUtil.packageResult(list));
        return ResultGenerator.genSuccessResult(pageInfo);
    }

    /**
     * Gets collect.
     *
     * @param startDate  the start date
     * @param endDate    the end date
     * @param type       the type
     * @param accoutType the accout type
     * @param appmodelId the appmodel id
     * @return the collect
     */
    @GetMapping("/collect")
    public Result getCollect(String startDate, String endDate, @RequestParam(defaultValue = "0") Integer type,
                             @RequestParam(defaultValue = "0") Integer accoutType,
                             @RequestHeader @NotBlank String appmodelId) {
        String info = financialDetailsService.collectFinancial(appmodelId, type, accoutType, startDate, endDate);
        return Result.success(info);
    }
}
