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

package com.mds.group.purchase.order.vo;

import com.mds.group.purchase.constant.OrderConstant;
import com.mds.group.purchase.order.model.Order;
import com.mds.group.purchase.order.model.OrderDetail;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 生成订单的参数对象类
 *
 * @author shuke
 * @date 2018 -12-8
 */
@Data
public class SolitaireOrderVo {

    @NotNull
    @ApiModelProperty("用户的id")
    private Long wxuserId;

    @NotNull
    @ApiModelProperty("活动商品id和数量的集合")
    private List<Map<String, String>> actGoodsIdsAndNums;

    @NotNull
    @ApiModelProperty("收货人信息id")
    private Long consigneeId;

    @ApiModelProperty("用户留言")
    private String userDesc;

    @ApiModelProperty("是否通知团长接单（true：立即向微信群发送自己的购买记录 false不发送）")
    private Boolean noticeGroupLeaderFlag;

    @ApiModelProperty("小程序模板id，此处可不填")
    private String appmodelId;

    /**
     * Vo 2 order order.
     *
     * @return the order
     */
    public Order vo2Order() {
        Order order = new Order();
        order.setWxuserId(wxuserId);
        order.setUserDesc(userDesc);
        order.setNoticeGroupLeaderFlag(noticeGroupLeaderFlag);
        order.setAppmodelId(appmodelId);
        order.setOrderType(OrderConstant.SOLITAIRE_ORDER);
        return order;
    }

    /**
     * Vo 2 order detail list.
     *
     * @return the list
     */
    public List<OrderDetail> vo2OrderDetail() {
        List<OrderDetail> ods = new ArrayList<>();
        actGoodsIdsAndNums.forEach(o -> {
            String actGoodsId = o.get("actGoodsId");
            String actGoodsNum = o.get("actGoodsNum");
            OrderDetail od = new OrderDetail();
            od.setWxuserId(wxuserId);
            od.setAppmodelId(appmodelId);
            od.setActGoodsId(Long.valueOf(actGoodsId));
            od.setGoodsNum(Integer.valueOf(actGoodsNum));
            ods.add(od);
        });
        return ods;
    }

}
