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
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import com.github.pagehelper.PageHelper;
import com.mds.group.purchase.constant.ActiviMqQueueName;
import com.mds.group.purchase.constant.Common;
import com.mds.group.purchase.constant.ReceiptBillConstant;
import com.mds.group.purchase.constant.SendBillConstant;
import com.mds.group.purchase.constant.enums.AfterSaleOrderStatus;
import com.mds.group.purchase.core.AbstractService;
import com.mds.group.purchase.exception.ServiceException;
import com.mds.group.purchase.jobhandler.ActiveDelaySendJobHandler;
import com.mds.group.purchase.logistics.model.Line;
import com.mds.group.purchase.logistics.service.LineService;
import com.mds.group.purchase.order.dao.AfterSaleOrderMapper;
import com.mds.group.purchase.order.dao.OrderMapper;
import com.mds.group.purchase.order.dao.ReceiptBillMapper;
import com.mds.group.purchase.order.dao.SendBillMapper;
import com.mds.group.purchase.order.model.*;
import com.mds.group.purchase.order.result.OrderResult;
import com.mds.group.purchase.order.result.SendBillExcel;
import com.mds.group.purchase.order.result.SendBillResult;
import com.mds.group.purchase.order.service.*;
import com.mds.group.purchase.order.vo.SendBillFilterVo;
import com.mds.group.purchase.order.vo.SendbillFindVO;
import com.mds.group.purchase.utils.OrderUtil;
import com.mds.group.purchase.utils.SendBillUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Condition;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;


/**
 * 发货单业务实现类
 *
 * @author shuke
 * @date 2019 /02/18
 */
@Service
public class SendBillServiceImpl extends AbstractService<SendBill> implements SendBillService {

    @Resource
    private OrderUtil orderUtil;
    @Resource
    private LineService lineService;
    @Resource
    private OrderMapper orderMapper;
    @Resource
    private AfterSaleOrderMapper afterSaleOrderMapper;
    @Resource
    private OrderService orderService;
    @Resource
    private SendBillMapper tSendBillMapper;
    @Resource
    private ReceiptBillMapper receiptBillMapper;
    @Resource
    private OrderDetailService orderDetailService;
    @Resource
    private OrderSendBillMappingService osbmService;
    @Resource
    private SendBillActivityService sendBillActivityService;
    @Resource
    private ActiveDelaySendJobHandler activeDelaySendJobHandler;
    @Resource
    private OrderSendBillMappingService orderSendBillMappingService;
    @Resource
    private NegotiateHistoryService negotiateHistoryService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void generateSendBill(String appmodelId) {
        //查询订单发货单关系表，查找当前appmodelId对应的所有未生成记录
        List<OrderSendBillMapping> byAppmodelId = osbmService.findByAppmodelId(appmodelId);
        if (byAppmodelId.isEmpty()) {
            return;
        }
        //提取上一步结果中的订单id
        List<Long> orderIdList = byAppmodelId.stream().map(OrderSendBillMapping::getOrderId)
                .collect(Collectors.toList());
        //根据订单id查询所有对应订单，得到总成交额和总佣金
        List<OrderResult> orderResultByOrderIds = orderService.findOrderResultByOrderIds(orderIdList);
        //过滤当前没有售后或者已经售后结束的订单
        orderResultByOrderIds =
                orderResultByOrderIds.stream()
                        .filter(orderResult -> orderResult.getAfterSaleOrderId() == null || AfterSaleOrderStatus.isEnd(orderResult.getAfterSaleStatus())).collect(Collectors.toList());
        //将所有订单按照活动分组
        Map<Long, List<OrderResult>> map = new HashMap<>(16);
        for (OrderResult orderResultByOrderId : orderResultByOrderIds) {
            Long activityId = orderResultByOrderId.getActivityId();
            if (!map.containsKey(activityId)) {
                List<OrderResult> orderList = new ArrayList<>();
                orderList.add(orderResultByOrderId);
                map.put(activityId, orderList);
            } else {
                List<OrderResult> orderResults = map.get(activityId);
                orderResults.add(orderResultByOrderId);
                map.put(activityId, orderResults);
            }
        }
        //得到map中所有活动id对应的生成日期,日期匹配的才能生成发货单
        //如果该活动没设置生成时间，则按原时间生成
        Set<Long> actIds = map.keySet();
        List<Long> actIdList = new ArrayList<>(actIds);
        List<SendBillActivity> byActIds = sendBillActivityService.getByActIds(actIdList);
        for (SendBillActivity byActId : byActIds) {
            String dateStr = byActId.getSendBillGenerateDate();
            //活动有设置发货单生成时间
            if (StringUtils.isNotBlank(dateStr)) {
                int date = DateUtil.parseDate(dateStr).getDate();
                int month1 = DateUtil.parseDate(dateStr).getMonth();
                int month2 = DateUtil.thisMonth();
                int today = DateUtil.thisDayOfMonth();
                int year2 = DateUtil.thisYear();
                int year1 = DateUtil.parseDate(dateStr).year();
                if (year1 >= year2 && month1 >= month2 && date > today) {
                    //将日期不匹配的数据从map中移除
                    map.remove(byActId.getActivityId());
                }
            }
        }
        //将map中的数据放入list中
        List<OrderResult> results = new ArrayList<>();
        Collection<List<OrderResult>> values = map.values();
        if (values.isEmpty()) {
            return;
        }
        for (List<OrderResult> orderResults : values) {
            results.addAll(orderResults);
        }
        //提取要生成发货单的订单id，用于发货单生成后将订单发货单映射表中的数据状态更新为已生成状态
        List<Long> orderIds = results.stream().map(OrderResult::getOrderId).collect(Collectors.toList());
        BigDecimal amount = BigDecimal.valueOf(0);
        BigDecimal commission = BigDecimal.valueOf(0);
        for (OrderResult orderResultByOrderId : results) {
            amount = amount.add(orderResultByOrderId.getPayFee());
            commission = commission.add(orderResultByOrderId.getGroupLeaderCommission());
        }
        SendBill sendBill = new SendBill();
        sendBill.setAmount(amount);
        sendBill.setAppmodelId(appmodelId);
        sendBill.setCommission(commission);
        sendBill.setOrders(results.size());
        sendBill.setCreateDate(DateUtil.now());
        sendBill.setDelFlag(Common.DEL_FLAG_FALSE);
        sendBill.setStatus(SendBillConstant.WAIT_SEND);
        sendBill.setSendBillName(SendBillUtil.getSendBillName());
        tSendBillMapper.insert(sendBill);

        //发货单生成后将订单发货单映射表中的数据状态更新为已生成状态
        orderSendBillMappingService.updateGenerateAndSendBillId(sendBill.getSendBillId(), orderIds);

        activeDelaySendJobHandler
                .savaTask(sendBill.getSendBillId().toString(), ActiviMqQueueName.GOODS_SORTING_ORDER_CACHE, 0L,
                        appmodelId,
                        true);
        activeDelaySendJobHandler
                .savaTask(sendBill.getSendBillId().toString(), ActiviMqQueueName.RECEIPT_BILL_CACHE, 0L, appmodelId,
                        true);
        activeDelaySendJobHandler
                .savaTask(sendBill.getSendBillId().toString(), ActiviMqQueueName.LINE_SORTING_ORDER_CACHE, 0L,
                        appmodelId,
                        true);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void doSend(String appmodelId, Long sendBillId) {
        //从订单发货单映射表中获取所有属于该发货单的订单编号集合
        List<OrderSendBillMapping> bySendBillId = orderSendBillMappingService.findBySendBillId(sendBillId, appmodelId);
        List<Long> orderIds = bySendBillId.stream().map(OrderSendBillMapping::getOrderId).collect(Collectors.toList());
        //更新这些订单的状态为已发货
        Boolean notRefundOrder = orderMapper.checkSendByAfterSaleOrder(orderIds).isEmpty();
        if (!notRefundOrder) {
            throw new ServiceException("该发货单中有退款订单,请前往查看！");
        }
        orderMapper.updateBatch(orderIds, null, 1);
        orderDetailService.updateSendTime(orderIds, new DateTime());
        addNegotiateHistory(orderIds);
        afterSaleOrderMapper.updateSatausByOrderIds(orderIds, AfterSaleOrderStatus.待签收.getValue());
        receiptBillMapper.updateStatusBySendBillId(sendBillId, ReceiptBillConstant.DISPATCHING, appmodelId);
        tSendBillMapper.updateStatus(sendBillId, SendBillConstant.DISPATCHING);
        //向用户发送发货模板消息
        orderUtil.orderSendMsg(orderIds);
//        solitaireUtil.ascySendAtWxuserMsg(orderIds);
    }

    private void addNegotiateHistory(List<Long> orderIds) {
        List<AfterSaleOrder> afterSaleOrders = afterSaleOrderMapper.findByNewOrderIds(orderIds);
        for (AfterSaleOrder afterSaleOrder : afterSaleOrders) {
            List<Order> orders = orderMapper.selectOrderByAfterSaleOrderId(afterSaleOrder.getAfterSaleOrderId());
            if (!orders.isEmpty()) {
                negotiateHistoryService.exchangeSend(orders.get(0), afterSaleOrder);
            }
        }
    }

    @Override
    public List<OrderResult> getDetail(String appmodelId, Long sendBillId) {
        //从订单发货单映射表中获取所有属于该发货单的订单编号集合
        List<OrderSendBillMapping> bySendBillId = orderSendBillMappingService.findBySendBillId(sendBillId, appmodelId);
        List<Long> orderIds = bySendBillId.stream().map(OrderSendBillMapping::getOrderId).collect(Collectors.toList());
        return orderService.findOrderResultByOrderIds(orderIds);
    }

    @Override
    public List<SendBillResult> filter(String appmodelId, SendBillFilterVo sendBillFilterVo) {
        String inputDate = sendBillFilterVo.getInputDate();
        Integer status = sendBillFilterVo.getSendBillStatus();
        Integer quicllyDate = sendBillFilterVo.getQuicklyDate();
        String startDateStr = "";
        String endDateStr = "";
        DateTime startDate = null;
        DateTime endDate = null;

        //默认发货单状态为查询全部
        if (status == null) {
            status = 0;
        }
        //默认获取近一个月的发货单
        if (quicllyDate == null) {
            quicllyDate = 1;
        }
        switch (quicllyDate) {
            case 0:
                startDateStr = null;
                break;
            case 1:
                startDate = DateUtil.lastMonth();
                endDate = DateUtil.date();
                break;
            case 2:
                startDate = DateUtil.offsetMonth(DateUtil.date(), -3);
                break;
            default:
                startDate = null;
                endDate = null;
        }

        if (StringUtils.isNotBlank(inputDate)) {
            startDateStr = inputDate.substring(0, inputDate.indexOf(","));
            endDateStr = inputDate.substring(inputDate.indexOf(",") + 1);
            startDate = DateUtil.parse(startDateStr + " 00:00:00");
            endDate = DateUtil.parse(endDateStr + " 23:59:59");
        }

        List<SendBillResult> sendBills = tSendBillMapper.selectByFilterVoSelective(appmodelId, status);
        if (sendBills.isEmpty()) {
            return new ArrayList<>();
        }
        for (SendBillResult sendBill : sendBills) {
            List<Long> orderIds = orderSendBillMappingService.selectAllBySendBillId(sendBill.getSendBillId(),
                    appmodelId);
            if (!CollectionUtil.isEmpty(orderIds)) {
                sendBill.setCanSend(orderMapper.checkSendByAfterSaleOrder(orderIds).isEmpty());
            }
        }

        if (startDateStr == null || startDate == null || endDate == null) {
            return sendBills;
        }
        Iterator<SendBillResult> iterator = sendBills.iterator();
        while (iterator.hasNext()) {
            SendBillResult sendBill = iterator.next();
            DateTime createTime = DateUtil.parse(sendBill.getCreateDate());
            if (!createTime.isIn(startDate, endDate)) {
                iterator.remove();
            }
        }
        return sendBills;
    }

    @Override
    public void downloadSendBill(HttpServletResponse response, String appmodelId, Long sendBillId) {
        List<OrderSendBillMapping> bySendBillId = orderSendBillMappingService.findBySendBillId(sendBillId, appmodelId);
        if (bySendBillId.isEmpty()) {
            throw new ServiceException("发货单内没有订单");
        }
        List<Long> orderIds = bySendBillId.stream().map(OrderSendBillMapping::getOrderId).collect(Collectors.toList());
        List<SendBillExcel> sendBillExcels = orderMapper.selectByorderIds(orderIds);
        SendBill sendBill = this.findById(sendBillId);

        //设置要导出的文件的名字
        String fileName = "发货单.xlsx";
        try (ExcelWriter writer = ExcelUtil.getBigWriter()) {
            //设置行首样式
            writer.getHeadCellStyle().setFillBackgroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
            Font font = writer.createFont();
            font.setBold(true);
            writer.getHeadCellStyle().setFont(font);
            writer.merge(0, 1, 0, 5, sendBill.getSendBillName(), false);
            writer.merge(2, 3, 0, 0, "序号", true);
            writer.merge(2, 3, 1, 1, "订单", true);
            writer.merge(2, 3, 2, 2, "商品", true);
            writer.merge(2, 3, 3, 3, "客户", true);
            writer.merge(2, 3, 4, 4, "提货点", true);
            writer.merge(2, 3, 5, 5, "线路", true);
            writer.setColumnWidth(0, 13);
            writer.setColumnWidth(1, 35);
            writer.setColumnWidth(2, 35);
            writer.setColumnWidth(3, 35);
            writer.setColumnWidth(4, 35);
            writer.setColumnWidth(5, 35);
            writer.passRows(4);
            //设置自动换行
            writer.getStyleSet().getCellStyle().setWrapText(true);
            Iterator<SendBillExcel> iterator = sendBillExcels.iterator();
            int firstRow = 1;
            while (iterator.hasNext()) {
                SendBillExcel sendBillExcel = iterator.next();
                StringBuilder orderStr = new StringBuilder();
                orderStr.append("订单号：").append(sendBillExcel.getOrderNo()).append("\r\n订单创建时间：")
                        .append(DateUtil.formatDateTime(sendBillExcel.getCreateTime())).append("\r\n付款时间：")
                        .append(DateUtil.formatDateTime(sendBillExcel.getPayTime())).append("\r\n备注：")
                        .append(StringUtils.isNotBlank(sendBillExcel.getUserDesc()) ? sendBillExcel.getUserDesc() : "");

                StringBuilder goodsStr = new StringBuilder();
                goodsStr.append("商品名称：").append(sendBillExcel.getGoodsName()).append("\r\n规格：").append("\r\n单价：￥")
                        .append(sendBillExcel.getGoodsPrice()).append("\r\n数量：").append(sendBillExcel.getGoodsNum())
                        .append("\r\n总佣金：￥").append(sendBillExcel.getGroupLeaderCommission());

                StringBuilder userStr = new StringBuilder();
                userStr.append("买家昵称：").append(sendBillExcel.getWxuserName()).append("\r\n姓名：")
                        .append(sendBillExcel.getBuyerName()).append("\r\n电话：").append(sendBillExcel.getBuyerPhone())
                        .append("\r\n地址：").append(sendBillExcel.getBuyerAddress());

                StringBuilder groupStr = new StringBuilder();
                groupStr.append("小区名称：").append(sendBillExcel.getCommunityName()).append("\r\n团长：")
                        .append(sendBillExcel.getGroupLeaderName()).append("\r\n团长电话：")
                        .append(sendBillExcel.getGroupLeaderPhone()).append("\r\n提货地址：")
                        .append(sendBillExcel.getPickupLocation());

                StringBuilder lineStr = new StringBuilder();
                lineStr.append("线路：").append(sendBillExcel.getLineName()).append("\r\n司机：")
                        .append(sendBillExcel.getDriverName()).append("\r\n司机电话：")
                        .append(sendBillExcel.getDriverPhone());
                List<String> rows = CollUtil
                        .newArrayList(String.valueOf(firstRow++), orderStr.toString(), goodsStr.toString(),
                                userStr.toString(), groupStr.toString(), lineStr.toString());
                writer.getStyleSet();
                writer.getStyleSet().getCellStyle().setAlignment(HorizontalAlignment.LEFT);
                writer.writeRow(rows);
            }
            response.setContentType("application/vnd.ms-excel;charset=utf-8");
            response.setCharacterEncoding("UTF-8");
            response.setHeader("Content-Disposition",
                    "attachment;filename=" + new String(fileName.getBytes("GBK"), "iso8859-1"));
            writer.flush(response.getOutputStream());
            response.getOutputStream().close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<SendBill> getByDate(String appmodelId, String generateDate) {
        generateDate = generateDate.replaceAll("/", "-");
        List<SendBill> sendBills = tSendBillMapper.selectByDate(appmodelId, generateDate);
        Iterator<SendBill> iterator = sendBills.iterator();
        while (iterator.hasNext()) {
            SendBill next = iterator.next();
            String substring = next.getCreateDate().substring(0, 10);
            substring = substring.replaceAll("/", "-");
            if (!generateDate.equals(substring)) {
                iterator.remove();
            }
        }
        return sendBills;
    }

    @Override
    public List<SendBill> find3SendBill(String appmodelId) {
        return tSendBillMapper.select3SendBill(appmodelId);
    }

    @Override
    public void updateInfo(Long sendBillId, int orders, double amount, double commission) {
        tSendBillMapper.updateInfo(sendBillId, orders, amount, commission);
    }

    @Override
    public void removeById(Long sendBillId) {
        tSendBillMapper.deleteById(sendBillId);
    }

    @Override
    public SendBill getRecentlySendBill(String appmodelId) {
        Condition condition = new Condition(SendBill.class);
        condition.createCriteria().andEqualTo("appmodelId", appmodelId).andEqualTo("delFlag", 0)
                .andIn("status", Arrays.asList(1, 2, 3));
        condition.orderBy("createDate").desc();
        PageHelper.startPage(1, 1);
        List<SendBill> sendBills = tSendBillMapper.selectByCondition(condition);
        if (CollectionUtil.isNotEmpty(sendBills)) {
            return sendBills.get(0);
        }
        return null;
    }

    @Override
    public List<SendbillFindVO> sendbillFind(String appmodelId, Integer type) {
        List<SendbillFindVO> sendBills = tSendBillMapper.selectBySendbillFind(appmodelId);
        //查询团长签收单封装线路
        if (type == 2 && CollectionUtil.isNotEmpty(sendBills)) {
            List<Long> sendBillIdList = sendBills.stream().map(SendbillFindVO::getSendBillId)
                    .collect(Collectors.toList());
            Condition condition1 = new Condition(OrderSendBillMapping.class);
            condition1.createCriteria().andIn("sendBillId", sendBillIdList);
            List<OrderSendBillMapping> mappingList = orderSendBillMappingService.findByCondition(condition1);
            List<Long> orderIdList = mappingList.stream().map(OrderSendBillMapping::getOrderId)
                    .collect(Collectors.toList());
            List<OrderDetail> orderDetails = orderDetailService.findByOrderIds(orderIdList);
            String lineIds = orderDetails.stream().map(obj -> obj.getLineId().toString())
                    .collect(Collectors.joining(","));
            List<Line> lineList = lineService.findByIds(lineIds);
            //按发货单分组
            Map<Long, List<OrderSendBillMapping>> sendBillIdGroup = mappingList.stream()
                    .collect(Collectors.groupingBy(OrderSendBillMapping::getSendBillId));
            Map<Long, OrderDetail> orderDetailMap = orderDetails.stream()
                    .collect(Collectors.toMap(OrderDetail::getOrderId, v -> v));
            Map<Long, Line> lineMap = lineList.stream().collect(Collectors.toMap(Line::getLineId, v -> v));

            for (SendbillFindVO sendBill : sendBills) {
                List<OrderSendBillMapping> orderSendBillMappings = sendBillIdGroup.get(sendBill.getSendBillId());
                List<SendbillFindVO.SendbillFindLineVO> sendbillFindLineVOS = new LinkedList<>();
                if (orderSendBillMappings != null) {
                    for (OrderSendBillMapping mapping : orderSendBillMappings) {
                        OrderDetail orderDetail = orderDetailMap.get(mapping.getOrderId());
                        Line line = lineMap.get(orderDetail.getLineId());
                        SendbillFindVO.SendbillFindLineVO sendbillFindLineVO = sendBill.createSendbillFindLineVO();
                        sendbillFindLineVO.setLineId(line.getLineId());
                        sendbillFindLineVO.setLineName(line.getLineName());
                        sendbillFindLineVOS.add(sendbillFindLineVO);
                    }
                }
                sendBill.setSendbillFindLineVOList(sendbillFindLineVOS);
            }
        }
        return sendBills;
    }

}
