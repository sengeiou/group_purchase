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

import com.mds.group.purchase.constant.enums.AfterSaleOrderType;
import com.mds.group.purchase.core.AbstractService;
import com.mds.group.purchase.order.dao.NegotiateHistoryMapper;
import com.mds.group.purchase.order.model.AfterSaleOrder;
import com.mds.group.purchase.order.model.NegotiateHistory;
import com.mds.group.purchase.order.model.Order;
import com.mds.group.purchase.order.service.NegotiateHistoryService;
import com.mds.group.purchase.order.vo.ApplyAfterSaleOrderVO;
import com.mds.group.purchase.order.vo.BaseApplyAfterSaleOrderVO;
import com.mds.group.purchase.user.model.GroupLeader;
import com.mds.group.purchase.user.model.Wxuser;
import com.mds.group.purchase.user.service.GroupLeaderService;
import com.mds.group.purchase.user.service.WxuserService;
import com.mds.group.purchase.utils.MerchantUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;


/**
 * The type Negotiate history service.
 *
 * @author pavawi
 */
@Service
public class NegotiateHistoryServiceImpl extends AbstractService<NegotiateHistory> implements NegotiateHistoryService {
    @Resource
    private WxuserService wxuserService;
    @Resource
    private MerchantUtil merchantUtil;
    @Resource
    private GroupLeaderService groupLeaderService;
    @Resource
    private NegotiateHistoryMapper negotiateHistoryMapper;


    @Override
    public void init(Order order, BaseApplyAfterSaleOrderVO baseApplyAfterSaleOrderVO, String user) {
        Wxuser wxuser = wxuserService.findById(order.getWxuserId());
        NegotiateHistory negotiateHistory = NegotiateHistory.userCreate(wxuser);
        negotiateHistory.setOrderId(order.getOrderId());
        negotiateHistory.setAppmodelId(order.getAppmodelId());
        negotiateHistory.setLeaderApply(AfterSaleOrderType.isLeader(baseApplyAfterSaleOrderVO.getAfterSaleType()));
        StringBuffer content = new StringBuffer(String.format("%s发起了%s申请", user,
                AfterSaleOrderType.getDesc(baseApplyAfterSaleOrderVO.getAfterSaleType())));
        if (StringUtils.isNotBlank(baseApplyAfterSaleOrderVO.getReason())) {
            content.append(String.format(",原因：%s", baseApplyAfterSaleOrderVO.getReason()));
        }
        if (baseApplyAfterSaleOrderVO.getAfterSaleType() == AfterSaleOrderType.团长退款.getValue()
                || baseApplyAfterSaleOrderVO.getAfterSaleType() == AfterSaleOrderType.退款.getValue()) {
            content.append(String.format(",金额：￥%s ", baseApplyAfterSaleOrderVO.getRefundFee()));
        } else {
            content.append(String.format(",数量：%s ", baseApplyAfterSaleOrderVO.getApplicationNum()));
        }
        if (StringUtils.isNotBlank(baseApplyAfterSaleOrderVO.getDescription())) {
            content.append(String.format(",说明：%s ", baseApplyAfterSaleOrderVO.getDescription()));
        }
        if (StringUtils.isNotBlank(baseApplyAfterSaleOrderVO.getImages())) {
            content.append(",凭证:");
            negotiateHistory.setImage(baseApplyAfterSaleOrderVO.getImages());
        }
        negotiateHistory.setInfo(content.toString());
        this.save(negotiateHistory);
    }

    @Override
    public void sellerRefusal(Order order, AfterSaleOrder afterSaleOrder) {
        NegotiateHistory negotiateHistory = NegotiateHistory
                .sellerCreate(merchantUtil.getMiniLogo(order.getAppmodelId()));
        StringBuffer content;
        content = new StringBuffer(
                String.format("卖家拒绝%s申请,%s失败,本次售后关闭,", AfterSaleOrderType.getDesc(afterSaleOrder.getAfterSaleType()),
                        AfterSaleOrderType.getDesc(afterSaleOrder.getAfterSaleType())));
        content.append(String.format("拒绝原因：%s", afterSaleOrder.getRefusalReason()));
        fillAndSave(order, negotiateHistory, content, AfterSaleOrderType.isLeader(afterSaleOrder.getAfterSaleType()));

    }

    @Override
    public void sellerApprove(Order order, AfterSaleOrder afterSaleOrder, Boolean isSystem) {
        NegotiateHistory negotiateHistory = NegotiateHistory
                .sellerCreate(merchantUtil.getMiniLogo(order.getAppmodelId()));

        StringBuffer content = new StringBuffer();
        if (isSystem) {
            content.append("卖家超时未操作，系统自动确认");
        }
        content = new StringBuffer(
                String.format("卖家已同意你的%s申请,等待卖家发货", AfterSaleOrderType.getDesc(afterSaleOrder.getAfterSaleType()),
                        AfterSaleOrderType.getDesc(afterSaleOrder.getAfterSaleType())));
        fillAndSave(order, negotiateHistory, content, AfterSaleOrderType.isLeader(afterSaleOrder.getAfterSaleType()));
    }

    @Override
    public void exchangeEnd(Order order, AfterSaleOrder afterSaleOrder) {
        GroupLeader groupLeader = groupLeaderService.findByWxuserId(order.getWxuserId());
        Wxuser wxuser = wxuserService.findById(groupLeader.getWxuserId());
        NegotiateHistory negotiateHistory = NegotiateHistory.userCreate(wxuser);
        StringBuffer content = new StringBuffer("换货单团长已经签收，本次售后结束");
        fillAndSave(order, negotiateHistory, content, AfterSaleOrderType.isLeader(afterSaleOrder.getAfterSaleType()));
    }

    @Override
    public void sellerApproveNeedReturn(Order order, AfterSaleOrder afterSaleOrder, Boolean isSystem) {
        NegotiateHistory negotiateHistory = NegotiateHistory
                .sellerCreate(merchantUtil.getMiniLogo(order.getAppmodelId()));

        StringBuffer content = new StringBuffer();
        if (isSystem) {
            content.append("卖家超时未操作，系统自动确认");
        }
        content.append(
                String.format("卖家已同意你的%s申请,请退货至团长处", AfterSaleOrderType.getDesc(afterSaleOrder.getAfterSaleType())));
        content.append(String.format(",退货地址：%s", order.getPickupLocation()));
        content.append(String.format(",请及时退货以免%s超时关闭", AfterSaleOrderType.getDesc(afterSaleOrder.getAfterSaleType())));
        fillAndSave(order, negotiateHistory, content, AfterSaleOrderType.isLeader(afterSaleOrder.getAfterSaleType()));
    }

    @Override
    public void refund(AfterSaleOrder afterSaleOrder, Order order, Boolean isSystem) {
        NegotiateHistory negotiateHistory = NegotiateHistory
                .systemCreate(merchantUtil.getMiniLogo(order.getAppmodelId()));
        StringBuffer content = new StringBuffer();
        if (isSystem) {
            content.append("团长超时未操作，系统自动确认");
        }
        content.append(String.format("系统自动退款，退款金额：￥%s，退款成功", order.getRefundFee()));
        fillAndSave(order, negotiateHistory, content, AfterSaleOrderType.isLeader(afterSaleOrder.getAfterSaleType()));
    }

    @Override
    public void userReturn(Order order) {
        Wxuser wxuser = wxuserService.findById(order.getWxuserId());
        NegotiateHistory negotiateHistory = NegotiateHistory.userCreate(wxuser);
        StringBuffer content = new StringBuffer("买家已退货至团长处，待团长确认收到退货");
        fillAndSave(order, negotiateHistory, content, false);
    }

    @Override
    public void leaderApprove(Order order, Boolean isSystem) {
        GroupLeader groupLeader = groupLeaderService.findByWxuserId(order.getWxuserId());
        Wxuser wxuser = wxuserService.findById(groupLeader.getWxuserId());
        NegotiateHistory negotiateHistory = NegotiateHistory.userCreate(wxuser);
        StringBuffer content = new StringBuffer();
        if (isSystem) {
            content.append("团长超时未操作，系统自动确认");
        }
        content.append(String.format("团长已收到你的退货商品"));
        fillAndSave(order, negotiateHistory, content, false);
    }

    @Override
    public void leaderRefusal(Order order, AfterSaleOrder afterSaleOrder) {
        GroupLeader groupLeader = groupLeaderService.findByWxuserId(order.getWxuserId());
        Wxuser wxuser = wxuserService.findById(groupLeader.getWxuserId());
        NegotiateHistory negotiateHistory = NegotiateHistory.userCreate(wxuser);
        StringBuffer content = new StringBuffer(String.format("团长未收到买家退货商品，%s失败，本次售后关闭",
                AfterSaleOrderType.getDesc(afterSaleOrder.getAfterSaleType())));
        fillAndSave(order, negotiateHistory, content, AfterSaleOrderType.isLeader(afterSaleOrder.getAfterSaleType()));
    }

    @Override
    public List<NegotiateHistory> findByOrderId(Long orderId, String type) {
        return negotiateHistoryMapper.selectByOrderId(orderId, type);
    }

    /**
     * 拒绝收货
     *
     * @param order
     * @param applyAfterSaleOrderVO
     */
    @Override
    public void refuseConfirm(Order order, ApplyAfterSaleOrderVO applyAfterSaleOrderVO) {
        Wxuser wxuser = wxuserService.findById(order.getWxuserId());
        NegotiateHistory negotiateHistory = NegotiateHistory.userCreate(wxuser);
        StringBuffer content = new StringBuffer();
        content.append("换货商品买家拒绝收货，换货失败，售后关闭");
        fillAndSave(order, negotiateHistory, content,
                AfterSaleOrderType.isLeader(applyAfterSaleOrderVO.getAfterSaleType()));
    }

    @Override
    public void cancel(Order order, AfterSaleOrder afterSaleOrder, Boolean isSystem, int type) {
        Wxuser wxuser;
        if (type == 1) {
            wxuser = wxuserService.findById(order.getWxuserId());
        } else {
            wxuser = wxuserService.findById(afterSaleOrder.getWxuserId());
        }
        NegotiateHistory negotiateHistory = NegotiateHistory.userCreate(wxuser);
        StringBuffer content = new StringBuffer();
        if (isSystem) {
            content.append(String.format("由于买家超时未退货，%s失败，本次售后关闭",
                    AfterSaleOrderType.getDesc(afterSaleOrder.getAfterSaleType())));
        } else {
            content.append(String.format("%s主动撤销了本次%s申请，本次售后关闭", type == 1 ? "买家" : "团长",
                    AfterSaleOrderType.getDesc(afterSaleOrder.getAfterSaleType())));
        }
        fillAndSave(order, negotiateHistory, content, AfterSaleOrderType.isLeader(afterSaleOrder.getAfterSaleType()));

    }

    @Override
    public void exchangeSend(Order order, AfterSaleOrder afterSaleOrder) {
        NegotiateHistory negotiateHistory =
                NegotiateHistory.systemCreate(merchantUtil.getMiniLogo(order.getAppmodelId()));
        StringBuffer content = new StringBuffer();
        content.append(String.format("卖家已经发出新货，正在配送中请耐心等待\n" +
                "收货地址:%s", order.getPickupLocation()));
        fillAndSave(order, negotiateHistory, content, AfterSaleOrderType.isLeader(afterSaleOrder.getAfterSaleType()));
    }

    @Override
    public void exchangeReceipt(Order order, AfterSaleOrder afterSaleOrder) {
        Wxuser wxuser = wxuserService.findById(order.getWxuserId());
        NegotiateHistory negotiateHistory = NegotiateHistory.userCreate(wxuser);
        StringBuffer content = new StringBuffer();
        content.append("换货商品买家已确认收货，换货完成");
        fillAndSave(order, negotiateHistory, content, AfterSaleOrderType.isLeader(afterSaleOrder.getAfterSaleType()));

    }

    /**
     * Fill and save.
     *
     * @param order            the order
     * @param negotiateHistory the negotiate history
     * @param content          the content
     * @param leaderApply      the leader apply
     */
    public void fillAndSave(Order order, NegotiateHistory negotiateHistory, StringBuffer content, boolean leaderApply) {
        negotiateHistory.setLeaderApply(leaderApply);
        negotiateHistory.setInfo(content.toString());
        negotiateHistory.setAppmodelId(order.getAppmodelId());
        negotiateHistory.setOrderId(order.getOrderId());
        this.save(negotiateHistory);
    }
}
