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

package com.mds.group.purchase.wechatbot.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.mds.group.purchase.constant.Common;
import com.mds.group.purchase.core.AbstractService;
import com.mds.group.purchase.core.CodeMsg;
import com.mds.group.purchase.core.Result;
import com.mds.group.purchase.exception.ServiceException;
import com.mds.group.purchase.logistics.model.Community;
import com.mds.group.purchase.logistics.model.Line;
import com.mds.group.purchase.logistics.service.CommunityService;
import com.mds.group.purchase.logistics.service.LineService;
import com.mds.group.purchase.user.model.GroupLeader;
import com.mds.group.purchase.user.model.Wxuser;
import com.mds.group.purchase.user.service.GroupLeaderService;
import com.mds.group.purchase.user.service.WxuserService;
import com.mds.group.purchase.utils.bot.WechatbotGroupUtil;
import com.mds.group.purchase.wechatbot.dao.WechatbotGroupMapper;
import com.mds.group.purchase.wechatbot.model.WechatBot;
import com.mds.group.purchase.wechatbot.model.WechatbotGroup;
import com.mds.group.purchase.wechatbot.service.WechatBotService;
import com.mds.group.purchase.wechatbot.service.WechatbotGroupService;
import com.mds.group.purchase.wechatbot.vo.BindingWechatGroupVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Condition;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


/**
 * The type Wechatbot group service.
 *
 * @author shuke
 * @date 2019 /05/15
 */
@Service
public class WechatbotGroupServiceImpl extends AbstractService<WechatbotGroup> implements WechatbotGroupService {
    @Resource
    private WechatbotGroupMapper tWechatbotGroupMapper;
    @Resource
    private GroupLeaderService groupLeaderService;
    @Resource
    private WechatBotService wechatBotService;
    @Resource
    private LineService lineService;
    @Resource
    private WxuserService wxuserService;
    @Resource
    private CommunityService communityService;
    @Resource
    private WechatbotGroupUtil wechatbotGroupUtil;

    @Override
    public Result bindingGroupLeaderToBot(String appmodelId, Long botUin, String groupLeaderId,
                                          String wechatGroupNickName) {
        //判断已绑定的群聊数量是否超过5000个
        int bindGroupCount = findBindGroupCount(appmodelId, botUin);
        if (bindGroupCount > 5000) {
            return Result.error(CodeMsg.FAIL.fillArgs("不能再绑定更多的群聊了"));
        }
        //1验证团长是否存在,并且时正常状态
        GroupLeader groupLeader = groupLeaderService.findById(groupLeaderId);
        if (groupLeader != null) {
            if (GroupLeader.Status.NORMAL != groupLeader.getStatus()) {
                return Result.error(CodeMsg.FAIL.fillArgs("团长状态错误"));
            }
            if (groupLeader.getDeleteState()) {
                return Result.error(CodeMsg.FAIL.fillArgs("团长已经被删除"));
            }
            //2绑定团长和微信群
            Condition condition1 = new Condition(WechatBot.class);
            condition1.createCriteria().andEqualTo("appmodelId", appmodelId).andEqualTo("botUin", botUin);
            WechatBot bot = wechatBotService.findByOneCondition(condition1);
            if (bot == null) {
                return Result.error(CodeMsg.FAIL.fillArgs("机器人不存在"));
            }
            String[] names = wechatGroupNickName.split(Common.REGEX);
            for (String name : names) {
                WechatbotGroup wechatbotGroup = new WechatbotGroup();
                wechatbotGroup.setAppmodelId(appmodelId);
                wechatbotGroup.setCreateTime(System.currentTimeMillis());
                wechatbotGroup.setGroupLeaderId(groupLeaderId);
                wechatbotGroup.setModifyTime(System.currentTimeMillis());
                wechatbotGroup.setWechatbotId(bot.getBotId());
                wechatbotGroup.setWechatGroupName(name);
                tWechatbotGroupMapper.insert(wechatbotGroup);
            }
            //刷新缓存
            wechatbotGroupUtil.flushBindedGroupCache(appmodelId, bot.getBotId());
            return Result.success("绑定成功");
        } else {
            return Result.error(CodeMsg.FAIL.fillArgs("团长不存在"));
        }
    }

    @Override
    public List<BindingWechatGroupVo> findBindingList(String appmodelId, Long botUin) {
        //查询机器人
        Condition condition1 = new Condition(WechatBot.class);
        condition1.createCriteria().andEqualTo("appmodelId", appmodelId).andEqualTo("botUin", botUin);
        WechatBot bot = wechatBotService.findByOneCondition(condition1);
        if (bot == null) {
            throw new ServiceException("机器人不存在");
        }
        List<BindingWechatGroupVo> bindingWechatGroupVos = new ArrayList<>();
        //1·查出appmodelId对应的已绑定的群聊信息
        Condition condition = new Condition(WechatbotGroup.class);
        condition.createCriteria().andEqualTo("appmodelId", appmodelId).andEqualTo("wechatbotId", bot.getBotId());
        List<WechatbotGroup> wechatbotGroupList = findByCondition(condition);
        if (CollectionUtil.isNotEmpty(wechatbotGroupList)) {
            //2·查出群聊信息的团长信息
            List<GroupLeader> groupLeaderList = groupLeaderService.findByGroupleaderIds(
                    wechatbotGroupList.stream().map(WechatbotGroup::getGroupLeaderId).distinct()
                            .collect(Collectors.toList()));
            //3·查出团长信息的线路信息

            String ids = groupLeaderList.stream().filter(o -> o.getLineId() != null).map(o -> o.getLineId().toString())
                    .distinct().collect(Collectors.joining(Common.REGEX));
            if (StringUtils.isBlank(ids)) {
                return bindingWechatGroupVos;
            }
            List<Line> lineList = lineService.findByIds(ids);
            //4·查出团长的头像
            List<Wxuser> wxusers = wxuserService.findByIds(
                    groupLeaderList.stream().filter(o -> o.getWxuserId() != null).map(o -> o.getWxuserId().toString())
                            .distinct().collect(Collectors.joining(Common.REGEX)));
            //5·查出团长的小区信息
            List<Community> communityList = communityService.findByIds(
                    groupLeaderList.stream().filter(o -> o.getCommunityId() != null)
                            .map(o -> o.getCommunityId().toString()).distinct()
                            .collect(Collectors.joining(Common.REGEX)));
            //6·将数据封装到BindingWechatGroupVo 中
            Map<String, List<WechatbotGroup>> wechatbotGroupMap = wechatbotGroupList.stream()
                    .collect(Collectors.groupingBy(WechatbotGroup::getGroupLeaderId));
            Map<String, GroupLeader> groupLeaderMap = groupLeaderList.stream()
                    .collect(Collectors.toMap(GroupLeader::getGroupLeaderId, v -> v));
            Map<String, String> lineMap = lineList.stream()
                    .collect(Collectors.toMap(k -> k.getLineId().toString(), Line::getLineName));
            Map<String, String> groupIconMap = wxusers.stream()
                    .collect(Collectors.toMap(k -> k.getWxuserId().toString(), Wxuser::getIcon));
            Map<String, String> communityNameMap = communityList.stream()
                    .collect(Collectors.toMap(k -> k.getCommunityId().toString(), Community::getCommunityName));
            wechatbotGroupMap.keySet().forEach(id -> {
                List<WechatbotGroup> wechatbotGroups = wechatbotGroupMap.get(id);
                GroupLeader groupLeader = groupLeaderMap.get(id);
                if (groupLeader != null) {
                    String lineName = lineMap.get(groupLeader.getLineId().toString());
                    String groupIcon = groupIconMap.get(groupLeader.getWxuserId().toString());
                    String communityName = communityNameMap.get(groupLeader.getCommunityId().toString());
                    BindingWechatGroupVo vo = new BindingWechatGroupVo(wechatbotGroups, groupLeader, lineName,
                            groupIcon,
                            communityName);
                    bindingWechatGroupVos.add(vo);
                }
            });
        }
        return bindingWechatGroupVos;
    }

    @Override
    public boolean deleteByGroupId(String appmodelId, String groupLeaderId, Long botUin) {
        //获取botid
        Condition condition1 = new Condition(WechatBot.class);
        condition1.createCriteria().andEqualTo("appmodelId", appmodelId).andEqualTo("botUin", botUin);
        WechatBot bot = wechatBotService.findByOneCondition(condition1);
        Long botId = bot.getBotId();
        Condition condition = new Condition(WechatbotGroup.class);
        condition.createCriteria().andEqualTo("appmodelId", appmodelId)
                .andEqualTo("groupLeaderId", groupLeaderId).andEqualTo("wechatbotId", botId);
        int modifyCloums = tWechatbotGroupMapper.deleteByCondition(condition);
        //刷新缓存
        wechatbotGroupUtil.flushBindedGroupCache(appmodelId, botId);
        return modifyCloums != 0;
    }

    @Override
    public void deleteByBotId(String appmodelId, Long botId) {
        Condition condition = new Condition(WechatbotGroup.class);
        condition.createCriteria().andEqualTo("appmodelId", appmodelId).andEqualTo("wechatbotId", botId);
        tWechatbotGroupMapper.deleteByCondition(condition);
        //刷新缓存
        wechatbotGroupUtil.flushBindedGroupCache(appmodelId, botId);
    }

    @Override
    public int deleteByGroupIds(String wechatbotGroupIds, String appmodelId) {
        List<WechatbotGroup> groupList = findByIds(wechatbotGroupIds);
        if (CollectionUtil.isEmpty(groupList)) {
            return 0;
        }
        Long botId = groupList.get(0).getWechatbotId();
        int i = deleteByIds(wechatbotGroupIds);
        //刷新缓存
        wechatbotGroupUtil.flushBindedGroupCache(appmodelId, botId);
        return i;
    }

    @Override
    public int findBindGroupCount(String appmodelId, Long botUin) {
        Condition condition1 = new Condition(WechatBot.class);
        condition1.createCriteria().andEqualTo("appmodelId", appmodelId).andEqualTo("botUin", botUin);
        WechatBot bot = wechatBotService.findByOneCondition(condition1);
        Condition condition = new Condition(WechatbotGroup.class);
        condition.createCriteria().andEqualTo("wechatbotId", bot.getBotId()).andEqualTo("appmodelId", appmodelId);
        List<WechatbotGroup> wechatbotGroups = findByCondition(condition);
        return wechatbotGroups.size();
    }

}
