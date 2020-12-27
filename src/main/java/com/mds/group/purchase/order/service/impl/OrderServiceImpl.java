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
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.binarywang.wxpay.bean.notify.WxPayNotifyResponse;
import com.github.binarywang.wxpay.bean.notify.WxPayOrderNotifyResult;
import com.github.binarywang.wxpay.constant.WxPayConstants;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.mds.group.purchase.activity.model.ActivityGoods;
import com.mds.group.purchase.activity.service.ActivityGoodsService;
import com.mds.group.purchase.configurer.GroupMallProperties;
import com.mds.group.purchase.configurer.WxServiceUtils;
import com.mds.group.purchase.constant.*;
import com.mds.group.purchase.constant.enums.AfterSaleOrderStatus;
import com.mds.group.purchase.core.AbstractService;
import com.mds.group.purchase.exception.ServiceException;
import com.mds.group.purchase.financial.model.GroupBrokerage;
import com.mds.group.purchase.financial.service.GroupBrokerageService;
import com.mds.group.purchase.goods.model.Goods;
import com.mds.group.purchase.goods.model.GoodsDetail;
import com.mds.group.purchase.goods.result.GoodsResult;
import com.mds.group.purchase.goods.service.GoodsDetailService;
import com.mds.group.purchase.goods.service.GoodsService;
import com.mds.group.purchase.jobhandler.ActiveDelaySendJobHandler;
import com.mds.group.purchase.logistics.model.LineDetail;
import com.mds.group.purchase.logistics.service.LineDetailService;
import com.mds.group.purchase.order.dao.AfterSaleOrderMapper;
import com.mds.group.purchase.order.dao.OrderMapper;
import com.mds.group.purchase.order.model.AfterSaleOrder;
import com.mds.group.purchase.order.model.Order;
import com.mds.group.purchase.order.model.OrderDetail;
import com.mds.group.purchase.order.model.OrderSendBillMapping;
import com.mds.group.purchase.order.result.*;
import com.mds.group.purchase.order.service.*;
import com.mds.group.purchase.order.vo.*;
import com.mds.group.purchase.shop.vo.HistoricalTransactionDataVO;
import com.mds.group.purchase.shop.vo.OrderDataVO;
import com.mds.group.purchase.shop.vo.SalesVolumeVO;
import com.mds.group.purchase.shop.vo.StatisticsVO;
import com.mds.group.purchase.user.model.Consignee;
import com.mds.group.purchase.user.model.GroupLeader;
import com.mds.group.purchase.user.model.Wxuser;
import com.mds.group.purchase.user.service.ConsigneeService;
import com.mds.group.purchase.user.service.GroupBpavawiceOrderService;
import com.mds.group.purchase.user.service.GroupLeaderService;
import com.mds.group.purchase.user.service.WxuserService;
import com.mds.group.purchase.user.vo.UserInfoVO;
import com.mds.group.purchase.utils.IdGenerateUtils;
import com.mds.group.purchase.utils.OrderUtil;
import com.mds.group.purchase.utils.SolitaireUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Condition;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

/**
 * The type Order service.
 *
 * @author shuke
 * @date 2018 /11/28
 */
@Service
public class OrderServiceImpl extends AbstractService<Order> implements OrderService {

    @Resource
    private OrderMapper tOrderMapper;
    @Resource
    private GoodsService goodsService;
    @Resource
    private WxuserService wxuserService;
    @Resource
    private OrderUtil orderUtil;
    @Resource
    private WxServiceUtils wxServiceUtils;
    @Resource
    private SendBillService sendBillService;
    @Resource
    private SolitaireUtil solitaireUtil;
    @Resource
    private ConsigneeService consigneeService;
    @Resource
    private LineDetailService lineDetailService;
    @Resource
    private GoodsDetailService goodsDetailService;
    @Resource
    private OrderDetailService orderDetailService;
    @Resource
    private GroupLeaderService groupLeaderService;
    @Resource
    private ActivityGoodsService activityGoodsService;
    @Resource
    private ActiveDelaySendJobHandler activeDelaySendJobHandler;
    @Resource
    private GroupBrokerageService groupBrokerageService;
    @Resource
    private OrderSendBillMappingService orderSendBillMappingService;
    @Resource
    private GroupBpavawiceOrderService groupBpavawiceOrderService;
    @Resource
    private AfterSaleOrderMapper afterSaleOrderMapper;
    @Resource
    private AfterSaleOrderService afterSaleOrderService;
    @Resource
    private ReceiptBillDetailService receiptBillDetailService;
    @Resource
    private NegotiateHistoryService negotiateHistoryService;
    @Resource
    private ReceiptBillService receiptBillService;

    private Logger log = LoggerFactory.getLogger(OrderServiceImpl.class);

    @Override
    public List<Order> findByOrder(Order order) {
        return tOrderMapper.select(order);
    }

    @Override
    public OrderResult getById(Long orderId) {

        OrderResult orderResult = tOrderMapper.selectById(orderId);
        AfterSaleOrder afterSaleOrder =
                afterSaleOrderMapper.selectUserApplyOrderByOriginalOrderId(orderResult.getOrderId());
        if (afterSaleOrder != null) {
            orderResult.setAfterSaleType(afterSaleOrder.getAfterSaleType());
            orderResult.setAfterSaleNo(afterSaleOrder.getAfterSaleNo());
            orderResult.setAfterSaleStatus(afterSaleOrder.getAfterSaleStatus());
            orderResult.setAfterSaleOrderId(afterSaleOrder.getAfterSaleOrderId());
        }
        if (orderResult.getOrderType().equals(OrderConstant.EXCHANGE_ORDER)) {
            afterSaleOrder = afterSaleOrderMapper.findByNewOrderId(orderResult.getOrderId());
            orderResult.setAfterSaleNo(afterSaleOrder.getAfterSaleNo());
            orderResult.setAfterSaleStatus(afterSaleOrder.getAfterSaleStatus());
            orderResult.setAfterSaleType(afterSaleOrder.getAfterSaleType());
            orderResult.setAfterSaleOrderId(afterSaleOrder.getAfterSaleOrderId());
        }
        return orderResult;
    }

    @Override
    public List<OrderResult> getAll(String appmodelId) {
        return tOrderMapper.selectAllOrder(appmodelId);
    }

    @Override
    public List<GroupOrderDTO> getGroupOrder(String groupLeaderId, Integer searchType, String appmodelId,
                                             Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        return tOrderMapper.selectGroupOrder(groupLeaderId, searchType, appmodelId);
    }

    @Override
    public List<WechatOrderResult> getByConditions(OrderVo orderVo) {
        return tOrderMapper.selectByConditions(orderVo);
    }

    @Override
    public List<WechatOrderResult> getUserOrderList(OrderVo orderVo) {
        return tOrderMapper.selectByConditions(orderVo);
    }

    @Override
    public List<WechatOrderResult> getUserOrderListV2(OrderVoV2 orderVo) {
        List<WechatOrderResult> wechatOrderResultList = tOrderMapper.selectByConditionsV2(orderVo);
        for (WechatOrderResult wechatOrderResult : wechatOrderResultList) {
            if (wechatOrderResult.getOrderType().equals(OrderConstant.EXCHANGE_ORDER)) {
                AfterSaleOrder afterSaleOrder;
                if (!OrderConstant.isEnd(wechatOrderResult.getPayStatus())) {
                    afterSaleOrder =
                            afterSaleOrderMapper.selectUserApplyOrderByExchangeOrderId(wechatOrderResult.getOrderId());
                } else {
                    afterSaleOrder = afterSaleOrderMapper.findByNewOrderId(wechatOrderResult.getOrderId());
                }
                if (afterSaleOrder != null) {
                    wechatOrderResult.setInitialOrderId(afterSaleOrder.getOriginalOrderId());
                    wechatOrderResult.setAfterSaleNo(afterSaleOrder.getAfterSaleNo());
                    wechatOrderResult.setAfterSaleStatus(afterSaleOrder.getAfterSaleStatus());
                    wechatOrderResult.setAfterSaleType(afterSaleOrder.getAfterSaleType());
                    wechatOrderResult.setAfterSaleOrderId(afterSaleOrder.getAfterSaleOrderId());
                }
                AfterSaleOrder originalOrder =
                        afterSaleOrderMapper.selectUserApplyOrderByOriginalOrderId(wechatOrderResult.getOrderId());
                if (originalOrder != null) {
                    wechatOrderResult.setOriginalOrderId(originalOrder.getOriginalOrderId());
                }
            } else {
                AfterSaleOrder afterSaleOrder =
                        afterSaleOrderMapper.selectUserApplyOrderByOriginalOrderId(wechatOrderResult.getOrderId());
                if (afterSaleOrder != null) {
                    wechatOrderResult.setAfterSaleNo(afterSaleOrder.getAfterSaleNo());
                    wechatOrderResult.setAfterSaleStatus(afterSaleOrder.getAfterSaleStatus());
                    wechatOrderResult.setAfterSaleType(afterSaleOrder.getAfterSaleType());
                    wechatOrderResult.setAfterSaleOrderId(afterSaleOrder.getAfterSaleOrderId());
                    wechatOrderResult.setOriginalOrderId(afterSaleOrder.getOriginalOrderId());
                }
            }

        }
        return wechatOrderResultList;
    }

    @Override
    public List<MembersResult> findMembersByIds(List<Long> orderIds) {
        return tOrderMapper.selectMembersByIds(orderIds);
    }

    @Override
    public List<WechatOrderResult> fuzzySearch(SearchOrderVo searchOrderVo, String appmodelId) {
        List<WechatOrderResult> list;
        if (searchOrderVo.getLineId() != null || searchOrderVo.getStreetId() != null
                || searchOrderVo.getActivityId() != null) {
            OrderVo orderVo = new OrderVo();
            orderVo.setOrderStatus(searchOrderVo.getOrderStatus());
            orderVo.setActivityId(searchOrderVo.getActivityId());
            orderVo.setStartTime(searchOrderVo.getCreateOrderTimeStart());
            orderVo.setEndTime(searchOrderVo.getCreateOrderTimeEnd());
            orderVo.setLineId(searchOrderVo.getLineId());
            orderVo.setStreetId(searchOrderVo.getStreetId());
            list = this.getByConditions(orderVo);
        } else {
            list = tOrderMapper.searchByConditions(searchOrderVo, appmodelId);
        }
        return list;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateBatch(OrderUpdateVo orderUpdateVo, HttpServletResponse response) {
        List<Long> orderIdList = Arrays.stream(orderUpdateVo.getOrderIds().split(",")).map(Long::parseLong)
                .collect(Collectors.toList());
        switch (orderUpdateVo.getUpdateStatus()) {
            case 1:
            case 2:
            case 4:
                //验证订单是否是老订单
                List<OrderResult> orderResults = tOrderMapper.selectByIdList(orderIdList);
                List<OrderResult> collect = orderResults.stream()
                        .filter(o -> o.getPayTime() != null && o.getPayTime().getTime() < DateUtil
                                .parse("2019-03-12 21:10:00").getTime()).collect(Collectors.toList());
                List<Long> collect1 = collect.stream().map(OrderResult::getOrderId).collect(Collectors.toList());
                if (!collect.isEmpty()) {
                    tOrderMapper.updateBatch(collect1, orderUpdateVo.getShopDesc(), orderUpdateVo.getUpdateStatus());
                }
                if (orderUpdateVo.getUpdateStatus() == 1) {
                    //发货更新发货时间
                    orderDetailService.updateSendTime(orderIdList, DateUtil.date());
                }
                break;
            case 3:
                //如果商家后台关闭订单,需要把库存归还回去
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("orderIds", orderIdList.stream().map(Object::toString).collect(Collectors.joining(",")));
                jsonObject.put("closeType", OrderConstant.ORDER_CLOSE_BY_SHOP);
                activeDelaySendJobHandler.savaTask(jsonObject.toJSONString(), ActiviMqQueueName.ORDER_CLOSE, 0L,
                        orderUpdateVo.getAppmodelId(), Boolean.FALSE);
                break;
            case 5:
                this.exportOkOrder(orderIdList, response);
                break;
            default:
                break;
        }
    }

    private void exportOkOrder(List<Long> orderIdList, HttpServletResponse response) {
        List<WechatOrderResult> list = tOrderMapper.selectExportOrder(orderIdList);
        if (CollectionUtil.isEmpty(list)) {
            throw new ServiceException("数据不存在");
        }
        // 通过工具类创建writer
        ExcelWriter writer = ExcelUtil.getBigWriter();
        //自定义标题别名
        //创建头部
        List<String> row1 = CollUtil
                .newArrayList("订单号", "创建时间", "线路分拣", "商品名称", "单价", "数量", "取货点", "客户", "地址", "实付款", "备注");
        //设计头部样式
        Font font = writer.createFont();
        font.setColor(IndexedColors.BLACK.getIndex());
        font.setFontName("华文细黑");
        font.setFontHeightInPoints((short) 10);
        font.setBold(true);
        writer.getHeadCellStyle().setFont(font);
        writer.getHeadCellStyle().setWrapText(true);
        writer.writeHeadRow(row1);
        try {
            int i = 1;
            for (WechatOrderResult wechatOrderResult : list) {
                Map<String, Object> map = new LinkedHashMap<>(8);
                map.put("订单号", wechatOrderResult.getOrderNo());
                map.put("创建时间", DateUtil.formatDateTime(wechatOrderResult.getCreateTime()).replace(" ", "  \r\n"));
                map.put("线路分拣", wechatOrderResult.getLineName().concat("  \r\n"));
                map.put("商品名称", wechatOrderResult.getGoodsName());
                map.put("单价", wechatOrderResult.getGoodsPrice());
                map.put("数量", wechatOrderResult.getGoodsNum());
                map.put("取货点", wechatOrderResult.getPickupLocation());
                map.put("客户", wechatOrderResult.getBuyerName());
                map.put("地址", wechatOrderResult.getBuyerAddress());
                BigDecimal price = wechatOrderResult.getGoodsPrice().subtract(wechatOrderResult.getPreferential())
                        .setScale(2, RoundingMode.HALF_UP);
                if (price.doubleValue() < 0.01) {
                    map.put("实付款", 0.01);
                } else {
                    map.put("实付款", price);
                }
                map.put("备注", wechatOrderResult.getUserDesc());
                writer.setColumnWidth(0, 17);
                writer.setColumnWidth(1, 15);
                writer.setColumnWidth(2, 15);
                writer.setColumnWidth(3, 41);
                writer.setColumnWidth(4, 6);
                writer.setColumnWidth(5, 7);
                writer.setColumnWidth(6, 35);
                writer.setColumnWidth(7, 30);
                writer.setColumnWidth(8, 30);
                writer.setColumnWidth(9, 7);
                writer.setColumnWidth(10, 7);
                writer.writeRow(map, false);
                writer.setRowHeight(i, 40);
                i++;
            }
            response.setContentType("application/vnd.ms-excel;charset=utf-8");
            String fileName = "已完成订单.xlsx";
            response.setHeader("Content-Disposition",
                    "attachment;filename=" + new String(fileName.getBytes("GBK"), "iso8859-1"));
            writer.flush(response.getOutputStream());
            response.getOutputStream().close();
        } catch (Exception e) {
            e.printStackTrace();
            // 关闭writer，释放内存
            throw new ServiceException("文件下载失败");
        } finally {
            writer.close();
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createExchange(Order order, Integer goodsNum, Boolean isGroupLeader) {
        Order newOrder = new Order();
        GroupLeader groupLeader = groupLeaderService.findById(order.getGroupId());
        BeanUtil.copyProperties(order, newOrder, "orderId", "confirmTime", "afterSaleOrderId");
        newOrder.setOrderType(OrderConstant.EXCHANGE_ORDER);
        newOrder.setCreateTime(new Date());
        newOrder.setPayStatus(OrderConstant.PAYED);
        newOrder.setPayTime(new Date());
        newOrder.setUserAfterSaleNum(0);
        newOrder.setDeleteFlag(Boolean.FALSE);
        newOrder.setOrderNo(IdGenerateUtils.getOrderNum());
        newOrder.setPayFee(BigDecimal.ZERO);
        if (isGroupLeader) {
            newOrder.setWxuserId(groupLeader.getWxuserId());
            newOrder.setBuyerName(groupLeader.getGroupName());
            newOrder.setBuyerAddress(order.getPickupLocation());
            newOrder.setBuyerPhone(groupLeader.getGroupPhone());
        }
        tOrderMapper.insert(newOrder);
        BigDecimal totalFee = BigDecimal.ZERO;
        List<OrderDetail> ods = orderDetailService.findByOrderIds(Arrays.asList(order.getOrderId()));
        for (OrderDetail od : ods) {
            OrderDetail newOd = new OrderDetail();
            BeanUtil.copyProperties(od, newOd, "groupLeaderCommission", "orderDetailId", "recordTime", "sendTime");
            newOd.setOrderId(newOrder.getOrderId());
            newOd.setGoodsNum(goodsNum);
            newOd.setGroupLeaderCommission(BigDecimal.ZERO);
            if (isGroupLeader) {
                newOd.setWxuserId(groupLeader.getWxuserId());
            }
            newOd.setOrderType(OrderConstant.EXCHANGE_ORDER);
            orderDetailService.save(newOd);
            totalFee = totalFee.add(newOd.getGoodsPrice().multiply(BigDecimal.valueOf(newOd.getGoodsNum())));
        }
        newOrder.setTotalFee(totalFee);
        newOrder.setDiscountsFee(totalFee);
        this.update(newOrder);
        //直接添加发货单
        OrderSendBillMapping osbm = orderSendBillMappingService.findByOrderId(newOrder.getOrderId());
        if (osbm == null) {
            osbm = new OrderSendBillMapping();
            osbm.setAppmodelId(newOrder.getAppmodelId());
            osbm.setDelFlag(Common.DEL_FLAG_FALSE);
            osbm.setGenerate(SendBillConstant.GENERATE_FALSE);
            osbm.setOrderId(newOrder.getOrderId());
            orderSendBillMappingService.save(osbm);
        }
        return newOrder.getOrderId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @SuppressWarnings("unchecked")
    public Long saveOrder(OrderSaveVo orderSaveVo) {
        ActivityGoods actGoods = activityGoodsService.findById(orderSaveVo.getActGoodsId());
        Goods goods = goodsService.findById(actGoods.getGoodsId());
        GoodsDetail gd = goodsDetailService.getGoodsDetailByGoodsId(goods.getGoodsId());
        // orderDetail
        OrderDetail od = orderSaveVo.vo2OrderDetail();
        //order信息
        Order order = orderSaveVo.vo2Order();
        od.setGoodsName(goods.getGoodsName());
        GoodsResult goodsResult = new GoodsResult();
        goodsResult.setGoodsDetail(gd);
        goodsResult.setAppmodelId(orderSaveVo.getAppmodelId());
        goodsResult.setGoodsStatus(goods.getGoodsStatus());
        goodsResult.setGoodsId(goods.getGoodsId());
        goodsResult.setGoodsDelFlag(goods.getGoodsDelFlag());
        goodsResult.setGoodsName(goods.getGoodsName());
        goodsResult.setGoodsTitle(goods.getGoodsTitle());
        goodsResult.setPrice(goods.getPrice());
        goodsResult.setCreateTime(goods.getCreateTime());
        int index = goods.getGoodsImg().indexOf(",");
        if (index == -1) {
            goodsResult.setGoodsImg(goods.getGoodsImg());
            od.setGoodsImg(goods.getGoodsImg());
        } else {
            goodsResult.setGoodsImg(goods.getGoodsImg().substring(0, index));
            od.setGoodsImg(goods.getGoodsImg().substring(0, index));
        }
        String goodsdetail = JSON.toJSONString(goodsResult);
        od.setGoodsDetail(goodsdetail);
        od.setGoodsPrice(goods.getPrice());
        //兼容v1.2版本之前的商品价格
        BigDecimal groupLeaderCommission;
        BigDecimal payfee;
        if (actGoods.getActivityDiscount() != null && actGoods.getActivityDiscount() != 0) {
            //这是v1.2之前的处理逻辑
            //计算单个商品的折扣价
            BigDecimal activityDiscount = NumberUtil.div(actGoods.getActivityDiscount(), Integer.valueOf(10))
                    .setScale(2, RoundingMode.HALF_UP);
            BigDecimal discount = NumberUtil.mul(goods.getPrice(), activityDiscount).setScale(2, RoundingMode.HALF_UP);
            //计算商品优惠了多少钱
            od.setPreferential(NumberUtil.sub(goods.getPrice(), discount));
            BigDecimal discountsFee = NumberUtil.sub(od.getGoodsPrice(), od.getPreferential())
                    .multiply(BigDecimal.valueOf(od.getGoodsNum())).setScale(2, RoundingMode.HALF_UP);
            if (gd.getCommissionType().equals(1)) {
                //计算团长佣金
                groupLeaderCommission = gd.getGroupLeaderCommission()
                        .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP).multiply(discountsFee);
            } else {
                groupLeaderCommission = gd.getGroupLeaderCommission().multiply(BigDecimal.valueOf(od.getGoodsNum()));
            }
            order.setDiscountsFee(discountsFee);
            if (discountsFee.doubleValue() < OrderConstant.MIN_PAYFEE) {
                payfee = BigDecimal.valueOf(OrderConstant.MIN_PAYFEE);
            } else {
                payfee = discountsFee;
            }
        } else {
            //这是v1.2版本的处理逻辑
            //计算团长佣金
            if (gd.getCommissionType().equals(1)) {
                groupLeaderCommission = gd.getGroupLeaderCommission()
                        .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP).multiply(actGoods.getActivityPrice())
                        .multiply(BigDecimal.valueOf(orderSaveVo.getGoodsNum()));
            } else {
                groupLeaderCommission = gd.getGroupLeaderCommission()
                        .multiply(BigDecimal.valueOf(orderSaveVo.getGoodsNum()));
            }
            payfee = actGoods.getActivityPrice().multiply(BigDecimal.valueOf(orderSaveVo.getGoodsNum()));
        }
        order.setPayFee(payfee);
        od.setGoodsId(goods.getGoodsId());
        od.setGroupLeaderCommission(groupLeaderCommission.setScale(2, RoundingMode.HALF_UP));
        od.setOrderDetailNo(IdGenerateUtils.getOrderNum());
        //设置订单是否为接龙订单
        od.setIsSolitaireOrder(actGoods.getJoinSolitaire());
        od.setActivityId(activityGoodsService.findById(orderSaveVo.getActGoodsId()).getActivityId());
        Consignee consignee = consigneeService.findById(orderSaveVo.getConsigneeId());
        order.setBuyerName(consignee.getUserName());
        if (!StringUtils.isNotBlank(consignee.getArea())) {
            throw new ServiceException("提货人详情地址不能为空");
        }
        order.setBuyerAddress(consignee.getAddress().replace(" ", "").concat(consignee.getArea()));
        order.setBuyerPhone(consignee.getPhone());
        order.setCreateTime(new Date());
        UserInfoVO userInfo = wxuserService.getUserInfo(orderSaveVo.getWxuserId(), orderSaveVo.getAppmodelId());
        GroupLeader groupLeader = groupLeaderService.findBySoleGroupLeader(userInfo.getCommunityId());
        if (groupLeader == null) {
            throw new ServiceException("团长被禁用");
        }
        order.setGroupLeaderName(groupLeader.getGroupName());
        order.setGroupLeaderPhone(groupLeader.getGroupPhone());
        order.setPickupLocation(groupLeader.getAddress());
        Wxuser groupUser = wxuserService.findByGroupleaderId(groupLeader.getGroupLeaderId());
        order.setGroupLeaderIcon(groupUser.getIcon());
        order.setGroupId(groupLeader.getGroupLeaderId());
        order.setDeleteFlag(false);
        order.setPayStatus(OrderConstant.WAIT4PAY);
        order.setOrderNo(IdGenerateUtils.getOrderNum());
        order.setUserAfterSaleNum(0);
        order.setAfterSaleOrderId(null);
        BigDecimal totalFee = od.getGoodsPrice().multiply(BigDecimal.valueOf(od.getGoodsNum()));
        order.setTotalFee(totalFee);
        tOrderMapper.insert(order);
        //更新订单详情的orderId
        od.setOrderId(order.getOrderId());
        // 线路id
        LineDetail lineDetail = lineDetailService.getLineDetailByUserId(orderSaveVo.getWxuserId());
        if (lineDetail == null) {
            throw new ServiceException("您所在的小区暂时不支持配送");
        }
        Long lineId = lineDetail.getLineId();
        od.setLineId(lineId);
        od.setCommunityId(userInfo.getCommunityId());
        // 区域id
        Long streetId = lineDetail.getStreetId();
        od.setStreetId(streetId);
        orderDetailService.save(od);
        //订单30分钟自动关闭
        //如果商家后台关闭订单,需要把库存归还回去
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("orderIds", order.getOrderId().toString());
        jsonObject.put("closeType", OrderConstant.ORDER_CLOSE);
        Long time;
        if (orderSaveVo.getActivityType().equals(1)) {
            time = TimeType.HALFHOUR;
        } else {
            time = TimeType.ONEHOUR;
        }
        activeDelaySendJobHandler
                .savaTask(jsonObject.toJSONString(), ActiviMqQueueName.ORDER_CLOSE, time, order.getAppmodelId(),
                        Boolean.FALSE);
        return order.getOrderId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, String> pay(Long orderId, HttpServletRequest request, String ip) {
        Order order = tOrderMapper.selectByPrimaryKey(orderId);
        if (order == null) {
            // 订单不存在
            throw new ServiceException("订单不存在");
        }
        if (order.getPayFee().doubleValue() < OrderConstant.MIN_PAYFEE) {
            throw new ServiceException("支付金额有误");
        }
        if (order.getPayStatus() == OrderConstant.PAYED || order.getPayStatus() == OrderConstant.SENDED
                || order.getPayStatus() == OrderConstant.NOT_COMMENT
                || order.getPayStatus() == OrderConstant.ORDER_COMPLETE) {
            throw new ServiceException("订单已支付");
        }
        if (order.getPayStatus() == OrderConstant.ORDER_CLOSE) {
            throw new ServiceException("订单已关闭");
        }
        Wxuser wxuser = wxuserService.findById(order.getWxuserId());
        if (wxuser == null) {
            throw new ServiceException("用户信息错误，支付失败");
        }
        try {
            String payOrderNum = IdGenerateUtils.getOrderNum();
            log.info("ip:" + ip + "发起微信支付请求！支付单号:" + payOrderNum + ",订单id：" + order.getOrderId() + ",支付时间：" + DateUtil
                    .date() + "支付金额：" + order.getPayFee() + "openId" + wxuser.getMiniOpenId() + "用户：" + wxuser
                    .getWxuserName());
            Map<String, String> map = wxServiceUtils
                    .wxJsapiPayRequest("微信支付", order.getOrderNo(), payOrderNum, order.getPayFee().toString(),
                            wxuser.getMiniOpenId(), GroupMallProperties.getOrderNotify(), order.getAppmodelId());
            Order newOrderNum = new Order();
            newOrderNum.setPayOrderId(payOrderNum);
            newOrderNum.setOrderId(order.getOrderId());
            newOrderNum.setPayChannel(OrderConstant.WX_PAY);
            if (tOrderMapper.updateByPrimaryKeySelective(newOrderNum) == 0) {
                throw new ServiceException("支付订单号未更新成功");
            }
            //保留发起支付成功的凭证
            log.info("ip:" + ip + "发起微信支付请求成功！支付单号:" + payOrderNum + ",订单id：" + order.getOrderId() + ",发起成功时间："
                    + DateUtil.date() + "支付金额：" + order.getPayFee() + "openId" + wxuser.getMiniOpenId() + "用户：" + wxuser
                    .getWxuserName());
            //异步生成接龙记录 （v1.2.3新功能）
            solitaireUtil.ascyGenerateSolitaireRecord(order.getOrderNo(), order.getAppmodelId());
            return map;
        } catch (Exception e) {
            e.printStackTrace();
            log.error("ip:" + ip + "发起微信支付请求失败");
            log.error("错误内容: ");
            log.error(e.getClass().toString());
            log.error(e.getMessage());
            throw new ServiceException(e.getMessage());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String notify(WxPayOrderNotifyResult payOrderNotifyResult, String version) {
        // 1、更新商品库存(包含商品规格的库存，活动商品的库存)
        log.info("订单回调XmlResult --------->" + payOrderNotifyResult.toString());
        if (WxPayConstants.ResultCode.SUCCESS.equalsIgnoreCase(payOrderNotifyResult.getResultCode())) {
            Order order = new Order();
            order.setPayOrderId(payOrderNotifyResult.getOutTradeNo());
            order = tOrderMapper.selectOne(order);
            if (order == null) {
                //防止支付时生成的payId不一致查询不到,用不变的orderId查询一次
                Order order2 = new Order();
                order2.setOrderNo(payOrderNotifyResult.getAttach());
                order2 = tOrderMapper.selectOne(order2);
                if (order2 == null) {
                    log.error("支付订单号不存在:".concat(payOrderNotifyResult.getOutTradeNo()));
                    return WxPayNotifyResponse.fail("订单回调处理失败");
                }
                order = order2;
            }
            if (order.getPayStatus().equals(OrderConstant.WAIT4PAY)) {
                log.info("开始处理订单回调: " + payOrderNotifyResult.getOutTradeNo() + " !!!");
                String result = orderUtil.paySuccess(order);
                if (WxPayConstants.ResultCode.SUCCESS.equals(result)) {
                    return this.orderNotifySuccessHandleByVersion(version, order, payOrderNotifyResult);
                } else {
                    return WxPayNotifyResponse.fail("订单回调处理失败");
                }
            } else {
                return WxPayNotifyResponse.success("已处理");
            }
        } else if (WxPayConstants.ResultCode.FAIL.equalsIgnoreCase(payOrderNotifyResult.getResultCode())) {
            log.info("订单号：" + payOrderNotifyResult.getAttach() + "调用支付失败！支付单号：" + payOrderNotifyResult.getOutTradeNo()
                    + "openId:" + payOrderNotifyResult.getOpenid() + "错误代码：" + payOrderNotifyResult.getErrCode()
                    + "错误描述：" + payOrderNotifyResult.getErrCodeDes());
            return WxPayNotifyResponse.fail("订单回调处理失败");
        } else {
            return WxPayNotifyResponse.fail("订单回调处理失败");
        }
    }

    private String orderNotifySuccessHandleByVersion(String version, Order order,
                                                     WxPayOrderNotifyResult payOrderNotifyResult) {
        switch (version) {
            case Version.V_1:
                return WxPayNotifyResponse.success("处理成功");
            case Version.V_1_2:
                //支付成功后会异步生成对应该订单的订单发货单关系映射数据
                String orderJsonData = JSON.toJSONString(order);
                activeDelaySendJobHandler
                        .savaTask(orderJsonData, ActiviMqQueueName.GENERATE_ORDER_SENDBILL_MAPPING,
                                0L, order.getAppmodelId(), false);
                log.info(
                        "订单支付回调处理成功: " + payOrderNotifyResult.getOutTradeNo() + " !!!orderNo" + payOrderNotifyResult
                                .getAttach() + "支付金额：" + payOrderNotifyResult.getTotalFee());
                return WxPayNotifyResponse.success("处理成功");
            default:
                return WxPayNotifyResponse.fail("订单回调处理失败");
        }
    }

    @Override
    public int updateGroupPostil(List<Long> orderIds, String postilText) {
        Condition condition = new Condition(Order.class);
        condition.createCriteria().andIn("orderId", orderIds);
        Order order = new Order();
        order.setPostil(postilText);
        return tOrderMapper.updateByConditionSelective(order, condition);
    }

    @Override
    public void cancelOrder(Long orderId, String appmodelId) {
        //验证订单是否存在，且订单状态为未支付状态
        OrderResult orderResult = tOrderMapper.selectById(orderId);
        if (orderResult == null) {
            throw new ServiceException("订单不存在");
        } else if (orderResult.getPayStatus() != OrderConstant.WAIT4PAY) {
            throw new ServiceException("订单状态错误");
        }
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("orderIds", orderResult.getOrderId().toString());
        jsonObject.put("closeType", OrderConstant.ORDER_CLOSE_BY_USER);
        activeDelaySendJobHandler
                .savaTask(jsonObject.toJSONString(), ActiviMqQueueName.ORDER_CLOSE, 0L, appmodelId, Boolean.FALSE);
        try {
            Thread.sleep(500L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void cancelOrder(String orderNo, Long wxuserId, String appmodelId) {
        //验证订单是否存在，且订单状态为未支付状态
        Order condition = new Order();
        condition.setOrderNo(orderNo);
        condition.setWxuserId(wxuserId);
        condition.setAppmodelId(appmodelId);
        Order order = tOrderMapper.selectOne(condition);
        OrderDetail detail = orderDetailService.findBy("orderNo", orderNo);
        if (order == null || detail == null) {
            throw new ServiceException("订单不存在");
        } else if (order.getPayStatus() != OrderConstant.WAIT4PAY) {
            throw new ServiceException("订单状态错误");
        }
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("orderIds", order.getOrderId().toString());
        jsonObject.put("closeType", OrderConstant.ORDER_CLOSE_BY_USER);
        activeDelaySendJobHandler
                .savaTask(jsonObject.toJSONString(), ActiviMqQueueName.ORDER_CLOSE, 0L, appmodelId, Boolean.FALSE);
        try {
            Thread.sleep(500L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void confirmReceipt(Long orderId, Boolean isCallback) {
        Order order = findById(orderId);
        if (order == null) {
            throw new ServiceException("用户id和订单不匹配或者订单不存在");
        } else if (order.getPayStatus() != OrderConstant.GROUP_LEADER_RECEIPT && !isCallback) {
            throw new ServiceException("订单状态错误");
        }
        if (order.getOrderType() != null) {
            receiptBillService.checkAfterSaleOrderStatus(order);
            if (order.getOrderType() == OrderConstant.EXCHANGE_ORDER) {
                order.setPayStatus(OrderConstant.ORDER_COMPLETE);
            } else {
                order.setPayStatus(OrderConstant.NOT_COMMENT);
            }
            tOrderMapper.updateByPrimaryKey(order);
        } else {
            order.setPayStatus(OrderConstant.NOT_COMMENT);
            tOrderMapper.updateByPrimaryKey(order);
        }
        OrderDetail orderDetail = orderDetailService.findBy("orderId", order.getOrderId());
        orderDetail.setRecordTime(DateUtil.date());
        orderDetailService.update(orderDetail);

        //换货单签收之后，自动签收原订单 并修改售后单状态为成功
        if (order.getOrderType() != null && order.getOrderType().equals(OrderConstant.EXCHANGE_ORDER)) {
            AfterSaleOrder afterSaleOrder = afterSaleOrderMapper.findByNewOrderId(order.getOrderId());
            //更改售后单状态
            afterSaleOrder.setAfterSaleStatus(AfterSaleOrderStatus.成功.getValue());
            afterSaleOrder.setSuccessTime(new Date());
            afterSaleOrderService.update(afterSaleOrder);
            List<Order> orders = tOrderMapper.selectOrderByAfterSaleOrderId(afterSaleOrder.getAfterSaleOrderId());
            if (orders.stream().findFirst().isPresent()) {
                negotiateHistoryService.exchangeReceipt(orders.get(0), afterSaleOrder);
            }
            Set<Long> originalOrderIds =
                    Arrays.stream(afterSaleOrder.getOriginalOrderId().split(",")).map(originalOrderId -> Long.parseLong(originalOrderId)).collect(Collectors.toSet());
            for (Long originalOrderId : originalOrderIds) {
                this.confirmReceipt(originalOrderId, true);
            }
        } else {
            //增加团长佣金
            GroupLeader groupLeader = groupLeaderService.findBy("groupLeaderId", order.getGroupId());
            if (groupLeader != null) {
                groupLeader.setBrokerage(groupLeader.getBrokerage().add(orderDetail.getGroupLeaderCommission())
                        .setScale(2, RoundingMode.HALF_UP));
                groupLeaderService.update(groupLeader);
            }
            //将团长佣金明细状态改为已结算
            groupBrokerageService.updateStatusByOrderId(orderId, GroupBrokerage.Constant.STATUS_ENTERED);
        }

        AfterSaleOrder afterSaleOrder = afterSaleOrderMapper.selectUserApplyOrderByOriginalOrderId(order.getOrderId());
        //更改售后单状态
        if (afterSaleOrder != null && afterSaleOrder.getAfterSaleStatus() != null && !AfterSaleOrderStatus.isEnd(afterSaleOrder.getAfterSaleStatus())) {
            afterSaleOrder.setAfterSaleStatus(AfterSaleOrderStatus.成功.getValue());
            afterSaleOrder.setSuccessTime(new Date());
            afterSaleOrderService.update(afterSaleOrder);
        }


        activeDelaySendJobHandler
                .savaTask(orderId.toString(), ActiviMqQueueName.SENDBILL_SUCCESS, 0L, order.getAppmodelId(), false);


    }

    @Override
    public List<HistoricalTransactionDataVO> findAweekVolumeOfBusinessData(String appmodelId, String currentDate,
                                                                           String lastWeek) {
        List<HistoricalTransactionDataVO> historicalTransactionDataVOS = tOrderMapper
                .selectAweekVolumeOfBusinessData(appmodelId, currentDate, lastWeek);
        //获取相差天数
        long daySum =
                DateUtil.betweenDay(DateUtil.parseDateTime(lastWeek), DateUtil.parseDateTime(currentDate), false) + 1;
        if (historicalTransactionDataVOS == null || historicalTransactionDataVOS.size() < daySum) {
            if (historicalTransactionDataVOS == null) {
                historicalTransactionDataVOS = new LinkedList<>();
            }
            //如果商品指定时间内都没有数据,则全部生成0
            if (historicalTransactionDataVOS.size() == 0) {
                for (long i = 0; i < daySum; i++) {
                    HistoricalTransactionDataVO dataVO = new HistoricalTransactionDataVO();
                    dataVO.setVolumeOfBusiness(new BigDecimal(0.0));
                    dataVO.setVolumeOfBusinessNumber(0);
                    DateTime payTime = DateUtil.offsetDay(DateUtil.parseDateTime(currentDate), (int) (0 - i));
                    dataVO.setPayTime(payTime);
                    historicalTransactionDataVOS.add(dataVO);
                }
            } else {
                Map<Date, HistoricalTransactionDataVO> dataVOMap = historicalTransactionDataVOS.stream()
                        .collect(Collectors.toMap(HistoricalTransactionDataVO::getPayTime, v -> v));
                //生成指定天数内的日期
                for (long i = 0; i < daySum; i++) {
                    DateTime dateTime = DateUtil
                            .offsetDay(DateUtil.beginOfDay(DateUtil.parseDateTime(currentDate)), (int) (0 - i));
                    HistoricalTransactionDataVO dataVO = dataVOMap.get(dateTime);
                    if (dataVO == null) {
                        HistoricalTransactionDataVO data = new HistoricalTransactionDataVO();
                        data.setVolumeOfBusiness(new BigDecimal(0.0));
                        data.setVolumeOfBusinessNumber(0);
                        data.setPayTime(dateTime);
                        historicalTransactionDataVOS.add(data);
                    }
                }
                historicalTransactionDataVOS
                        .sort(Comparator.comparing(HistoricalTransactionDataVO::getPayTime).reversed());
            }
            return historicalTransactionDataVOS;
        } else {
            return historicalTransactionDataVOS;
        }
    }

    @Override
    public OrderDataVO findByAppmodelIdStatistics(String appmodelId) {
        List<StatisticsVO> salesVolumeDataVOS = tOrderMapper.selectByAppmodelIdStatistics(appmodelId);
        if (salesVolumeDataVOS.size() == 0) {
            return new OrderDataVO(0L, 0L, 0L, 0L);
        }
        OrderDataVO orderDataVO = new OrderDataVO(0L, 0L, 0L, 0L);
        //0.等待买家付款 1.买家已付款2.卖家已发货3.待评价4.交易成功
        for (StatisticsVO statisticsVO : salesVolumeDataVOS) {
            switch (statisticsVO.getPayStatu()) {
                case 0:
                    orderDataVO.setWaitPayOrder(orderDataVO.getWaitPayOrder() + statisticsVO.getPayStatusSum());
                    break;
                case 1:
                    orderDataVO.setWaitSendGoods(orderDataVO.getWaitSendGoods() + statisticsVO.getPayStatusSum());
                    break;
                case 2:
                    orderDataVO.setWaitPick(orderDataVO.getWaitPick() + statisticsVO.getPayStatusSum());
                    break;
                case 3:
                    orderDataVO.setPayOkOrder(orderDataVO.getPayOkOrder() + statisticsVO.getPayStatusSum());
                    break;
                case 4:
                    orderDataVO.setPayOkOrder(orderDataVO.getPayOkOrder() + statisticsVO.getPayStatusSum());
                    break;
                default:
                    break;
            }
        }
        return orderDataVO;
    }

    @Override
    public List<SalesVolumeVO> findByGroupleaderSale(String appmodelId) {
        List<SalesVolumeVO> salesVolumeVOS = new LinkedList<>();
        //团长销售量
        SalesVolumeVO salesVolumeVO = new SalesVolumeVO();
        List<SalesVolumeDataVO> salesVolumeDataVO = tOrderMapper.selectByBackSaleStatistics(appmodelId, 1, 1);
        salesVolumeVO.setSalesVolumeDataVOS(salesVolumeDataVO);
        BigDecimal groupLeaderSaleTotle = tOrderMapper.selectByBackSaleStatisticsTotle(appmodelId, 1, 0);
        salesVolumeVO.setTotle(groupLeaderSaleTotle);
        salesVolumeVOS.add(salesVolumeVO);

        //商品销售额
        SalesVolumeVO salesVolumeVO2 = new SalesVolumeVO();
        List<SalesVolumeDataVO> salesVolumeDataVO2 = tOrderMapper.selectByBackSaleStatistics(appmodelId, 2, 1);
        salesVolumeVO2.setSalesVolumeDataVOS(salesVolumeDataVO2);
        BigDecimal goodsSalaTotle = tOrderMapper.selectByBackSaleStatisticsTotle(appmodelId, 2, 0);
        salesVolumeVO2.setTotle(goodsSalaTotle);
        salesVolumeVOS.add(salesVolumeVO2);

        //会员消费统计
        SalesVolumeVO salesVolumeVO3 = new SalesVolumeVO();
        List<SalesVolumeDataVO> salesVolumeDataVO3 = tOrderMapper.selectByBackSaleStatistics(appmodelId, 3, 1);
        salesVolumeVO3.setSalesVolumeDataVOS(salesVolumeDataVO3);
        BigDecimal userSale = tOrderMapper.selectByBackSaleStatisticsTotle(appmodelId, 3, 0);
        salesVolumeVO3.setTotle(userSale);
        salesVolumeVOS.add(salesVolumeVO3);
        return salesVolumeVOS;
    }

    @Override
    public void deleteByWxuserId(Long wxuserId, String orderNo) {
        //删除order
        tOrderMapper.deleteOrderByOrderNoWxuserId(wxuserId, orderNo);
    }

    @Override
    public List<Order> findByWxuserIds(List<Long> wxuserId) {
        Condition condition = new Condition(Order.class);
        condition.createCriteria().andIn("wxuserId", wxuserId).andGreaterThanOrEqualTo("payStatus", 1)
                .andLessThanOrEqualTo("payStatus", 4);
        condition.orderBy("payTime").desc();
        return tOrderMapper.selectByCondition(condition);
    }


    @Override
    public void updateOrderStatus(Map<String, Object> map) {
        tOrderMapper.updateOrderStatus(map);
    }

    @Override
    public List<ActivityTurnoverDTO> findActivityTurnover(List<Long> activityId, String appmodelId) {
        return tOrderMapper.selectActivityTurnover(activityId, appmodelId);
    }

    @Override
    public MyOrderSumResult myOrderSum(Long wxuserId, String appmodelId) {
        MyOrderSumResult myOrderSumResult = new MyOrderSumResult();
        Order order = new Order();
        order.setWxuserId(wxuserId);
        order.setAppmodelId(appmodelId);
        order.setPayStatus(0);
        myOrderSumResult.setWaitPay(tOrderMapper.selectCount(order));
        order.setPayStatus(1);
        //已付款的订单数量
        int payedOrderNum = tOrderMapper.selectCount(order);
        order.setPayStatus(2);
        //已发货的订单数量
        int sendOrderNum = tOrderMapper.selectCount(order);
        myOrderSumResult.setWaitTakeDelivery(payedOrderNum + sendOrderNum);
        return myOrderSumResult;
    }

    @Override
    public List<OrderResult> findOrderResultByOrderIds(List<Long> orderIdList) {
        return tOrderMapper.selectByIdList(orderIdList);
    }

    @Override
    public List<PcOrderResult> fuzzySearchV2(SearchOrderVoV2 searchOrderVo, String appmodelId) {
        String startDate;
        String endDate;
        switch (searchOrderVo.getQuicklyDate()) {
            case 0:
                startDate = searchOrderVo.getCreateOrderTimeStart();
                endDate = searchOrderVo.getCreateOrderTimeEnd();
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
        List<PcOrderResult> pcOrderResults = tOrderMapper
                .selectByDateAndStatus(startDate, endDate, searchOrderVo, appmodelId);
        pcOrderResults.parallelStream().forEach(o -> {
            //2019年3月12日21时之前的订单状态判断n
            DateTime parse = DateUtil.parse("2019-03-12 21:10:00");
            if (o.getPayTime() != null && o.getPayTime().getTime() < parse.getTime()) {
                o.setOldOrder(true);
            } else {
                o.setOldOrder(false);
            }
            if (SendBillConstant.WAIT_SEND.equals(o.getSendBillStatus())) {
                o.setInSendBill(true);
            } else {
                o.setInSendBill(false);
            }
            if (o.getSendBillCreateDate() != null) {
                String substring = o.getSendBillCreateDate().substring(0, 16);
                o.setSendBillCreateDate(substring);
            }
            //将评价团长和评价商品的内容合并
            if (StringUtils.isNotBlank(o.getGroupComment())) {
                o.setGoodsComment(o.getGoodsComment() + "," + o.getGroupComment());
            }
        });
        return pcOrderResults;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeFromSendBill(String appmodelId, String orderIds) {
        //将订单id提取到list
        List<String> orderIdStrList = Arrays.asList(orderIds.split(","));
        List<Long> orderIdList = orderIdStrList.stream().map(Long::parseLong).collect(Collectors.toList());
        //根据orderIdList查询t_order_send_bill_Mapping,确定该orderId存在于发货单中且发货单状态为未发货状态,将无用orderId剔除
        List<OrderSendBillMapping> byOrderIds = orderSendBillMappingService.findByOrderIds(appmodelId, orderIdList);
        if (byOrderIds.isEmpty()) {
            return;
        }
        List<Long> orderIdList1 = byOrderIds.stream().map(OrderSendBillMapping::getOrderId).distinct()
                .collect(Collectors.toList());
        //查询出符合要求的orderId 对应的订单信息，
        List<OrderResult> orderResults = tOrderMapper.selectByIdList(orderIdList1);
        Map<Long, OrderResult> resultMap = orderResults.stream()
                .collect(Collectors.toMap(OrderResult::getOrderId, v -> v));
        Map<Long, List<OrderResult>> map = new HashMap<>(16);
        byOrderIds.forEach(o -> {
            Long orderId = o.getOrderId();
            Long sendBillId = o.getSendBillId();
            OrderResult orderResult = resultMap.get(orderId);
            List<OrderResult> list = map.get(sendBillId);
            if (list == null) {
                list = new ArrayList<>();
            }
            list.add(orderResult);
            map.put(sendBillId, list);
        });

        for (Map.Entry<Long, List<OrderResult>> next : map.entrySet()) {
            List<OrderResult> value = next.getValue();
            int orders = value.size();
            double amount = 0.0;
            double commission = 0.0;
            for (OrderResult orderResult : value) {
                amount += orderResult.getPayFee().doubleValue();
                commission += orderResult.getGroupLeaderCommission().doubleValue();
            }
            //将对应发货单的汇总信息更新
            sendBillService.updateInfo(next.getKey(), orders, amount, commission);
        }

        //将订单从发货单移除
        orderSendBillMappingService.removeFromSendBill(appmodelId, orderIdList1);
        byOrderIds.stream().map(OrderSendBillMapping::getSendBillId).collect(Collectors.toSet()).forEach(sendBillId -> {
            List<OrderSendBillMapping> orderSendBillMappings = orderSendBillMappingService
                    .findBySendBillId(sendBillId, appmodelId);
            if (orderSendBillMappings.isEmpty()) {
                sendBillService.removeById(sendBillId);
            }

            activeDelaySendJobHandler
                    .savaTask(sendBillId.toString(), ActiviMqQueueName.GOODS_SORTING_ORDER_CACHE, 0L, appmodelId,
                            true);
            activeDelaySendJobHandler
                    .savaTask(sendBillId.toString(), ActiviMqQueueName.RECEIPT_BILL_CACHE, 0L, appmodelId, true);
            activeDelaySendJobHandler
                    .savaTask(sendBillId.toString(), ActiviMqQueueName.LINE_SORTING_ORDER_CACHE, 0L, appmodelId, true);
        });

    }

    @Override
    public List<Order> findWaitPayOrderByIds(List<Long> stringList) {
        return tOrderMapper.selectWaitPayOrderByIds(stringList);
    }

    @Override
    public Map<String, OrderResult> findByOrderNos(List<String> orderNos) {
        if (orderNos.isEmpty()) {
            return new HashMap<>(2);
        }
        List<OrderResult> orderResults = tOrderMapper.selectByOrderNos(orderNos);
        return orderResults.stream().collect(Collectors.toMap(OrderResult::getOrderNo, v -> v));
    }


    @Override
    public void deductionCommission(Order order, BigDecimal refundFee) {
        OrderDetail orderDetail = orderDetailService.findBy("orderId", order.getOrderId());
        BigDecimal deductionCommission =
                order.getPayFee().divide(refundFee, 2, RoundingMode.HALF_UP).multiply(orderDetail.getGroupLeaderCommission());
        //已经确认收货的还需要从团长的佣金里面扣除
        if (order.getPayStatus() == OrderConstant.NOT_COMMENT) {
            //扣除团长佣金
            GroupLeader groupLeader = groupLeaderService.findBy("groupLeaderId", order.getGroupId());
            if (groupLeader != null) {
                groupLeader.setBrokerage(groupLeader.getBrokerage().subtract(deductionCommission.setScale(2,
                        RoundingMode.HALF_UP)));
                groupLeaderService.update(groupLeader);
            }
        }
        //更新订单详情中的团长佣金
        orderDetail.setGroupLeaderCommission(orderDetail.getGroupLeaderCommission().subtract(deductionCommission.setScale(2, RoundingMode.HALF_UP)));
        orderDetailService.update(orderDetail);
        //更新团长签收单中的团长佣金
        receiptBillDetailService.updateGroupLeaderCommissionByOrderDetailId(orderDetail.getOrderDetailId());
    }

    @Override
    public WorkbenchResult workbenchSummary(Long wxuserId) {
        WorkbenchResult workbenchResult = new WorkbenchResult();
        GroupLeader groupLeader = groupLeaderService.findByWxuserId(wxuserId);
        if (groupLeader == null) {
            throw new ServiceException(String.format("未能找到用户{}有效的团长身份", wxuserId));
        }
        DateTime today = DateUtil.date();
        Date startDate = DateUtil.beginOfDay(today);
        Date endDate = DateUtil.endOfDay(today);
        List<Order> orders = tOrderMapper.selectWorkbenchSummary(groupLeader.getGroupLeaderId(), startDate, endDate);
        //今日订单数
        workbenchResult.setTodayOrderNum(orders.size());
        //今日成交额
        workbenchResult.setTodayTurnover(orders.stream().map(Order::getPayFee).reduce(BigDecimal.ZERO,
                BigDecimal::add));
        BigDecimal todayCommission =
                orderDetailService.countCommissionByOrderId(orders.stream().map(Order::getOrderId).collect(Collectors.toSet()));
        //今日佣金
        workbenchResult.setTodayCommission(todayCommission);
        //累积提现
        workbenchResult.setCumulativeCashWithdrawal(groupBpavawiceOrderService.countCumulativeCashWithdrawal(groupLeader.getGroupLeaderId()));
        //我的客户数
        List<Wxuser> wxusers = wxuserService.findCustomersByGroupLeaderId(groupLeader.getGroupLeaderId(), null);
        workbenchResult.setCustomers(wxusers.size());
        List<OrderDetail> orderDetails =
                orderDetailService.findWait4SignByGroupLeaderId(groupLeader.getGroupLeaderId());
        //待结算
        BigDecimal settlement = orderDetailService.countSettlementCommission(groupLeader.getGroupLeaderId());
        if (settlement == null) {
            settlement = BigDecimal.ZERO;
        }
        //待提现
        BigDecimal brokerage = groupLeader.getBrokerage();
        workbenchResult.setCommission(settlement.add(brokerage).setScale(2, RoundingMode.HALF_UP));
        //待签收商品数量
        workbenchResult.setWaitReceipt(orderDetails.stream().mapToInt(OrderDetail::getGoodsNum).sum());
        //售后订单
        Integer afterSaleOrders =
                afterSaleOrderService.countNotEndByUserId(wxusers.stream().map(Wxuser::getWxuserId).collect(Collectors.toSet()));
        workbenchResult.setAfterOrder(afterSaleOrders);
        return workbenchResult;
    }

    @Override
    public List<Order> findWaitReceiptByOrderIds(String orderIds) {
        List<Long> idList = Arrays.stream(orderIds.split(",")).map(Long::parseLong).collect(Collectors.toList());
        return tOrderMapper.selectWaitReceiptByOrderIds(idList);
    }

    @Override
    public List<OrderResult> findByWxuserIdAndStatus(Long wxuserId, String groupLeaderId, Integer status) {
        return tOrderMapper.selectByWxuserIdAndStatus(wxuserId, groupLeaderId, status);
    }

    @Override
    public List<Order> findByGroupLeaderIdAndStatus(String groupLeaderId, Integer type) {
        return tOrderMapper.selectByGroupLeaderIdAndStatus(groupLeaderId, type);
    }

    @Override
    public void pickUp(Long orderId) {
        this.confirmReceipt(orderId, false);
    }

    @Override
    public List findGroupLeaderNotSettlementOrder(Long wxuserId) {
        GroupLeader groupLeader = groupLeaderService.findByWxuserId(wxuserId);
        if (groupLeader == null) {
            return Collections.EMPTY_LIST;
        }
        List<NotSettlementCommissionResult> list =
                tOrderMapper.selectGroupLeaderNotSettlementOrder(groupLeader.getGroupLeaderId());
        list.forEach(notSettlementCommissionResult -> notSettlementCommissionResult.setEstimatedTime());
        return list;
    }

    @Override
    public PageInfo<PcOrderResult> findPayedOrder(String groupLeaderId, String appmodelId, Integer page, Integer size) {
        List<PcOrderResult> orderResults = new ArrayList<>();
        //根据团长id查询订单id
        List<GroupBrokerage> brokerages = groupBrokerageService.findList(groupLeaderId, null, null);
        if (CollectionUtil.isNotEmpty(brokerages)) {
            List<Long> orderIds = brokerages.stream()
                    .filter(o -> o.getStatus().equals(GroupBrokerage.Constant.STATUS_WAIT))
                    .map(GroupBrokerage::getOrderId).collect(Collectors.toList());
            if (CollectionUtil.isNotEmpty(orderIds)) {
                PageHelper.startPage(page, size);
                PageHelper.orderBy("o.pay_time  desc");
                orderResults = tOrderMapper.selectByIdListPc(orderIds);
            }
        }
        return new PageInfo<>(orderResults);
    }

    @Override
    public List<OrderResult> findByIdList(List<Long> orderIds) {
        return tOrderMapper.selectByIdList(orderIds);
    }

    @Override
    public List<Order> findByWaitPay() {
        return tOrderMapper.findByWaitPay();
    }

}
