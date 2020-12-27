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

package com.mds.group.purchase.solitaire.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Strings;
import com.mds.group.purchase.activity.result.ActGoodsInfoResult;
import com.mds.group.purchase.constant.ActivityConstant;
import com.mds.group.purchase.constant.Common;
import com.mds.group.purchase.constant.WechatBotApiURL;
import com.mds.group.purchase.exception.ServiceException;
import com.mds.group.purchase.solitaire.model.SolitaireRecord;
import com.mds.group.purchase.solitaire.service.SolitaireMsgService;
import com.mds.group.purchase.solitaire.service.SolitaireRecordService;
import com.mds.group.purchase.solitaire.service.SolitaireSettingService;
import com.mds.group.purchase.solitaire.vo.SolitaireSettingVo;
import com.mds.group.purchase.user.model.GroupLeader;
import com.mds.group.purchase.user.service.GroupLeaderService;
import com.mds.group.purchase.utils.ActivityGoodsUtil;
import com.mds.group.purchase.wechatbot.model.WechatBot;
import com.mds.group.purchase.wechatbot.model.WechatbotGroup;
import com.mds.group.purchase.wechatbot.service.WechatBotService;
import com.mds.group.purchase.wechatbot.service.WechatbotGroupService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMethod;
import tk.mybatis.mapper.entity.Condition;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * The type Solitaire msg service.
 *
 * @author pavawi
 */
@Service
public class SolitaireMsgServiceImpl implements SolitaireMsgService {

    @Resource
    private ActivityGoodsUtil activityGoodsUtil;
    @Resource
    private WechatBotService wechatBotService;
    @Resource
    private WechatbotGroupService wechatbotGroupService;
    @Resource
    private SolitaireRecordService solitaireRecordService;
    @Resource
    private SolitaireSettingService solitaireSettingService;
    @Resource
    private GroupLeaderService groupLeaderService;

    private Logger log = LoggerFactory.getLogger(SolitaireMsgServiceImpl.class);


    @Override
    public void sendStartSolitaireActMsg(String appmodelId) {
        //获取所有已经绑定的群
        WechatBot bot = wechatBotService.findBy("appmodelId", appmodelId);
        Condition condition = new Condition(WechatbotGroup.class);
        condition.createCriteria().andEqualTo("wechatbotId", bot.getBotId());
        List<WechatbotGroup> wechatbotGroups = wechatbotGroupService.findByCondition(condition);
        List<String> wxGroupList = wechatbotGroups.stream().map(WechatbotGroup::getWechatGroupName)
                .collect(Collectors.toList());
        sendStartSolitaireActMsgToGroups(appmodelId, wechatbotGroups);
    }

    @Override
    public void sendStartSolitaireActMsgToGroups(String appmodelId, List<WechatbotGroup> wxGroupList) {
        Map<String, ActGoodsInfoResult> allActGoodsResultCache = activityGoodsUtil
                .getAllActGoodsResultCache(appmodelId);
        List<ActGoodsInfoResult> joinSolitaire;
        if (allActGoodsResultCache == null) {
            joinSolitaire = new ArrayList<>();
        } else {
            //筛选已经开始售卖并且参加接龙活动的活动商品
            joinSolitaire = allActGoodsResultCache.values().stream()
                    .filter(o -> ActivityConstant.ACTIVITY_STATUS_START.equals(o.getActivityStatus()) && o
                            .getJoinSolitaire()).collect(Collectors.toList());
        }
        StringBuilder msg = new StringBuilder();
        msg.append("接龙活动已开始，大家快来抢购！！！%0a");
        msg.append("参与活动商品\uD83D\uDC47%0a");
        for (int i = 0; i < joinSolitaire.size(); i++) {
            ActGoodsInfoResult actGoodsInfoResult = joinSolitaire.get(i);
            msg.append(i + 1).append(".").append(actGoodsInfoResult.getGoodsName()).append("    ￥")
                    .append(actGoodsInfoResult.getActPrice()).append("元%0a");
        }
        msg.append("\uD83D\uDD0D参与戳这里\uD83D\uDC47:%0a").append("https://www.superprism.cn/groupMallH5/?")
                .append(appmodelId).append("&");
        for (WechatbotGroup group : wxGroupList) {
            GroupLeader groupLeader = groupLeaderService.findById(group.getGroupLeaderId());
            String mssage = msg.toString().replace("&", "" + groupLeader.getCommunityId());
            log.info("接龙测试" + mssage);
            String s = wechatBotService.requestWechatBot(WechatBotApiURL.SENDMSG, RequestMethod.POST, appmodelId,
                    group.getWechatGroupName(), mssage);
            log.info("消息发送结果+++++" + s);
        }
    }

    @Override
    public void sendStartSolitaireActMsgToGroups(String appmodelId, String wxGroups) {
        if (!Strings.isNullOrEmpty(wxGroups)) {
            List<String> wxGroupList = Arrays.asList(wxGroups.split(Common.REGEX));
            Condition condition = new Condition(WechatbotGroup.class);
            condition.createCriteria().andEqualTo("appmodelId", appmodelId).andIn("wechatGroupName", wxGroupList);
            List<WechatbotGroup> wechatbotGroupList = wechatbotGroupService.findByCondition(condition);
            sendStartSolitaireActMsgToGroups(appmodelId, wechatbotGroupList);
        }
    }

    @Override
    public void sendSolitaireRecordActMsg(String appmodelId, String groupLeaderId) {
        log.info("开始发送接龙微信消息" + appmodelId + "," + groupLeaderId);
        GroupLeader groupLeader = groupLeaderService.findById(groupLeaderId);
        Condition condition = new Condition(SolitaireRecord.class);
        condition.createCriteria().andEqualTo("appmodelId", appmodelId).andEqualTo("groupLeaderId", groupLeaderId);
        List<SolitaireRecord> recordList = solitaireRecordService.findByCondition(condition);
        //只发送10条记录，中间的记录用省略号代替
        //记录多于10条时，展示第一条和最近9条，中间数据省略
        // 排序
        List<SolitaireRecord> res;
        Collections.sort(recordList);
        int index;
        if (recordList.size() > 10) {
            res = new ArrayList<>();
            res.add(recordList.get(0));
            List<SolitaireRecord> last9 = recordList.stream().skip(recordList.size() - 9).collect(Collectors.toList());
            res.addAll(last9);
            index = recordList.size() - 9;
        } else {
            res = recordList;
            index = 1;
        }
        StringBuilder msg = new StringBuilder();
        SolitaireSettingVo solitaireInfo = solitaireSettingService.getSolitaireInfo(appmodelId);
        msg.append(solitaireInfo.getDesc()).append("%0a");
        msg.append("\uD83C\uDF6D接龙开始:%0a");
        for (int i = 0; i < res.size(); i++) {
            SolitaireRecord solitaireRecord = res.get(i);
            if (i == 0) {
                msg.append(1).append(".").append(solitaireRecord.getBuyerName()).append(":");
            } else {
                index++;
                msg.append(index).append(".").append(solitaireRecord.getBuyerName()).append(":");
            }
            JSONArray jsonArray = JSONArray.parseArray(solitaireRecord.getRecordDetail());
            for (int j = 0; j < jsonArray.size(); j++) {
                JSONObject jsonObject = jsonArray.getJSONObject(j);
                msg.append(jsonObject.get("goodsName")).append("➕+").append(jsonObject.get("goodsNum")).append("%0a");
            }
            if (recordList.size() > 10 && i == 0) {
                msg.append("...%0a");
                msg.append("...%0a");
            }
        }
        msg.append("\uD83D\uDD0D参与戳这里\uD83D\uDC47:%0a").append("https://www.superprism.cn/groupMallH5/?")
                .append(appmodelId).append(groupLeader.getCommunityId());
        //获取团长已经绑定的群
        List<WechatBot> bots = wechatBotService.findByList("appmodelId", appmodelId);
        //筛选在线的机器人
        bots = bots.stream().filter(WechatBot::getOnline).collect(Collectors.toList());
        if (CollectionUtil.isEmpty(bots)) {
            return;
        } else if (bots.size() > 1) {
            throw new ServiceException("机器人在线状态错误，请校验");
        } else {
            Condition condition1 = new Condition(WechatbotGroup.class);
            condition1.createCriteria().andEqualTo("groupLeaderId", groupLeaderId)
                    .andEqualTo("wechatbotId", bots.get(0).getBotId());
            List<WechatbotGroup> groupBindList = wechatbotGroupService.findByCondition(condition1);
            for (WechatbotGroup wechatbotGroup : groupBindList) {
                String s = wechatBotService.requestWechatBot(WechatBotApiURL.SENDMSG, RequestMethod.POST, appmodelId,
                        wechatbotGroup.getWechatGroupName(), msg.toString());
                log.info(s);
                log.info(wechatbotGroup.getWechatGroupName() + ":" + appmodelId);
            }
            log.info("发送接龙微信消息成功" + msg);
        }
    }


    @Override
    public void sendAtMsg(String appmodelId, String groupLeaderId, String buyerName, String goodsName,
                          String groupLeaderName) {
        String msg = "@%s 你购买的%s已经到货了，请前往%s提货点提货哟！！！";
        msg = String.format(msg, buyerName, goodsName, groupLeaderName);
        //获取团长已经绑定的群
        List<WechatBot> bots = wechatBotService.findByList("appmodelId", appmodelId);
        //筛选在线的机器人
        bots = bots.stream().filter(WechatBot::getOnline).collect(Collectors.toList());
        if (CollectionUtil.isEmpty(bots)) {
            return;
        } else if (bots.size() > 1) {
            throw new ServiceException("机器人在线状态错误，请校验");
        } else {
            Condition condition1 = new Condition(WechatbotGroup.class);
            condition1.createCriteria().andEqualTo("groupLeaderId", groupLeaderId)
                    .andEqualTo("wechatbotId", bots.get(0).getBotId());
            List<WechatbotGroup> groupBindList = wechatbotGroupService.findByCondition(condition1);
            for (WechatbotGroup wechatbotGroup : groupBindList) {
                wechatBotService.requestWechatBot(WechatBotApiURL.SENDMSG, RequestMethod.POST, appmodelId,
                        wechatbotGroup.getWechatGroupName(), msg);
            }
        }

    }
}
