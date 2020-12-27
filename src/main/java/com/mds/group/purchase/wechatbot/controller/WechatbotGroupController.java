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

package com.mds.group.purchase.wechatbot.controller;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.mds.group.purchase.core.CodeMsg;
import com.mds.group.purchase.core.Result;
import com.mds.group.purchase.solitaire.service.SolitaireMsgService;
import com.mds.group.purchase.wechatbot.model.WechatBot;
import com.mds.group.purchase.wechatbot.model.WechatbotGroup;
import com.mds.group.purchase.wechatbot.service.WechatBotService;
import com.mds.group.purchase.wechatbot.service.WechatbotGroupService;
import com.mds.group.purchase.wechatbot.vo.BindingWechatGroupVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.*;
import tk.mybatis.mapper.entity.Condition;

import javax.annotation.Resource;
import javax.validation.constraints.NotBlank;
import java.util.List;
import java.util.Map;

/**
 * The type Wechatbot group controller.
 *
 * @author shuke
 * @date 2019 /05/15
 */
@Api("所有接口")
@RestController
@RequestMapping("/wechatbot/group")
public class WechatbotGroupController {
    @Resource
    private WechatbotGroupService wechatbotGroupService;
    @Resource
    private WechatBotService wechatBotService;
    @Resource
    private SolitaireMsgService solitaireMsgService;

    /**
     * Binding group leader to bot result.
     *
     * @param appmodelId the appmodel id
     * @param params     the params
     * @return the result
     */
    @ApiOperation(value = "绑定团长和群聊")
    @PostMapping("/binding")
    public Result bindingGroupLeaderToBot(@RequestHeader @ApiParam("appmpdelId") @NotBlank String appmodelId,
                                          @RequestBody Map<String, String> params) {
        String groupLeaderId = params.get("groupLeaderId");
        String wechatGroupNickName = params.get("wechatGroupNickName");
        Long botUin = Long.valueOf(params.get("botUin"));
        Result result = wechatbotGroupService
                .bindingGroupLeaderToBot(appmodelId, botUin, groupLeaderId, wechatGroupNickName);
        if (result.getCode() == 200) {
            //绑定成功以后发送活动商品信息
            solitaireMsgService.sendStartSolitaireActMsgToGroups(appmodelId, wechatGroupNickName);
        }
        return result;
    }

    /**
     * 已绑定的群聊列表
     *
     * @param appmodelId the appmodel id
     * @param botUin     the bot uin
     * @param pageSize   the page size
     * @param pageNum    the page num
     * @return the result
     */
    @ApiOperation(value = "获取已绑定的群聊列表")
    @GetMapping("/list")
    public Result bindingGroupList(@RequestHeader @ApiParam("appmpdelId") @NotBlank String appmodelId,
                                   @RequestParam @ApiParam("机器人唯一标识") Long botUin,
                                   @RequestParam @ApiParam("每页的数量") Integer pageSize,
                                   @RequestParam @ApiParam("当前页") Integer pageNum) {
        PageHelper.startPage(pageNum, pageSize);
        List<BindingWechatGroupVo> bindingList = wechatbotGroupService.findBindingList(appmodelId, botUin);
        PageInfo<BindingWechatGroupVo> pageInfo = new PageInfo<>(bindingList);
        return Result.success(pageInfo);
    }

    /**
     * 删除只有单个群的绑定
     *
     * @param appmodelId    小程序模板id
     * @param botUin        the bot uin
     * @param groupLeaderId the group leader id
     * @return the result
     */
    @ApiOperation(value = "删除只有单个群的绑定")
    @DeleteMapping("/delete")
    public Result deleteWechatbotGroupOne(@RequestHeader @ApiParam("appmpdelId") @NotBlank String appmodelId,
                                          @RequestParam @ApiParam("") Long botUin,
                                          @RequestParam @ApiParam("团长id") String groupLeaderId) {
        boolean res = wechatbotGroupService.deleteByGroupId(appmodelId, groupLeaderId, botUin);
        return res ? Result.success("删除成功") : Result.error(CodeMsg.FAIL.fillArgs("删除失败，请联系管理员"));
    }

    /**
     * 查询团长绑定的群聊列表
     *
     * @param appmodelId    小程序模板id
     * @param botUin        the bot uin
     * @param groupLeaderId 团长id
     * @param pageSize      the page size
     * @param pageNum       the page num
     * @return the result
     */
    @ApiOperation(value = "查询团长绑定的群聊列表")
    @GetMapping("/binding/list")
    public Result bindingListByGroupLeader(@RequestHeader @ApiParam("appmpdelId") @NotBlank String appmodelId,
                                           @RequestParam Long botUin,
                                           @RequestParam @ApiParam("团长id") String groupLeaderId,
                                           @RequestParam @ApiParam("每页的数量") Integer pageSize,
                                           @RequestParam @ApiParam("当前页") Integer pageNum) {
        Condition condition1 = new Condition(WechatBot.class);
        condition1.createCriteria().andEqualTo("appmodelId", appmodelId).andEqualTo("botUin", botUin);
        WechatBot bot = wechatBotService.findByOneCondition(condition1);
        PageHelper.startPage(pageNum, pageSize);
        Condition condition = new Condition(WechatbotGroup.class);
        condition.createCriteria().andEqualTo("wechatbotId", bot.getBotId()).andEqualTo("groupLeaderId", groupLeaderId);
        List<WechatbotGroup> wechatbotGroups = wechatbotGroupService.findByCondition(condition);
        PageInfo<WechatbotGroup> pageInfo = new PageInfo<>(wechatbotGroups);
        return Result.success(pageInfo);
    }

    /**
     * 删除有多个群的绑定
     *
     * @param appmodelId        小程序模板id
     * @param wechatbotGroupIds 绑定id，多个id用逗号分隔
     * @return the result
     */
    @DeleteMapping("/delete/ids")
    public Result deleteWechatbotGroupNotOne(@RequestHeader @ApiParam("appmpdelId") @NotBlank String appmodelId,
                                             @RequestParam @ApiParam("微信群id，多个id用逗号分隔") String wechatbotGroupIds) {
        int deleteCloums = wechatbotGroupService.deleteByGroupIds(wechatbotGroupIds, appmodelId);
        return deleteCloums != 0 ? Result.success("删除成功") : Result.error(CodeMsg.FAIL.fillArgs("删除失败，请联系管理员"));
    }

    /**
     * 获取剩余可绑定的群聊数量
     *
     * @param appmodelId the appmodel id
     * @param botUin     the bot uin
     * @return bind group count
     */
    @GetMapping("/can/bind/count")
    public Result getBindGroupCount(@RequestHeader String appmodelId, @RequestParam Long botUin) {
        int bindGroupCount = wechatbotGroupService.findBindGroupCount(appmodelId, botUin);
        return Result.success(5000 - bindGroupCount);
    }
}
