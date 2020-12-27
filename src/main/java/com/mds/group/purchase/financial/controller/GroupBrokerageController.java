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
import com.mds.group.purchase.financial.model.GroupBrokerage;
import com.mds.group.purchase.financial.service.GroupBrokerageService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * The type Group brokerage controller.
 *
 * @author shuke
 * @date 2019 /04/23
 */
@RestController
@RequestMapping("/group/brokerage")
public class GroupBrokerageController {
    @Resource
    private GroupBrokerageService groupBrokerageService;

    /**
     * List result.
     *
     * @param groupId    the group id
     * @param page       the page
     * @param size       the size
     * @param startDate  the start date
     * @param endDate    the end date
     * @param appmodelId the appmodel id
     * @return the result
     */
    @GetMapping("/list")
    public Result list(@RequestParam String groupId, @RequestParam(defaultValue = "0") Integer page,
                       @RequestParam(defaultValue = "0") Integer size, String startDate, String endDate,
                       @RequestHeader String appmodelId) {
        PageHelper.orderBy("id desc");
        PageHelper.startPage(page, size);
        List<GroupBrokerage> list = groupBrokerageService.findList(groupId, startDate, endDate);
        PageInfo<GroupBrokerage> pageInfo = new PageInfo<>(list);
        return ResultGenerator.genSuccessResult(pageInfo);
    }

    /**
     * Collect brokerage result.
     *
     * @param groupId    the group id
     * @param startDate  the start date
     * @param endDate    the end date
     * @param appmodelId the appmodel id
     * @return the result
     */
    @GetMapping("/collect")
    public Result collectBrokerage(@RequestParam String groupId, String startDate, String endDate,
                                   @RequestHeader String appmodelId) {
        String info = groupBrokerageService.collectBrokerage(groupId, startDate, endDate);
        return Result.success(info);
    }
}
