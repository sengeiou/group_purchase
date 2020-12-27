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

package com.mds.group.purchase.order.dao;

import com.mds.group.purchase.core.Mapper;
import com.mds.group.purchase.order.model.Order;
import com.mds.group.purchase.order.result.*;
import com.mds.group.purchase.order.vo.*;
import com.mds.group.purchase.shop.vo.HistoricalTransactionDataVO;
import com.mds.group.purchase.shop.vo.StatisticsVO;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * The interface Order mapper.
 *
 * @author pavawi
 */
public interface OrderMapper extends Mapper<Order> {

    /**
     * Select by id order result.
     *
     * @param orderId the order id
     * @return the order result
     */
    OrderResult selectById(Long orderId);

    /**
     * Select all order list.
     *
     * @param appmodelId the appmodel id
     * @return the list
     */
    List<OrderResult> selectAllOrder(String appmodelId);

    /**
     * Select order by create time.
     *
     * @param time the time
     */
    void selectOrderByCreateTime(String time);

    /**
     * Select by conditions list.
     *
     * @param orderVo the order vo
     * @return the list
     */
    List<WechatOrderResult> selectByConditions(OrderVo orderVo);


    /**
     * Select by conditions v 2 list.
     *
     * @param orderVo the order vo
     * @return the list
     */
    List<WechatOrderResult> selectByConditionsV2(OrderVoV2 orderVo);

    /**
     * 查询用户个人订单列表
     *
     * @param orderVo the order vo
     * @return list list
     */
    List<WechatOrderResult> selectUserOrderList(@Param("orderVo") OrderVo orderVo);

    /**
     * 查询所属团长订单
     *
     * @param groupLeaderId the group leader id
     * @param searchType    the search type
     * @param appmodelId    the appmodel id
     * @return list list
     */
    List<GroupOrderDTO> selectGroupOrder(@Param("groupLeaderId") String groupLeaderId,
                                         @Param("searchType") Integer searchType,
                                         @Param("appmodelId") String appmodelId);

    /**
     * Search by conditions list.
     *
     * @param searchOrderVo the search order vo
     * @param appmodelId    the appmodel id
     * @return the list
     */
    List<WechatOrderResult> searchByConditions(@Param("searchOrderVo") SearchOrderVo searchOrderVo,
                                               @Param("appmodelId") String appmodelId);

    /**
     * 根据订单id批量更新
     *
     * @param ids          the ids
     * @param shopDesc     the shop desc
     * @param updateStatus 修改类型（1：发货，2备注,3 关闭 4.删除 5导出
     */
    void updateBatch(@Param("ids") List<Long> ids, @Param("shopDesc") String shopDesc,
                     @Param("updateStatus") Integer updateStatus);

    //	Order selectByPayNo(String payNo);

    /**
     * 查询指定时间段的交易额和交易量
     *
     * @param appmodelId  the appmodel id
     * @param currentDate the current date
     * @param lastWeek    the last week
     * @return the list
     */
    List<HistoricalTransactionDataVO> selectAweekVolumeOfBusinessData(@Param("appmodelId") String appmodelId,
                                                                      @Param("currentDate") String currentDate,
                                                                      @Param("lastWeek") String lastWeek);

    /**
     * 统计等待发货订单数 |等待支付订单数|等待取货订单数|成功的订单
     *
     * @param appmodelId the appmodel id
     * @return list list
     */
    List<StatisticsVO> selectByAppmodelIdStatistics(String appmodelId);

    /**
     * 统计团长销售量,商品销售量,会员消费统计
     *
     * @param appmodelId the appmodel id
     * @param optionType 1=团长销售量 2=商品销售量 3=会员消费统计
     * @param condition  condition == 1 搜索前五名信息
     * @return list list
     */
    List<SalesVolumeDataVO> selectByBackSaleStatistics(@Param("appmodelId") String appmodelId,
                                                       @Param("optionType") int optionType,
                                                       @Param("condition") int condition);

    /**
     * Select by back sale statistics totle big decimal.
     *
     * @param appmodelId the appmodel id
     * @param optionType 1=团长销售量 2=商品销售量 3=会员消费统计
     * @param condition  condition != 1 统计销售总额
     * @return big decimal
     */
    BigDecimal selectByBackSaleStatisticsTotle(@Param("appmodelId") String appmodelId,
                                               @Param("optionType") int optionType, @Param("condition") int condition);

    /**
     * 根据用户id和订单编号删除已关闭的订单
     *
     * @param wxuserId the wxuser id
     * @param orderNo  the order no
     */
    void deleteOrderByOrderNoWxuserId(@Param("wxuserId") Long wxuserId, @Param("orderNo") String orderNo);

    /**
     * 查询指定导出订单
     *
     * @param orderIdList the order id list
     * @return list list
     */
    List<WechatOrderResult> selectExportOrder(@Param("orderIdList") List<Long> orderIdList);

    /**
     * 查询指定活动的成功订单
     *
     * @param activityId the activity id
     * @return list list
     */
    List<WechatOrderResult> selectByOrderPayOk(Long activityId);

    /**
     * 更新订单状态
     * orderIdList
     * closeTime
     * payStatu
     *
     * @param map the map
     */
    void updateOrderStatus(Map<String, Object> map);

    /**
     * 查询活动成交量,成交额,参与人数
     *
     * @param activityIdList the activity id list
     * @param appmodelId     the appmodel id
     * @return list list
     */
    List<ActivityTurnoverDTO> selectActivityTurnover(@Param("activityIdList") List<Long> activityIdList,
                                                     @Param("appmodelId") String appmodelId);

    /**
     * 根据id列表查询OrderResult
     *
     * @param orderIdList the order id list
     * @return list list
     */
    List<OrderResult> selectByIdList(@Param("orderIdList") List<Long> orderIdList);

    /**
     * 根据订单id查询发货单订单详情
     *
     * @param orderIds the order ids
     * @return list list
     */
    List<SendBillExcel> selectByorderIds(@Param("orderIds") List<Long> orderIds);

    /**
     * 根据条件模糊查找订单
     * v1.2版本
     *
     * @param searchOrderVo 订单搜索的参数对象
     * @param appmodelId    小程序模板id
     * @return PcOrderResult list
     */
    List<PcOrderResult> selectByConditionsV2(@Param("searchOrderVo") SearchOrderVoV2 searchOrderVo,
                                             @Param("appmodelId") String appmodelId);

    /**
     * 根据时间和状态筛选订单
     *
     * @param startDate     the start date
     * @param endDate       the end date
     * @param searchOrderVo the search order vo
     * @param appmodelId    the appmodel id
     * @return list list
     */
    List<PcOrderResult> selectByDateAndStatus(@Param("startDate") String startDate, @Param("endDate") String endDate,
                                              @Param("searchOrderVo") SearchOrderVoV2 searchOrderVo, @Param(
            "appmodelId") String appmodelId);

    /**
     * 根据id查询未付款订单
     *
     * @param stringList the string list
     * @return list list
     */
    List<Order> selectWaitPayOrderByIds(@Param("idList") List<Long> stringList);

    /**
     * Select close order list.
     *
     * @param appmodelId the appmodel id
     * @return the list
     */
    List<Order> selectCloseOrder(String appmodelId);

    /**
     * Select by order nos list.
     *
     * @param orderNos the order nos
     * @return the list
     */
    List<OrderResult> selectByOrderNos(@Param("orderNos") List<String> orderNos);

    /**
     * Select by id list pc list.
     *
     * @param orderIds the order ids
     * @return the list
     */
    List<PcOrderResult> selectByIdListPc(@Param("orderIds") List<Long> orderIds);


    /**
     * Select order by after sale order id list.
     *
     * @param afterSaleOrderId the after sale order id
     * @return the list
     */
    List<Order> selectOrderByAfterSaleOrderId(@Param("afterSaleOrderId") Long afterSaleOrderId);

    /**
     * Select workbench summary list.
     *
     * @param groupLeaderId the group leader id
     * @param startDate     the start date
     * @param endDate       the end date
     * @return the list
     */
    List<Order> selectWorkbenchSummary(@Param("groupLeaderId") String groupLeaderId,
                                       @Param("startDate") Date startDate, @Param("endDate") Date endDate);

    /**
     * 根据订单ID查询有售后进行的订单
     *
     * @param orderIds the order ids
     * @return list list
     */
    List<Order> selectWaitReceiptByOrderIds(@Param("orderIds") List<Long> orderIds);

    /**
     * 按照订单状态查询制定用户的订单
     *
     * @param wxuserId      the wxuser id
     * @param groupLeaderId the group leader id
     * @param status        the status
     * @return list list
     */
    List<OrderResult> selectByWxuserIdAndStatus(@Param("wxuserId") Long wxuserId, String groupLeaderId,
                                                @Param("status") Integer status);

    /**
     * 根据团长ID和状态查询订单
     *
     * @param groupLeaderId the group leader id
     * @param type          the type
     * @return list list
     */
    List<Order> selectByGroupLeaderIdAndStatus(@Param("groupLeaderId") String groupLeaderId,
                                               @Param("type") Integer type);

    /**
     * 根据团长ID查询团长待结算的订单
     *
     * @param groupLeaderId the group leader id
     * @return list list
     */
    List<NotSettlementCommissionResult> selectGroupLeaderNotSettlementOrder(@Param("groupLeaderId") String groupLeaderId);

    /**
     * Search pc order result by order ids list.
     *
     * @param appmodelId the appmodel id
     * @param orderIds   the order ids
     * @return the list
     */
    List<PcOrderResult> searchPcOrderResultByOrderIds(@Param("appmodelId") String appmodelId,
                                                      @Param("orderIds") List<Long> orderIds);

    /**
     * Select members by ids list.
     *
     * @param orderIds the order ids
     * @return the list
     */
    List<MembersResult> selectMembersByIds(@Param("orderIds") List<Long> orderIds);

    /**
     * Find by wait pay list.
     *
     * @return the list
     */
    List<Order> findByWaitPay();

    /**
     * 查询当前订单是否含有不能签收的状态
     *
     * @param orderIds the order ids
     * @return the list
     */
    List<Long> checkSendByAfterSaleOrder(@Param("orderIds") List<Long> orderIds);
}