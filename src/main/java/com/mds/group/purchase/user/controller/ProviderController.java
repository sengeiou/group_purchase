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

import cn.hutool.core.date.DateUtil;
import com.github.pagehelper.PageInfo;
import com.mds.group.purchase.configurer.GroupMallProperties;
import com.mds.group.purchase.constant.Common;
import com.mds.group.purchase.constant.RedisPrefix;
import com.mds.group.purchase.core.CodeMsg;
import com.mds.group.purchase.core.Result;
import com.mds.group.purchase.exception.ServiceException;
import com.mds.group.purchase.user.service.ProviderService;
import com.mds.group.purchase.user.vo.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * The type Provider controller.
 *
 * @author CodeGenerator
 * @date 2018 /11/27 供应商
 */
@RestController
@Validated
@RequestMapping("/provider")
@Api(tags = {"所有接口"})
public class ProviderController {

    @Resource
    private RedisTemplate<String, Boolean> redisTemplate;
    @Resource
    private ProviderService providerService;

    /**
     * 接口迭代,新增需求,每个用户一天只能申请一次
     *
     * @param providerApplyRegisterVO the provider apply register vo
     * @param appmodelId              the appmodel id
     * @return the result
     */
    @PostMapping("/v1/apply/register")
    @ApiOperation(value = "资源入驻申请", tags = "添加接口")
    @Deprecated
    public Result providerApplyRegister(@RequestBody @Valid ProviderApplyRegisterVO providerApplyRegisterVO,
                                        @ApiParam(value = "小程序模板id", required = true) @NotNull @RequestHeader String appmodelId) {
        String key = GroupMallProperties.getRedisPrefix().concat(appmodelId).concat(RedisPrefix.PROVIDER_APPLY_REGISTER)
                .concat(providerApplyRegisterVO.getWxuserId());
        Boolean flag = redisTemplate.opsForValue().get(key);
        if (flag != null && flag) {
            throw new ServiceException("一天只能申请一次");
        }
        providerApplyRegisterVO.setAppmodelId(appmodelId);
        int sum = providerService.providerApplyRegister(providerApplyRegisterVO);
        if (sum > 0) {
            long time = DateUtil.endOfDay(DateUtil.date()).getTime() - DateUtil.date().getTime();
            redisTemplate.opsForValue().set(key, true, time, TimeUnit.MILLISECONDS);
            return Result.success();
        }
        throw new ServiceException("申请失败");
    }

    /**
     * Provider apply result.
     *
     * @param providerApplyVO the provider apply vo
     * @param appmodelId      the appmodel id
     * @return the result
     */
    @PutMapping("/v1/apply")
    @ApiOperation(value = "同意/拒绝入驻申请/新增供应商", tags = "更新接口")
    public Result providerApply(@RequestBody @Valid ProviderApplyVO providerApplyVO,
                                @RequestHeader @NotBlank(message = "appmodelId不能为空") @Size(max = 27, min = 27,
                                        message = "商品标示错误") String appmodelId) {
        providerApplyVO.setAppmodelId(appmodelId);
        if (providerApplyVO.getOptionType().equals(ProviderApplyVO.OK) || providerApplyVO.getOptionType()
                .equals(ProviderApplyVO.ADD)) {
            if (!StringUtils.isNotBlank(providerApplyVO.getProviderAddress())) {
                throw new ServiceException("地址不能为空");
            }
            if (!StringUtils.isNotBlank(providerApplyVO.getProviderArea())) {
                throw new ServiceException("地区不能为空");
            }
        }
        int sum = providerService.providerApply(providerApplyVO);
        if (sum > 0) {
            return Result.success();
        }
        throw new ServiceException("申请失败");
    }

    /**
     * Provider apply delete result.
     *
     * @param deleteVO   the delete vo
     * @param appmodelId the appmodel id
     * @return the result
     */
    @PutMapping("/v1/delete")
    @ApiOperation(value = "批量删除单个或多个供应商|申请记录", tags = "更新接口")
    public Result providerApplyDelete(@RequestBody @Valid DeleteVO deleteVO,
                                      @RequestHeader @NotBlank(message = "appmodelId不能为空") @Size(max = 27, min = 27,
                                              message = "商品标示错误") String appmodelId) {
        deleteVO.setAppmodelId(appmodelId);
        List<String> sum = providerService.providerDelete(deleteVO);
        if (sum.size() > 0) {
            if (deleteVO.getIds().split(Common.REGEX).length > 1) {
                return Result.error(new CodeMsg(99, "部分供应商有商品在售,请下架商品后再删除"));
            }
            return Result.error(new CodeMsg(99, "此供应商有商品在售,请下架商品后再删除"));
        }
        return Result.success();
    }

    /**
     * Provider search result.
     *
     * @param appmodelId    the appmodel id
     * @param searchType    the search type
     * @param pageNum       the page num
     * @param pageSize      the page size
     * @param providerName  the provider name
     * @param providerPhone the provider phone
     * @param providerId    the provider id
     * @return the result
     */
    @GetMapping("/v1/manager")
    @ApiOperation(value = "供应商管理", tags = "查询接口")
    public Result<PageInfo<ProviderManagerVO>> providerSearch(
            @RequestHeader @NotBlank(message = "appmodelId不能为空") @Size(max = 27, min = 27, message = "商品标示错误") String appmodelId,
            @RequestParam @NotNull @ApiParam(value = "搜索类型 1-供应商管理 2-禁用中  3-待审核  4-已关闭", required = true) Integer searchType,
            @RequestParam @NotNull(message = "pageNum不能为空") Integer pageNum,
            @RequestParam @NotNull(message = "pageSize不能为空") Integer pageSize,
            @RequestParam(required = false) @ApiParam(value = "供应商名称") String providerName,
            @RequestParam(required = false) @ApiParam(value = "供应商手机") String providerPhone,
            @RequestParam(required = false) @ApiParam(value = "供应商id") String providerId) {
        List<ProviderManagerVO> providerManagerVOS = providerService
                .providerApplySearch(searchType, appmodelId, providerName, providerPhone, providerId, pageNum,
                        pageSize);
        PageInfo<ProviderManagerVO> pageInfo = new PageInfo<>(providerManagerVOS);
        return Result.success(pageInfo);
    }

    /**
     * Provider statu result.
     *
     * @param providerStatuVO the provider statu vo
     * @return the result
     */
    @PutMapping("/v1/statu")
    @ApiOperation(value = "禁用/开启供应商", tags = "更新接口")
    public Result providerStatu(@RequestBody @Valid ProviderStatuVO providerStatuVO) {
        int sum = providerService.providerStatu(providerStatuVO);
        if (sum > 0) {
            return Result.success();
        }
        throw new ServiceException("操作失败");
    }

}
