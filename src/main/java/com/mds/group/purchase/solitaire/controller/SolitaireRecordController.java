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

package com.mds.group.purchase.solitaire.controller;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.mds.group.purchase.core.Result;
import com.mds.group.purchase.solitaire.model.SolitaireRecord;
import com.mds.group.purchase.solitaire.result.PrivateSolitaireRecord;
import com.mds.group.purchase.solitaire.service.SolitaireRecordService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;
import tk.mybatis.mapper.entity.Condition;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import javax.validation.constraints.NotBlank;
import java.util.List;
import java.util.Map;

/**
 * The type Solitaire record controller.
 *
 * @author shuke
 * @date 2019 /05/16
 */
@RestController
@RequestMapping("/solitaire/record")
@Slf4j
public class SolitaireRecordController {
    @Resource
    private SolitaireRecordService solitaireRecordService;

    /**
     * Solitaire record result.
     *
     * @param appmodelId the appmodel id
     * @param pageNum    the page num
     * @param pageSize   the page size
     * @return the result
     */
    @ApiOperation(value = "获取接龙记录列表", tags = "查询接口")
    @GetMapping("/list")
    public Result solitaireRecord(@RequestHeader @NotBlank @ApiParam(value = "appmodelId") String appmodelId,
                                  @RequestParam @ApiParam("当前页码") int pageNum,
                                  @RequestParam @ApiParam("每页的数量") int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        PageHelper.orderBy("create_time desc");
        List<SolitaireRecord> records = solitaireRecordService.findByList("appmodelId", appmodelId);
        PageInfo<SolitaireRecord> pageInfo = new PageInfo<>(records);
        return Result.success(pageInfo);
    }

    /**
     * Solitaire buy record result.
     *
     * @param appmodelId the appmodel id
     * @param buyerId    the buyer id
     * @param pageNum    the page num
     * @param pageSize   the page size
     * @return the result
     */
    @ApiOperation(value = "获取用户购买记录列表", tags = "查询接口")
    @GetMapping("/h5/userBuy")
    public Result solitaireBuyRecord(@RequestHeader @NotBlank @ApiParam(value = "appmodelId") String appmodelId,
                                     @RequestParam @ApiParam("用户id") String buyerId,
                                     @RequestParam @ApiParam("当前页码") int pageNum,
                                     @RequestParam @ApiParam("每页的数量") int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        PageHelper.orderBy("create_time desc");
        List<PrivateSolitaireRecord> records = solitaireRecordService.findUserBuyRecord(appmodelId, buyerId);
        PageInfo<PrivateSolitaireRecord> pageInfo = new PageInfo<>(records);
        return Result.success(pageInfo);
    }

    /**
     * Solitaire record result.
     *
     * @param appmodelId the appmodel id
     * @param goodsName  the goods name
     * @param pageNum    the page num
     * @param pageSize   the page size
     * @return the result
     */
    @ApiOperation(value = "根据商品名称搜索接龙记录", tags = "查询接口")
    @GetMapping("/search")
    public Result solitaireRecord(@RequestHeader @NotBlank @ApiParam(value = "appmodelId") String appmodelId,
                                  @RequestParam @NotBlank @ApiParam(value = "商品名称") String goodsName,
                                  @RequestParam @ApiParam("当前页码") int pageNum,
                                  @RequestParam @ApiParam("每页的数量") int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        Condition condition = new Condition(SolitaireRecord.class);
        Example.Criteria criteria = condition.createCriteria().andEqualTo("appmodelId", appmodelId);
        if (StringUtils.isNotBlank(goodsName)) {
            criteria.andLike("recordDetail", "%" + goodsName + "%");
        }
        List<SolitaireRecord> records = solitaireRecordService.findByCondition(condition);
        PageInfo<SolitaireRecord> pageInfo = new PageInfo<>(records);
        return Result.success(pageInfo);
    }

    /**
     * 手动删除接龙记录
     *
     * @param appmodelId 小程序模板id
     * @param recordIds  记录id 多个id用逗号分隔
     * @return result result
     */
    @ApiOperation(value = "手动删除接龙记录", tags = "删除接口")
    @DeleteMapping("/delete")
    public Result deleteRecord(@RequestHeader @NotBlank @ApiParam(value = "appmodelId") String appmodelId,
                               @RequestParam @NotBlank @ApiParam(value = "接龙记录id，多个id用逗号分隔") String recordIds) {
        int res = solitaireRecordService.deleteByIds(recordIds);
        return Result.success("删除" + res + "条记录");
    }

    /**
     * Gets person count.
     *
     * @param appmodelId the appmodel id
     * @return the person count
     */
    @ApiOperation(value = "获取h5浏览量和购买人数", tags = "查询接口")
    @GetMapping("/person-count")
    public Result getPersonCount(@RequestHeader @NotBlank @ApiParam(value = "appmodelId") String appmodelId) {
        Map<String, String> countMap = solitaireRecordService.getPersonCount(appmodelId);
        return Result.success(countMap);
    }
}
