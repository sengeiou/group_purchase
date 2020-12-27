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

import cn.hutool.core.collection.CollectionUtil;
import com.mds.group.purchase.logistics.model.Line;
import com.mds.group.purchase.logistics.service.LineService;
import com.mds.group.purchase.order.model.GoodsSortingOrderDetail;
import com.mds.group.purchase.order.model.OrderDetail;
import com.mds.group.purchase.order.model.SendBill;
import com.mds.group.purchase.order.service.OrderDetailService;
import com.mds.group.purchase.order.service.OrderSendBillMappingService;
import com.mds.group.purchase.order.service.SendBillService;
import com.mds.group.purchase.order.vo.GoodsSortingOrderViewVO;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * The type Goods sorting order util.
 *
 * @author shuke
 * @since v1.2
 */
@Component
public class GoodsSortingOrderUtil {

    @Resource
    private LineService lineService;
    @Resource
    private SendBillService sendBillService;
    @Resource
    private OrderDetailService orderDetailService;
    @Resource
    private OrderSendBillMappingService orderSendBillMappingService;


    /**
     * Generate goods sort order list.
     *
     * @param sendBillId the send bill id
     * @param appmodelId the appmodel id
     * @return the list
     */
    public List<GoodsSortingOrderViewVO> generateGoodsSortOrder(Long sendBillId, String appmodelId) {
        SendBill recentlySendBill;
        if (sendBillId < 0) {
            sendBillId = 0L;
        }
        if (sendBillId == 0) {
            recentlySendBill = sendBillService.getRecentlySendBill(appmodelId);
            if (recentlySendBill == null) {
                return new ArrayList<>();
            }
            sendBillId = recentlySendBill.getSendBillId();
        } else {
            recentlySendBill = sendBillService.findById(sendBillId);
        }
        if (recentlySendBill == null) {
            return new ArrayList<>();
        }
        List<GoodsSortingOrderViewVO> sortingOrders = new ArrayList<>();
        List<Long> orderIds = orderSendBillMappingService.selectAllBySendBillId(sendBillId, appmodelId);
        if (orderIds.isEmpty()) {
            return new ArrayList<>();
        }
        SendBill sendBill = sendBillService.findById(sendBillId);
        //查询所有订单详情
        List<OrderDetail> orderDetails = orderDetailService.findByOrderIds(orderIds);
        if (CollectionUtil.isEmpty(orderDetails)) {
            return new ArrayList<>();
        }
        //查询出所有线路
        if (orderDetails.isEmpty()) {
            return new ArrayList<>();
        }
        String lineIds = orderDetails.stream().map(obj -> obj.getLineId().toString()).distinct()
                .collect(Collectors.joining(","));
        List<Line> lineList = lineService.findByIds(lineIds);
        Map<Long, Line> lineMap = lineList.stream().collect(Collectors.toMap(Line::getLineId, v -> v));
        //按商品分组
        Map<Long, List<OrderDetail>> goodsGroup = orderDetails.stream()
                .collect(Collectors.groupingBy(OrderDetail::getGoodsId));
        Long finalSendBillId = sendBillId;
        List<GoodsSortingOrderViewVO> finalSortingOrders = sortingOrders;
        final int[] index = {1};
        goodsGroup.forEach((goodsId, orderDeails) -> {
            OrderDetail orderDetail = orderDeails.get(0);
            GoodsSortingOrderViewVO sortingOrder = new GoodsSortingOrderViewVO();
            sortingOrder.setId(index[0]);
            sortingOrder.setPrice(orderDetail.getGoodsPrice().toString());
            sortingOrder.setGoodsId(orderDetail.getGoodsId());
            sortingOrder.setGoodsName(orderDetail.getGoodsName());
            sortingOrder.setGoodsImage(orderDetail.getGoodsImg());
            int goodsTotleSum = orderDeails.stream().mapToInt(OrderDetail::getGoodsNum).sum();
            sortingOrder.setGoodsTotleSum(goodsTotleSum);
            sortingOrder.setAppmodelId(orderDetail.getAppmodelId());
            sortingOrder.setSendBillId(finalSendBillId);
            sortingOrder.setSendBillName(sendBill.getSendBillName());
            List<GoodsSortingOrderDetail> goodsSortingOrderDetails = new ArrayList<>();
            Map<Long, List<OrderDetail>> collect = orderDeails.parallelStream()
                    .collect(Collectors.groupingBy(OrderDetail::getLineId));
            collect.forEach((k, details) -> {
                OrderDetail orderDetail1 = details.get(0);
                if (orderDetail1 != null) {
                    Integer goodsNum = 0;
                    for (OrderDetail detail : details) {
                        goodsNum = detail.getGoodsNum() + goodsNum;
                    }
                    GoodsSortingOrderDetail sortingOrderDetail = new GoodsSortingOrderDetail();
                    sortingOrderDetail.setActGoodsId(details.get(0).getActGoodsId());
                    sortingOrderDetail.setGoodsNumber(goodsNum);
                    sortingOrderDetail.setActivityId(details.get(0).getActivityId());
                    sortingOrderDetail.setActivityType(details.get(0).getOrderType());
                    sortingOrderDetail.setLineId(details.get(0).getLineId());
                    sortingOrderDetail.setLineName(lineMap.get(details.get(0).getLineId()).getLineName());
                    sortingOrderDetail.setGoodsSortingOrderId(sortingOrder.getGoodsSortingOrderId());
                    goodsSortingOrderDetails.add(sortingOrderDetail);
                }
            });
            sortingOrder.setGoodsSortingOrderDetailList(goodsSortingOrderDetails);
            finalSortingOrders.add(sortingOrder);
            index[0]++;
        });
        sortingOrders = finalSortingOrders;
        return sortingOrders;
    }
}
