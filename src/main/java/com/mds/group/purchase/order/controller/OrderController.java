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

package com.mds.group.purchase.order.controller;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import com.github.binarywang.wxpay.bean.notify.WxPayNotifyResponse;
import com.github.binarywang.wxpay.bean.notify.WxPayOrderNotifyResult;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.mds.group.purchase.activity.model.ActivityGoods;
import com.mds.group.purchase.activity.result.ActGoodsInfoResult;
import com.mds.group.purchase.activity.service.ActivityGoodsService;
import com.mds.group.purchase.configurer.GroupMallProperties;
import com.mds.group.purchase.configurer.RequestLimit;
import com.mds.group.purchase.constant.*;
import com.mds.group.purchase.core.CodeMsg;
import com.mds.group.purchase.core.Result;
import com.mds.group.purchase.core.ResultGenerator;
import com.mds.group.purchase.exception.ServiceException;
import com.mds.group.purchase.logistics.model.GoodsAreaMapping;
import com.mds.group.purchase.logistics.model.Line;
import com.mds.group.purchase.logistics.service.GoodsAreaMappingService;
import com.mds.group.purchase.logistics.service.LineService;
import com.mds.group.purchase.order.model.Comment;
import com.mds.group.purchase.order.model.NegotiateHistory;
import com.mds.group.purchase.order.model.Order;
import com.mds.group.purchase.order.model.SendBill;
import com.mds.group.purchase.order.result.*;
import com.mds.group.purchase.order.service.*;
import com.mds.group.purchase.order.vo.*;
import com.mds.group.purchase.utils.ActivityGoodsUtil;
import com.mds.group.purchase.utils.OrderUtil;
import io.swagger.annotations.*;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import tk.mybatis.mapper.entity.Condition;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * The type Order controller.
 *
 * @author shuke
 * @date 2018 /11/28
 */
@RestController
@RequestMapping("order")
@Validated
@Api(tags = "所有接口")
public class OrderController {

    @Resource
    private OrderUtil orderUtil;
    @Resource
    private LineService lineService;
    @Resource
    private OrderService orderService;
    @Resource
    private RedisTemplate redisTemplate;
    @Resource
    private CommentService commentService;
    @Resource
    private SendBillService sendBillService;
    @Resource
    private ActivityGoodsService activityGoodsService;
    @Resource
    private RedisTemplate<String, Integer> redisTemplate4Int;
    @Resource
    private ActivityGoodsUtil activityGoodsUtil;
    @Resource
    private AfterSaleOrderService afterSaleOrderService;
    @Resource
    private NegotiateHistoryService negotiateHistoryService;
    @Resource
    private GoodsAreaMappingService goodsAreaMappingService;

    private Logger log = LoggerFactory.getLogger(OrderController.class);

    /**
     * 获取一条订单信息
     *
     * @param orderId the order id
     * @return the result
     */
    @ApiOperation(value = "获取一条订单信息", tags = "查询接口")
    @ApiResponses({@ApiResponse(code = 200, message = "success", response = OrderResult.class),})
    @GetMapping("/v1/info")
    public Result<OrderResult> list(@ApiParam(value = "订单id", required = true) @NotNull @RequestParam Long orderId) {
        OrderResult orderResult = orderService.getById(orderId);
        return Result.success(orderResult);
    }

    /**
     * 获取订单的售后协商历史
     *
     * @param orderId the order id
     * @param type    the type
     * @param page    the page
     * @param size    the size
     * @return the result
     */
    @ApiOperation(value = "获取订单的售后协商历史", tags = "查询接口")
    @ApiResponses({@ApiResponse(code = 200, message = "success", response = NegotiateHistory.class),})
    @GetMapping("/v1/negotiate/history")
    public Result<PageInfo<NegotiateHistory>> negotiateHistoryList(
            @ApiParam(value = "订单id", required = true) @NotNull @RequestParam Long orderId,
            @ApiParam(value = "用户类型 0.用户,1.团长 不传查询所有") @RequestParam(required = false) String type,
            @RequestParam(defaultValue = "0") Integer page, @RequestParam(defaultValue = "0") Integer size) {
        PageHelper.startPage(page, size);
        List<NegotiateHistory> list = negotiateHistoryService.findByOrderId(orderId, type);
        PageInfo<NegotiateHistory> pageInfo = new PageInfo<>(list);
        return ResultGenerator.genSuccessResult(pageInfo);
    }

    /**
     * 根据线路查询订单
     *
     * @param appmodelId the appmodel id
     * @param page       the page
     * @param size       the size
     * @return the result
     */
    @ApiOperation(value = "根据线路查询订单", tags = "查询接口")
    @ApiResponses({@ApiResponse(code = 200, message = "success", response = OrderResult.class),})
    @GetMapping("/v1")
    public Result<PageInfo<OrderResult>> orderListByLine(
            @ApiParam(value = "小程序模板id", required = true) @RequestHeader String appmodelId,
            @RequestParam(defaultValue = "0") Integer page, @RequestParam(defaultValue = "0") Integer size) {
        PageHelper.startPage(page, size);
        List<OrderResult> list = orderService.getAll(appmodelId);
        PageInfo<OrderResult> pageInfo = new PageInfo<>(list);
        return ResultGenerator.genSuccessResult(pageInfo);
    }

    /**
     * 根据条件搜索订单
     *
     * @param appmodelId    the appmodel id
     * @param searchOrderVo the search order vo
     * @return the result
     */
    @ApiOperation(value = "根据条件搜索订单", tags = "查询接口")
    @ApiResponses({@ApiResponse(code = 200, message = "success", response = OrderResult.class),})
    @PostMapping("/v1/search")
    public Result<PageInfo<WechatOrderResult>> searchOrder(
            @ApiParam(value = "小程序模板id", required = true) @RequestHeader String appmodelId,
            @RequestBody SearchOrderVo searchOrderVo) {
        if (searchOrderVo == null) {
            searchOrderVo = new SearchOrderVo();
        }
        PageHelper.startPage(searchOrderVo.getPage(), searchOrderVo.getSize());
        if (searchOrderVo.getOrderStatus() == -1 || searchOrderVo.getOrderStatus() == OrderConstant.WAIT4PAY
                || OrderConstant.isClose(searchOrderVo.getOrderStatus())) {
            PageHelper.orderBy("create_time desc");
        } else if (searchOrderVo.getOrderStatus() == OrderConstant.PAYED) {
            PageHelper.orderBy("pay_time  desc");
        } else if (searchOrderVo.getOrderStatus() == OrderConstant.SENDED) {
            PageHelper.orderBy("create_time desc");
        } else if (searchOrderVo.getOrderStatus() == OrderConstant.NOT_COMMENT) {
            PageHelper.orderBy("pay_time desc");
        }
        List<WechatOrderResult> list = orderService.fuzzySearch(searchOrderVo, appmodelId);
        PageInfo<WechatOrderResult> pageInfo = new PageInfo<>(list);
        return ResultGenerator.genSuccessResult(pageInfo);
    }

    /**
     * 根据条件搜索订单
     *
     * @param appmodelId    模板id
     * @param searchOrderVo 条件参数
     * @return 订单结果集合 result
     * @since v1.2
     */
    @ApiOperation(value = "根据条件搜索订单V1.2版本", tags = "v1.2版本接口")
    @ApiResponses({@ApiResponse(code = 200, message = "success", response = OrderResult.class),})
    @PostMapping("/v2/search")
    public Result<PageInfo> searchOrderV2(
            @ApiParam(value = "小程序模板id", required = true) @RequestHeader String appmodelId,
            @RequestBody SearchOrderVoV2 searchOrderVo) {
        if (searchOrderVo == null) {
            searchOrderVo = new SearchOrderVoV2();
        }
        PageHelper.orderBy("o.create_time desc");
        PageHelper.startPage(searchOrderVo.getPage(), searchOrderVo.getSize());
        if (searchOrderVo.getOrderStatus() == 99) {
            List<AfterSaleOrderManageResult> afterSaleOrderManageResults = afterSaleOrderService
                    .searchManageAfterSaleOrder(appmodelId, searchOrderVo);
            PageInfo<AfterSaleOrderManageResult> pageInfo = new PageInfo<>(afterSaleOrderManageResults);
            return Result.success(pageInfo);
        }
        List<PcOrderResult> list = orderService.fuzzySearchV2(searchOrderVo, appmodelId);
        PageInfo<PcOrderResult> pageInfo = new PageInfo<>(list);
        return Result.success(pageInfo);
    }


    /**
     * 查看已付款但未收货订单 （用于团长佣金详情页面跳转查询）
     *
     * @param appmodelId    模板id
     * @param groupLeaderId 团长id
     * @param page          the page
     * @param size          the size
     * @return 订单结果集合 result
     * @since v1.2.2
     */
    @ApiOperation(value = "根据条件搜索订单V1.2版本", tags = "v1.2版本接口")
    @ApiResponses({@ApiResponse(code = 200, message = "success", response = OrderResult.class),})
    @GetMapping("/v1.2.2/searchPayedOrder")
    public Result<PageInfo<PcOrderResult>> searchPayedOrder(
            @ApiParam(value = "小程序模板id", required = true) @RequestHeader @NotBlank String appmodelId,
            @RequestParam @NotBlank String groupLeaderId, @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "0") Integer size) {
        PageInfo<PcOrderResult> payedOrder = orderService.findPayedOrder(groupLeaderId, appmodelId, page, size);
        return Result.success(payedOrder);
    }

    /**
     * 批量备注，批量发货
     *
     * @param orderUpdateVo the order update vo
     * @param appmodelId    the appmodel id
     * @param response      the response
     * @return the result
     */
    @ApiOperation(value = "批量更新订单 (1：发货，2备注,3 关闭 4.删除 5导出)", tags = "更新接口")
    @PutMapping("/v1/updateBatch")
    public Result updateBatch(@RequestBody @Valid OrderUpdateVo orderUpdateVo, @RequestHeader String appmodelId,
                              HttpServletResponse response) {
        orderUpdateVo.setAppmodelId(appmodelId);
        orderService.updateBatch(orderUpdateVo, response);
        if (!orderUpdateVo.getUpdateStatus().equals(OrderUpdateVo.EXPORT)) {
            if (orderUpdateVo.getUpdateStatus().equals(OrderUpdateVo.CLOSE)) {
                try {
                    Thread.sleep(700L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return Result.success();
        }
        return null;
    }


    /**
     * 支付接口
     *
     * @param orderId 订单id
     * @param request the request
     * @return the result
     */
    @ApiOperation(value = "支付", tags = "更新接口")
    @GetMapping("/wx/v1/pay")
    public Result<Map<String, String>> pay(@RequestParam Long orderId, HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.length() == 0) {
            ip = request.getRemoteAddr();
        }
        log.info("ip:" + ip + "请求支付接口！订单id：" + orderId + ",请求时间：" + DateUtil.date());
        Map<String, String> map = orderService.pay(orderId, request, ip);
        return Result.success(map);
    }

    /**
     * 支付回调
     * 订单支付回调
     *
     * @param request request
     * @return the string
     * @throws Exception 异常
     */
    @PostMapping("/v1/notify")
    @ApiOperation(value = "订单支付回调", tags = "回调接口")
    @Deprecated
    protected String notify(HttpServletRequest request) throws Exception {
        String xmlResult = IOUtils.toString(request.getInputStream(), request.getCharacterEncoding());
        WxPayOrderNotifyResult payOrderNotifyResult = WxPayOrderNotifyResult.fromXML(xmlResult);
        return orderService.notify(payOrderNotifyResult, Version.V_1);
    }

    /**
     * 支付回调
     *
     * @param request request
     * @return the string
     * @throws Exception 异常
     * @since v1.2
     */
    @PostMapping("/v2/notify")
    @ApiOperation(value = "订单支付回调V2版本，此版本支付成功后会生成对应该订单的订单发货单关系映射数据", tags = "v1.2版本接口")
    protected String notifyV2(HttpServletRequest request) throws Exception {
        String xmlResult = IOUtils.toString(request.getInputStream(), request.getCharacterEncoding());
        WxPayOrderNotifyResult payOrderNotifyResult = WxPayOrderNotifyResult.fromXML(xmlResult);
        try {
            return orderService.notify(payOrderNotifyResult, Version.V_1_2);
        } catch (Exception e) {
            orderUtil.notifyFailHandle(e, payOrderNotifyResult);
            return WxPayNotifyResponse.fail("订单回调处理失败");
        }

    }

    /**
     * 用户个人订单查询
     *
     * @param orderVo the order vo
     * @return 小程序端的订单返回结果对象 result
     */
    @ApiOperation(value = "用户个人订单查询", tags = "查询接口")
    @PostMapping("/wx/userorder")
    public Result<PageInfo<WechatOrderResult>> userOrder(@RequestBody @Valid OrderVo orderVo) {
        PageHelper.startPage(orderVo.getPage(), orderVo.getSize());
        if (orderVo.getOrderStatus() == -1) {
            PageHelper.orderBy("o.pay_time desc");
        } else if (orderVo.getOrderStatus() == 0) {
            PageHelper.orderBy("o.create_time desc");
        }
        List<WechatOrderResult> orderResult = orderService.getUserOrderList(orderVo);
        PageInfo<WechatOrderResult> pageInfo = new PageInfo<>(orderResult);
        return Result.success(pageInfo);
    }

    /**
     * 用户个人订单查询
     *
     * @param orderVo the order vo
     * @return 小程序端的订单返回结果对象 result
     */
    @ApiOperation(value = "用户个人订单查询", tags = "查询接口")
    @PostMapping("/wx/v2/userorder")
    public Result<PageInfo> userOrderV2(@RequestBody @Valid OrderVoV2 orderVo) {
        PageHelper.startPage(orderVo.getPage(), orderVo.getSize());
        PageHelper.orderBy("create_time desc");

        if (orderVo.getOrderStatus() == 99) {
            List<AfterSaleOrderResult> afterSaleOrderResultList = afterSaleOrderService
                    .getUserAfterSaleOrderList(orderVo);
            PageInfo<AfterSaleOrderResult> pageInfo = new PageInfo<>(afterSaleOrderResultList);
            return Result.success(pageInfo);
        }
        List<WechatOrderResult> orderResult = orderService.getUserOrderListV2(orderVo);
        PageInfo<WechatOrderResult> pageInfo = new PageInfo<>(orderResult);
        return Result.success(pageInfo);
    }


    /**
     * Save comment result.
     *
     * @param saveCommentVo the save comment vo
     * @param appmodelId    the appmodel id
     * @return the result
     */
    @ApiOperation(value = "用户评价订单", tags = "添加接口")
    @PostMapping("/wx/comment")
    public Result saveComment(@RequestBody @Valid SaveCommentVo saveCommentVo,
                              @RequestHeader @NotBlank String appmodelId) {
        saveCommentVo.setAppmodelId(appmodelId);
        commentService.saveComment(saveCommentVo);
        return Result.success();
    }

    /**
     * 用户取消订单
     *
     * @param appmodelId 模板id
     * @param orderId    订单id
     * @return the result
     * @since v1.2
     */
    @ApiOperation(value = "用户取消订单v2版本", tags = "v1.2版本接口")
    @GetMapping("/wx/v2/cancelOrder")
    public Result cancelOrder(@RequestHeader @NotBlank String appmodelId,
                              @ApiParam("订单id") @RequestParam @NotNull Long orderId) {
        String redisKey = GroupMallProperties.getRedisPrefix() + appmodelId + RedisPrefix.CANCLE_ORDER_ID + orderId;
        Integer integer = null;
        Long o = (Long) redisTemplate.opsForValue().get(redisKey);
        if (o != null) {
            integer = o.intValue();
        }
        if (integer == null) {
            integer = -1;
            redisTemplate.opsForValue().set(redisKey, orderId, 60, TimeUnit.SECONDS);
        }
        if (orderId.equals(Long.valueOf(integer))) {
            return Result.error(CodeMsg.FAIL.fillArgs("订单：" + orderId + "正在处理中"));
        }
        orderService.cancelOrder(orderId, appmodelId);

        return Result.success();
    }

    /**
     * 用户取消订单
     *
     * @param appmodelId the appmodel id
     * @param wxuserId   the wxuser id
     * @param orderNo    the order no
     * @return the result
     */
    @ApiOperation(value = "用户取消订单", tags = "更新接口")
    @Deprecated
    @GetMapping("/wx/cancelOrder")
    public Result cancelOrder(@RequestHeader @NotBlank String appmodelId, @RequestParam @NotNull Long wxuserId,
                              @RequestParam @NotBlank String orderNo) {
        orderService.cancelOrder(orderNo, wxuserId, appmodelId);
        return Result.success();
    }

    /**
     * 获取用户的评价
     *
     * @param wxuserId   the wxuser id
     * @param appmodelId the appmodel id
     * @param page       the page
     * @param size       the size
     * @return the user comment
     */
    @ApiOperation(value = "获取用户的评价", tags = "查询接口")
    @GetMapping("/wx/comment")
    public Result<PageInfo<Comment>> getUserComment(@ApiParam("用户id") @RequestParam @NotNull Long wxuserId,
                                                    @RequestHeader @NotBlank String appmodelId,
                                                    @ApiParam("页码") @RequestParam(defaultValue = "0") int page,
                                                    @ApiParam("页面大小") @RequestParam(defaultValue = "0") int size) {
        PageHelper.startPage(page, size);
        List<Comment> comments = commentService.findByUserId(wxuserId, appmodelId);
        PageInfo<Comment> pageInfo = new PageInfo<>(comments);
        return Result.success(pageInfo);
    }

    /**
     * 用户确认收货
     *
     * @param orderId the order id
     * @return the result
     */
    @ApiOperation(value = "用户确认收货", tags = "更新接口")
    @GetMapping("/wx/receipt")
    public Result confirmReceipt(@RequestParam @NotNull Long orderId) {
        orderService.confirmReceipt(orderId, false);
        return Result.success();
    }

    /**
     * 用户确认收货
     *
     * @return the result
     */
    @ApiOperation(value = "用户确认收货", tags = "更新接口")
    @GetMapping("/shoudong")
    public Result confirmReceiptshoudong() {
        Condition condition = new Condition(Order.class);
        condition.createCriteria().andEqualTo("appmodelId", "S00050001wx0380472793fe5f92").andEqualTo("payStatus", 8)
                .andBetween("payTime", "2019-06-01 00:00:00", "2019-07-21 00:00:00");
        List<Order> byCondition = orderService.findByCondition(condition);
        List<Long> collect = byCondition.stream().map(Order::getOrderId).collect(Collectors.toList());
        for (Long orderId : collect) {
            orderService.confirmReceipt(orderId, false);
        }
        return Result.success(collect);
    }

    /**
     * 小程序端用户删除已关闭的订单
     *
     * @param wxuserId the wxuser id
     * @param orderNo  the order no
     * @return the result
     */
    @ApiOperation(value = "小程序端用户删除已关闭的订单", tags = "删除接口")
    @DeleteMapping("/v1/wx/delete")
    public Result deleteOrder(@ApiParam("用户id") @RequestParam Long wxuserId,
                              @ApiParam("订单编号") @RequestParam String orderNo) {
        orderService.deleteByWxuserId(wxuserId, orderNo);
        return Result.success();
    }

    /**
     * 我的订单数量
     *
     * @param wxuserId   the wxuser id
     * @param appmodelId the appmodel id
     * @return the result
     */
    @GetMapping("/v1/my/order/sum")
    @ApiOperation(value = "我的订单数量", tags = "查询接口")
    public Result<MyOrderSumResult> myOrderSum(@ApiParam("用户id") @RequestParam Long wxuserId,
                                               @RequestHeader @NotBlank(message = "appmodelId不能为空") @Size(max = 27,
                                                       min = 27, message = "商品标示错误") String appmodelId) {
        MyOrderSumResult myOrderSumResult = orderService.myOrderSum(wxuserId, appmodelId);
        return Result.success(myOrderSumResult);
    }

    /**
     * 订单搜索界面获取线路的接口
     *
     * @param appmodelId the appmodel id
     * @return the line
     * @since v1.2
     */
    @ApiOperation(value = "订单搜索界面获取线路的接口", tags = "v1.2版本接口")
    @GetMapping("/v2/line")
    public Result<List<Line>> getLine(@RequestHeader @NotBlank String appmodelId) {
        List<Line> list = lineService.findByAppmodelIdV2(appmodelId);
        return Result.success(list);
    }

    /**
     * 获取未完成发货单信息接口
     *
     * @param appmodelId the appmodel id
     * @return the 3 send bill
     * @since v1.2
     */
    @ApiOperation(value = "获取未完成发货单信息接口", tags = "v1.2版本接口")
    @GetMapping("/v2/lately3SendBill")
    public Result<List<SendBill>> get3SendBill(@RequestHeader @NotBlank String appmodelId) {
        List<SendBill> list = sendBillService.find3SendBill(appmodelId);
        return Result.success(list);
    }

    /**
     * 将订单移出发货单
     *
     * @param appmodelId the appmodel id
     * @param orderIds   the order ids
     * @return the result
     * @since v1.2
     */
    @ApiOperation(value = "将订单移出发货单", tags = "v1.2版本接口")
    @GetMapping("/v2/remove/from/send/bill")
    public Result removeFromSendBill(@RequestHeader @NotBlank String appmodelId,
                                     @ApiParam("订单id，多个id用逗号分隔") @RequestParam @NotBlank String orderIds) {
        orderService.removeFromSendBill(appmodelId, orderIds);
        return Result.success();
    }

    /**
     * 获取fromid
     *
     * @param wxuserId   用户id
     * @param fromId     fromid
     * @param appmodelId 模板id
     * @return the from id
     */
    @GetMapping("/fromId")
    public Result getFromId(@RequestParam @NotBlank String wxuserId, @RequestParam @NotBlank String fromId,
                            @RequestHeader @NotBlank String appmodelId) {
        String key = GroupMallProperties.getRedisPrefix() + appmodelId + RedisPrefix.FROMID + wxuserId;
        List<String> fromIds = (List<String>) redisTemplate.opsForValue().get(key);
        if (CollectionUtil.isEmpty(fromIds)) {
            fromIds = new ArrayList<>();
        }
        fromIds.add(fromId);
        redisTemplate.opsForValue().set(key, fromIds);
        return Result.success();
    }

    /**
     * 发送待支付模板消息
     *
     * @param appmodelId 模板id
     * @param orderId    订单id
     * @return the result
     */
    @GetMapping("/waitPay")
    public Result sendWaitPayMsg(@RequestHeader @NotBlank String appmodelId, Long orderId) {
        orderUtil.sendWaitPayMsg(orderId);
        return Result.success();
    }


    /**
     * 创建一个团购订单
     *
     * @param orderSaveVo 订单保存参数对象
     * @param appmodelId  小程序模板id
     * @param request     the request
     * @return 订单id result
     */
    @ApiOperation(value = "创建一个团购订单", tags = "新增接口")
    @PostMapping("/wx/v1/group")
    @RequestLimit(count = 5, time = 3000)
    public Result saveGroupOrderTest(@RequestBody @Valid OrderSaveVo orderSaveVo, @RequestHeader String appmodelId,
                                     HttpServletRequest request) {
        //设置订单状态为团购订单
        orderSaveVo.setOrderType(OrderConstant.GROUP_ORDER);
        orderSaveVo.setAppmodelId(appmodelId);
        boolean goodsOnSale = activityGoodsUtil.thisGoodsOnSale(orderSaveVo.getActGoodsId(), appmodelId);
        if (!goodsOnSale) {
            return Result.error(CodeMsg.FAIL.fillArgs("该商品已售罄"));
        }
        //读取内存标记，判断活动商品是否已经售罄
        String redisKeyPrefix = GroupMallProperties.getRedisPrefix().concat(appmodelId);
        //在redis中 预减库存
        String actGoodsStockRedisKey = GroupMallProperties.getRedisPrefix().concat(appmodelId)
                .concat(RedisPrefix.ACT_GOODS_STOCK).concat(orderSaveVo.getActGoodsId().toString());
        String redisKeySaleVolum = redisKeyPrefix.concat(RedisPrefix.ACT_GOODS_SALES_VOLUME)
                .concat(orderSaveVo.getActGoodsId().toString());
        Long surplusActStock;
        Integer actStock = redisTemplate4Int.opsForValue().get(actGoodsStockRedisKey);
        //redis缓存过期后从数据库获取数据，并重新存入redis
        if (actStock == null) {
            ActGoodsInfoResult actGoods = activityGoodsService.getActGoodsById(orderSaveVo.getActGoodsId(), appmodelId);
            actStock = actGoods.getActivityStock();
            redisTemplate4Int.opsForValue().set(actGoodsStockRedisKey, actStock, 3, TimeUnit.DAYS);
        }
        if (actStock <= 0) {
            return Result.error(CodeMsg.GOODS_UNDERSTOCK.fillArgs("商品库存不足，下单失败"));
        }
        surplusActStock = redisTemplate4Int.opsForValue()
                .decrement(actGoodsStockRedisKey, orderSaveVo.getGoodsNum().longValue());
        if (surplusActStock != null && surplusActStock < 0) {
            //库存减少失败，不能下单,将减少的库存还回redis
            redisTemplate4Int.opsForValue().increment(actGoodsStockRedisKey, orderSaveVo.getGoodsNum().longValue());
            return Result.error(CodeMsg.GOODS_UNDERSTOCK.fillArgs("商品库存不足，下单失败"));
        }
        Long orderId = saveOrder(orderSaveVo, redisKeySaleVolum, actGoodsStockRedisKey, surplusActStock);
        return Result.success(orderId);
    }

    /**
     * 创建一个秒杀订单
     *
     * @param orderSaveVo 订单保存参数对象
     * @param appmodelId  小程序模板id
     * @param request     the request
     * @return 订单号 result
     */
    @RequestLimit(count = 5, time = 3000)
    @ApiOperation(value = "创建一个秒杀订单", tags = "新增接口")
    @PostMapping("/wx/v1/seckill")
    public Result saveSeckillOrderTest(@RequestBody @Valid OrderSaveVo orderSaveVo, @RequestHeader String appmodelId,
                                       HttpServletRequest request) {
        //设置订单状态为秒杀订单
        orderSaveVo.setOrderType(OrderConstant.SECKILL_ORDER);
        //判断商品是否时在售卖状态
        boolean goodsOnSale = activityGoodsUtil.thisGoodsOnSale(orderSaveVo.getActGoodsId(), appmodelId);
        if (!goodsOnSale) {
            return Result.error(CodeMsg.FAIL.fillArgs("该商品已售罄"));
        }
        orderSaveVo.setAppmodelId(appmodelId);
        String redisKeyPrefix = GroupMallProperties.getRedisPrefix().concat(appmodelId);
        //用户已购买的数量
        String buyAmountKey = redisKeyPrefix.concat(RedisPrefix.ACTIVITY_BUY_AMOUNT)
                .concat(orderSaveVo.getWxuserId().toString()).concat(orderSaveVo.getActGoodsId().toString());
        String actGoodsStockRedisKey = GroupMallProperties.getRedisPrefix().concat(appmodelId)
                .concat(RedisPrefix.ACT_GOODS_STOCK).concat(orderSaveVo.getActGoodsId().toString());
        String redisKeySaleVolum = redisKeyPrefix.concat(RedisPrefix.ACT_GOODS_SALES_VOLUME)
                .concat(orderSaveVo.getActGoodsId().toString());
        //获取商品的活动库存
        Integer stock = redisTemplate4Int.opsForValue().get(actGoodsStockRedisKey);
        if (stock == null) {
            //redis缓存过期后从数据库获取数据，并重新存入redis
            ActGoodsInfoResult actGoods = activityGoodsService.getActGoodsById(orderSaveVo.getActGoodsId(), appmodelId);
            stock = actGoods.getActivityStock();
            redisTemplate4Int.opsForValue().set(actGoodsStockRedisKey, stock, 3, TimeUnit.DAYS);
        }
        if (stock <= 0) {
            throw new ServiceException("该商品已售罄");
        }
        //在redis中得到限购数量
        String redisKey4limit = GroupMallProperties.getRedisPrefix().concat(appmodelId).concat(RedisPrefix.MAX_QUANTITY)
                .concat(orderSaveVo.getActGoodsId().toString());
        Integer maxQuantity = redisTemplate4Int.opsForValue().get(redisKey4limit);
        //redis中该商品的限购数量为null，则查询数据库获取限购，并且将其存入redis
        if (maxQuantity == null) {
            ActivityGoods activityGoods = activityGoodsService.findById(orderSaveVo.getActGoodsId());
            maxQuantity = activityGoods.getMaxQuantity();
            redisTemplate4Int.opsForValue().set(redisKey4limit, maxQuantity);
        }
        //获取用户已购买数量
        //从redis中取,redis没有则是用户没下过单
        Integer userBuyNumber = redisTemplate4Int.opsForValue().get(buyAmountKey);
        userBuyNumber = userBuyNumber == null ? 0 : userBuyNumber;
        //购买数量已经超过最大限购值或已购买的数量+要购买的数量超过最大限购,则无法购买
        if (userBuyNumber >= maxQuantity || userBuyNumber + orderSaveVo.getGoodsNum() > maxQuantity) {
            return Result.error(CodeMsg.MAX_QUANTITY);
        }
        //减去购买的商品数量后剩余库存,如果是小于0或等于0,则商品已售罄
        Long surplusActStock = redisTemplate4Int.opsForValue()
                .decrement(actGoodsStockRedisKey, orderSaveVo.getGoodsNum().longValue());
        if (surplusActStock == null) {
            throw new ServiceException("商品库存不足" + orderSaveVo.getActGoodsId());
        } else if (surplusActStock < 0) {
            redisTemplate4Int.opsForValue().increment(actGoodsStockRedisKey, orderSaveVo.getGoodsNum().longValue());
            throw new ServiceException("库存不足" + orderSaveVo.getActGoodsId());
        }
        //未达到购买限制数量增加
        redisTemplate4Int.opsForValue().increment(buyAmountKey, orderSaveVo.getGoodsNum().longValue());
        Long orderId = this.saveOrder(orderSaveVo, redisKeySaleVolum, actGoodsStockRedisKey, surplusActStock);
        return Result.success(orderId);
    }

    private Long saveOrder(@Valid @RequestBody OrderSaveVo orderSaveVo, String redisKeySaleVolum,
                           String actGoodsStockRedisKey, Long surplusActStock) {
        try {
            //更新活动商品销量
            //将活动销量在redis增加
            redisTemplate4Int.opsForValue().increment(redisKeySaleVolum, orderSaveVo.getGoodsNum().longValue());
            Long orderId = orderService.saveOrder(orderSaveVo);
            if (surplusActStock <= 0) {
                //库存减少后为0，标识活动商品已售罄，更改活动商品状态
                activityGoodsService.updateActGoodsPreheatStatusById(orderSaveVo.getActGoodsId(),
                        ActivityConstant.ACTIVITY_GOODS_OVER);
            }
            return orderId;
        } catch (Exception e) {
            //发生异常，不能下单,将减少的库存还回redis
            redisTemplate4Int.opsForValue().increment(actGoodsStockRedisKey, orderSaveVo.getGoodsNum().longValue());
            //销量减少
            redisTemplate4Int.opsForValue().decrement(redisKeySaleVolum, orderSaveVo.getGoodsNum().longValue());
            log.error(e.getMessage());
            throw e;
        }
    }

    /**
     * 订单页面切换小区验证
     *
     * @param appmodelId  the appmodel id
     * @param communityId the community id
     * @param goodsIds    the goods ids
     * @return 订单号 result
     */
    @ApiOperation(value = "订单页面切换小区验证", tags = "新增接口")
    @GetMapping("/wx/check")
    public Result orderCheck(@RequestHeader String appmodelId, @RequestParam Integer communityId,
                             @RequestParam String goodsIds) {
        List<Long> res = null;
        List<Long> goodsIdList = Arrays.stream(goodsIds.split(Common.REGEX)).map(o -> Long.valueOf(o))
                .collect(Collectors.toList());
        Condition condition = new Condition(GoodsAreaMapping.class);
        condition.createCriteria().andEqualTo("appmodelId", appmodelId).andIn("goodsId", goodsIdList)
                .andEqualTo("communityId", communityId);
        List<GoodsAreaMapping> goodsAreaMapping = goodsAreaMappingService.findByCondition(condition);
        if (CollectionUtil.isNotEmpty(goodsAreaMapping)) {
            List<Long> list = goodsAreaMapping.stream().map(o -> o.getGoodsId()).collect(Collectors.toList());
            res = goodsIdList.stream().filter(o -> !list.contains(o)).collect(Collectors.toList());
            return CollectionUtil.isNotEmpty(res) ? Result.error(CodeMsg.FAIL.fillArgs("该商品在此小区中不售卖")) :
                    Result.success();
        } else {
            return Result.error(CodeMsg.FAIL.fillArgs("该商品在此小区中不售卖"));
        }

    }

}
