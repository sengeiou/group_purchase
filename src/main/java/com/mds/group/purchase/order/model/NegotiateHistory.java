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

package com.mds.group.purchase.order.model;

import com.mds.group.purchase.user.model.Wxuser;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

/**
 * The type Negotiate history.
 *
 * @author pavawi
 */
@Getter
@Setter
@Table(name = "t_negotiate_history")
public class NegotiateHistory {
    /**
     * id
     */
    @Id
    @Column(name = "negotiate_history_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long negotiateHistoryId;

    /**
     * 用户ID
     */
    @Column(name = "user_id")
    private Long userId;

    /**
     * 用户名
     */
    @Column(name = "user_name")
    private String userName;

    /**
     * 用户头像
     */
    @Column(name = "user_icon")
    private String userIcon;

    /**
     * 操作
     */
    private String info;

    /**
     * 图片
     */
    private String image;

    /**
     * 创建时间
     */
    @Column(name = "create_time")
    private Date createTime;

    /**
     * 关联订单
     */
    @Column(name = "order_id")
    private Long orderId;

    @Column(name = "appmodel_id")
    private String appmodelId;

    @Column(name = "leader_apply")
    private Boolean leaderApply;

    /**
     * Seller create negotiate history.
     *
     * @param logo the logo
     * @return the negotiate history
     */
    public static NegotiateHistory sellerCreate(String logo) {
        NegotiateHistory negotiateHistory = new NegotiateHistory();
        negotiateHistory.setUserId(1L);
        negotiateHistory.setUserName("卖家");
        negotiateHistory.setUserIcon(logo);
        negotiateHistory.setCreateTime(new Date());
        return negotiateHistory;
    }

    /**
     * System create negotiate history.
     *
     * @param logo the logo
     * @return the negotiate history
     */
    public static NegotiateHistory systemCreate(String logo) {
        NegotiateHistory negotiateHistory = new NegotiateHistory();
        negotiateHistory.setUserId(0L);
        negotiateHistory.setUserName("系统");
        negotiateHistory.setUserIcon(logo);
        negotiateHistory.setCreateTime(new Date());
        return negotiateHistory;

    }

    /**
     * User create negotiate history.
     *
     * @param wxuser the wxuser
     * @return the negotiate history
     */
    public static NegotiateHistory userCreate(Wxuser wxuser) {
        NegotiateHistory negotiateHistory = new NegotiateHistory();
        negotiateHistory.setUserId(wxuser.getWxuserId());
        negotiateHistory.setUserName(wxuser.getWxuserName());
        negotiateHistory.setUserIcon(wxuser.getIcon());
        negotiateHistory.setCreateTime(new Date());
        return negotiateHistory;
    }


}