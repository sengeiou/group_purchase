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

package com.mds.group.purchase.order.service.impl;

import com.mds.group.purchase.activity.model.ActivityGoods;
import com.mds.group.purchase.activity.service.ActivityGoodsService;
import com.mds.group.purchase.constant.OrderConstant;
import com.mds.group.purchase.core.AbstractService;
import com.mds.group.purchase.order.dao.AfterSaleOrderMapper;
import com.mds.group.purchase.order.dao.ReceiptBillDetailMapper;
import com.mds.group.purchase.order.model.AfterSaleOrder;
import com.mds.group.purchase.order.model.Order;
import com.mds.group.purchase.order.model.OrderDetail;
import com.mds.group.purchase.order.model.ReceiptBillDetail;
import com.mds.group.purchase.order.result.AfterSaleOrderResult;
import com.mds.group.purchase.order.result.MyTeamMembersResult;
import com.mds.group.purchase.order.service.OrderDetailService;
import com.mds.group.purchase.order.service.OrderService;
import com.mds.group.purchase.order.service.ReceiptBillDetailService;
import com.mds.group.purchase.order.vo.ReceiptBillInfoVO;
import com.mds.group.purchase.user.model.Wxuser;
import com.mds.group.purchase.user.service.WxuserService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * The type Receipt bill detail service.
 *
 * @author Administrator
 */
@Service
public class ReceiptBillDetailServiceImpl extends AbstractService<ReceiptBillDetail> implements ReceiptBillDetailService {

    @Resource
    private ReceiptBillDetailMapper receiptBillDetailMapper;
    @Resource
    private OrderService orderService;
    @Resource
    private WxuserService wxuserService;
    @Resource
    private AfterSaleOrderMapper afterSaleOrderMapper;
    @Resource
    private OrderDetailService orderDetailService;
    @Resource
    private ActivityGoodsService activityGoodsService;

    @Override
    public List<MyTeamMembersResult> getInfo(ReceiptBillInfoVO receiptBillInfoVO) {
        List<MyTeamMembersResult> myTeamMembersResults = new ArrayList<>();
        ReceiptBillDetail receiptBillDetail = this.findById(receiptBillInfoVO.getBillDetailId());
        List<Order> orders = orderService.findByIds(receiptBillDetail.getOrderIds());
        //待签收的订单
        if (receiptBillInfoVO.getType() == 0) {
            orders =
                    orders.stream().filter(order -> order.getPayStatus() == OrderConstant.PAYED || order.getPayStatus() == OrderConstant.SENDED).collect(Collectors.toList());
        }
        //已签收的订单
        if (receiptBillInfoVO.getType() == 1) {
            orders =
                    orders.stream().filter(order -> OrderConstant.isReceipt(order.getPayStatus())).collect(Collectors.toList());
        }
        if (orders.isEmpty()) {
            return Collections.emptyList();
        }
        List<Long> orderIds = orders.stream().map(Order::getOrderId).collect(Collectors.toList());
        List<OrderDetail> orderDetails = orderDetailService.findByOrderIds(orderIds);
        List<ActivityGoods> activityGoodsList =
                activityGoodsService.selectByActGoodsIds(orderDetails.stream().map(OrderDetail::getActGoodsId).distinct().collect(Collectors.toList()));
        String wxuserIds =
                orders.stream().map(order -> order.getWxuserId()).map(String::valueOf).collect(Collectors.joining(","));
        List<Wxuser> wxusers = wxuserService.findByIds(wxuserIds);
        Map<Long, Order> groupOrderMap = orders.stream()
                .collect(Collectors.toMap(Order::getOrderId, v -> v));
        Map<Long, Wxuser> wxuserMap = wxusers.stream()
                .collect(Collectors.toMap(Wxuser::getWxuserId, v -> v));
        Map<Long, ActivityGoods> actGoodsMap = activityGoodsList.stream()
                .collect(Collectors.toMap(ActivityGoods::getActivityGoodsId, v -> v));
        for (OrderDetail orderDetail : orderDetails) {
            MyTeamMembersResult myTeamMembersResult = new MyTeamMembersResult();
            myTeamMembersResult.setBillDetailId(receiptBillDetail.getBillDetailId());
            myTeamMembersResult.setOrderDetailId(orderDetail.getOrderDetailId());
            myTeamMembersResult.setOrderId(orderDetail.getOrderId());
            myTeamMembersResult.setActGoodsId(orderDetail.getActGoodsId());
            myTeamMembersResult.setGoodsId(orderDetail.getGoodsId());
            myTeamMembersResult.setGoodsImg(orderDetail.getGoodsImg());
            myTeamMembersResult.setGoodsName(orderDetail.getGoodsName());
            myTeamMembersResult.setGoodsNum(orderDetail.getGoodsNum());
            myTeamMembersResult.setPrice(orderDetail.getGoodsPrice());
            myTeamMembersResult.setActGoodsPrice(actGoodsMap.get(orderDetail.getActGoodsId()).getActivityPrice());
            for (Long orderId : groupOrderMap.keySet()) {
                if (orderDetail.getOrderId().equals(orderId)) {
                    Order order = groupOrderMap.get(orderId);
                    Wxuser wxuser = wxuserMap.get(order.getWxuserId());
                    myTeamMembersResult.setWxuserIcon(wxuser.getIcon());
                    myTeamMembersResult.setWxuserName(wxuser.getWxuserName());
                    myTeamMembersResult.setBuyerAddress(order.getBuyerAddress());
                    myTeamMembersResult.setBuyerName(order.getBuyerName());
                    myTeamMembersResult.setBuyerPhone(order.getBuyerPhone());
                    myTeamMembersResult.setUserDesc(order.getUserDesc());
                    myTeamMembersResult.setOrderType(order.getOrderType());
                    myTeamMembersResult.setPayStatus(order.getPayStatus());
                    AfterSaleOrder afterSaleOrder;
                    //待签收的订单只显示团长的售后状态
                    if (receiptBillInfoVO.getType() == 0) {
                        if (order.getOrderType().equals(OrderConstant.EXCHANGE_ORDER)) {
                            afterSaleOrder =
                                    afterSaleOrderMapper.selectLeaderApplyOrderByExchangeOrderId(order.getOrderId());
                            //当前换货单不显示自己的售后状态
                            if (afterSaleOrder != null && afterSaleOrder.getOrderId() != null && afterSaleOrder.getOrderId().equals(order.getOrderId())) {
                                afterSaleOrder = null;
                            }
                        } else {
                            afterSaleOrder =
                                    afterSaleOrderMapper.selectLeaderApplyOrderByOriginalOrderId(order.getOrderId());
                        }
                    } else {
                        afterSaleOrder = afterSaleOrderMapper.selectOrderByOriginalOrderId(order.getOrderId());
                    }
                    if (afterSaleOrder != null) {
                        myTeamMembersResult.setAfterSaleOrderId(afterSaleOrder.getAfterSaleOrderId());
                        myTeamMembersResult.setAfterSaleStatus(afterSaleOrder.getAfterSaleStatus());
                        myTeamMembersResult.setAfterSaleType(afterSaleOrder.getAfterSaleType());
                        myTeamMembersResult.setAfterSaleStatusText(AfterSaleOrderResult.getStatusText(afterSaleOrder.getAfterSaleType(), afterSaleOrder.getAfterSaleStatus()));
                        myTeamMembersResult.setChildOrderId(afterSaleOrder.getOrderId());

                    }
                }
            }
            myTeamMembersResults.add(myTeamMembersResult);
        }
        return myTeamMembersResults;
    }

    @Override
    public List<ReceiptBillDetail> selectByBillIds(Set<Long> collect) {
        return receiptBillDetailMapper.selectByBillIds(collect);
    }

    @Override
    public void updateGroupLeaderCommissionByOrderDetailId(Long orderDetailId) {
        ReceiptBillDetail receiptBillDetail = receiptBillDetailMapper.selectByOrderDetailId(orderDetailId);
        if (receiptBillDetail == null) {
            return;
        }
        List<OrderDetail> orderDetails = orderDetailService.findByIds(receiptBillDetail.getOrderDetailIds());
        BigDecimal groupLeaderCommission =
                orderDetails.stream().map(OrderDetail::getGroupLeaderCommission).reduce(BigDecimal.ZERO,
                        BigDecimal::add);
        receiptBillDetail.setGroupLeaderCommission(groupLeaderCommission);
        receiptBillDetailMapper.updateByPrimaryKey(receiptBillDetail);
    }

    @Override
    public void updateGroupLeaderCommission() {
        List<ReceiptBillDetail> receiptBillDetailList = receiptBillDetailMapper.selectAll();
        for (ReceiptBillDetail receiptBillDetail : receiptBillDetailList) {
            List<OrderDetail> orderDetails = orderDetailService.findByIds(receiptBillDetail.getOrderDetailIds());
            BigDecimal groupLeaderCommission =
                    orderDetails.stream().map(OrderDetail::getGroupLeaderCommission).reduce(BigDecimal.ZERO,
                            BigDecimal::add);
            receiptBillDetail.setGroupLeaderCommission(groupLeaderCommission);
            receiptBillDetailMapper.updateByPrimaryKey(receiptBillDetail);
        }
    }

    @Override
    public ReceiptBillDetail findByOrderId(Long orderId) {
        return receiptBillDetailMapper.selectByOrderId(orderId);
    }

}
