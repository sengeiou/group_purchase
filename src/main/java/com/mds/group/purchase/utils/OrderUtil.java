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
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.RandomUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.binarywang.wxpay.bean.notify.WxPayOrderNotifyResult;
import com.github.binarywang.wxpay.constant.WxPayConstants;
import com.mds.group.purchase.configurer.GroupMallProperties;
import com.mds.group.purchase.configurer.WxServiceUtils;
import com.mds.group.purchase.constant.ActiviMqQueueName;
import com.mds.group.purchase.constant.OrderConstant;
import com.mds.group.purchase.constant.RedisPrefix;
import com.mds.group.purchase.constant.TemplateMsgType;
import com.mds.group.purchase.financial.service.FinancialDetailsService;
import com.mds.group.purchase.financial.service.GroupBrokerageService;
import com.mds.group.purchase.jobhandler.ActiveDelaySendJobHandler;
import com.mds.group.purchase.logistics.model.Community;
import com.mds.group.purchase.logistics.service.CommunityService;
import com.mds.group.purchase.order.model.AfterSaleOrder;
import com.mds.group.purchase.order.model.Order;
import com.mds.group.purchase.order.model.OrderDetail;
import com.mds.group.purchase.order.result.OrderResult;
import com.mds.group.purchase.order.service.OrderDetailService;
import com.mds.group.purchase.order.service.OrderService;
import com.mds.group.purchase.order.vo.PayOkNotify;
import com.mds.group.purchase.user.model.GroupLeader;
import com.mds.group.purchase.user.model.Wxuser;
import com.mds.group.purchase.user.service.GroupLeaderService;
import com.mds.group.purchase.user.service.WxuserService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The type Order util.
 *
 * @author pavawi
 */
@Component
public class OrderUtil {

    @Resource
    private OrderService orderService;
    @Resource
    private RedisTemplate redisTemplate;
    @Resource
    private WxuserService wxuserService;
    @Resource
    private WxServiceUtils wxServiceUtils;
    @Resource
    private CommunityService communityService;
    @Resource
    private GroupLeaderService groupLeaderService;
    @Resource
    private GroupBrokerageService groupBrokerageService;
    @Resource
    private FinancialDetailsService financialDetailsService;
    @Resource
    private OrderDetailService orderDetailService;
    @Resource
    private ActiveDelaySendJobHandler activeDelaySendJobHandler;

    private Logger log = LoggerFactory.getLogger(OrderUtil.class);

    /**
     * Send wait pay msg.
     *
     * @param orderId the order id
     */
    public void sendWaitPayMsg(Long orderId) {
        OrderResult order = orderService.getById(orderId);
        String key =
                GroupMallProperties.getRedisPrefix() + order.getAppmodelId() + RedisPrefix.FROMID + order.getWxuserId();
        List<String> formIds = (List<String>) redisTemplate.opsForValue().get(key);
        if (CollectionUtil.isNotEmpty(formIds)) {
            String formId = formIds.get(0);
            formIds.remove(0);
            redisTemplate.opsForValue().set(key, formIds);
            Map<String, Object> map = new HashMap<>(16);
            map.put("formId", formId);
            map.put("appmodelId", order.getAppmodelId());
            map.put("orderNo", order.getOrderNo());
            map.put("goodsName", order.getGoodsName());
            map.put("payFee", order.getPayFee());
            map.put("wxuserId", order.getWxuserId());
            map.put("time", "30分钟");
            map.put("type", TemplateMsgType.WAITPAY);
            activeDelaySendJobHandler
                    .savaTask(JSON.toJSONString(map), ActiviMqQueueName.ORDER_MINIPROGRAM_TEMPLATE_MESSAGE, 0L,
                            order.getAppmodelId(), false);
        }
    }


    /**
     * Send refund msg.
     *
     * @param order the order
     */
    public void sendRefundMsg(Order order) {
        OrderDetail orderDetail = orderDetailService.findBy("orderId", order.getOrderId());
        String key =
                GroupMallProperties.getRedisPrefix() + order.getAppmodelId() + RedisPrefix.FROMID + order.getWxuserId();
        List<String> formIds = (List<String>) redisTemplate.opsForValue().get(key);
        if (CollectionUtil.isNotEmpty(formIds)) {
            String formId = formIds.get(0);
            formIds.remove(0);
            redisTemplate.opsForValue().set(key, formIds);
            Map<String, Object> map = new HashMap<>(16);
            map.put("formId", formId);
            map.put("appmodelId", order.getAppmodelId());
            map.put("refundNo", order.getOrderNo());
            map.put("goodsName", orderDetail.getGoodsName());
            map.put("payFee", order.getPayFee());
            map.put("wxuserId", order.getWxuserId());
            map.put("type", TemplateMsgType.REFUNDSUCCESS);
            activeDelaySendJobHandler
                    .savaTask(JSON.toJSONString(map), ActiviMqQueueName.ORDER_MINIPROGRAM_TEMPLATE_MESSAGE, 0L,
                            order.getAppmodelId(), false);
        }
    }

    /**
     * Order send msg.
     *
     * @param orderIds the order ids
     */
    public void orderSendMsg(List<Long> orderIds) {
        if (CollectionUtil.isEmpty(orderIds)) {
            return;
        }
        List<OrderResult> orderResults = orderService.findByIdList(orderIds);
        orderResults.forEach(o -> {
            String key = GroupMallProperties.getRedisPrefix() + o.getAppmodelId() + RedisPrefix.FROMID + o.getWxuserId();
            List<String> formIds = (List<String>) redisTemplate.opsForValue().get(key);
            if (CollectionUtil.isNotEmpty(formIds)) {
                String formId = formIds.get(0);
                formIds.remove(0);
                redisTemplate.opsForValue().set(key,formIds);
                Map<String, Object> map = new HashMap<>(16);
                map.put("formId", formId);
                map.put("appmodelId", o.getAppmodelId());
                map.put("orderNo", o.getOrderNo());
                map.put("goodsName", o.getGoodsName());
                //预计收货时间
                map.put("time", DateUtil.formatDate(DateUtil.offsetDay(DateUtil.date(), 1)));
                map.put("wxuserId", o.getWxuserId());
                map.put("type", TemplateMsgType.ORDERSEND);
                activeDelaySendJobHandler
                        .savaTask(JSON.toJSONString(map), ActiviMqQueueName.ORDER_MINIPROGRAM_TEMPLATE_MESSAGE, 0L,
                                o.getAppmodelId(), false);
            }
        });
    }

    /**
     * 支付成功后的处理方法 1、更改订单状态 2、支付时间 3、商品销量处理 4、减库存 5.活动参与信息
     *
     * @param order 订单
     * @return 支付结果 string
     */
    public String paySuccess(Order order) {
        order.setPayStatus(OrderConstant.PAYED);
        order.setPayTime(new Date());
        if (orderService.update(order) == 0) {
            log.info("微信支付回调处理失败");
            return WxPayConstants.ResultCode.FAIL;
        }
        OrderDetail orderDetail = orderDetailService.findBy("orderId", order.getOrderId());
        Wxuser wxuser = wxuserService.findById(order.getWxuserId());
        Community community = communityService.findByGroupleaderId(order.getGroupId());
        //付款成功后增加团长累计佣金
        GroupLeader groupLeader = groupLeaderService.findById(order.getGroupId());
        if (groupLeader != null) {
            BigDecimal total = groupLeader.getTotalBrokerage().add(orderDetail.getGroupLeaderCommission());
            groupLeader.setTotalBrokerage(total);
            groupLeaderService.update(groupLeader);
        }
        //付款成功后插入团长佣金明细
        groupBrokerageService.save(order, orderDetail);
        //付款成功后插入对账单记录
        financialDetailsService.save(order, true);

        PayOkNotify payOkNotify = new PayOkNotify();
        payOkNotify.setImage(orderDetail.getGoodsImg());
        String name = "某某某";
        if (wxuser != null && StringUtils.isNotBlank(wxuser.getWxuserName())) {
            name = wxuser.getWxuserName();
            //websocket 广播
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("name", name);
            jsonObject.put("icon", wxuser.getIcon());
            jsonObject.put("actGoodsId", orderDetail.getActGoodsId());
            jsonObject.put("appmodelId", order.getAppmodelId());
            String content = jsonObject.toJSONString();
            activeDelaySendJobHandler
                    .savaTask(content, ActiviMqQueueName.ORDER_BROADCAST, 0L, order.getAppmodelId(), false);
            log.info("======================>请求发送订单弹幕");
        }
        payOkNotify.setContent(community.getCommunityName().concat("的").concat(name).concat("成功抢购1件..."));
        payOkNotify.setCreateTime(DateUtil.date());
        redisTemplate.opsForZSet().add(GroupMallProperties.getRedisPrefix().concat(order.getAppmodelId())
                        .concat(RedisPrefix.PAY_OK_NOTIFY), payOkNotify,
                System.currentTimeMillis() + RandomUtil.randomInt(1000));
        log.info("微信支付回调处理成功");
        return WxPayConstants.ResultCode.SUCCESS;
    }

    /**
     * 支付回调失败后的操作
     *
     * @param e                    the e
     * @param payOrderNotifyResult the pay order notify result
     * @throws Exception the exception
     */
    public void notifyFailHandle(Exception e, WxPayOrderNotifyResult payOrderNotifyResult) throws Exception {
        e.printStackTrace();
        StackTraceElement stackTraceElement = e.getStackTrace()[0];
        String target = "错误类:".concat(stackTraceElement.getClassName()).concat("错误方法:")
                .concat(stackTraceElement.getMethodName()).concat(",错误行数:")
                .concat(stackTraceElement.getLineNumber() + "");
        log.info(e.getMessage());
        log.info(target);

        Order order = new Order();
        order.setPayOrderId(payOrderNotifyResult.getOutTradeNo());
        List<Order> orderList = orderService.findByOrder(order);
        order = orderList.get(0);
        //失败后先插入收入对账单记录
        financialDetailsService.save(order, true);
        //失败后插入支出退款对账单记录
        financialDetailsService.save(order, false);
        //向用户发送退款成功模板消息
        this.sendRefundMsg(order);
        //向开发者发送短信通知
        try {
            SmsUtil.sendMsg(GroupMallProperties.getDevPhone(), order.getOrderNo());
        } catch (Exception e1) {
            e1.printStackTrace();
        } finally {
            wxServiceUtils.wechatRefund(order.getPayOrderId(), order.getOrderNo(),
                    String.valueOf(order.getPayFee().multiply(BigDecimal.valueOf(100L)).intValue()),
                    String.valueOf(order.getPayFee().multiply(BigDecimal.valueOf(100L)).intValue()), order.getAppmodelId());
        }
    }

    /**
     * Order arrive msg.
     *
     * @param orderIds the order ids
     */
    public void orderArriveMsg(List<Long> orderIds) {
        if (CollectionUtil.isEmpty(orderIds)) {
            return;
        }
        List<OrderResult> orderResults = orderService.findByIdList(orderIds);
        orderResults.forEach(o -> {
            String key =
                    GroupMallProperties.getRedisPrefix() + o.getAppmodelId() + RedisPrefix.FROMID + o.getWxuserId();
            List<String> formIds = (List<String>) redisTemplate.opsForValue().get(key);
            if (CollectionUtil.isNotEmpty(formIds)) {
                String formId = formIds.get(0);
                formIds.remove(0);
                redisTemplate.opsForValue().set(key, formIds);
                Map<String, Object> map = new HashMap<>(16);
                map.put("formId", formId);
                map.put("appmodelId", o.getAppmodelId());
                map.put("wxuserId", o.getWxuserId());

                //订单编号
                map.put("orderNo", o.getOrderNo());
                //商品名称
                map.put("goodsName", o.getGoodsName());
                //提货站点
                map.put("pickupStation", o.getCommunityId());
                //手机号
                map.put("phone", o.getGroupLeaderPhone());
                //提货地址
                map.put("pickupLocation", o.getPickupLocation());
                //营业时间
                map.put("businessHours", o.getWxuserId());
                map.put("type", TemplateMsgType.SENDORDERARRIVE);
                activeDelaySendJobHandler
                        .savaTask(JSON.toJSONString(map), ActiviMqQueueName.ORDER_MINIPROGRAM_TEMPLATE_MESSAGE, 0L,
                                o.getAppmodelId(), false);
            }
        });
    }

    /**
     * Order refund fail msg.
     *
     * @param afterSaleOrder the after sale order
     */
    public void orderRefundFailMsg(AfterSaleOrder afterSaleOrder) {
        if (afterSaleOrder == null) {
            return;
        }
        Map<String, Object> map = new HashMap<>(16);
        map.put("formId", afterSaleOrder.getFormId());
        map.put("appmodelId", afterSaleOrder.getAppmodelId());
        map.put("wxuserId", afterSaleOrder.getWxuserId());

        //售后订单编号
        map.put("afterSaleOrderNo", afterSaleOrder.getAfterSaleNo());
        //商品名称
        map.put("goodsName", afterSaleOrder.getGoodsName());
        //退款金额
        map.put("refundFee", afterSaleOrder.getRefundFee());
        //拒绝原因
        map.put("refundReason", afterSaleOrder.getRefusalReason());
        map.put("type", TemplateMsgType.SENDREFUNDFAIL);
        activeDelaySendJobHandler
                .savaTask(JSON.toJSONString(map), ActiviMqQueueName.ORDER_MINIPROGRAM_TEMPLATE_MESSAGE, 0L,
                        afterSaleOrder.getAppmodelId(), false);
    }
}
