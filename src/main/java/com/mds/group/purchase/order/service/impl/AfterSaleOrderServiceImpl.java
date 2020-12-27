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

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import com.mds.group.purchase.activity.result.ActGoodsInfoResult;
import com.mds.group.purchase.activity.service.ActivityGoodsService;
import com.mds.group.purchase.constant.ActiviMqQueueName;
import com.mds.group.purchase.constant.AfterSaleOrderConstant;
import com.mds.group.purchase.constant.OrderConstant;
import com.mds.group.purchase.constant.ReceiptBillConstant;
import com.mds.group.purchase.constant.enums.AfterSaleOrderStatus;
import com.mds.group.purchase.constant.enums.AfterSaleOrderType;
import com.mds.group.purchase.core.AbstractService;
import com.mds.group.purchase.exception.ServiceException;
import com.mds.group.purchase.jobhandler.ActiveDelaySendJobHandler;
import com.mds.group.purchase.order.dao.*;
import com.mds.group.purchase.order.model.*;
import com.mds.group.purchase.order.result.*;
import com.mds.group.purchase.order.service.*;
import com.mds.group.purchase.order.vo.*;
import com.mds.group.purchase.user.model.GroupLeader;
import com.mds.group.purchase.user.model.Wxuser;
import com.mds.group.purchase.user.service.GroupLeaderService;
import com.mds.group.purchase.user.service.WxuserService;
import com.mds.group.purchase.utils.IdGenerateUtils;
import com.mds.group.purchase.utils.OrderUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * The type After sale order service.
 *
 * @author pavawi
 */
@Slf4j
@Service
public class AfterSaleOrderServiceImpl extends AbstractService<AfterSaleOrder> implements AfterSaleOrderService {

    private static final Integer MERCHANT_REVIEW_PERIOD = 7;
    private static final Integer USER_REVIEW_PERIOD = 3;
    private static final Integer LEADER_REVIEW_PERIOD = 3;
    private static final Integer USER_MAX_NUMBER_AFTER_SALE_APPLY = 3;
    private static final Integer LEADER_MAX_NUMBER_AFTER_SALE_APPLY = 3;


    @Resource
    private OrderService orderService;
    @Resource
    private WxuserService wxuserService;
    @Resource
    private ActivityGoodsService activityGoodsService;
    @Resource
    private RefundBillService refundBillService;
    @Resource
    private OrderDetailService orderDetailService;
    @Resource
    private NegotiateHistoryService negotiateHistoryService;
    @Resource
    private ActiveDelaySendJobHandler activeDelaySendJobHandler;
    @Resource
    private GroupLeaderService groupLeaderService;
    @Resource
    private AfterSaleOrderMapper afterSaleOrderMapper;
    @Resource
    private OrderMapper tOrderMapper;
    @Resource
    private OrderUtil orderUtil;
    @Resource
    private ReceiptBillMapper receiptBillMapper;
    @Resource
    private ReceiptBillService receiptBillService;
    @Resource
    private ReceiptBillDetailMapper receiptBillDetailMapper;
    @Resource
    private OrderSendBillMappingMapper orderSendBillMappingMapper;


    @Override
    @Transactional(rollbackFor = Exception.class)
    public void userApply(ApplyAfterSaleOrderVO applyAfterSaleOrderVO) {
        Order order = orderService.findById(applyAfterSaleOrderVO.getOrderId());
        if (order.getOrderType().equals(OrderConstant.EXCHANGE_ORDER)) {
            AfterSaleOrder afterSaleOrder = afterSaleOrderMapper.findByNewOrderId(order.getOrderId());
            order = orderService.findById(afterSaleOrder.getOriginalOrderId());
        }
        //检查订单状态
        checkOrderStatus(order, null);
        List<Long> orderIds = Arrays.asList(order.getOrderId());
        List<OrderDetail> orderDetails = orderDetailService.findByOrderIds(orderIds);
        //按照售后类型检查是否满足申请条件
        switch (applyAfterSaleOrderVO.getAfterSaleType()) {
            //退款
            case 3:
                checkRefund(order, applyAfterSaleOrderVO, orderDetails);
                break;
            //换货
            case 4:
                if (order.getPayStatus() == OrderConstant.PAYED) {
                    throw new ServiceException("订单还未配送,不支持换货");
                }
                checkExchange(orderDetails, applyAfterSaleOrderVO);
                break;
            //退货退款
            case 5:
                checkRefund(order, applyAfterSaleOrderVO, orderDetails);
                checkReturn(applyAfterSaleOrderVO, orderDetails);
                break;
            default:
                throw new ServiceException("不是有效的售后类型");
        }
        AfterSaleOrder afterSaleOrder = new AfterSaleOrder();
        afterSaleOrder.setAfterSaleNo(IdGenerateUtils.getAfterSaleOrderNum(order.getOrderNo(),
                applyAfterSaleOrderVO.getAfterSaleType(), order.getUserAfterSaleNum() + 1));
        afterSaleOrder.setAfterSaleStatus(AfterSaleOrderStatus.待商家审核.getValue());
        BeanUtil.copyProperties(applyAfterSaleOrderVO, afterSaleOrder, "orderId");
        afterSaleOrder.setOriginalOrderId(order.getOrderId().toString());
        for (OrderDetail orderDetail : orderDetails) {
            if (orderDetail.getActGoodsId().equals(applyAfterSaleOrderVO.getActGoodsId())) {
                ActGoodsInfoResult actGoodsInfoResult =
                        activityGoodsService.getActGoodsById(applyAfterSaleOrderVO.getActGoodsId(),
                                orderDetail.getAppmodelId());
                afterSaleOrder.setActGoodsPrice(actGoodsInfoResult.getActPrice());
                afterSaleOrder.setGoodsImage(orderDetail.getGoodsImg());
                afterSaleOrder.setGoodsName(orderDetail.getGoodsName());
                afterSaleOrder.setGoodsId(orderDetail.getGoodsId());
            }
        }
        afterSaleOrder.setGroupId(order.getGroupId());
        afterSaleOrder.setWxuserId(order.getWxuserId());
        afterSaleOrder.setAppmodelId(order.getAppmodelId());
        afterSaleOrder.setCreateTime(new Date());
        afterSaleOrder.setDeadline(DateUtil.offsetDay(DateUtil.date(), MERCHANT_REVIEW_PERIOD));
        if (order.getOrderType().equals(OrderConstant.EXCHANGE_ORDER)) {
            negotiateHistoryService.refuseConfirm(order, applyAfterSaleOrderVO);
        }
        //初始化协商历史
        negotiateHistoryService.init(order, applyAfterSaleOrderVO, "买家");
        this.save(afterSaleOrder);
        //更新售后信息到原订单
        order.setAfterSaleOrderId(afterSaleOrder.getAfterSaleOrderId());
        order.setUserAfterSaleNum(order.getUserAfterSaleNum() + 1);
        orderService.update(order);
        //添加商家操作倒计时
        long mili = afterSaleOrder.getDeadline().getTime() - System.currentTimeMillis();
        if (mili < 0) {
            mili = 0L;
        }
        activeDelaySendJobHandler
                .savaTask(afterSaleOrder.getAfterSaleOrderId().toString(), ActiviMqQueueName.MERCHANT_AUTO_CONFIRM,
                        mili, afterSaleOrder.getAppmodelId(), true);


    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public void leaderApply(BatchApplyAfterSaleOrderVO batchApplyAfterSaleOrderVO) {
        String ordIds =
                batchApplyAfterSaleOrderVO.getOrderIds().stream().map(String::valueOf).collect(Collectors.joining(","));
        List<Order> orders = orderService.findByIds(ordIds);
        if (orders.isEmpty()) {
            throw new ServiceException("没有找到有效的订单信息");
        }
        for (Order order : orders) {
            if (order.getOrderType().equals(OrderConstant.EXCHANGE_ORDER)) {
                //删除售后单
                orders.remove(order);
                AfterSaleOrder afterSaleOrder = afterSaleOrderMapper.findByNewOrderId(order.getOrderId());
                //添加原订单
                orders.addAll(orderService.findByIds(afterSaleOrder.getOriginalOrderId()));
            }
        }
        //订单去重
        orders = orders.stream().distinct().collect(Collectors.toList());
        //检查订单状态
        ReceiptBillDetail receiptBillDetail =
                receiptBillDetailMapper.selectByPrimaryKey(batchApplyAfterSaleOrderVO.getBillDetailId());
        for (Order order : orders) {
            checkOrderStatus(order, receiptBillDetail);
        }
        List<Long> orderIds = orders.stream().map(Order::getOrderId).collect(Collectors.toList());
        batchApplyAfterSaleOrderVO.setOrderIds(orderIds);
        List<OrderDetail> orderDetails = orderDetailService.findByOrderIds(orderIds);
        //按照售后类型检查是否满足申请条件
        switch (batchApplyAfterSaleOrderVO.getAfterSaleType()) {
            //团长换货
            case 1:
                batchCheckExchange(orderDetails, batchApplyAfterSaleOrderVO);
                break;
            //团长退款
            case 2:
                batchCheckRefund(orders, batchApplyAfterSaleOrderVO);
                break;
            default:
                throw new ServiceException("不是有效的售后类型");
        }
        AfterSaleOrder afterSaleOrder = new AfterSaleOrder();
        afterSaleOrder.setAfterSaleNo(IdGenerateUtils.getAfterSaleOrderNum(orders.get(0).getOrderNo(),
                batchApplyAfterSaleOrderVO.getAfterSaleType(), receiptBillDetail.getLeaderAfterSaleNum() + 1));
        afterSaleOrder.setAfterSaleStatus(AfterSaleOrderStatus.待商家审核.getValue());
        BeanUtil.copyProperties(batchApplyAfterSaleOrderVO, afterSaleOrder);
        boolean mach = false;
        afterSaleOrder.setOriginalOrderId(orderDetails.stream().map(OrderDetail::getOrderId).map(String::valueOf).collect(Collectors.joining(",")));
        for (OrderDetail orderDetail : orderDetails) {
            if (orderDetail.getActGoodsId().equals(batchApplyAfterSaleOrderVO.getActGoodsId())) {
                ActGoodsInfoResult actGoodsInfoResult =
                        activityGoodsService.getActGoodsById(batchApplyAfterSaleOrderVO.getActGoodsId(),
                                orderDetail.getAppmodelId());
                afterSaleOrder.setActGoodsPrice(actGoodsInfoResult.getActPrice());
                afterSaleOrder.setGoodsImage(orderDetail.getGoodsImg());
                afterSaleOrder.setGoodsName(orderDetail.getGoodsName());
                afterSaleOrder.setGoodsId(orderDetail.getGoodsId());
                mach = true;
            }
        }
        if (!mach) {
            throw new ServiceException("非法参数，请确认后重试");
        }
        GroupLeader groupLeader = groupLeaderService.findById(orders.get(0).getGroupId());
        afterSaleOrder.setWxuserId(groupLeader.getWxuserId());
        afterSaleOrder.setGroupId(groupLeader.getGroupLeaderId());
        afterSaleOrder.setAppmodelId(orders.get(0).getAppmodelId());
        afterSaleOrder.setCreateTime(new Date());
        afterSaleOrder.setDeadline(DateUtil.offsetDay(DateUtil.date(), MERCHANT_REVIEW_PERIOD));
        this.save(afterSaleOrder);
        //添加团长售后次数到签收单
        receiptBillDetail.setLeaderAfterSaleNum(receiptBillDetail.getLeaderAfterSaleNum() + 1);
        receiptBillDetailMapper.updateByPrimaryKey(receiptBillDetail);
        //初始化协商历史
        for (Order order : orders) {
            //更新售后信息到原订单
            order.setAfterSaleOrderId(afterSaleOrder.getAfterSaleOrderId());
            orderService.update(order);
            negotiateHistoryService.init(order, batchApplyAfterSaleOrderVO, "团长");
        }
        //添加商家操作倒计时
        long mili = afterSaleOrder.getDeadline().getTime() - System.currentTimeMillis();
        if (mili < 0) {
            mili = 0L;
        }
        activeDelaySendJobHandler
                .savaTask(afterSaleOrder.getAfterSaleOrderId().toString(), ActiviMqQueueName.MERCHANT_AUTO_CONFIRM,
                        mili, afterSaleOrder.getAppmodelId(), true);


    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cancel(String applyAfterSaleOrderId, Boolean isSystem, int type) {
        AfterSaleOrder afterSaleOrder = this.findById(applyAfterSaleOrderId);
        if (afterSaleOrder != null && AfterSaleOrderStatus.canReceipt(afterSaleOrder.getAfterSaleStatus())) {
            if (AfterSaleOrderType.isLeader(afterSaleOrder.getAfterSaleType()) && type == 1) {
                throw new ServiceException("团长申请的售后用户不能撤销");
            }
            if (!AfterSaleOrderType.isLeader(afterSaleOrder.getAfterSaleType()) && type == 2) {
                throw new ServiceException("用户申请的团长用户不能撤销");
            }
            afterSaleOrder.setAfterSaleStatus(AfterSaleOrderStatus.关闭.getValue());
            if (type == 1) {
                afterSaleOrder.setCloseType(AfterSaleOrderConstant.CLOSE_BY_USER_CANCEL);
            } else {
                afterSaleOrder.setCloseType(AfterSaleOrderConstant.CLOSE_BY_LEADER_CANCEL);
            }
            afterSaleOrder.setCloseTime(new Date());
            afterSaleOrder.setDeadline(null);
            this.update(afterSaleOrder);
            List<Order> orders = orderService.findByIds(afterSaleOrder.getOriginalOrderId());
            for (Order order : orders) {
                negotiateHistoryService.cancel(order, afterSaleOrder, isSystem, type);
            }
        } else {
            throw new ServiceException("没有符合条件的售后单，请刷新后重试");
        }

    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void sellerRefusal(AfterSaleOrderUpdateVO applyAfterSaleOrderVO) {
        AfterSaleOrder afterSaleOrder = this.findById(applyAfterSaleOrderVO.getAfterSaleOrderId());
        if (afterSaleOrder.getAfterSaleStatus().equals(AfterSaleOrderStatus.待商家审核.getValue())) {
            afterSaleOrder.setRefusalTime(new Date());
            afterSaleOrder.setDeadline(null);
            afterSaleOrder.setRefusalReason(applyAfterSaleOrderVO.getRefusalReason());
            afterSaleOrder.setAfterSaleStatus(AfterSaleOrderStatus.商家拒绝.getValue());
            List<Order> orders = orderService.findByIds(afterSaleOrder.getOriginalOrderId());
            for (Order order : orders) {
                negotiateHistoryService.sellerRefusal(order, afterSaleOrder);
            }
            this.update(afterSaleOrder);
            if (afterSaleOrder.getAfterSaleType() == 3 || afterSaleOrder.getAfterSaleType() == 5) {
                orderUtil.orderRefundFailMsg(afterSaleOrder);
            }
        } else {
            throw new ServiceException("没有符合条件的售后单，请刷新后重试");
        }


    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void sellerApprove(AfterSaleOrderUpdateVO applyAfterSaleOrderVO, Boolean isSystem) {
        AfterSaleOrder afterSaleOrder = this.findById(applyAfterSaleOrderVO.getAfterSaleOrderId());
        if (afterSaleOrder == null) {
            return;
        }
        //关闭售后单中的换货单及生成此换货单的售后单
        closeAfterSaleOrderAndExChangeOrder(afterSaleOrder);
        if (afterSaleOrder.getAfterSaleStatus().equals(AfterSaleOrderStatus.待商家审核.getValue())) {
            //1.团长换货 2.团长退款 3.退款 4.换货 5.退货退款
            switch (afterSaleOrder.getAfterSaleType()) {
                case 1:
                    //生成新的订单
                    groupLeaderExchange(afterSaleOrder, isSystem);
                    break;
                case 2:
                    //团长退款
                    groupLeaderRefund(afterSaleOrder, isSystem);
                    break;
                case 3:
                    //直接退款
                    userRefund(afterSaleOrder, isSystem);
                    break;
                case 4:
                    //检查订单状态。如果未签收，直接换货，已签收需先退货
                    userExchange(afterSaleOrder, isSystem, false);
                    break;
                case 5:
                    //检查订单状态。如果未签收，直退款，已签收需先退货
                    userReturn(afterSaleOrder, isSystem, false);
                    break;
                default:
                    break;
            }
        } else {
            throw new ServiceException("没有符合条件的售后单，请刷新后重试");
        }

    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public void userReturn(String applyAfterSaleOrderId) {
        AfterSaleOrder afterSaleOrder = this.findById(applyAfterSaleOrderId);
        if (afterSaleOrder != null && afterSaleOrder.getAfterSaleStatus().equals(AfterSaleOrderStatus.待退货.getValue())) {
            afterSaleOrder.setDeadline(DateUtil.offsetDay(DateUtil.date(), LEADER_REVIEW_PERIOD));
            afterSaleOrder.setAfterSaleStatus(AfterSaleOrderStatus.待团长确认收货.getValue());
            this.update(afterSaleOrder);
            //3. 添加协商历史
            List<Order> orders = orderService.findByIds(afterSaleOrder.getOriginalOrderId());
            for (Order order : orders) {
                negotiateHistoryService.userReturn(order);
            }
            //添加团长操作倒计时
            long mili = afterSaleOrder.getDeadline().getTime() - System.currentTimeMillis();
            if (mili < 0) {
                mili = 0L;
            }
            activeDelaySendJobHandler
                    .savaTask(afterSaleOrder.getAfterSaleOrderId().toString(), ActiviMqQueueName.LEADER_AUTO_CONFIRM,
                            mili, afterSaleOrder.getAppmodelId(), true);

        } else {
            throw new ServiceException("没有符合条件的售后单，请刷新后重试");
        }

    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void leaderApprove(String applyAfterSaleOrderId, Boolean isSystem) {
        AfterSaleOrder afterSaleOrder = this.findById(applyAfterSaleOrderId);
        if (afterSaleOrder != null && afterSaleOrder.getAfterSaleStatus().equals(AfterSaleOrderStatus.待团长确认收货.getValue())) {
            List<Order> orders = orderService.findByIds(afterSaleOrder.getOriginalOrderId());
            for (Order order : orders) {
                negotiateHistoryService.leaderApprove(order, isSystem);
            }
            afterSaleOrder.setDeadline(null);
            if (afterSaleOrder.getAfterSaleType() == AfterSaleOrderType.换货.getValue()) {
                userExchange(afterSaleOrder, isSystem, true);
            } else {
                userRefund(afterSaleOrder, isSystem);
            }
        } else {
            throw new ServiceException("没有符合条件的售后单，请刷新后重试");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void leaderRefusal(String applyAfterSaleOrderId) {
        AfterSaleOrder afterSaleOrder = this.findById(applyAfterSaleOrderId);
        if (afterSaleOrder != null && afterSaleOrder.getAfterSaleStatus().equals(AfterSaleOrderStatus.待团长确认收货.getValue())) {
            afterSaleOrder.setAfterSaleStatus(AfterSaleOrderStatus.关闭.getValue());
            afterSaleOrder.setCloseType(AfterSaleOrderConstant.CLOSE_BY_LEADER_NOT_RECEIVED);
            afterSaleOrder.setCloseTime(new Date());
            afterSaleOrder.setDeadline(null);
            this.update(afterSaleOrder);
            List<Order> orders = orderService.findByIds(afterSaleOrder.getOriginalOrderId());
            for (Order order : orders) {
                negotiateHistoryService.leaderRefusal(order, afterSaleOrder);
            }
        } else {
            throw new ServiceException("没有符合条件的售后单，请刷新后重试");
        }

    }

    @Override
    public Integer countNotEndByUserId(Set<Long> userIds) {
        if (userIds.isEmpty()) {
            return 0;
        }
        return afterSaleOrderMapper.countNotEndByUserId(userIds);
    }

    @Override
    public List<AfterSaleOrderResult> searchAfterSaleOrder(SearchAfterSaleOrderVO searchAfterSaleOrderVo) {
        GroupLeader groupLeader = groupLeaderService.findByWxuserId(searchAfterSaleOrderVo.getWxuserId());
        //按照团长ID查找未结束的申请单
        List<AfterSaleOrderResult> afterSaleOrderResultList =
                afterSaleOrderMapper.findByGroupId(groupLeader.getGroupLeaderId(),
                        searchAfterSaleOrderVo.getSearch());
        for (AfterSaleOrderResult afterSaleOrderResult : afterSaleOrderResultList) {
            afterSaleOrderResult.setAfterSaleStatusText(AfterSaleOrderResult.getStatusText(afterSaleOrderResult.getAfterSaleType(), afterSaleOrderResult.getAfterSaleStatus()));
        }
        return afterSaleOrderResultList;
    }

    @Override
    public List<CustomerResult> customerList(Long wxuserId, String search) {
        GroupLeader groupLeader = groupLeaderService.findByWxuserId(wxuserId);
        if (groupLeader == null) {
            throw new ServiceException("不是有效的团长用户！");
        }
        List<CustomerResult> customerResults = new ArrayList<>();
        List<Wxuser> wxusers = wxuserService.findCustomersByGroupLeaderId(groupLeader.getGroupLeaderId(), search);
        List<Order> waitSendOrders = orderService.findByGroupLeaderIdAndStatus(groupLeader.getGroupLeaderId(),
                OrderConstant.PAYED);
        List<Order> waitPickOrders = orderService.findByGroupLeaderIdAndStatus(groupLeader.getGroupLeaderId(),
                OrderConstant.GROUP_LEADER_RECEIPT);
        Map<Long, List<Order>> waitSendOrderMap =
                waitSendOrders.stream().collect(Collectors.groupingBy(Order::getWxuserId));
        Map<Long, List<Order>> waitPickOrderMap =
                waitPickOrders.stream().collect(Collectors.groupingBy(Order::getWxuserId));
        for (Wxuser wxuser : wxusers) {
            CustomerResult customerResult = new CustomerResult();
            customerResult.setWxuserId(wxuser.getWxuserId());
            customerResult.setWxuserName(wxuser.getWxuserName());
            customerResult.setWxuserIcon(wxuser.getIcon());
            if (waitSendOrderMap.get(wxuser.getWxuserId()) != null) {
                customerResult.setWaitSendNum(waitSendOrderMap.get(wxuser.getWxuserId()).size());
            } else {
                customerResult.setWaitSendNum(0);
            }
            if (waitPickOrderMap.get(wxuser.getWxuserId()) != null) {
                customerResult.setWaitPickNum(waitPickOrderMap.get(wxuser.getWxuserId()).size());
            } else {
                customerResult.setWaitPickNum(0);
            }
            customerResults.add(customerResult);
        }
        return customerResults;
    }

    @Override
    public List<OrderResult> customerOrderInfo(Long wxuserId, String groupLeaderId, Integer type) {
        Integer status = OrderConstant.PAYED;
        if (type == 0) {
            status = OrderConstant.PAYED;
        }
        if (type == 1) {
            status = OrderConstant.GROUP_LEADER_RECEIPT;
        }
        return orderService.findByWxuserIdAndStatus(wxuserId, groupLeaderId, status);
    }

    @Override
    public List<AfterSaleOrderManageResult> searchManageAfterSaleOrder(String appmodelId,
                                                                       SearchOrderVoV2 searchOrderVoV2) {
        String startDate;
        String endDate;
        switch (searchOrderVoV2.getQuicklyDate()) {
            case 0:
                startDate = searchOrderVoV2.getCreateOrderTimeStart();
                endDate = searchOrderVoV2.getCreateOrderTimeEnd();
                break;
            case 1:
                startDate = DateUtil.offsetMonth(DateUtil.date(), -1).toDateStr();
                endDate = DateUtil.date().toDateStr();
                break;
            case 2:
                startDate = DateUtil.offsetMonth(DateUtil.date(), -3).toDateStr();
                endDate = DateUtil.date().toDateStr();
                break;
            default:
                startDate = null;
                endDate = null;
        }
        List<AfterSaleOrderManageResult> afterSaleOrders = afterSaleOrderMapper.searchAfterSaleOrder(startDate,
                endDate, appmodelId, searchOrderVoV2);
        List<Long> orderIds = new ArrayList<>();
        for (AfterSaleOrderManageResult afterSaleOrder : afterSaleOrders) {
            orderIds.addAll(Arrays.stream(afterSaleOrder.getOriginalOrderId().split(",")).map(Long::parseLong).collect(Collectors.toSet()));
        }
        if (CollectionUtil.isEmpty(orderIds)) {
            return Collections.emptyList();
        }
        List<PcOrderResult> pcOrderResults = tOrderMapper.searchPcOrderResultByOrderIds(appmodelId, orderIds);
        Map<Long, PcOrderResult> pcOrderResultMap =
                pcOrderResults.stream().collect(Collectors.toMap(PcOrderResult::getOrderId, v -> v));
        for (AfterSaleOrderManageResult afterSaleOrder : afterSaleOrders) {
            List<PcOrderResult> orderResults = new ArrayList<>();
            for (String s : afterSaleOrder.getOriginalOrderId().split(",")) {
                orderResults.add(pcOrderResultMap.get(Long.parseLong(s)));
            }
            afterSaleOrder.setOrderResults(orderResults);
        }
        return afterSaleOrders;
    }

    @Override
    public AfterSaleApplyNumberResult applyNumber(Long orderId) {
        Order order = orderService.findById(orderId);
        ReceiptBillDetail receiptBillDetail = receiptBillDetailMapper.selectByOrderId(order.getOrderId());
        AfterSaleApplyNumberResult afterSaleApplyNumberResult = new AfterSaleApplyNumberResult();
        //用户刚刚付款完成，还没有生成团长签收单
        if (receiptBillDetail == null) {
            afterSaleApplyNumberResult.setGroupLeaderUsedNumber(LEADER_MAX_NUMBER_AFTER_SALE_APPLY);
        } else {
            afterSaleApplyNumberResult.setGroupLeaderUsedNumber(LEADER_MAX_NUMBER_AFTER_SALE_APPLY - receiptBillDetail.getLeaderAfterSaleNum());
        }
        afterSaleApplyNumberResult.setUserUsedNumber(USER_MAX_NUMBER_AFTER_SALE_APPLY - order.getUserAfterSaleNum());
        return afterSaleApplyNumberResult;
    }

    @Override
    public List<AfterSaleOrderResult> getUserAfterSaleOrderList(OrderVoV2 orderVoV2) {
        List<AfterSaleOrderResult> afterSaleOrders = afterSaleOrderMapper.selectUserAfterSaleOrderList(orderVoV2);
        if (afterSaleOrders.isEmpty()) {
            return Collections.emptyList();
        }
        List<Long> orderIds =
                Arrays.stream(afterSaleOrders.stream().map(AfterSaleOrderResult::getOriginalOrderId).collect(Collectors.joining(",")).split(",")).map(Long::parseLong).collect(Collectors.toList());
        List<PcOrderResult> pcOrderResults =
                tOrderMapper.searchPcOrderResultByOrderIds(afterSaleOrders.get(0).getAppmodelId(), orderIds);
        Map<Long, PcOrderResult> orderResultMap =
                pcOrderResults.stream().collect(Collectors.toMap(PcOrderResult::getOrderId, v -> v));
        for (AfterSaleOrderResult afterSaleOrder : afterSaleOrders) {
            afterSaleOrder.setAfterSaleStatusText(AfterSaleOrderResult.getStatusText(afterSaleOrder.getAfterSaleType(), afterSaleOrder.getAfterSaleStatus()));
            afterSaleOrder.setOrderResult(orderResultMap.get(Long.parseLong(afterSaleOrder.getOriginalOrderId())));
        }
        return afterSaleOrders;
    }

    @Override
    public AfterSaleOrderDetailResult findDetailById(String applyAfterSaleOrderId) {
        AfterSaleOrderDetailResult afterSaleOrderDetailResult = new AfterSaleOrderDetailResult();
        AfterSaleOrder afterSaleOrder = afterSaleOrderMapper.selectByPrimaryKey(applyAfterSaleOrderId);
        BeanUtil.copyProperties(afterSaleOrder, afterSaleOrderDetailResult);
        List<Long> originalOrderId =
                Arrays.stream(afterSaleOrder.getOriginalOrderId().split(",")).map(Long::parseLong).collect(Collectors.toList());
        List<AfterSaleOrder> afterSaleOrders = afterSaleOrderMapper.findByOriginalOrderIds(originalOrderId);
        if (AfterSaleOrderType.isLeader(afterSaleOrder.getAfterSaleType())) {
            afterSaleOrders =
                    afterSaleOrders.stream().filter(aso -> AfterSaleOrderType.isLeader(aso.getAfterSaleType())).collect(Collectors.toList());
        } else {
            afterSaleOrders =
                    afterSaleOrders.stream().filter(aso -> !AfterSaleOrderType.isLeader(aso.getAfterSaleType())).collect(Collectors.toList());
        }
        afterSaleOrderDetailResult.setHistory(afterSaleOrders);
        OrderResult order = orderService
                .getById(Arrays.stream(afterSaleOrder.getOriginalOrderId().split(",")).map(Long::parseLong).findFirst().get());
        afterSaleOrderDetailResult.setOrderResult(order);
        afterSaleOrderDetailResult.setPickupLocation(order.getPickupLocation());
        afterSaleOrderDetailResult.setGroupLeaderPhone(order.getGroupLeaderPhone());
        afterSaleOrderDetailResult.setPayStatus(order.getPayStatus());
        return afterSaleOrderDetailResult;
    }

    @Override
    public List<MembersResult> getMemberListById(String applyAfterSaleOrderId) {
        AfterSaleOrder afterSaleOrder = afterSaleOrderMapper.selectByPrimaryKey(applyAfterSaleOrderId);
        if (afterSaleOrder == null || afterSaleOrder.getAfterSaleType() > 2) {
            return null;
        } else {
            List<Long> orderIds =
                    Arrays.stream(afterSaleOrder.getOriginalOrderId().split(",")).map(Long::parseLong).collect(Collectors.toList());
            List<MembersResult> membersResults = orderService.findMembersByIds(orderIds);
            return membersResults;
        }

    }


    private void groupLeaderRefund(AfterSaleOrder afterSaleOrder, Boolean isSystem) {
        userRefund(afterSaleOrder, isSystem);
    }

    private void groupLeaderExchange(AfterSaleOrder afterSaleOrder, Boolean isSystem) {
        //获取退款金额和原订单信息
        List<Order> orders = orderService.findByIds(afterSaleOrder.getOriginalOrderId());
        //新增订单并直接进入发货流程
        Long newOrderIds = orderService.createExchange(orders.get(0), afterSaleOrder.getApplicationNum(), true);
        //添加协商历史
        for (Order order : orders) {
            negotiateHistoryService.sellerApprove(order, afterSaleOrder, isSystem);
        }
        afterSaleOrder.setOrderId(newOrderIds);
        afterSaleOrder.setAfterSaleStatus(AfterSaleOrderStatus.待发货.getValue());
        this.update(afterSaleOrder);
    }

    @Override
    public void closeAfterSaleOrderAndExChangeOrder(AfterSaleOrder afterSaleOrder) {
        List<AfterSaleOrder> afterSaleOrders =
                afterSaleOrderMapper.findNotEndByOriginalOrderIdsAndIdNotIn(afterSaleOrder.getOriginalOrderId(),
                        afterSaleOrder.getAfterSaleOrderId());
        if (afterSaleOrders.isEmpty()) {
            return;
        }
        //关闭售后单中的生成此换货单的售后单
        for (AfterSaleOrder saleOrder : afterSaleOrders) {
            //防止用户申请的换货售后被团长申请的售后给关闭了
            if (saleOrder.getAfterSaleType().equals(afterSaleOrder.getAfterSaleType()) || !AfterSaleOrderType.isExChange(afterSaleOrder.getAfterSaleType())) {
                saleOrder.setAfterSaleStatus(AfterSaleOrderStatus.关闭.getValue());
                afterSaleOrder.setCloseType(AfterSaleOrderConstant.CLOSE_BY_EXCHANGE);
                saleOrder.setCloseTime(new Date());
                this.update(saleOrder);
            }
        }
        List<Order> orders =
                orderService.findByIds(afterSaleOrders.stream().map(AfterSaleOrder::getOrderId).map(String::valueOf).collect(Collectors.joining(",")));
        if (orders.isEmpty()) {
            return;
        }
        //关闭售后单中的换货单
        for (Order order : orders) {
            //如果原订单是换货单则关闭原订单 并且更新包含原订单的签收单的状态
            if (order.getOrderType().equals(OrderConstant.EXCHANGE_ORDER)) {
                order.setCloseTime(new Date());
                order.setPayStatus(OrderConstant.ORDER_CLOSE_BY_EXCHANGE);
                //关闭原始订单
                orderService.update(order);
                //更新包含原始订单的签收单状态
                closeReceipt(order);
            }
        }

    }

    private void userRefund(AfterSaleOrder afterSaleOrder, Boolean isSystem) {
        //获取退款金额和原订单信息
        List<Order> orders = orderService.findByIds(afterSaleOrder.getOriginalOrderId());
        for (Order order : orders) {
            //调用退款流程
            closeOrder(afterSaleOrder.getRefundFee(), order);
            //更新原始订单的签收单状态
            closeReceipt(order);
            //添加协商历史
            negotiateHistoryService.refund(afterSaleOrder, order, isSystem);
        }
        //更新退款单的的签收单状态
        closeReceipt(orderService.findById(afterSaleOrder.getOrderId()));
        //更改售后单状态
        afterSaleOrder.setAfterSaleStatus(AfterSaleOrderStatus.成功.getValue());
        afterSaleOrder.setSuccessTime(new Date());
        this.update(afterSaleOrder);
    }

    private void closeOrder(BigDecimal refundFee, Order order) {
        //调用扣除团长佣金
        orderService.deductionCommission(order, refundFee);
        //写入退款金额
        order.setRefundFee(refundFee);
        //根据剩余金额设置订单状态
        BigDecimal fee = order.getPayFee().subtract(refundFee);
        if (fee.compareTo(BigDecimal.ZERO) == 0) {
            order.setCloseTime(new Date());
            order.setPayStatus(OrderConstant.ORDER_CLOSE_BY_REFUND);
        } else {
            order.setPayStatus(OrderConstant.ORDER_COMPLETE);
        }
        //保存订单状态
        orderService.update(order);
        refundBillService.create(order, refundFee);


    }

    private void closeReceipt(Order order) {
        if (order == null) {
            return;
        }
        ReceiptBillDetail receiptBillDetaill = receiptBillDetailMapper.selectByOrderId(order.getOrderId());
        //如果退款订单还没有生成发货单
        if (receiptBillDetaill == null) {
            //将退款订单移除发货单
            orderService.removeFromSendBill(order.getAppmodelId(), order.getOrderId().toString());
            orderSendBillMappingMapper.deleteByOrderIds(order.getOrderId().toString());
        } else {
            ReceiptBill receiptBill = receiptBillMapper.selectByPrimaryKey(receiptBillDetaill.getBillId());
            //将待发货的订单移出发货单并且清空发货单映射
            if (receiptBill.getStatus().equals(ReceiptBillConstant.WAIT_SEND)) {
                orderService.removeFromSendBill(order.getAppmodelId(), order.getOrderId().toString());
                orderSendBillMappingMapper.deleteByOrderIds(order.getOrderId().toString());
            }
            //将已发货的订单签收并且更新签收单的状态
            else if (receiptBill.getStatus().equals(ReceiptBillConstant.DISPATCHING) || receiptBill.getStatus().equals(ReceiptBillConstant.WAIT_RECEIPT)) {
                List<OrderDetail> orderDetails = orderDetailService.findByOrderIds(Arrays.asList(order.getOrderId()));
                //计算商品数量
                int receiptGoodsNum = orderDetails.stream()
                        .filter(orderDetail -> orderDetail.getGoodsId().equals(receiptBillDetaill.getGoodsId()))
                        .mapToInt(OrderDetail::getGoodsNum)
                        .sum();
                if (receiptGoodsNum > receiptBillDetaill.getGoodsNum()) {
                    receiptBillDetaill.setReceiptGoodsNum(receiptBillDetaill.getGoodsNum());
                } else {
                    receiptBillDetaill.setReceiptGoodsNum(receiptBillDetaill.getReceiptGoodsNum() + receiptGoodsNum);
                }
                receiptBillDetailMapper.updateByPrimaryKey(receiptBillDetaill);
                receiptBillService.checkReceiptStatus(receiptBillDetaill);
            } else {
                receiptBillService.checkReceiptStatus(receiptBillDetaill);
            }
        }

    }


    @Override
    public List<Order> findOrderByOriginalOrder(Order order) {
        List<Order> originalOrderList = new ArrayList<>();
        //如果是换货单，通过售后单查询原订单ID
        if (order.getOrderType() == OrderConstant.EXCHANGE_ORDER) {
            AfterSaleOrder afterSaleOrder =
                    afterSaleOrderMapper.findByNewOrderId(order.getOrderId());
            originalOrderList = tOrderMapper.selectOrderByAfterSaleOrderId(afterSaleOrder.getAfterSaleOrderId());
        } else {
            //如果不是换货单，将当前订单添加到列表中
            originalOrderList.add(order);
        }
        //检查列表中是否还有换货单
        List<Order> exChangeOrders = originalOrderList.stream().filter(o -> o.getOrderType() == OrderConstant
                .EXCHANGE_ORDER).collect(Collectors.toList());
        for (Order o : exChangeOrders) {
            originalOrderList.addAll(findOrderByOriginalOrder(o));
        }
        originalOrderList = originalOrderList.stream().filter(o -> o.getOrderType() != OrderConstant
                .EXCHANGE_ORDER).collect(Collectors.toList());
        return originalOrderList;
    }


    private void userExchange(AfterSaleOrder afterSaleOrder, Boolean isSystem, Boolean fromLeaderApprove) {
        //获取原订单信息
        List<Order> orders = orderService.findByIds(afterSaleOrder.getOriginalOrderId());

        //如果原订单已经确认收货并且没有经过团长确认收货的环节，更改订单状态。需要用户先退货
        for (Order order : orders) {
            if (order.getPayStatus() == OrderConstant.NOT_COMMENT && !fromLeaderApprove) {
                //先执行退货流程
                userReturn(afterSaleOrder, isSystem, fromLeaderApprove);
            } else {
                //如果用户没有收货
                //新增订单并直接进入发货流程
                Long newOrderId = orderService.createExchange(order, afterSaleOrder.getApplicationNum(), false);
                afterSaleOrder.setAfterSaleStatus(AfterSaleOrderStatus.待发货.getValue());
                afterSaleOrder.setOrderId(newOrderId);
                this.update(afterSaleOrder);
                //添加协商历史
                negotiateHistoryService.sellerApprove(order, afterSaleOrder, isSystem);
            }
        }

    }

    private void userReturn(AfterSaleOrder afterSaleOrder, Boolean isSystem, Boolean fromLeaderApprove) {
        //1.获取原订单信息
        List<Order> orders = orderService.findByIds(afterSaleOrder.getOriginalOrderId());
        //2.更改订单状态。需要用户先退货
        for (Order order : orders) {
            //是否已经经过团长确认
            if (!fromLeaderApprove) {
                afterSaleOrder.setDeadline(DateUtil.offsetDay(DateUtil.date(), USER_REVIEW_PERIOD));
                afterSaleOrder.setAfterSaleStatus(AfterSaleOrderStatus.待退货.getValue());
                this.update(afterSaleOrder);
                //2.2 添加协商历史
                negotiateHistoryService.sellerApproveNeedReturn(order, afterSaleOrder, isSystem);
                //添加用户操作倒计时
                long mili = afterSaleOrder.getDeadline().getTime() - System.currentTimeMillis();
                if (mili < 0) {
                    mili = 0L;
                }
                activeDelaySendJobHandler
                        .savaTask(afterSaleOrder.getAfterSaleOrderId().toString(), ActiviMqQueueName.USER_AUTO_CANCEL
                                , mili, afterSaleOrder.getAppmodelId(), true);

            } else {
                userRefund(afterSaleOrder, isSystem);
            }
        }
    }

    private void checkReturn(ApplyAfterSaleOrderVO applyAfterSaleOrderVO, List<OrderDetail> orderDetails) {
        if (applyAfterSaleOrderVO.getApplicationNum() == null || applyAfterSaleOrderVO.getApplicationNum().equals(0)) {
            throw new ServiceException("退货数量不得小于1");
        }
        for (OrderDetail orderDetail : orderDetails) {
            if (orderDetail.getActGoodsId().equals(applyAfterSaleOrderVO.getActGoodsId())) {
                if (orderDetail.getGoodsNum() < applyAfterSaleOrderVO.getApplicationNum()) {
                    throw new ServiceException("退货数量不得大于总数量");
                }
            }
        }
    }


    private void checkExchange(List<OrderDetail> orderDetails, BaseApplyAfterSaleOrderVO baseApplyAfterSaleOrderVO) {
        if (baseApplyAfterSaleOrderVO.getApplicationNum() < 1) {
            throw new ServiceException("换货数量小于1");
        }
        Integer goodsNum = 0;
        for (OrderDetail orderDetail : orderDetails) {
            if (orderDetail.getActGoodsId().equals(baseApplyAfterSaleOrderVO.getActGoodsId())) {
                goodsNum += orderDetail.getGoodsNum();
            }
        }
        if (baseApplyAfterSaleOrderVO.getApplicationNum() > goodsNum) {
            throw new ServiceException("换货数量不得大于总数量");
        }
    }

    private void checkRefund(Order order, ApplyAfterSaleOrderVO applyAfterSaleOrderVO, List<OrderDetail> orderDetails) {
        if (applyAfterSaleOrderVO.getRefundFee() == null || BigDecimal.ZERO.compareTo(applyAfterSaleOrderVO.getRefundFee()) >= 0) {
            throw new ServiceException("退款金额不得小于0");
        }
        if (applyAfterSaleOrderVO.getRefundFee().compareTo(order.getPayFee()) > 0) {
            throw new ServiceException("退款金额不得大于实际支付金额");
        }
    }

    /**
     * Check order status.
     *
     * @param order             the order
     * @param receiptBillDetail the receipt bill detail
     */
    public void checkOrderStatus(Order order, ReceiptBillDetail receiptBillDetail) {
        if (order == null) {
            throw new ServiceException("订单不存在，请刷新后再试");
        }
        if (order.getUserAfterSaleNum() >= USER_MAX_NUMBER_AFTER_SALE_APPLY) {
            throw new ServiceException(String.format("订单%s申请售后次数已经超过最大限制，不能继续申请售后", order.getOrderNo()));
        }
        if (receiptBillDetail != null) {
            if (receiptBillDetail.getLeaderAfterSaleNum() >= LEADER_MAX_NUMBER_AFTER_SALE_APPLY) {
                throw new ServiceException(String.format("订单%s团长申请售后次数已经超过最大限制，不能继续申请售后", order.getOrderNo()));
            }
        }
        if (OrderConstant.isClose(order.getPayStatus())) {
            throw new ServiceException(String.format("订单%s已经关闭，不能继续申请售后", order.getOrderNo()));
        }
        if (order.getConfirmTime() != null && DateUtil.offsetDay(order.getConfirmTime(), 10).getTime() < System.currentTimeMillis()) {
            throw new ServiceException(String.format("订单%s已经离确认收货已经超过%s天，不能继续申请售后", order.getOrderNo(), 10));
        }
        if (order.getRefundFee() != null && order.getRefundFee().compareTo(BigDecimal.ONE) > 0) {
            throw new ServiceException(String.format("订单%s已经成功退款，不能继续申请售后", order.getOrderNo()));
        }
        AfterSaleOrder afterSaleOrder = this.findById(order.getAfterSaleOrderId());
        if (afterSaleOrder != null && (afterSaleOrder.getAfterSaleStatus() != AfterSaleOrderStatus.商家拒绝.getValue() && afterSaleOrder.getAfterSaleStatus() != AfterSaleOrderStatus.关闭.getValue() && afterSaleOrder.getAfterSaleStatus() != AfterSaleOrderStatus.成功.getValue())) {
            //当前售后订单没有结束，并且当前售后订单不是换货单的不可再次申请售后
            if (!AfterSaleOrderType.isExChange(afterSaleOrder.getAfterSaleType())) {
                throw new ServiceException(String.format("订单%s正在申请售后，请等待当前售后流程结束", order.getOrderNo()));
            } else {
                //当前换货单等待审核的也不能再次申请
                if (afterSaleOrder.getAfterSaleStatus() == AfterSaleOrderStatus.待商家审核.getValue()) {
                    throw new ServiceException(String.format("订单%s正在申请售后，请等待当前售后流程结束", order.getOrderNo()));
                }

            }
        }

    }

    private void batchCheckRefund(List<Order> orders, BatchApplyAfterSaleOrderVO batchApplyAfterSaleOrderVO) {
        BigDecimal fee = BigDecimal.ZERO;
        for (Order order : orders) {
            if (order.getPayStatus() == OrderConstant.PAYED || order.getPayStatus() == OrderConstant.GROUP_LEADER_RECEIPT || order.getPayStatus() == OrderConstant.SENDED) {
                fee = fee.add(order.getPayFee());
            }
        }
        batchApplyAfterSaleOrderVO.setRefundFee(fee);

    }

    private void batchCheckExchange(List<OrderDetail> orderDetails,
                                    BaseApplyAfterSaleOrderVO baseApplyAfterSaleOrderVO) {
        checkExchange(orderDetails, baseApplyAfterSaleOrderVO);

    }

}
