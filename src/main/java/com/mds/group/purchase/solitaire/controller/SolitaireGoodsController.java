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

import cn.hutool.core.date.DateUtil;
import com.mds.group.purchase.activity.result.ActGoodsInfoResult;
import com.mds.group.purchase.constant.WechatBotApiURL;
import com.mds.group.purchase.core.Result;
import com.mds.group.purchase.solitaire.service.SolitaireGoodsService;
import com.mds.group.purchase.utils.ActivityGoodsUtil;
import com.mds.group.purchase.wechatbot.model.WechatBot;
import com.mds.group.purchase.wechatbot.service.WechatBotService;
import com.mds.group.purchase.wechatbot.service.WechatbotGroupService;
import com.mds.group.purchase.wechatbot.vo.BindingWechatGroupVo;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * The type Solitaire goods controller.
 *
 * @author pavawi
 */
@RestController
@RequestMapping("/solitaire/goods")
@Slf4j
public class SolitaireGoodsController {

    @Resource
    private SolitaireGoodsService solitaireGoodsService;
    @Resource
    private ActivityGoodsUtil activityGoodsUtil;
    @Resource
    private WechatBotService wechatBotService;
    @Resource
    private WechatbotGroupService wechatbotGroupService;

    /**
     * Solitaire goods list result.
     *
     * @param appmodelId the appmodel id
     * @param wxuserId   the wxuser id
     * @return the result
     */
    @ApiOperation(value = "h5页面获取接龙商品", tags = "查询接口")
    @GetMapping("/h5/list")
    public Result solitaireGoodsList(@RequestHeader @NotBlank String appmodelId, @RequestParam @NotNull Long wxuserId) {
        List<ActGoodsInfoResult> solitaireGoodsList = solitaireGoodsService.getSolitaireGoodsList(appmodelId, wxuserId);
        return Result.success(solitaireGoodsList);
    }

    /**
     * 发送接龙商品到群里面
     *
     * @param appmodelId the appmodel id
     * @return the result
     */
    @PostMapping("/send-msg")
    public Result sendSolitaireInfoToWXGroup(@RequestHeader String appmodelId) {
        Map<String, ActGoodsInfoResult> allActGoodsResultCache = activityGoodsUtil
                .getAllActGoodsResultCache(appmodelId);
        List<ActGoodsInfoResult> joinSolitaire = allActGoodsResultCache.values().stream()
                .filter(o -> o.getJoinSolitaire() == null).collect(Collectors.toList());
        StringBuilder msg = new StringBuilder();
        ActGoodsInfoResult actGoodsInfoResult1 = joinSolitaire.get(0);
        String actStartTDate = actGoodsInfoResult1.getActStartTDate();
        String actEndDate = actGoodsInfoResult1.getActEndDate();
        DateUtil.formatChineseDate(DateUtil.parse(actStartTDate), false);
        msg.append(DateUtil.formatChineseDate(DateUtil.parse(actStartTDate), false));
        msg.append(actStartTDate, 11, 16);
        msg.append("-");
        msg.append(DateUtil.formatChineseDate(DateUtil.parse(actEndDate), false));
        msg.append(actEndDate, 11, 16);
        msg.append("接龙活动已开始，大家快来抢购！！！%0a");
        msg.append("参与活动商品\uD83D\uDC47%0a");
        for (int i = 0; i < joinSolitaire.size(); i++) {
            ActGoodsInfoResult actGoodsInfoResult = joinSolitaire.get(i);
            msg.append(i + 1).append(".").append(actGoodsInfoResult.getGoodsName()).append("    ￥")
                    .append(actGoodsInfoResult.getActPrice()).append("元%0a");
        }
        msg.append("\uD83D\uDD0D参与戳这里\uD83D\uDC47:%0a").append("https://www.superprism.cn");
        //获取所有已经绑定的群
        WechatBot bot = wechatBotService.findBy("appmodelId", appmodelId);
        List<BindingWechatGroupVo> bindingList = wechatbotGroupService.findBindingList(appmodelId, bot.getBotUin());
        List<List<String>> wxGroupList = bindingList.stream().map(BindingWechatGroupVo::getWechatGroupName)
                .collect(Collectors.toList());
        for (List<String> stringList : wxGroupList) {
            for (String s : stringList) {
                wechatBotService
                        .requestWechatBot(WechatBotApiURL.SENDMSG, RequestMethod.POST, appmodelId, s, msg.toString());
            }
        }
        return null;
    }


    /**
     * 发送接龙商品到群里面
     *
     * @param appmodelId the appmodel id
     * @param msg        the msg
     * @return the result
     */
    @PostMapping("/send-msg1")
    public Result sendInfoToWXGroup(@RequestHeader String appmodelId, String msg) {
        //获取所有已经绑定的群
        WechatBot bot = wechatBotService.findBy("appmodelId", appmodelId);
        List<BindingWechatGroupVo> bindingList = wechatbotGroupService.findBindingList(appmodelId, bot.getBotUin());
        List<List<String>> wxGroupList = bindingList.stream().map(BindingWechatGroupVo::getWechatGroupName)
                .collect(Collectors.toList());
        for (List<String> stringList : wxGroupList) {
            for (String s : stringList) {
                wechatBotService
                        .requestWechatBot(WechatBotApiURL.SENDMSG, RequestMethod.POST, appmodelId, s, msg);
            }
        }
        return null;
    }

    /**
     * 发送接龙商品到群里面
     *
     * @param appmodelId the appmodel id
     * @return the result
     */
    @PostMapping("/send-msg2")
    public Result sendSolitaireToWXGroup(@RequestHeader String appmodelId) {
        StringBuilder msg = new StringBuilder();
        msg.append("\uD83C\uDF6D接龙开始:");
        msg.append("\uD83D\uDD0D参与戳这里\uD83D\uDC47:%0a").append("https://www.superprism.cn");
        //获取所有已经绑定的群
        WechatBot bot = wechatBotService.findBy("appmodelId", appmodelId);
        List<BindingWechatGroupVo> bindingList = wechatbotGroupService.findBindingList(appmodelId, bot.getBotUin());
        List<List<String>> wxGroupList = bindingList.stream().map(BindingWechatGroupVo::getWechatGroupName)
                .collect(Collectors.toList());
        for (List<String> stringList : wxGroupList) {
            for (String s : stringList) {
                wechatBotService
                        .requestWechatBot(WechatBotApiURL.SENDMSG, RequestMethod.POST, appmodelId, s, msg.toString());
            }
        }
        return null;
    }

}
