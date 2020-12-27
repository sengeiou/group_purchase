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

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageInfo;
import com.mds.group.purchase.constant.WechatBotApiURL;
import com.mds.group.purchase.core.CodeMsg;
import com.mds.group.purchase.core.Result;
import com.mds.group.purchase.utils.PageUtil;
import com.mds.group.purchase.wechatbot.model.WechatBot;
import com.mds.group.purchase.wechatbot.model.WechatbotGroup;
import com.mds.group.purchase.wechatbot.service.WechatBotService;
import com.mds.group.purchase.wechatbot.service.WechatbotGroupService;
import com.mds.group.purchase.wechatbot.service.impl.WechatBotServiceImpl;
import io.swagger.annotations.*;
import org.springframework.web.bind.annotation.*;
import tk.mybatis.mapper.entity.Condition;

import javax.annotation.Resource;
import javax.validation.constraints.NotBlank;
import java.util.List;
import java.util.stream.Collectors;

/**
 * The type Wechat bot controller.
 *
 * @author shuke
 * @date 2019 /05/15
 */
@Api(value = "机器人相关操作", tags = "所有接口")
@RestController
@RequestMapping("/wechat/bot")
public class WechatBotController {
    @Resource
    private WechatBotService wechatBotService;
    @Resource
    private WechatbotGroupService wechatbotGroupService;

    /**
     * Wechat bot login result.
     *
     * @param appmodelId the appmodel id
     * @return the result
     */
    @ApiOperation(value = "机器人登陆")
    @ApiResponse(code = 200, message = "success", responseContainer = "登陆二维码路径")
    @PostMapping("/login")
    public Result wechatBotLogin(@RequestHeader @NotBlank @ApiParam("appmodelId") String appmodelId) {
        WechatBotService wechatBotService = new WechatBotServiceImpl();
        String res = wechatBotService
                .requestWechatBot(WechatBotApiURL.BASEURL, RequestMethod.POST, "login", appmodelId);
        return JSONObject.toJavaObject(JSON.parseObject(res), Result.class);
    }

    /**
     * Check login result.
     *
     * @param appmodelId the appmodel id
     * @return the result
     */
    @ApiOperation(value = "检查登陆状态")
    @ApiResponses({@ApiResponse(code = 200, message = "success", response = WechatBot.class),
            @ApiResponse(code = 400, message = "请登录"), @ApiResponse(code = 201, message = "请扫码"),
            @ApiResponse(code = 202, message = "请在手机上确认登陆")})
    @GetMapping("/checkLogin")
    public Result checkLogin(@RequestHeader @NotBlank @ApiParam("appmodelId") String appmodelId) {
        return wechatBotService.checkLogin(WechatBotApiURL.BASEURL, RequestMethod.GET, "checkLogin", appmodelId);
    }

    /**
     * Gets group list.
     *
     * @param appmodelId the appmodel id
     * @param botUin     the bot uin
     * @param pageNum    the page num
     * @param pageSize   the page size
     * @return the group list
     */
    @ApiOperation(value = "获取群聊列表")
    @GetMapping("/groupList")
    public Result getGroupList(@RequestHeader @NotBlank @ApiParam("appmodelId") String appmodelId,
                               @RequestParam Long botUin,
                               @RequestParam @ApiParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                               @RequestParam @ApiParam(value = "pageSize", defaultValue = "10") Integer pageSize) {
        String res = wechatBotService
                .requestWechatBot(WechatBotApiURL.BASEURL, RequestMethod.GET, "groupList", appmodelId);
        Result result = JSONObject.toJavaObject(JSON.parseObject(res), Result.class);
        if (null != result.getData() && result.getData() instanceof List) {
            List<String> data = (List<String>) result.getData();
            //过滤掉已绑定的群聊
            data = filterBindGroup(appmodelId, botUin, data);
            PageInfo<String> stringPageInfo = PageUtil.pageUtil(pageNum, pageSize, data);
            return Result.success(stringPageInfo);
        }
        return Result.error(CodeMsg.FAIL.fillArgs("没有数据"));
    }

    /**
     * Gets group list.
     *
     * @param appmodelId the appmodel id
     * @param groupName  the group name
     * @param botUin     the bot uin
     * @param pageNum    the page num
     * @param pageSize   the page size
     * @return the group list
     */
    @ApiOperation(value = "搜索群聊")
    @GetMapping("/search")
    public Result getGroupList(@RequestHeader @NotBlank @ApiParam("appmodelId") String appmodelId,
                               @RequestParam @ApiParam(value = "groupName") String groupName,
                               @RequestParam Long botUin,
                               @RequestParam @ApiParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                               @RequestParam @ApiParam(value = "pageSize", defaultValue = "10") Integer pageSize) {
        String res = wechatBotService
                .requestWechatBot(WechatBotApiURL.BASEURL, RequestMethod.GET, "groupList", appmodelId);
        Result result = JSONObject.toJavaObject(JSON.parseObject(res), Result.class);
        if (null != result.getData() && result.getData() instanceof List) {
            List<String> data = (List<String>) result.getData();
            //过滤掉已绑定的群聊
            data = filterBindGroup(appmodelId, botUin, data);
            data = data.stream().filter(o -> o.contains(groupName)).collect(Collectors.toList());
            PageInfo<String> stringPageInfo = PageUtil.pageUtil(pageNum, pageSize, data);
            return Result.success(stringPageInfo);
        }
        return Result.error(CodeMsg.FAIL.fillArgs("没有数据"));
    }

    /**
     * Logout result.
     *
     * @param appmodelId the appmodel id
     * @return the result
     */
    @ApiOperation(value = "登出")
    @PostMapping("/logout")
    public Result logout(@RequestHeader @NotBlank @ApiParam("appmodelId") String appmodelId) {
        String res = wechatBotService
                .requestWechatBot(WechatBotApiURL.BASEURL, RequestMethod.POST, "logout", appmodelId);
        return JSONObject.toJavaObject(JSON.parseObject(res), Result.class);
    }

    /**
     * Remove bot result.
     *
     * @param appmodelId the appmodel id
     * @param botUin     the bot uin
     * @return the result
     */
    @ApiOperation(value = "删除机器人")
    @DeleteMapping("/remove")
    public Result removeBot(@RequestHeader @NotBlank @ApiParam("appmodelId") String appmodelId,
                            @RequestParam @ApiParam("机器人唯一标识") Long botUin) {
        wechatBotService.removeBotByUin(appmodelId, botUin);
        return Result.success();
    }

    private List<String> filterBindGroup(String appmodelId, Long botUin, List<String> groupNames) {
        Condition condition1 = new Condition(WechatBot.class);
        condition1.createCriteria().andEqualTo("appmodelId", appmodelId).andEqualTo("botUin", botUin);
        WechatBot bot = wechatBotService.findByOneCondition(condition1);
        Long botId = bot.getBotId();
        //过滤掉已绑定的群聊
        Condition condition = new Condition(WechatbotGroup.class);
        condition.createCriteria().andEqualTo("appmodelId", appmodelId).andEqualTo("wechatbotId", botId);
        List<WechatbotGroup> wechatbotGroupList = wechatbotGroupService.findByCondition(condition);
        List<String> bindGroups = wechatbotGroupList.stream().map(WechatbotGroup::getWechatGroupName)
                .collect(Collectors.toList());
        return groupNames.stream().filter(o -> !bindGroups.contains(o)).collect(Collectors.toList());
    }

}
