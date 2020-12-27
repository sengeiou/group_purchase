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

package com.mds.group.purchase.order.service;

import com.github.binarywang.wxpay.bean.notify.WxPayOrderNotifyResult;
import com.github.pagehelper.PageInfo;
import com.mds.group.purchase.core.Service;
import com.mds.group.purchase.order.model.Order;
import com.mds.group.purchase.order.result.*;
import com.mds.group.purchase.order.vo.*;
import com.mds.group.purchase.shop.vo.HistoricalTransactionDataVO;
import com.mds.group.purchase.shop.vo.OrderDataVO;
import com.mds.group.purchase.shop.vo.SalesVolumeVO;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;


/**
 * Created by CodeGenerator on 2018/11/28.
 *
 * @author pavawi
 */
public interface OrderService extends Service<Order> {

    /**
     * 查询订单列表
     *
     * @param order the order
     * @return the list
     */
    List<Order> findByOrder(Order order);

    /**
     * 根据订单ID查询订单详情
     *
     * @param orderId the order id
     * @return the by id
     */
    OrderResult getById(Long orderId);

    /**
     * 根据appmodel查询订单
     *
     * @param appmodelId the appmodel id
     * @return the all
     */
    List<OrderResult> getAll(String appmodelId);

    /**
     * 查询符合条件的订单类别
     *
     * @param orderVo the order vo
     * @return the by conditions
     */
    List<WechatOrderResult> getByConditions(OrderVo orderVo);

    /**
     * 获取用户个人订单列表
     *
     * @param orderVo the order vo
     * @return user order list
     */
    List<WechatOrderResult> getUserOrderList(OrderVo orderVo);

    /**
     * 查询团长订单
     *
     * @param groupLeaderId the group leader id
     * @param searchType    the search type
     * @param appmodelId    the appmodel id
     * @param pageNum       the page num
     * @param pageSize      the page size
     * @return group order
     */
    List<GroupOrderDTO> getGroupOrder(String groupLeaderId, Integer searchType, String appmodelId, Integer pageNum,
                                      Integer pageSize);

    /**
     * 根据条件模糊查找订单
     *
     * @param searchOrderVo 订单搜索的参数对象
     * @param appmodelId    小程序模板id
     * @return OrderResult list
     */
    List<WechatOrderResult> fuzzySearch(SearchOrderVo searchOrderVo, String appmodelId);

    /**
     * 订单批量备注，发货
     *
     * @param orderUpdateVo the order update vo
     * @param response      the response
     */
    void updateBatch(OrderUpdateVo orderUpdateVo, HttpServletResponse response);


    /**
     * 创建换货单
     *
     * @param order         the order
     * @param goodsNum      the goods num
     * @param isGroupLeader the is group leader
     * @return the long
     */
    Long createExchange(Order order, Integer goodsNum, Boolean isGroupLeader);

    /**
     * 新建订单
     *
     * @param orderSaveVo 新建订单的参数对象
     * @return orderNo 订单号
     */
    Long saveOrder(OrderSaveVo orderSaveVo);

    /**
     * 支付
     *
     * @param orderId 订单id
     * @param request the request
     * @param ip      the ip
     * @return the map
     */
    Map<String, String> pay(Long orderId, HttpServletRequest request, String ip);

    /**
     * 订单回调
     *
     * @param xmlResult the xml result
     * @param version   the version
     * @return string string
     * @throws Exception the exception
     */
    String notify(WxPayOrderNotifyResult xmlResult, String version) throws Exception;

    /**
     * Update group postil int.
     *
     * @param orderIds   the order ids
     * @param postilText the postil text
     * @return the int
     */
    int updateGroupPostil(List<Long> orderIds, String postilText);

    /**
     * 用户取消未付款订单
     *
     * @param orderId    订单号
     * @param appmodelId 小程序模板id
     */
    void cancelOrder(Long orderId, String appmodelId);


    /**
     * 用户取消未付款订单
     *
     * @param orderNo    订单号
     * @param wxuserId   用户id
     * @param appmodelId 小程序模板id
     */
    void cancelOrder(String orderNo, Long wxuserId, String appmodelId);

    /**
     * 用户确认收货
     * 订单状态必须为团长已提货，或者是售后订单确认成功的回调，且订单和用户id匹配
     *
     * @param orderId    the order id
     * @param isCallback the is callback
     */
    void confirmReceipt(Long orderId, Boolean isCallback);


    /**
     * 查询订单历史交易额和交易量
     *
     * @param appmodelId  the appmodel id
     * @param currentDate the current date
     * @param lastWeek    the last week
     * @return list list
     */
    List<HistoricalTransactionDataVO> findAweekVolumeOfBusinessData(String appmodelId, String currentDate,
                                                                    String lastWeek);

    /**
     * 统计等待发货订单数 |等待支付订单数|等待取货订单数|成功的订单
     *
     * @param appmodelId the appmodel id
     * @return order data vo
     */
    OrderDataVO findByAppmodelIdStatistics(String appmodelId);

    /**
     * 统计团长销售量,商品销售量,会员消费统计
     *
     * @param appmodelId the appmodel id
     * @return list list
     */
    List<SalesVolumeVO> findByGroupleaderSale(String appmodelId);

    /**
     * 用户根据订单编号删除已关闭的订单
     *
     * @param wxuserId the wxuser id
     * @param orderNo  the order no
     */
    void deleteByWxuserId(Long wxuserId, String orderNo);

    /**
     * 查询指定用户订单
     *
     * @param wxuserId the wxuser id
     * @return list list
     */
    List<Order> findByWxuserIds(List<Long> wxuserId);


    /**
     * 更新指定订单得状态或订单关闭时间
     *
     * @param map the map
     */
    void updateOrderStatus(Map<String, Object> map);

    /**
     * 查询活动成交量,成交额,参与人数
     *
     * @param activityId the activity id
     * @param appmodelId the appmodel id
     * @return list list
     */
    List<ActivityTurnoverDTO> findActivityTurnover(List<Long> activityId, String appmodelId);

    /**
     * 查询我的代付款订单和待收货数量
     *
     * @param wxuserId   the wxuser id
     * @param appmodelId the appmodel id
     * @return my order sum result
     */
    MyOrderSumResult myOrderSum(Long wxuserId, String appmodelId);

    /**
     * 根据传入的orderId查询对应的OrderResult
     *
     * @param orderIds the order ids
     * @return list list
     */
    List<OrderResult> findOrderResultByOrderIds(List<Long> orderIds);

    /**
     * 根据条件模糊查找订单
     * v1.2版本
     *
     * @param searchOrderVo 订单搜索的参数对象
     * @param appmodelId    小程序模板id
     * @return OrderResult list
     */
    List<PcOrderResult> fuzzySearchV2(SearchOrderVoV2 searchOrderVo, String appmodelId);

    /**
     * 将订单从发货单中移除
     *
     * @param appmodelId the appmodel id
     * @param orderIds   the order ids
     */
    void removeFromSendBill(String appmodelId, String orderIds);

    /**
     * 根据订单id获取等待付款的订单
     *
     * @param stringList the string list
     * @return list list
     */
    List<Order> findWaitPayOrderByIds(List<Long> stringList);

    /**
     * 根据订单NO查询订单Map
     *
     * @param orderNos the order nos
     * @return the map
     */
    Map<String, OrderResult> findByOrderNos(List<String> orderNos);

    /**
     * 查看团长的未入账佣金订单
     *
     * @param groupLeaderId 团长id
     * @param appmodelId    模板id
     * @param page          the page
     * @param size          the size
     * @return OrderResult page info
     */
    PageInfo<PcOrderResult> findPayedOrder(String groupLeaderId, String appmodelId, Integer page, Integer size);

    /**
     * 根据订单ID列表查询订单
     *
     * @param orderIds the order ids
     * @return the list
     */
    List<OrderResult> findByIdList(List<Long> orderIds);


    /**
     * 扣除佣金
     *
     * @param order     the order
     * @param refundFee the refund fee
     */
    void deductionCommission(Order order, BigDecimal refundFee);

    /**
     * 根据团长wxuserId查询团长工作台首页
     *
     * @param wxuserId the wxuser id
     * @return workbench result
     */
    WorkbenchResult workbenchSummary(Long wxuserId);

    /**
     * 根据订单ID查询有售后进行的订单
     *
     * @param orderIds the order ids
     * @return list list
     */
    List<Order> findWaitReceiptByOrderIds(String orderIds);

    /**
     * 按照订单状态查询制定用户的订单
     *
     * @param wxuserId      the wxuser id
     * @param groupLeaderId the group leader id
     * @param status        the status
     * @return list list
     */
    List<OrderResult> findByWxuserIdAndStatus(Long wxuserId, String groupLeaderId, Integer status);

    /**
     * 根据团长groupLeaderId和订单状态查询订单
     *
     * @param groupLeaderId the group leader id
     * @param value         the value
     * @return list list
     */
    List<Order> findByGroupLeaderIdAndStatus(String groupLeaderId, Integer value);

    /**
     * 用户提货
     *
     * @param orderId the order id
     */
    void pickUp(Long orderId);

    /**
     * 根据团长wxuserId查询所有未结算的订单信息
     *
     * @param wxuserId the wxuser id
     * @return list list
     */
    List<NotSettlementCommissionResult> findGroupLeaderNotSettlementOrder(Long wxuserId);

    /**
     * 获取用户个人订单列表
     * v1.2版本
     *
     * @param orderVo the order vo
     * @return user order list v 2
     */
    List<WechatOrderResult> getUserOrderListV2(OrderVoV2 orderVo);

    /**
     * 根据ID获取团员
     *
     * @param orderIds the order ids
     * @return list list
     */
    List<MembersResult> findMembersByIds(List<Long> orderIds);

    /**
     * 查询等待支付的订单
     *
     * @return the list
     */
    List<Order> findByWaitPay();
}
