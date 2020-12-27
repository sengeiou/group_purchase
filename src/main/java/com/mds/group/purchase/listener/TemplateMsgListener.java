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

package com.mds.group.purchase.listener;

import cn.hutool.core.date.DateUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.mds.group.purchase.activity.result.ActGoodsInfoResult;
import com.mds.group.purchase.activity.service.ActivityGoodsService;
import com.mds.group.purchase.configurer.WxServiceUtils;
import com.mds.group.purchase.constant.ActiviMqQueueName;
import com.mds.group.purchase.constant.Url;
import com.mds.group.purchase.exception.ServiceException;
import com.mds.group.purchase.jobhandler.ActiveDelaySendJobHandler;
import com.mds.group.purchase.logistics.model.Community;
import com.mds.group.purchase.logistics.service.CommunityService;
import com.mds.group.purchase.order.model.Order;
import com.mds.group.purchase.order.model.OrderDetail;
import com.mds.group.purchase.order.service.OrderDetailService;
import com.mds.group.purchase.shop.model.Manager;
import com.mds.group.purchase.shop.service.ManagerService;
import com.mds.group.purchase.user.model.Wxuser;
import com.mds.group.purchase.user.service.WxuserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * The type Template msg listener.
 *
 * @author shuke
 */
@Component
public class TemplateMsgListener {

    @Resource
    private WxuserService wxuserService;
    @Resource
    private WxServiceUtils wxServiceUtils;
    @Resource
    private CommunityService communityService;
    @Resource
    private ManagerService managerService;
    @Resource
    private OrderDetailService orderDetailService;
    @Resource
    private ActivityGoodsService activityGoodsService;
    @Resource
    private ActiveDelaySendJobHandler activeDelaySendJobHandler;

    /**
     * Send template msg.
     *
     * @param jsonData the json data
     */
    @JmsListener(destination = ActiviMqQueueName.ORDER_MINIPROGRAM_TEMPLATE_MESSAGE)
    public void sendTemplateMsg(String jsonData) {
        JSONObject jsonObject = JSON.parseObject(jsonData);
        Integer type = jsonObject.getInteger("type");
        switch (type) {
            //AT0463 客户预约通知
            case 1:
                sendActivityStartNotify(jsonObject);
                break;
            //AT1410 订单关闭通知
            case 2:
                sendOrderCloseNotify(jsonObject);
                break;
            //AT0146 团长申请审核结果通知
            case 101:
                sendGroupLeaderApplyNotify(jsonObject);
                break;
            //AT0008 待付款通知
            case 3:
                waitPayNotify(jsonObject);
                break;
            //AT0787 退款成功通知
            case 4:
                refundSuccessNotify(jsonObject);
                break;
            //AT0007 订单发货提醒
            case 5:
                orderSendNotify(jsonObject);
                break;
            //AT0146 供应商申请审核结果通知
            case 6:
                sendProviderApplyNotify(jsonObject);
                break;
            //AT0830 提现到账通知
            case 7:
                withdrawSuccessNotify(jsonObject);
                break;
            //AT1242 提现失败通知
            case 8:
                withdrawFailNotify(jsonObject);
                break;
            //提货通知
            case 9:
                sendOrderArriveNotify(jsonObject);
                break;
            //退款拒绝通知
            case 102:
                sendRefundFailNotify(jsonObject);
                break;
            default:
        }
        String activeMqTaskTOId = jsonObject.getString("activeMqTaskTOId");
        if (StringUtils.isNotBlank(activeMqTaskTOId)) {
            activeDelaySendJobHandler.updateState(activeMqTaskTOId);
        }
    }

    /**
     * 订单关闭通知
     */
    private void sendOrderCloseNotify(JSONObject jsonObject) {
        Order order = jsonObject.getObject("order", Order.class);
        OrderDetail orderDetail = orderDetailService.findBy("orderId", order.getOrderId());
        List<String> keyWords = new ArrayList<>();
        keyWords.add(order.getOrderNo());
        keyWords.add(orderDetail.getGoodsName());
        keyWords.add(order.getPayFee().toString());
        keyWords.add(DateUtil.formatDateTime(order.getCreateTime()));
        keyWords.add("订单关闭");
        String authorizerAppid = order.getAppmodelId().substring(9);
        String templateId = getTemplateId(authorizerAppid, "AT1410");
        Wxuser wxuser = wxuserService.findById(order.getWxuserId());
        wxServiceUtils
                .platSendMiniProgramTemplateMessage(keyWords, templateId, wxuser.getMiniOpenId(), order.getFormId(),
                        authorizerAppid);
    }

    /**
     * 提货通知
     *
     * @param jsonObject
     */
    private void sendOrderArriveNotify(JSONObject jsonObject) {
        String appmodelId = jsonObject.getString("appmodelId");
        String formId = jsonObject.getString("formId");
        String wxuserId = jsonObject.getString("wxuserId");
        String orderNo = jsonObject.getString("orderNo");
        String goodsName = jsonObject.getString("goodsName");
        String pickupStation = jsonObject.getString("pickupStation");
        String phone = jsonObject.getString("phone");
        String pickupLocation = jsonObject.getString("pickupLocation");
        String businessHours = jsonObject.getString("businessHours");
        Wxuser wxuser = wxuserService.findById(wxuserId);
        List<String> keyWords = new ArrayList<>();
        keyWords.add(orderNo);
        keyWords.add(goodsName);
        keyWords.add(pickupStation);
        keyWords.add(phone);
        keyWords.add(pickupLocation);
        keyWords.add(businessHours);
        String authorizerAppid = appmodelId.substring(9);
        String templateId = getTemplateId(authorizerAppid, "AT0837");
        wxServiceUtils.platSendMiniProgramTemplateMessage(keyWords, templateId, wxuser.getMiniOpenId(), formId,
                authorizerAppid);
    }

    /**
     * 退款被拒绝
     * @param jsonObject
     */
    private void sendRefundFailNotify(JSONObject jsonObject) {
        String appmodelId = jsonObject.getString("appmodelId");
        String formId = jsonObject.getString("formId");
        String wxuserId = jsonObject.getString("wxuserId");
        String afterSaleOrderNo = jsonObject.getString("afterSaleOrderNo");
        String goodsName = jsonObject.getString("goodsName");
        String refundFee = jsonObject.getString("refundFee");
        String refundReason = jsonObject.getString("refundReason");
        Wxuser wxuser = wxuserService.findById(wxuserId);
        List<String> keyWords = new ArrayList<>();
        keyWords.add(afterSaleOrderNo);
        keyWords.add(goodsName);
        keyWords.add(refundFee);
        keyWords.add(refundReason);
        String authorizerAppid = appmodelId.substring(9);
        String templateId = getTemplateId(authorizerAppid, "AT1983");
        wxServiceUtils.platSendMiniProgramTemplateMessage(keyWords, templateId, wxuser.getMiniOpenId(), formId,
                authorizerAppid);
    }


    /**
     * 活动预约提醒
     */
    private void sendActivityStartNotify(JSONObject jsonObject) {
        String goodsName = jsonObject.getString("goodsName");
        String formId = jsonObject.getString("formId");
        Long actGoodsId = jsonObject.getLong("actGoodsId");
        String appmodelId = jsonObject.getString("appmodelId");
        String authorizerAppid = appmodelId.substring(9);
        Long wxuserId = jsonObject.getLong("wxuserId");
        Wxuser wxuser = wxuserService.findById(wxuserId);
        ActGoodsInfoResult actGoodsById = activityGoodsService.getActGoodsById(actGoodsId, appmodelId);
        List<String> keyWords = new ArrayList<>();
        keyWords.add(actGoodsById.getActivityName() +" 的 "+ goodsName);
        keyWords.add(actGoodsById.getActStartTDate());
        keyWords.add("活动即将开始，请前往小程序查看详情!");
        String templateId = getTemplateId(authorizerAppid, "AT0104");
        wxServiceUtils.platSendMiniProgramTemplateMessage(keyWords, templateId, wxuser.getMiniOpenId(), formId,
                authorizerAppid);
    }

    /**
     * 供应商审核通知
     */
    private void sendProviderApplyNotify(JSONObject jsonObject) {
        String formId = jsonObject.getString("formId");
        String appmodelId = jsonObject.getString("appmodelId");
        String applyResult = jsonObject.getString("applyResult");
        String time = jsonObject.getString("time");
        String authorizerAppid = appmodelId.substring(9);
        Long wxuserId = jsonObject.getLong("wxuserId");
        Wxuser wxuser = wxuserService.findById(wxuserId);
        String userName = "";
        if (wxuser != null) {
            userName = wxuser.getWxuserName();
        }
        String miniName = "";
        Manager byAppmodelId = managerService.findByAppmodelId(appmodelId);
        if (byAppmodelId != null) {
            miniName = byAppmodelId.getMiniName();
        }
        List<String> keyWords = new ArrayList<>();
        keyWords.add(userName);
        keyWords.add("申请成为 " + miniName + " 的供应商");
        keyWords.add(time);
        keyWords.add(applyResult);
        String templateId = getTemplateId(authorizerAppid, "AT0146");
        if (!StringUtils.isNotBlank(templateId)) {
            throw new ServiceException("模板id未生成");
        }
        assert wxuser != null;
        wxServiceUtils.platSendMiniProgramTemplateMessage(keyWords, templateId, wxuser.getMiniOpenId(), formId,
                authorizerAppid);
    }

    /**
     * 待付款提醒
     */
    private void waitPayNotify(JSONObject jsonObject){
        String formId = jsonObject.getString("formId");
        String appmodelId = jsonObject.getString("appmodelId");
        String orderNo = jsonObject.getString("orderNo");
        String goodsName = jsonObject.getString("goodsName");
        String payFee = jsonObject.getString("payFee");
        String time = jsonObject.getString("time");
        String authorizerAppid = appmodelId.substring(9);
        Long wxuserId = jsonObject.getLong("wxuserId");
        Wxuser wxuser = wxuserService.findById(wxuserId);
        List<String> keyWords = new ArrayList<>();
        keyWords.add(orderNo);
        keyWords.add(goodsName);
        keyWords.add(payFee);
        keyWords.add(time);
        String templateId = getTemplateId(authorizerAppid, "AT0008");
        if (!StringUtils.isNotBlank(templateId)) {
            throw new ServiceException("模板id未生成");
        }
        assert wxuser != null;
        wxServiceUtils.platSendMiniProgramTemplateMessage(keyWords, templateId, wxuser.getMiniOpenId(), formId,
                authorizerAppid);
    }


    /**
     * 退款成功通知
     */
    private void refundSuccessNotify(JSONObject jsonObject){
        String formId = jsonObject.getString("formId");
        String appmodelId = jsonObject.getString("appmodelId");
        String refundNo = jsonObject.getString("refundNo");
        String goodsName = jsonObject.getString("goodsName");
        String payFee = jsonObject.getString("payFee");
        String authorizerAppid = appmodelId.substring(9);
        Long wxuserId = jsonObject.getLong("wxuserId");
        Wxuser wxuser = wxuserService.findById(wxuserId);
        List<String> keyWords = new ArrayList<>();
        keyWords.add(refundNo);
        keyWords.add(goodsName);
        keyWords.add(payFee);
        keyWords.add("退回到零钱");
        keyWords.add("退款已经原路返回，具体到账时间可能会有1-3天延迟");
        String templateId = getTemplateId(authorizerAppid, "AT0787");
        if (!StringUtils.isNotBlank(templateId)) {
            throw new ServiceException("模板id未生成");
        }
        assert wxuser != null;
        wxServiceUtils.platSendMiniProgramTemplateMessage(keyWords, templateId, wxuser.getMiniOpenId(), formId,
                authorizerAppid);
    }

    /**
     * 订单发货提醒
     */
    private void orderSendNotify(JSONObject jsonObject){
        String formId = jsonObject.getString("formId");
        String appmodelId = jsonObject.getString("appmodelId");
        String orderNo = jsonObject.getString("orderNo");
        String goodsName = jsonObject.getString("goodsName");
        String time = jsonObject.getString("time");
        String authorizerAppid = appmodelId.substring(9);
        Long wxuserId = jsonObject.getLong("wxuserId");
        Wxuser wxuser = wxuserService.findById(wxuserId);
        List<String> keyWords = new ArrayList<>();
        keyWords.add(orderNo);
        keyWords.add(goodsName);
        keyWords.add(time);
        String templateId = getTemplateId(authorizerAppid, "AT0007");
        if (!StringUtils.isNotBlank(templateId)) {
            throw new ServiceException("模板id未生成");
        }
        assert wxuser != null;
        wxServiceUtils.platSendMiniProgramTemplateMessage(keyWords, templateId, wxuser.getMiniOpenId(), formId,
                authorizerAppid);
    }

    /**
     * 提现到账通知
     */
    private void withdrawSuccessNotify(JSONObject jsonObject){
        String formId = jsonObject.getString("formId");
        String appmodelId = jsonObject.getString("appmodelId");
        //提现申请时间
        String time = jsonObject.getString("time");
        String withdrawAccount = jsonObject.getString("withdrawAccount");
        String incomeAccount = jsonObject.getString("incomeAccount");
        String authorizerAppid = appmodelId.substring(9);
        Long wxuserId = jsonObject.getLong("wxuserId");
        Wxuser wxuser = wxuserService.findById(wxuserId);
        List<String> keyWords = new ArrayList<>();
        keyWords.add(time);
        keyWords.add(withdrawAccount);
        keyWords.add(incomeAccount);
        keyWords.add("你申请的团长佣金提现已到账，请注意查收");

        String templateId = getTemplateId(authorizerAppid, "AT0830");
        if (!StringUtils.isNotBlank(templateId)) {
            throw new ServiceException("模板id未生成");
        }
        assert wxuser != null;
        wxServiceUtils.platSendMiniProgramTemplateMessage(keyWords, templateId, wxuser.getMiniOpenId(), formId,
                authorizerAppid);
    }

    /**
     * 提现失败通知
     */
    private void withdrawFailNotify(JSONObject jsonObject){
        String formId = jsonObject.getString("formId");
        String appmodelId = jsonObject.getString("appmodelId");
        //提现申请时间
        String time = jsonObject.getString("time");
        String withdrawAccount = jsonObject.getString("withdrawAccount");
        String authorizerAppid = appmodelId.substring(9);
        Long wxuserId = jsonObject.getLong("wxuserId");
        Wxuser wxuser = wxuserService.findById(wxuserId);
        List<String> keyWords = new ArrayList<>();
        keyWords.add(time);
        keyWords.add(withdrawAccount);
        keyWords.add("你申请的团长佣金提现被拒绝，请联系商家处理！");

        String templateId = getTemplateId(authorizerAppid, "AT1242");
        if (!StringUtils.isNotBlank(templateId)) {
            throw new ServiceException("模板id未生成");
        }
        assert wxuser != null;
        wxServiceUtils.platSendMiniProgramTemplateMessage(keyWords, templateId, wxuser.getMiniOpenId(), formId,
                authorizerAppid);
    }

    private void sendGroupLeaderApplyNotify(JSONObject jsonObject) {
        String formId = jsonObject.getString("formId");
        Long communityId = jsonObject.getLong("communityId");
        String appmodelId = jsonObject.getString("appmodelId");
        String applyResult = jsonObject.getString("applyResult");
        String time = jsonObject.getString("time");
        String authorizerAppid = appmodelId.substring(9);
        Long wxuserId = jsonObject.getLong("wxuserId");
        Wxuser wxuser = wxuserService.findById(wxuserId);
        String userName = "";
        if (wxuser != null) {
            userName = wxuser.getWxuserName();
        }
        Community community = communityService.getCommunityById(communityId);
        String communityName = "";
        if (community != null) {
            communityName = community.getCommunityName();
        }
        List<String> keyWords = new ArrayList<>();
        keyWords.add(userName);
        keyWords.add("申请成为 " + communityName + " 的团长");
        keyWords.add(time);
        keyWords.add(applyResult);
        String templateId = getTemplateId(authorizerAppid, "AT0146");
        if (!StringUtils.isNotBlank(templateId)) {
            throw new ServiceException("模板id未生成");
        }
        assert wxuser != null;
        wxServiceUtils.platSendMiniProgramTemplateMessage(keyWords, templateId, wxuser.getMiniOpenId(), formId,
                authorizerAppid);
    }

    private String getTemplateId(String authorizerAppid, String titleId) {
        String url = Url.GET_TEMPLATID_URL.concat("?authorizerAppid=").concat(authorizerAppid).concat("&titleId=")
                .concat(titleId);
        HttpResponse execute = HttpRequest.get(url).execute();
        return execute.body();
    }
}
