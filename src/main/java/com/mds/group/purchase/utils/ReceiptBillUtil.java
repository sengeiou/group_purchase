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

package com.mds.group.purchase.utils;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.NumberUtil;
import com.mds.group.purchase.constant.ReceiptBillConstant;
import com.mds.group.purchase.logistics.model.Community;
import com.mds.group.purchase.logistics.model.Line;
import com.mds.group.purchase.logistics.service.CommunityService;
import com.mds.group.purchase.logistics.service.LineService;
import com.mds.group.purchase.order.model.ReceiptBill;
import com.mds.group.purchase.order.model.ReceiptBillDetail;
import com.mds.group.purchase.order.model.SendBill;
import com.mds.group.purchase.order.result.OrderResult;
import com.mds.group.purchase.order.result.ReceiptBillResult;
import com.mds.group.purchase.order.service.*;
import com.mds.group.purchase.user.model.GroupLeader;
import com.mds.group.purchase.user.service.GroupLeaderService;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * The type Receipt bill util.
 *
 * @author shuke
 * @since v1.2
 */
@Component
public class ReceiptBillUtil {

    @Resource
    private LineService lineService;
    @Resource
    private OrderService orderService;
    @Resource
    private SendBillService sendBillService;
    @Resource
    private CommunityService communityService;
    @Resource
    private GroupLeaderService groupLeaderService;
    @Resource
    private ReceiptBillService receiptBillService;
    @Resource
    private ReceiptBillDetailService receiptBillDetailService;
    @Resource
    private OrderSendBillMappingService orderSendBillMappingService;

    /**
     * Generate receipt bill.
     *
     * @param sendBillId the send bill id
     * @param appmodelId the appmodel id
     */
    public void generateReceiptBill(Long sendBillId, String appmodelId) {
        SendBill recentlySendBill;
        if (sendBillId == 0) {
            recentlySendBill = sendBillService.getRecentlySendBill(appmodelId);
        } else {
            recentlySendBill = sendBillService.findById(sendBillId);
        }
        if (recentlySendBill == null) {
            return;
        }
        //删除之前生成的签收单
        receiptBillService.deleteReceiptBySendBillId(sendBillId, appmodelId);

        //根据发货单Id找出已生成的发货订单
        List<Long> orderIds = orderSendBillMappingService
                .selectAllBySendBillId(recentlySendBill.getSendBillId(), appmodelId);
        //根据订单id查出所有订单
        if (orderIds.isEmpty()) {
            return ;
        }
        List<OrderResult> orderList = orderService.findOrderResultByOrderIds(orderIds);
        //按团长分组订单
        Map<String, List<OrderResult>> groupOrder = orderList.stream()
                .collect(Collectors.groupingBy(OrderResult::getGroupId));

        List<Long> lineIdList = orderList.stream().map(OrderResult::getLineId).distinct().collect(Collectors.toList());
        List<Line> lineList = lineService
                .findByIds(lineIdList.stream().map(Object::toString).collect(Collectors.joining(",")));
        Map<Long, Line> lineMap = lineList.stream().collect(Collectors.toMap(Line::getLineId, v -> v));
        //查询小区
        String communityIds = orderList.stream().map(obj -> obj.getCommunityId().toString()).distinct()
                .collect(Collectors.joining(","));
        List<Community> communityList = communityService.findByIds(communityIds);
        Map<Long, Community> communityMap = communityList.stream()
                .collect(Collectors.toMap(Community::getCommunityId, v -> v));

        //查询所有团长
        List<String> groupleaderIdList = orderList.stream().map(OrderResult::getGroupId).distinct()
                .collect(Collectors.toList());
        Map<String, GroupLeader> groupLeaderMap = groupLeaderService.findByGroupleaderIds(groupleaderIdList).stream()
                .collect(Collectors.toMap(GroupLeader::getGroupLeaderId, v -> v));

        DateTime generateTime = DateUtil.date();
        //生成团长签收单
        groupOrder.forEach((groupLeaderId, orders) -> {
            GroupLeader groupLeader = groupLeaderMap.get(groupLeaderId);
            Community community = communityMap.get(groupLeader.getCommunityId());
            if (community == null) {
                community = communityService.getCommunityById(groupLeader.getCommunityId());
            }
            ReceiptBill receiptBill = new ReceiptBill();
            OrderResult or = orders.get(0);
            Line line = lineMap.get(or.getLineId());
            receiptBill.setCommunityId(community.getCommunityId());
            receiptBill.setCommunityName(community.getCommunityName());
            receiptBill.setStatus(ReceiptBillConstant.WAIT_SEND);
            receiptBill.setGroupLeaderId(groupLeaderId);
            receiptBill.setGroupLeaderName(groupLeader.getGroupName());
            receiptBill.setGroupLeaderPhone(groupLeader.getGroupPhone());
            receiptBill.setLineId(line.getLineId());
            receiptBill.setLineName(line.getLineName());
            receiptBill.setDriverName(line.getDriverName());
            receiptBill.setPhone(line.getDriverPhone());
            receiptBill.setAppmodelId(groupLeader.getAppmodelId());
            receiptBill.setCreateTime(generateTime);
            receiptBill.setSendBillId(recentlySendBill.getSendBillId());
            receiptBill.setSendBillName(recentlySendBill.getSendBillName());
            receiptBillService.save(receiptBill);
            ReceiptBillResult receiptBillResult = new ReceiptBillResult();
            BeanUtil.copyProperties(receiptBill, receiptBillResult);
            List<ReceiptBillDetail> receiptBillDetails = new ArrayList<>();
            Map<Long, List<OrderResult>> collect = orders.stream()
                    .collect(Collectors.groupingBy(OrderResult::getGoodsId));
            collect.forEach((o, orderResults) -> {
                ReceiptBillDetail receiptBillDetail = new ReceiptBillDetail();
                receiptBillDetail.setBillId(receiptBill.getBillId());
                receiptBillDetail.setOrderIds(orderResults.stream().map(OrderResult::getOrderId).map(String::valueOf).distinct().collect(Collectors.joining(",")));
                receiptBillDetail.setOrderDetailIds(orderResults.stream().map(OrderResult::getOrderDetailId).map(String::valueOf).distinct().collect(Collectors.joining(",")));
                receiptBillDetail.setActivityId(orderResults.get(0).getActivityId());
                receiptBillDetail.setAppmodelId(orderResults.get(0).getAppmodelId());
                receiptBillDetail.setGoodsImg(orderResults.get(0).getGoodsImg());
                Integer goodsNum = orderResults.stream().mapToInt(OrderResult::getGoodsNum).sum();
                BigDecimal groupLeaderCommission = orderResults.stream().map(OrderResult::getGroupLeaderCommission).reduce(BigDecimal.ZERO, BigDecimal::add);
                receiptBillDetail.setReceiptGoodsNum(0);
                receiptBillDetail.setGroupLeaderCommission(groupLeaderCommission);
                receiptBillDetail.setLeaderAfterSaleNum(0);
                receiptBillDetail.setGoodsNum(goodsNum);
                receiptBillDetail.setGoodsName(orderResults.get(0).getGoodsName());
                receiptBillDetail.setGoodsId(o);
                BigDecimal sub = NumberUtil.sub(orderResults.get(0).getGoodsPrice(), orderResults.get(0).getPreferential());
                receiptBillDetail.setPrice(sub);
                receiptBillDetails.add(receiptBillDetail);
                receiptBillResult.setReceiptBillDetailList(receiptBillDetails);
            });
            receiptBillDetailService.save(receiptBillDetails);
        });
    }
}
