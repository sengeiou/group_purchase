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
import com.google.common.collect.Maps;
import com.mds.group.purchase.activity.model.ActivityGoods;
import com.mds.group.purchase.activity.service.ActivityGoodsService;
import com.mds.group.purchase.configurer.GroupMallProperties;
import com.mds.group.purchase.constant.Common;
import com.mds.group.purchase.constant.RedisPrefix;
import com.mds.group.purchase.core.AbstractService;
import com.mds.group.purchase.order.model.Order;
import com.mds.group.purchase.order.model.OrderDetail;
import com.mds.group.purchase.order.service.OrderDetailService;
import com.mds.group.purchase.order.service.OrderService;
import com.mds.group.purchase.shop.model.Manager;
import com.mds.group.purchase.shop.service.ManagerService;
import com.mds.group.purchase.solitaire.dao.SolitaireRecordMapper;
import com.mds.group.purchase.solitaire.model.SolitaireRecord;
import com.mds.group.purchase.solitaire.model.SolitaireRecordSetting;
import com.mds.group.purchase.solitaire.result.PrivateSolitaireRecord;
import com.mds.group.purchase.solitaire.service.SolitaireRecordService;
import com.mds.group.purchase.solitaire.service.SolitaireRecordSettingService;
import com.mds.group.purchase.user.model.GroupLeader;
import com.mds.group.purchase.user.model.Wxuser;
import com.mds.group.purchase.user.service.GroupLeaderService;
import com.mds.group.purchase.user.service.WxuserService;
import com.mds.group.purchase.utils.SolitaireUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Condition;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


/**
 * The type Solitaire record service.
 *
 * @author shuke
 * @date 2019 /05/16
 */
@Service
public class SolitaireRecordServiceImpl extends AbstractService<SolitaireRecord> implements SolitaireRecordService {
    @Resource
    private SolitaireRecordMapper tSolitaireRecordMapper;
    @Resource
    private OrderService orderService;
    @Resource
    private OrderDetailService orderDetailService;
    @Resource
    private WxuserService wxuserService;
    @Resource
    private ManagerService managerService;
    @Resource
    private RedisTemplate redisTemplate;
    @Resource
    private GroupLeaderService groupLeaderService;
    @Resource
    private SolitaireRecordSettingService solitaireRecordSettingService;
    @Resource
    private ActivityGoodsService activityGoodsService;
    @Resource
    private SolitaireUtil solitaireUtil;

    @Override
    public void addRecord(String orderNo) {
        List<Order> orders = orderService.findByList("orderNo", orderNo);
        List<OrderDetail> orderDetails = orderDetailService
                .findByOrderIds(orders.stream().map(Order::getOrderId).collect(Collectors.toList()));
        List<ActivityGoods> activityGoodsList = activityGoodsService.findByIds(
                orderDetails.stream().map(o -> o.getActGoodsId().toString()).collect(Collectors.joining(Common.REGEX)));
        for (ActivityGoods activityGoods : activityGoodsList) {
            if (!activityGoods.getJoinSolitaire()) {
                //如果该商品没有被设置加入接龙活动，则不生成记录
                return;
            }
        }
        Wxuser wxuser = wxuserService.findById(orders.get(0).getWxuserId());
        String appmodelId = wxuser.getAppmodelId();
        SolitaireRecord solitaireRecord = new SolitaireRecord(orders, orderDetails, wxuser);
        save(solitaireRecord);
        //如果设置了满多少条自动删除多少条记录
        SolitaireRecordSetting setting = solitaireRecordSettingService.findBy("appmodelId", appmodelId);
        if (setting != null && setting.getAutoDeleteRecord() && setting.getRecordDeleteMethod() == 2) {
            //查询现在有多少条记录
            Condition condition = new Condition(SolitaireRecord.class);
            condition.createCriteria().andEqualTo("appmodelId", appmodelId);
            List<SolitaireRecord> recordCount = tSolitaireRecordMapper.selectByCondition(condition);
            if (recordCount.size() >= setting.getAttainRecordCount()) {
                String deleteIds = recordCount.stream().limit(setting.getDeleteCount()).map(o -> o.getId().toString())
                        .collect(Collectors.joining(Common.REGEX));
                tSolitaireRecordMapper.deleteByIds(deleteIds);
            }
        }
        //异步发送接龙记录微信消息
        solitaireUtil.ascySendSolitaireRecordMsg(solitaireRecord.getGroupLeaderId(), appmodelId,
                orders.get(0).getNoticeGroupLeaderFlag());
    }

    @Override
    public void deleteByAppmodelIds(List<String> canDeleteAppmodelIds) {
        Condition condition = new Condition(SolitaireRecord.class);
        condition.createCriteria().andIn("appmodelId", canDeleteAppmodelIds);
        tSolitaireRecordMapper.deleteByCondition(condition);
    }

    @Override
    public List<PrivateSolitaireRecord> findUserBuyRecord(String appmodelId, String buyerId) {
        List<PrivateSolitaireRecord> res = new ArrayList<>();
        Condition condition = new Condition(SolitaireRecord.class);
        condition.createCriteria().andEqualTo("appmodelId", appmodelId).andEqualTo("buyerId", buyerId);
        List<SolitaireRecord> solitaireRecords = tSolitaireRecordMapper.selectByCondition(condition);
        if (CollectionUtil.isNotEmpty(solitaireRecords)) {
            Map<String, List<SolitaireRecord>> collect = solitaireRecords.stream()
                    .collect(Collectors.groupingBy(SolitaireRecord::getGroupLeaderId));
            Map<String, GroupLeader> groupLeaderMap = groupLeaderService
                    .findByGroupleaderIds(new ArrayList<>(collect.keySet())).stream()
                    .collect(Collectors.toMap(GroupLeader::getGroupLeaderId, v -> v));
            solitaireRecords.forEach(o -> res.add(new PrivateSolitaireRecord(o,
                    groupLeaderMap.get(o.getGroupLeaderId()))));
        }
        //按照创建时间倒序排序
        Collections.sort(res);
        return res;
    }

    @Override
    public Map<String, String> getPersonCount(String appmodelId) {
        String redisKey = GroupMallProperties.getRedisPrefix() + appmodelId + RedisPrefix.H5_PAYED_USER_COUNT;
        Long payedCount = redisTemplate.opsForSet().size(redisKey);
        String viewKey = GroupMallProperties.getRedisPrefix() + appmodelId + RedisPrefix.H5_PAGE_VIEW;
        Long viewCount = Long.valueOf("" + redisTemplate.opsForValue().get(viewKey));
        String appmodelKey = GroupMallProperties.getRedisPrefix() + appmodelId + RedisPrefix.MINI_NAME;
        String miniName = (String) redisTemplate.opsForValue().get(appmodelKey);
        if (StringUtils.isBlank(miniName)) {
            Manager manager = managerService.findByAppmodelId(appmodelId);
            miniName = manager.getMiniName();
            redisTemplate.opsForValue().set(appmodelKey, miniName);
        }
        Map<String, String> resMap = Maps.newHashMapWithExpectedSize(3);
        resMap.put("payedCount", payedCount == null ? "0" : payedCount.toString());
        resMap.put("viewCount", viewCount.toString());
        resMap.put("miniName", miniName);
        return resMap;
    }
}
