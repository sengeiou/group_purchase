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

import com.mds.group.purchase.logistics.model.Community;
import com.mds.group.purchase.logistics.model.Line;
import com.mds.group.purchase.logistics.service.CommunityService;
import com.mds.group.purchase.logistics.service.LineService;
import com.mds.group.purchase.order.model.LineSortingOrderGoodsDetail;
import com.mds.group.purchase.order.model.Order;
import com.mds.group.purchase.order.model.OrderDetail;
import com.mds.group.purchase.order.model.SendBill;
import com.mds.group.purchase.order.service.OrderDetailService;
import com.mds.group.purchase.order.service.OrderSendBillMappingService;
import com.mds.group.purchase.order.service.OrderService;
import com.mds.group.purchase.order.service.SendBillService;
import com.mds.group.purchase.order.vo.LineSortingOrderGoodsVO;
import com.mds.group.purchase.order.vo.LineSortingOrderViewVo;
import com.mds.group.purchase.user.model.GroupLeader;
import com.mds.group.purchase.user.service.GroupLeaderService;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * The type Line sorting order util.
 *
 * @author shuke
 */
@Component
public class LineSortingOrderUtil {

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
    private OrderDetailService orderDetailService;
    @Resource
    private OrderSendBillMappingService orderSendBillMappingService;

    /**
     * Generate line sort order list.
     *
     * @param sendBillId the send bill id
     * @param appmodelId the appmodel id
     * @return the list
     */
    public List<LineSortingOrderViewVo> generateLineSortOrder(Long sendBillId, String appmodelId) {
        SendBill recentlySendBill;
        if (sendBillId < 0) {
            sendBillId = 0L;
        }
        if (sendBillId == 0) {
            recentlySendBill = sendBillService.getRecentlySendBill(appmodelId);
        } else {
            recentlySendBill = sendBillService.findById(sendBillId);
        }
        if (recentlySendBill == null) {
            return new ArrayList<>();
        }
        List<LineSortingOrderViewVo> lineSortingOrderViewVoList = new ArrayList<>();
        //根据发货单Id找出订单
        List<Long> orderIds = orderSendBillMappingService.selectAllBySendBillId(sendBillId, appmodelId);
        //根据订单id查出所有订单
        if (orderIds.isEmpty()) {
            return new ArrayList<>();
        }
        List<Order> orderList = orderService
                .findByIds(orderIds.stream().map(Object::toString).collect(Collectors.joining(",")));
        Map<Long, Order> orderMap = orderList.stream().collect(Collectors.toMap(Order::getOrderId, v -> v));
        //查询所有团长
        List<String> groupleaderIdList = orderList.stream().map(Order::getGroupId).distinct()
                .collect(Collectors.toList());
        Map<String, GroupLeader> groupLeaderMap = groupLeaderService.findByGroupleaderIds(groupleaderIdList).stream()
                .collect(Collectors.toMap(GroupLeader::getGroupLeaderId, v -> v));
        //查询所有订单详情
        List<OrderDetail> orderDetails = orderDetailService.findByOrderIds(orderIds);
        Map<Long, List<OrderDetail>> detailLine = orderDetails.stream()
                .collect(Collectors.groupingBy(OrderDetail::getLineId));
        //查询出所有线路
        String lineIds = orderDetails.stream().map(obj -> obj.getLineId().toString()).distinct()
                .collect(Collectors.joining(","));
        List<Line> lineList = lineService.findByIds(lineIds);
        //查询小区
        String communityIds = orderDetails.stream().filter(o -> o.getCommunityId() != null)
                .map(obj -> obj.getCommunityId().toString()).distinct().collect(Collectors.joining(","));
        List<Community> communityList = communityService.findByIds(communityIds);
        Map<Long, Community> communityMap = communityList.stream()
                .collect(Collectors.toMap(Community::getCommunityId, v -> v));
        //记录当前发货单生成的线路分拣单id
        int index = 1;
        //循环线路添加线路分拣单
        for (Line line : lineList) {
            //添加线路分拣单
            LineSortingOrderViewVo lineSortingOrder = new LineSortingOrderViewVo();
            lineSortingOrder.setId(index++);
            lineSortingOrder.setSendBillName(recentlySendBill.getSendBillName());
            lineSortingOrder.setDriverName(line.getDriverName());
            lineSortingOrder.setPhone(line.getDriverPhone());
            lineSortingOrder.setLineName(line.getLineName());
            List<LineSortingOrderGoodsVO> lineSortingOrderGoodsVOS = new ArrayList<>();
            //添加线路分拣单详情
            List<OrderDetail> orderDetailsList = detailLine.get(line.getLineId());
            //按活动分组
            Map<Long, List<OrderDetail>> actOrderDetail = orderDetailsList.stream()
                    .collect(Collectors.groupingBy(OrderDetail::getActivityId));
            actOrderDetail.forEach((activityId, actOd) -> {
                //按商品分组
                Map<Long, List<OrderDetail>> goodsIdMap = actOd.stream()
                        .collect(Collectors.groupingBy(OrderDetail::getGoodsId));
                goodsIdMap.forEach((goodsId, orderDetailList) -> {
                    OrderDetail details = orderDetailList.get(0);
                    LineSortingOrderGoodsVO lineSortingOrderGoods = new LineSortingOrderGoodsVO();
                    lineSortingOrderGoods.setGoodsImage(details.getGoodsImg());
                    lineSortingOrderGoods.setGoodsName(details.getGoodsName());
                    lineSortingOrderGoods
                            .setGoodsSum(orderDetailList.stream().mapToInt(OrderDetail::getGoodsNum).sum());
                    //找出小区对应的商品数量
                    List<LineSortingOrderGoodsDetail> lineSortingOrderGoodsDetailList = new ArrayList<>();
                    for (OrderDetail od : orderDetailList) {
                        Order order = orderMap.get(od.getOrderId());
                        GroupLeader groupLeader = groupLeaderMap.get(order.getGroupId());
                        Community community = communityMap.get(groupLeader.getCommunityId());
                        if (community == null) {
                            continue;
                        }
                        LineSortingOrderGoodsDetail lineSortingOrderGoodsDetail = new LineSortingOrderGoodsDetail();
                        lineSortingOrderGoodsDetail.setCommunityId(community.getCommunityId());
                        lineSortingOrderGoodsDetail.setCommunityName(community.getCommunityName());
                        lineSortingOrderGoodsDetail.setGoodsNumber(od.getGoodsNum());
                        lineSortingOrderGoodsDetailList.add(lineSortingOrderGoodsDetail);
                    }
                    Map<Long, List<LineSortingOrderGoodsDetail>> collect = lineSortingOrderGoodsDetailList.stream()
                            .collect(Collectors.groupingBy(LineSortingOrderGoodsDetail::getCommunityId));
                    List<LineSortingOrderGoodsDetail> lineSortingOrderGoodsDetailListResult = new ArrayList<>();
                    collect.forEach((id, detail) -> {
                        Integer goodsNum = 0;
                        for (LineSortingOrderGoodsDetail lineSortingOrderGoodsDetail : detail) {
                            goodsNum = goodsNum + lineSortingOrderGoodsDetail.getGoodsNumber();
                        }
                        LineSortingOrderGoodsDetail lineSortingOrderGoodsDetail = detail.get(0);
                        lineSortingOrderGoodsDetail.setGoodsNumber(goodsNum);
                        lineSortingOrderGoodsDetailListResult.add(lineSortingOrderGoodsDetail);
                    });

                    lineSortingOrderGoods.setLineSortingOrderDetailList(lineSortingOrderGoodsDetailListResult);
                    lineSortingOrderGoodsVOS.add(lineSortingOrderGoods);
                });
                lineSortingOrder.setLineSortingOrderGoodsVOS(lineSortingOrderGoodsVOS);
            });
            lineSortingOrderViewVoList.add(lineSortingOrder);
        }
        return lineSortingOrderViewVoList;
    }
}
