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
import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.binarywang.wxpay.bean.notify.WxPayNotifyResponse;
import com.github.binarywang.wxpay.bean.notify.WxPayOrderNotifyResult;
import com.github.binarywang.wxpay.constant.WxPayConstants;
import com.mds.group.purchase.activity.model.ActivityGoods;
import com.mds.group.purchase.activity.service.ActivityGoodsService;
import com.mds.group.purchase.configurer.GroupMallProperties;
import com.mds.group.purchase.configurer.WxServiceUtils;
import com.mds.group.purchase.constant.ActiviMqQueueName;
import com.mds.group.purchase.constant.Common;
import com.mds.group.purchase.constant.OrderConstant;
import com.mds.group.purchase.constant.TimeType;
import com.mds.group.purchase.exception.ServiceException;
import com.mds.group.purchase.goods.model.Goods;
import com.mds.group.purchase.goods.model.GoodsDetail;
import com.mds.group.purchase.goods.result.GoodsResult;
import com.mds.group.purchase.goods.service.GoodsDetailService;
import com.mds.group.purchase.goods.service.GoodsService;
import com.mds.group.purchase.jobhandler.ActiveDelaySendJobHandler;
import com.mds.group.purchase.logistics.model.LineDetail;
import com.mds.group.purchase.logistics.service.LineDetailService;
import com.mds.group.purchase.order.model.Order;
import com.mds.group.purchase.order.model.OrderDetail;
import com.mds.group.purchase.order.service.OrderDetailService;
import com.mds.group.purchase.order.service.OrderService;
import com.mds.group.purchase.order.vo.SolitaireOrderVo;
import com.mds.group.purchase.solitaire.service.SolitaireOrderService;
import com.mds.group.purchase.user.model.Consignee;
import com.mds.group.purchase.user.model.GroupLeader;
import com.mds.group.purchase.user.model.Wxuser;
import com.mds.group.purchase.user.service.ConsigneeService;
import com.mds.group.purchase.user.service.GroupLeaderService;
import com.mds.group.purchase.user.service.WxuserService;
import com.mds.group.purchase.user.vo.UserInfoVO;
import com.mds.group.purchase.utils.IdGenerateUtils;
import com.mds.group.purchase.utils.OrderUtil;
import com.mds.group.purchase.utils.SolitaireUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * The type Solitaire order service.
 *
 * @author pavawi
 */
@Service
public class SolitaireOrderServiceImpl implements SolitaireOrderService {

    @Resource
    private GoodsService goodsService;
    @Resource
    private OrderUtil orderUtil;
    @Resource
    private SolitaireUtil solitaireUtil;
    @Resource
    private GoodsDetailService goodsDetailService;
    @Resource
    private LineDetailService lineDetailService;
    @Resource
    private ConsigneeService consigneeService;
    @Resource
    private GroupLeaderService groupLeaderService;
    @Resource
    private OrderService orderService;
    @Resource
    private OrderDetailService orderDetailService;
    @Resource
    private WxuserService wxuserService;
    @Resource
    private WxServiceUtils wxServiceUtils;
    @Resource
    private ActivityGoodsService activityGoodsService;
    @Resource
    private ActiveDelaySendJobHandler activeDelaySendJobHandler;

    private Logger log = LoggerFactory.getLogger(SolitaireOrderServiceImpl.class);

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, String> h5Pay(String orderNo, HttpServletRequest request, String ip) {
        List<Order> orderList = orderService.findByList("orderNo", orderNo);
        if (CollectionUtil.isEmpty(orderList)) {
            // 订单不存在
            throw new ServiceException("订单不存在");
        }
        Wxuser wxuser = wxuserService.findById(orderList.get(0).getWxuserId());
        if (wxuser == null) {
            throw new ServiceException("用户信息错误，支付失败");
        }
        String appmodelId = wxuser.getAppmodelId();
        BigDecimal totalPayFee = BigDecimal.valueOf(0);
        for (Order order : orderList) {
            totalPayFee = totalPayFee.add(order.getPayFee());
            if (order.getPayStatus() == OrderConstant.PAYED || order.getPayStatus() == OrderConstant.SENDED
                    || order.getPayStatus() == OrderConstant.NOT_COMMENT
                    || order.getPayStatus() == OrderConstant.ORDER_COMPLETE) {
                throw new ServiceException("订单已支付");
            }
            if (order.getPayStatus() == OrderConstant.ORDER_CLOSE) {
                throw new ServiceException("订单已关闭");
            }
        }
        if (totalPayFee.doubleValue() < OrderConstant.MIN_PAYFEE) {
            throw new ServiceException("支付金额有误");
        }
        try {
            String payOrderNum = IdGenerateUtils.getOrderNum();
            log.info("ip:" + ip + "群接龙订单发起微信支付请求！支付单号:" + payOrderNum + ",订单编号：" + orderNo + ",支付时间：" + DateUtil.date()
                    + "支付金额：" + totalPayFee.doubleValue() + "openId" + wxuser.getMpOpenid() + "用户：" + wxuser
                    .getWxuserName());
            Map<String, String> map = wxServiceUtils
                    .wxJsapiPayRequestH5("微信支付", orderNo, payOrderNum, totalPayFee.toString(), wxuser.getMpOpenid(),
                            GroupMallProperties.getOrderNotifyH5(), appmodelId);
            for (Order order : orderList) {
                order.setPayOrderId(payOrderNum);
                order.setPayChannel(OrderConstant.WX_PAY);
                if (orderService.update(order) == 0) {
                    throw new ServiceException("支付订单号未更新成功");
                }
            }
            //保留发起支付成功的凭证
            log.info("ip:" + ip + "群接龙订单发起微信支付请求成功！支付单号:" + payOrderNum + ",订单编号：" + orderNo + ",发起成功时间：" + DateUtil
                    .date() + "支付金额：" + totalPayFee + "公众号openId" + wxuser.getMpOpenid() + "用户：" + wxuser.getWxuserName());
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
    public String saveSolitaireOrder(SolitaireOrderVo vo) {
        List<OrderDetail> details = vo.vo2OrderDetail();
        String orderNo = IdGenerateUtils.getOrderNum();

        List<ActivityGoods> activityGoodsList = activityGoodsService.findByIds(
                details.stream().map(o -> o.getActGoodsId().toString()).collect(Collectors.joining(Common.REGEX)));
        String goodsIds = activityGoodsList.stream().map(o -> o.getGoodsId().toString()).distinct()
                .collect(Collectors.joining(Common.REGEX));
        Map<Long, ActivityGoods> activityGoodsMap = activityGoodsList.stream()
                .collect(Collectors.toMap(ActivityGoods::getActivityGoodsId, v -> v));
        Map<Long, Goods> goodsMap = goodsService.findByIds(goodsIds).stream()
                .collect(Collectors.toMap(Goods::getGoodsId, v -> v));
        Map<Long, GoodsDetail> goodsDetailMap = goodsDetailService.findByGoodsIds(goodsIds).stream()
                .collect(Collectors.toMap(GoodsDetail::getGoodsId, v -> v));
        UserInfoVO userInfo = wxuserService.getUserInfo(vo.getWxuserId(), vo.getAppmodelId());
        details.forEach(od -> {
            ActivityGoods activityGoods = activityGoodsMap.get(od.getActGoodsId());
            Long goodsId = activityGoods.getGoodsId();
            Goods goods = goodsMap.get(goodsId);
            GoodsDetail goodsDetail = goodsDetailMap.get(goodsId);
            GoodsResult goodsResult = new GoodsResult();
            goodsResult.setGoodsDetail(goodsDetail);
            goodsResult.setAppmodelId(od.getAppmodelId());
            goodsResult.setGoodsStatus(goods.getGoodsStatus());
            goodsResult.setGoodsId(goods.getGoodsId());
            goodsResult.setGoodsDelFlag(goods.getGoodsDelFlag());
            goodsResult.setGoodsName(goods.getGoodsName());
            goodsResult.setGoodsTitle(goods.getGoodsTitle());
            goodsResult.setPrice(goods.getPrice());
            // 线路id
            LineDetail lineDetail = lineDetailService.getLineDetailByUserId(vo.getWxuserId());
            if (lineDetail == null) {
                throw new ServiceException("您所在的小区暂时不支持配送");
            }
            Long lineId = lineDetail.getLineId();
            od.setLineId(lineId);
            od.setCommunityId(userInfo.getCommunityId());
            // 区域id
            Long streetId = lineDetail.getStreetId();
            od.setStreetId(streetId);
            //设置订单为接龙订单
            od.setIsSolitaireOrder(true);
            goodsResult.setCreateTime(goods.getCreateTime());
            int index = goods.getGoodsImg().indexOf(",");
            od.setGoodsName(goods.getGoodsName());
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
            od.setGoodsId(goods.getGoodsId());
            BigDecimal groupLeaderCommission;
            //这是v1.2版本的处理逻辑
            //计算团长佣金
            if (goodsDetail.getCommissionType().equals(1)) {
                groupLeaderCommission = goodsDetail.getGroupLeaderCommission()
                        .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP)
                        .multiply(activityGoods.getActivityPrice()).multiply(BigDecimal.valueOf(od.getGoodsNum()));
            } else {
                groupLeaderCommission = goodsDetail.getGroupLeaderCommission()
                        .multiply(BigDecimal.valueOf(od.getGoodsNum()));
            }
            od.setGroupLeaderCommission(groupLeaderCommission.setScale(2, RoundingMode.HALF_UP));
            od.setOrderDetailNo(IdGenerateUtils.getOrderNum());
            od.setActivityId(activityGoods.getActivityId());
            od.setOrderType(activityGoods.getActivityType());
            BigDecimal payFee = activityGoods.getActivityPrice().multiply(BigDecimal.valueOf(od.getGoodsNum()));
            od.setPayFee(payFee);
            //order信息
            Order order = vo.vo2Order();
            order.setPayFee(payFee);
            Consignee consignee = consigneeService.findById(vo.getConsigneeId());
            order.setBuyerName(consignee.getUserName());
            if (!StringUtils.isNotBlank(consignee.getArea())) {
                throw new ServiceException("提货人详情地址不能为空");
            }
            order.setBuyerAddress(consignee.getAddress().replace(" ", "").concat(consignee.getArea()));
            order.setBuyerPhone(consignee.getPhone());
            order.setCreateTime(new Date());

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
            order.setOrderNo(orderNo);
            BigDecimal totalFee = goods.getPrice().multiply(BigDecimal.valueOf(od.getGoodsNum()));
            order.setTotalFee(totalFee);
            orderService.save(order);
            //更新订单详情的orderId
            od.setOrderId(order.getOrderId());
            orderDetailService.save(od);
            //订单30分钟自动关闭
            //如果商家后台关闭订单,需要把库存归还回去
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("orderIds", order.getOrderId().toString());
            jsonObject.put("closeType", OrderConstant.ORDER_CLOSE);
            Long time;
            if (activityGoods.getActivityType().equals(1)) {
                time = TimeType.HALFHOUR;
            } else {
                time = TimeType.SIX_HOURS;
            }
            activeDelaySendJobHandler
                    .savaTask(jsonObject.toJSONString(), ActiviMqQueueName.ORDER_CLOSE, time, order.getAppmodelId(),
                            Boolean.FALSE);
        });
        return orderNo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String notifyH5(WxPayOrderNotifyResult payOrderNotifyResult) {
        String appmodelId = null;
        Long wxuserId = null;
        // 1、更新商品库存(包含商品规格的库存，活动商品的库存)
        if (WxPayConstants.ResultCode.SUCCESS.equalsIgnoreCase(payOrderNotifyResult.getResultCode())) {
            List<Order> ordersByPayId = orderService.findByList("payOrderId", payOrderNotifyResult.getOutTradeNo());
            if (CollectionUtil.isEmpty(ordersByPayId)) {
                //防止支付时生成的payId不一致查询不到,用不变的orderNo查询一次
                List<Order> ordersByNo = orderService.findByList("orderNo", payOrderNotifyResult.getAttach());
                if (CollectionUtil.isEmpty(ordersByNo)) {
                    log.error("支付订单号不存在:".concat(payOrderNotifyResult.getOutTradeNo()));
                    return WxPayNotifyResponse.fail("订单回调处理失败");
                }
            }
            for (Order order : ordersByPayId) {
                log.info("订单状态" + order.getPayStatus());
                if (order.getPayStatus() > OrderConstant.WAIT4PAY) {
                    //订单状态不为未支付，则抛出异常并退款
                    log.error("订单回调处理已经处理: " + payOrderNotifyResult.getOutTradeNo() + " !!!orderNo" + payOrderNotifyResult
                            .getAttach() + "支付金额：" + payOrderNotifyResult.getTotalFee() + "->订单" + order.getOrderId()
                            + "订单回调处理已经处理完成");
                    return WxPayNotifyResponse.fail("订单回调处理已经处理");
                }
            }
            log.info("订单回调XmlResult --------->" + payOrderNotifyResult.toString());
            log.info("开始处理订单回调: " + payOrderNotifyResult.getOutTradeNo() + " !!!");
            for (Order order : ordersByPayId) {
                if (appmodelId == null) {
                    appmodelId = order.getAppmodelId();
                }
                if (wxuserId == null) {
                    wxuserId = order.getWxuserId();
                }
                String result = orderUtil.paySuccess(order);
                if (WxPayConstants.ResultCode.SUCCESS.equalsIgnoreCase(result)) {
                    //支付成功后会异步生成对应该订单的订单发货单关系映射数据
                    String orderJsonData = JSON.toJSONString(order);
                    log.info("------+++++++++" + orderJsonData);
                    activeDelaySendJobHandler
                            .savaTask(orderJsonData, ActiviMqQueueName.GENERATE_ORDER_SENDBILL_MAPPING,
                                    0L, appmodelId, false);
                }
            }
            //异步生成接龙记录 （v1.2.3新功能）
            solitaireUtil.ascyGenerateSolitaireRecord(payOrderNotifyResult.getAttach(), appmodelId);
            //异步增加购买过的人数统计
            solitaireUtil.ascyUpdatePayedUserNum(wxuserId, appmodelId);
            log.info("订单支付回调处理成功: " + payOrderNotifyResult.getOutTradeNo() + " !!!orderNo" + payOrderNotifyResult
                    .getAttach() + "支付金额：" + payOrderNotifyResult.getTotalFee());
            return WxPayNotifyResponse.success("处理成功");
        } else if (WxPayConstants.ResultCode.FAIL.equalsIgnoreCase(payOrderNotifyResult.getResultCode())) {
            log.info("订单号：" + payOrderNotifyResult.getAttach() + "调用支付失败！支付单号：" + payOrderNotifyResult.getOutTradeNo()
                    + "openId:" + payOrderNotifyResult.getOpenid() + "错误代码：" + payOrderNotifyResult.getErrCode()
                    + "错误描述：" + payOrderNotifyResult.getErrCodeDes());
        }
        return WxPayNotifyResponse.fail("订单回调处理失败");
    }
}
