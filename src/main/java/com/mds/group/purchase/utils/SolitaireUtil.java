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

import com.alibaba.fastjson.JSONObject;
import com.mds.group.purchase.constant.ActiviMqQueueName;
import com.mds.group.purchase.jobhandler.ActiveDelaySendJobHandler;
import com.mds.group.purchase.order.result.OrderResult;
import com.mds.group.purchase.order.service.OrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * The type Solitaire util.
 *
 * @author pavawi
 */
@Component
public class SolitaireUtil {

    @Resource
    private ActiveDelaySendJobHandler activeDelaySendJobHandler;
    @Resource
    private OrderService orderService;

    private Logger logger = LoggerFactory.getLogger(SolitaireUtil.class);

    /**
     * 异步生成接龙记录 （v1.2.3新功能）
     *
     * @param orderNo    the order no
     * @param appmodelId the appmodel id
     */
    public void ascyGenerateSolitaireRecord(String orderNo, String appmodelId) {
        activeDelaySendJobHandler.savaTask(orderNo, ActiviMqQueueName.GENERATE_SOLITAIRE_RECORD, 0L, appmodelId, false);
    }

    /**
     * 异步发送接龙记录微信消息
     *
     * @param groupLeaderId         the group leader id
     * @param appmodelId            the appmodel id
     * @param noticeGroupLeaderFlag the notice group leader flag
     */
    public void ascySendSolitaireRecordMsg(String groupLeaderId, String appmodelId, Boolean noticeGroupLeaderFlag) {
        logger.info("noticeGroupLeaderFlag@@@@@@@@@@@" + noticeGroupLeaderFlag);
        if (noticeGroupLeaderFlag != null && noticeGroupLeaderFlag) {
            activeDelaySendJobHandler
                    .savaTask(groupLeaderId, ActiviMqQueueName.SOLITAIRE_RECORD_MSG, 0L, appmodelId, true);
        }
    }

    /**
     * 发货时异步发送@购买者的微信消息
     *
     * @param orderIds the order ids
     */
    public void ascySendAtWxuserMsg(List<Long> orderIds) {
        List<OrderResult> orderResults = orderService.findOrderResultByOrderIds(orderIds);
        //筛选出接龙订单
        orderResults = orderResults.stream().filter(o -> o.getIsSolitaireOrder() != null && o.getIsSolitaireOrder())
                .collect(Collectors.toList());
        for (OrderResult orderResult : orderResults) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("groupLeaderId", orderResult.getGroupId());
            jsonObject.put("appmodelId", orderResult.getAppmodelId());
            jsonObject.put("buyerName", orderResult.getWxuserName());
            jsonObject.put("goodsName", orderResult.getGoodsName());
            jsonObject.put("groupLeaderName", orderResult.getGroupLeaderName());
            activeDelaySendJobHandler.savaTask(jsonObject.toJSONString(), ActiviMqQueueName.SOLITAIRE_AT_MSG, 0L,
                    orderResult.getAppmodelId(), false);
        }

    }

    /**
     * 异步增加购买过的人数数量 （v1.2.3新功能）
     *
     * @param wxuserId   the wxuser id
     * @param appmodelId the appmodel id
     */
    public void ascyUpdatePayedUserNum(Long wxuserId, String appmodelId) {
        activeDelaySendJobHandler
                .savaTask(wxuserId.toString(), ActiviMqQueueName.UPDATE_PAYED_USER_NUM, 0L, appmodelId, true);
    }
}
