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

package com.mds.group.purchase.solitaire.model;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.mds.group.purchase.order.model.Order;
import com.mds.group.purchase.order.model.OrderDetail;
import com.mds.group.purchase.user.model.Wxuser;
import lombok.Data;
import org.jetbrains.annotations.NotNull;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * The type Solitaire record.
 *
 * @author pavawi
 */
@Table(name = "t_solitaire_record")
@Data
public class SolitaireRecord implements Comparable<SolitaireRecord> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 订单编号
     */
    @Column(name = "order_no")
    private String orderNo;

    /**
     * 创建时间
     */
    @Column(name = "create_time")
    private Long createTime;

    /**
     * 是否通知团长接单（true：立即向微信群发送自己的购买记录 false不发送）
     */
    @Column(name = "notice_group_leader_flag")
    private Boolean noticeGroupLeaderFlag;

    /**
     * 记录详情
     */
    @Column(name = "record_detail")
    private String recordDetail;

    /**
     * 买家名称
     */
    @Column(name = "buyer_id")
    private Long buyerId;

    /**
     * 买家名称
     */
    @Column(name = "buyer_name")
    private String buyerName;

    /**
     * 买家头像
     */
    @Column(name = "buyer_icon")
    private String buyerIcon;

    /**
     * 团长id
     */
    @Column(name = "group_leader_id")
    private String groupLeaderId;

    /**
     * 提货地址
     */
    @Column(name = "group_address")
    private String groupAddress;

    /**
     * 交易额
     */
    @Column(name = "total_price")
    private BigDecimal totalPrice;

    /**
     * 小程序模板id
     */
    @Column(name = "appmodel_id")
    private String appmodelId;

    /**
     * Instantiates a new Solitaire record.
     */
    public SolitaireRecord() {
    }

    /**
     * Instantiates a new Solitaire record.
     *
     * @param orders       the orders
     * @param orderDetails the order details
     * @param wxuser       the wxuser
     */
    public SolitaireRecord(List<Order> orders, List<OrderDetail> orderDetails, Wxuser wxuser) {
        this.appmodelId = wxuser.getAppmodelId();
        this.buyerId = wxuser.getWxuserId();
        this.buyerIcon = wxuser.getIcon();
        this.buyerName = wxuser.getWxuserName();
        this.createTime = System.currentTimeMillis();
        this.orderNo = orders.get(0).getOrderNo();
        this.groupAddress = orders.get(0).getPickupLocation();
        this.groupLeaderId = orders.get(0).getGroupId();
        this.noticeGroupLeaderFlag = orders.get(0).getNoticeGroupLeaderFlag();
        BigDecimal totalFee = BigDecimal.valueOf(0);
        for (Order order : orders) {
            totalFee = totalFee.add(order.getPayFee());
        }
        Map<Long, Order> orderMap = orders.stream().collect(Collectors.toMap(k -> k.getOrderId(), v -> v));
        this.totalPrice = totalFee;
        JSONArray jsonArray = new JSONArray();
        for (OrderDetail orderDetail : orderDetails) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("goodsIcon", orderDetail.getGoodsImg());
            jsonObject.put("goodsName", orderDetail.getGoodsName());
            jsonObject.put("goodsNum", orderDetail.getGoodsNum());
            jsonObject.put("goodsPrice", orderDetail.getGoodsPrice());
            jsonObject.put("activityPrice", orderMap.get(orderDetail.getOrderId()).getPayFee()
                    .divide(BigDecimal.valueOf(orderDetail.getGoodsNum()), 2, BigDecimal.ROUND_HALF_UP));
            jsonArray.add(jsonObject);
        }
        this.recordDetail = jsonArray.toJSONString();
    }

    @Override
    public int compareTo(@NotNull SolitaireRecord o) {
        if (this.getCreateTime().equals(o.createTime)) {
            return 0;
        } else {
            return this.getCreateTime() > o.createTime ? 1 : -1;
        }

    }
}