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

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import com.github.pagehelper.PageInfo;
import com.mds.group.purchase.constant.ActiviMqQueueName;
import com.mds.group.purchase.constant.OrderConstant;
import com.mds.group.purchase.constant.ReceiptBillConstant;
import com.mds.group.purchase.constant.TimeType;
import com.mds.group.purchase.constant.enums.AfterSaleOrderStatus;
import com.mds.group.purchase.constant.enums.AfterSaleOrderType;
import com.mds.group.purchase.core.AbstractService;
import com.mds.group.purchase.exception.ServiceException;
import com.mds.group.purchase.jobhandler.ActiveDelaySendJobHandler;
import com.mds.group.purchase.order.dao.AfterSaleOrderMapper;
import com.mds.group.purchase.order.dao.ReceiptBillDetailMapper;
import com.mds.group.purchase.order.dao.ReceiptBillMapper;
import com.mds.group.purchase.order.model.*;
import com.mds.group.purchase.order.result.ReceiptBillResult;
import com.mds.group.purchase.order.service.*;
import com.mds.group.purchase.order.vo.SearchReceiptBillVO;
import com.mds.group.purchase.utils.OrderUtil;
import com.mds.group.purchase.utils.PageUtil;
import com.mds.group.purchase.utils.SolitaireUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;


/**
 * The type Receipt bill service.
 *
 * @author CodeGenerator
 * @date 2018 /12/19
 */
@Slf4j
@Service
public class ReceiptBillServiceImpl extends AbstractService<ReceiptBill> implements ReceiptBillService {


    /**
     * 团长签收之后用户订单自动确认签收时间（7天）
     */
    private static final Long USER_AUTO_CONFIRM_TIME = TimeType.SEVENDAY;
    @Resource
    private OrderUtil orderUtil;
    @Resource
    private SendBillService sendBillService;
    @Resource
    private OrderService orderService;
    @Resource
    private SolitaireUtil solitaireUtil;
    @Resource
    private NegotiateHistoryService negotiateHistoryService;
    @Resource
    private OrderDetailService orderDetailService;
    @Resource
    private ReceiptBillDetailService receiptBillDetailService;
    @Resource
    private ReceiptBillMapper receiptBillMapper;
    @Resource
    private AfterSaleOrderMapper afterSaleOrderMapper;
    @Resource
    private AfterSaleOrderService afterSaleOrderService;
    @Resource
    private ReceiptBillDetailMapper receiptBillDetailMapper;
    @Resource
    private ActiveDelaySendJobHandler activeDelaySendJobHandler;

    @Override
    public List<ReceiptBillResult> getReceiptBill(String appmodelId, Long sendBillId, Long lineId) {
        SendBill recentlySendBill;
        if (sendBillId == 0) {
            recentlySendBill = sendBillService.getRecentlySendBill(appmodelId);
        } else {
            recentlySendBill = sendBillService.findById(sendBillId);
        }
        if (recentlySendBill == null) {
            return Collections.emptyList();
        }
        List<ReceiptBillResult> bills = receiptBillMapper.selectByReceiptBill(sendBillId, lineId, appmodelId);
        if (lineId != null && lineId != 0) {
            bills = bills.stream().filter(obj -> obj.getLineId().equals(lineId)).collect(Collectors.toList());
        }
        if (CollectionUtil.isEmpty(bills)) {
            return Collections.emptyList();
        }
        List<ReceiptBillDetail> receiptBillDetails =
                receiptBillDetailMapper.selectByBillIds(bills.stream().map(ReceiptBill::getBillId).collect(Collectors.toSet()));
        Map<Long, List<ReceiptBillDetail>> billDetailMap =
                receiptBillDetails.stream().collect(Collectors.groupingBy(ReceiptBillDetail::getBillId));
        for (ReceiptBillResult receiptBill : bills) {
            receiptBill.setReceiptBillDetailList(billDetailMap.get(receiptBill.getBillId()));
        }
        return bills;
    }

    @Override
    public void export(String appmodelId, Integer pageNum, Integer pageSize, Long lineId, Long sendBillId,
                       HttpServletResponse response) {
        List<ReceiptBillResult> list = getReceiptBill(appmodelId, sendBillId, lineId);
        PageInfo pageInfo = PageUtil.pageUtil(pageNum, pageSize, list);
        list = pageInfo.getList();
        // 此处设置成-1是为了防止内存溢出，且要使用bigWriter
        // 当大数据量，需要在一个excel中创建很多行，然后此时就发现一个问题：创建的行数超过windowSize的大小时，会报以下错误：
        // java.lang.IllegalArgumentException: Attempting to write a row[0] in the range [0,0] that is already
        // written to disk.
        // 经排查，当通过createRow（）创建一个新行并且未刷新记录的总数超过指定的窗口大小时，具有最低索引值的行将被刷新，并且不能再通过getRow（）等访问。 
        // 比如窗口行数为100，内存当前有100行，createRow（）创建一个新行，索引值为0的那一行被刷新到本地文件,该行将无法访问，因为它们已写入磁盘。
        // 如果创建的行数没有超过windowSize的大小，则不会有此问题，但现在内存中不能存过多数据，所以windowSize的大小肯定会小于新建行数值。
        // 查看api，最终找到解决方法，设置一下在内存中的行数即可，-1表示无限制
        try (ExcelWriter writer = ExcelUtil.getBigWriter(-1)) {
            Map<Long, List<ReceiptBillResult>> lineMap = list.stream()
                    .collect(Collectors.groupingBy(ReceiptBill::getLineId));
            AtomicInteger i = new AtomicInteger(0);
            lineMap.forEach((k, v) -> {
                Map<String, List<ReceiptBillResult>> groupMap = v.stream()
                        .collect(Collectors.groupingBy(ReceiptBill::getGroupLeaderId));
                String sheetName = v.get(0).getLineName();
                if (i.get() > 0) {
                    writer.setSheet(sheetName);
                } else {
                    writer.getWorkbook().setSheetName(i.get(), sheetName);
                }
                i.addAndGet(1);
                //合并首行
                StringBuilder sb = new StringBuilder();
                sb.append(v.get(0).getSendBillName().replace("发货单", "团长签收单"));
                sb.append("    线路名称:".concat(v.get(0).getLineName()));
                sb.append("    司机姓名:".concat(v.get(0).getDriverName()));
                sb.append("    司机电话:".concat(v.get(0).getPhone()));
                //设置行首样式
                writer.getHeadCellStyle().setFillBackgroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
                Font font = writer.createFont();
                font.setBold(true);
                writer.getHeadCellStyle().setFont(font);
                //设置自动换行
                writer.getStyleSet().getCellStyle().setWrapText(true);
                writer.merge(0, 1, 0, 6, sb.toString(), false);
                writer.merge(2, 3, 0, 0, "团长/小区", true);
                writer.merge(2, 3, 1, 2, "商品", true);
                writer.merge(2, 3, 3, 3, "数量", true);
                writer.merge(2, 3, 4, 6, "团长签名", true);
                writer.setColumnWidth(0, 30);
                writer.setColumnWidth(1, 15);
                writer.setColumnWidth(2, 15);
                writer.setColumnWidth(3, 12);
                writer.setColumnWidth(4, 8);
                writer.setColumnWidth(5, 8);
                writer.setColumnWidth(6, 8);
                writer.passRows(4);
                groupMap.forEach((gId, results) -> {
                    int currentRow = writer.getCurrentRow();
                    List<ReceiptBillDetail> receiptBillDetailList1 = new ArrayList<>();
                    results.forEach(obj -> receiptBillDetailList1.addAll(obj.getReceiptBillDetailList()));
                    StringBuilder st = new StringBuilder();
                    st.append("小区:".concat(results.get(0).getCommunityName()).concat("\n团长:")
                            .concat(results.get(0).getGroupLeaderName()).concat("\r\n电话:")
                            .concat(results.get(0).getPhone()));
                    int size = results.stream().mapToInt(obj -> obj.getReceiptBillDetailList().size()).sum() - 1;
                    int height = 25;
                    if (size > 0) {
                        writer.merge(currentRow, currentRow + size, 0, 0, st.toString(), false);
                        writer.merge(currentRow, currentRow + size, 4, 6, "", false);
                    } else {
                        Row row = writer.getOrCreateRow(currentRow);
                        Cell cell = row.createCell(0);
                        cell.setCellValue(st.toString());
                        writer.writeCellValue(cell.getColumnIndex(), cell.getRowIndex(), cell);
                        writer.merge(currentRow, currentRow, 4, 6, "", false);
                        writer.setRowHeight(currentRow, height * 2);
                    }
                    for (ReceiptBillDetail receiptBillDetailVO : receiptBillDetailList1) {
                        int currentRow1 = writer.getCurrentRow();
                        writer.merge(currentRow1, currentRow1, 1, 2, receiptBillDetailVO.getGoodsName(), false);
                        Row row = writer.getOrCreateRow(currentRow1);
                        Cell goodsSum = row.createCell(3);
                        goodsSum.setCellValue(receiptBillDetailVO.getGoodsNum().toString());
                        writer.writeCellValue(goodsSum.getColumnIndex(), goodsSum.getRowIndex(), goodsSum);
                        height = receiptBillDetailVO.getGoodsName().length() / 15 * 10 + height;
                        writer.passRows(1);
                        writer.setRowHeight(currentRow1, height);
                    }
                });
            });
            response.setContentType("application/vnd.ms-excel;charset=gb2312");
            String filename = new String(list.get(0).getSendBillName().replace("发货单", "团长签收单").getBytes(
                    StandardCharsets.UTF_8),
                    "gb2312");
            response.setHeader("Content-Disposition", "attachment;filename=".concat(filename));
            writer.flush(response.getOutputStream());
            response.getOutputStream().close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<ReceiptBillResult> searchReceiptBill(SearchReceiptBillVO searchReceiptBillVO) {
        if (searchReceiptBillVO.getType() == 1) {
            if (StringUtils.isBlank(searchReceiptBillVO.getStartDate()) && StringUtils.isBlank(searchReceiptBillVO.getEndDate())) {
                searchReceiptBillVO.setStartDate(DateUtil.lastMonth().toString());
                searchReceiptBillVO.setEndDate(DateUtil.now());
            } else {
                searchReceiptBillVO.setStartDate(DateUtil.parse(searchReceiptBillVO.getStartDate()).toString());
                searchReceiptBillVO.setEndDate(DateUtil.endOfDay(DateUtil.parse(searchReceiptBillVO.getEndDate()).toJdkDate()).toString());
            }
        }
        //查询符合条件的团长签收单
        List<ReceiptBillResult> receiptBillResults = receiptBillMapper.selectByGroupLeaderId(searchReceiptBillVO);
        if (receiptBillResults.isEmpty()) {
            return Collections.EMPTY_LIST;
        }
        List<ReceiptBillDetail> receiptBillDetails =
                receiptBillDetailService.selectByBillIds(receiptBillResults.stream().map(ReceiptBill::getBillId).collect(Collectors.toSet()));
        if (searchReceiptBillVO.getType() == 0) {
            //过滤掉已经签收的详情
            receiptBillDetails =
                    receiptBillDetails.stream().filter(receiptBillDetail -> receiptBillDetail.getReceiptGoodsNum() < receiptBillDetail.getGoodsNum()).collect(Collectors.toList());
        } else {
            //过滤掉没有签收的详情
            receiptBillDetails =
                    receiptBillDetails.stream().filter(receiptBillDetail -> receiptBillDetail.getReceiptGoodsNum() > 0).collect(Collectors.toList());
        }
        //按照签收单ID分组
        Map<Long, List<ReceiptBillDetail>> groupReceiptBillDetail = receiptBillDetails.stream()
                .collect(Collectors.groupingBy(ReceiptBillDetail::getBillId));
        for (ReceiptBillResult receiptBillResult : receiptBillResults) {
            //将签收单所属的详情添加到签收单下面
            receiptBillResult.setReceiptBillDetailList(groupReceiptBillDetail.get(receiptBillResult.getBillId()));
        }
        return receiptBillResults;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void receiptByBillId(Long billId) {
        ReceiptBill receiptBill = this.findById(billId);
        if (receiptBill == null) {
            throw new ServiceException("无效的ID");
        }
        List<ReceiptBillDetail> receiptBillDetails =
                receiptBillDetailService.selectByBillIds(Collections.singleton(billId));
        receiptBillDetails =
                receiptBillDetails.stream().filter(receiptBillDetail -> receiptBillDetail.getGoodsNum() > receiptBillDetail.getReceiptGoodsNum()).collect(Collectors.toList());
        //获取当前签收单可以被签收的订单
        List<Order> orders =
                orderService.findWaitReceiptByOrderIds(receiptBillDetails.stream().map(ReceiptBillDetail::getOrderIds).collect(Collectors.joining(",")));
        cancelPendingAfterSaleOrder(orders);
        List<Long> orderIds = orders.stream().map(Order::getOrderId).collect(Collectors.toList());
        for (ReceiptBillDetail receiptBillDetail : receiptBillDetails) {
            countGoodsNum(receiptBillDetail, orders);
        }
        //换货单签收时同步更新售后单的状态
        afterSaleOrderMapper.updateSatausByOrderIds(orderIds, AfterSaleOrderStatus.待提货.getValue());
        //签收订单中换货单的原始订单
        updateOriginalOrderStatusByExChangeOrder(orders);
        //发送提货通知
        orderUtil.orderArriveMsg(orders.stream().map(Order::getOrderId).collect(Collectors.toList()));
        //查看商品是否已经全部签收
        checkReceiptStatus(receiptBillDetails.get(0));
        //机器人发送到货提醒
        solitaireUtil.ascySendAtWxuserMsg(orderIds);
    }

    /**
     * Cancel pending after sale order.
     *
     * @param orders the orders
     */
    public void cancelPendingAfterSaleOrder(List<Order> orders) {
        if (orders.isEmpty()) {
            throw new ServiceException("没有符合签收条件的订单");
        }
        for (Order order : orders) {
            checkAfterSaleOrderStatus(order);
        }
    }

    @Override
    public void checkAfterSaleOrderStatus(Order order) {
        List<Long> orderIds = new ArrayList<>();
        AfterSaleOrder generateExchangeAfterSaleOrder = new AfterSaleOrder();
        if (order.getOrderType() != null && order.getOrderType().equals(OrderConstant.EXCHANGE_ORDER)) {
            generateExchangeAfterSaleOrder = afterSaleOrderMapper.findByNewOrderId(order.getOrderId());
            orderIds.addAll(orderService.findByIds(generateExchangeAfterSaleOrder.getOriginalOrderId()).stream().map(Order::getOrderId).collect(Collectors.toList()));
        } else {
            orderIds.add(order.getOrderId());
        }
        List<AfterSaleOrder> afterSaleOrders = afterSaleOrderMapper.findByOriginalOrderIds(orderIds);
        afterSaleOrders.remove(generateExchangeAfterSaleOrder);
        for (AfterSaleOrder afterSaleOrder : afterSaleOrders) {
            if (AfterSaleOrderStatus.canReceipt(afterSaleOrder.getAfterSaleStatus())) {
                if (afterSaleOrder.getAfterSaleStatus().equals(AfterSaleOrderStatus.待商家审核.getValue())) {
                    //取消售后申请
                    int type = AfterSaleOrderType.isLeader(afterSaleOrder.getAfterSaleType()) ? 2 : 1;
                    afterSaleOrderService.cancel(afterSaleOrder.getAfterSaleOrderId().toString(), false, type);
                }
            } else {
                //2个订单都是团长申请的或者都是用户申请的
                if (generateExchangeAfterSaleOrder != null && generateExchangeAfterSaleOrder.getAfterSaleType() != null && (AfterSaleOrderType.isLeader(generateExchangeAfterSaleOrder.getAfterSaleType()) == AfterSaleOrderType.isLeader(afterSaleOrder.getAfterSaleType()))) {
                    throw new ServiceException("当前申请的售后流程还未结束，不能直接确认收货");
                }
            }
        }
    }

    /**
     * Update original order status by ex change order.
     *
     * @param orders the orders
     */
    public void updateOriginalOrderStatusByExChangeOrder(List<Order> orders) {
        //先过滤订单，只留下换货单
        orders =
                orders.stream().filter(order -> order.getOrderType() == OrderConstant.EXCHANGE_ORDER).collect(Collectors.toList());
        if (orders.isEmpty()) {
            return;
        }
        //找出生成换货单的售后单
        List<AfterSaleOrder> afterSaleOrders =
                afterSaleOrderMapper.findByNewOrderIds(orders.stream().map(Order::getOrderId).collect(Collectors.toList()));
        for (AfterSaleOrder afterSaleOrder : afterSaleOrders) {
            if (afterSaleOrder.getAfterSaleType() == AfterSaleOrderType.团长换货.getValue()) {
                //关闭团长换货单
                Order order = orderService.findById(afterSaleOrder.getOrderId());
                order.setPayStatus(OrderConstant.ORDER_SUCCESS_BY_EXCHANGE);
                order.setCloseTime(new Date());
                orderService.update(order);
                //关闭售后申请
                afterSaleOrder.setSuccessTime(new Date());
                afterSaleOrder.setAfterSaleStatus(AfterSaleOrderStatus.成功.getValue());
                List<AfterSaleOrder> afterSaleOrderList =
                        afterSaleOrderMapper.findByOriginalOrderIds(Arrays.stream(afterSaleOrder.getOriginalOrderId().split(",")).map(Long::parseLong).collect(Collectors.toList()));
                afterSaleOrderList = afterSaleOrderList.stream()
                        .filter(aso -> aso.getAfterSaleStatus().equals(AfterSaleOrderStatus.待签收.getValue()))
                        .filter(aso -> !AfterSaleOrderType.isLeader(aso.getAfterSaleType()))
                        .collect(Collectors.toList());
                for (AfterSaleOrder saleOrder : afterSaleOrderList) {
                    saleOrder.setAfterSaleStatus(AfterSaleOrderStatus.待提货.getValue());
                    afterSaleOrderMapper.updateByPrimaryKey(saleOrder);
                }
                negotiateHistoryService.exchangeEnd(order, afterSaleOrder);
            } else {
                afterSaleOrder.setAfterSaleStatus(AfterSaleOrderStatus.待提货.getValue());
            }
            afterSaleOrderMapper.updateByPrimaryKey(afterSaleOrder);
            //签收获得生成售后单的原始订单
            receiptByOrderIds(afterSaleOrder.getOriginalOrderId());
        }
    }

    /**
     * Receipt by order ids.
     *
     * @param orderIds the order ids
     */
    public void receiptByOrderIds(String orderIds) {
        Long orderId = Arrays.stream(orderIds.split(",")).findFirst().map(Long::parseLong).get();
        ReceiptBillDetail receiptBillDetail = receiptBillDetailService.findByOrderId(orderId);
        //获取当前签收单可以被签收的订单
        List<Order> orders = orderService.findByIds(orderIds);
        countGoodsNum(receiptBillDetail, orders);
        checkReceiptStatus(receiptBillDetail);

    }

    /**
     * Count goods num.
     *
     * @param receiptBillDetail the receipt bill detail
     * @param orders            the orders
     */
    public void countGoodsNum(ReceiptBillDetail receiptBillDetail, List<Order> orders) {
        List<Long> canReceiptOrder =
                Arrays.stream(receiptBillDetail.getOrderIds().split(",")).map(Long::parseLong).collect(Collectors.toList());
        orders =
                orders.stream().filter(order -> canReceiptOrder.contains(order.getOrderId())).collect(Collectors.toList());
        List<OrderDetail> orderDetails =
                orderDetailService.findByOrderIds(orders.stream().map(Order::getOrderId).collect(Collectors.toList()));
        Map<Long, List<OrderDetail>> groupOrderDetails =
                orderDetails.stream().collect(Collectors.groupingBy(OrderDetail::getOrderId));
        Integer receiptGoodsNum = 0;
        for (Order order : orders) {
            if (!OrderConstant.isReceipt(order.getPayStatus())) {
                order.setPayStatus(OrderConstant.GROUP_LEADER_RECEIPT);
                orderService.update(order);
                activeDelaySendJobHandler
                        .savaTask(order.getOrderId().toString(), ActiviMqQueueName.USER_AUTO_CONFIRM,
                                USER_AUTO_CONFIRM_TIME, receiptBillDetail.getAppmodelId(), false);
                //计算商品数量
                if (groupOrderDetails.get(order.getOrderId()) != null) {
                    receiptGoodsNum += groupOrderDetails.get(order.getOrderId()).stream()
                            .filter(orderDetail -> orderDetail.getGoodsId().equals(receiptBillDetail.getGoodsId()))
                            .mapToInt(OrderDetail::getGoodsNum)
                            .sum();
                } else {
                    log.error("订单ID{}未能找到订单详情", order.getOrderId());
                }

            }
        }
        receiptBillDetail.setReceiptGoodsNum(receiptBillDetail.getReceiptGoodsNum() + receiptGoodsNum);
        receiptBillDetailService.update(receiptBillDetail);
    }

    @Override
    public void checkReceiptStatus(ReceiptBillDetail receiptBillDetail) {
        //查看商品是否已经全部签收
        ReceiptBill receiptBill = receiptBillMapper.selectByPrimaryKey(receiptBillDetail.getBillId());
        List<ReceiptBillDetail> receiptBillDetailList =
                receiptBillDetailMapper.selectByBillIds(CollUtil.newHashSet(Arrays.asList(receiptBill.getBillId())));
        Long notReceipt =
                receiptBillDetailList.stream().filter(receiptBillDetail1 -> receiptBillDetail1.getGoodsNum() > receiptBillDetail1.getReceiptGoodsNum()).count();
        if (notReceipt == 0) {
            //如果已经全部签收就修改签收单状态为已完成
            receiptBillMapper.updateStatus(receiptBill.getBillId(), ReceiptBillConstant.RECEIPT_SUCCESS,
                    receiptBill.getAppmodelId());
        } else {
            if (!receiptBill.getStatus().equals(ReceiptBillConstant.WAIT_RECEIPT)) {
                receiptBillMapper.updateStatus(receiptBill.getBillId(), ReceiptBillConstant.WAIT_RECEIPT,
                        receiptBill.getAppmodelId());
            }
        }
        activeDelaySendJobHandler
                .savaTask(Arrays.stream(receiptBillDetail.getOrderIds().split(",")).findFirst().get(),
                        ActiviMqQueueName.SENDBILL_SUCCESS, 0L, receiptBillDetail.getAppmodelId(), false);
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public void receiptByOrderIds(String receiptBillDetailId, String orderIds) {
        ReceiptBillDetail receiptBillDetail = receiptBillDetailService.findById(receiptBillDetailId);
        //获取当前签收单可以被签收的订单
        List<Order> orders = orderService.findWaitReceiptByOrderIds(orderIds);
        cancelPendingAfterSaleOrder(orders);
        List<Long> orderIdList = orders.stream().map(Order::getOrderId).collect(Collectors.toList());
        countGoodsNum(receiptBillDetail, orders);
        //换货单签收时同步更新售后单的状态
        afterSaleOrderMapper.updateSatausByOrderIds(orderIdList, AfterSaleOrderStatus.待提货.getValue());
        //更新换货单关联的售后订单状态
        updateOriginalOrderStatusByExChangeOrder(orders);
        //发送提货通知
        orderUtil.orderArriveMsg(orders.stream().map(Order::getOrderId).collect(Collectors.toList()));
        //查看商品是否已经全部签收
        checkReceiptStatus(receiptBillDetail);
    }

    @Override
    public void deleteReceiptBySendBillId(Long sendBillId, String appmodelId) {
        List<ReceiptBillResult> receiptBills = receiptBillMapper.selectByReceiptBill(sendBillId, 0L, appmodelId);
        if (CollectionUtil.isEmpty(receiptBills)) {
            return;
        }
        receiptBillDetailMapper.deleteByBillIds(receiptBills.stream().map(ReceiptBill::getBillId).collect(Collectors.toList()));
        receiptBillMapper.deleteByIds(receiptBills.stream().map(ReceiptBill::getBillId).map(String::valueOf).collect(Collectors.joining(",")));
    }

}

