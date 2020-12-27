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

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.util.Date;

/**
 * The type Comment.
 *
 * @author Administrator
 */
@Table(name = "t_comment")
@Data
public class Comment {
    /**
     * 评论id
     */
    @Id
    @Column(name = "comment_id")
    private Long commentId;

    /**
     * 订单编号
     */
    @Column(name = "order_no")
    private String orderNo;

    /**
     * 评论商品内容
     */
    @Column(name = "comment_content_goods")
    private String commentContentGoods;

    /**
     * 供应商id
     */
    @Column(name = "provider_id")
    private String providerId;


    /**
     * 评论团长内容
     */
    @Column(name = "comment_content_group")
    private String commentContentGroup;

    /**
     * 团长id
     */
    @Column(name = "group_leader_id")
    private String groupLeaderId;

    /**
     * 团长名称
     */
    @Column(name = "group_leader_name")
    private String groupLeaderName;

    /**
     * 团长头像
     */
    @Column(name = "group_leader_icon")
    private String groupLeaderIcon;

    /**
     * 商品id
     */
    @Column(name = "goods_id")
    private Long goodsId;

    /**
     * 活动商品id
     */
    @Column(name = "act_goods_id")
    private Long actGoodsId;

    /**
     * 商品名称
     */
    @Column(name = "goods_name")
    private String goodsName;

    /**
     * 商品图片
     */
    @Column(name = "goods_img")
    private String goodsImg;

    /**
     * 商品数量
     */
    @Column(name = "goods_number")
    private Integer goodsNumber;

    /**
     * 商品价格
     */
    @Column(name = "goods_price")
    private BigDecimal goodsPrice;

    /**
     * 评论者id
     */
    @Column(name = "comment_user")
    private String commentUser;

    /**
     * 评论时间
     */
    @Column(name = "comment_time")
    private Date commentTime;

    /**
     * 商品分数
     */
    @Column(name = "goods_score")
    private Double goodsScore;

    /**
     * 团长分数
     */
    @Column(name = "group_score")
    private Double groupScore;

    /**
     * 逻辑删除标识(1:删除，0：存在)
     */
    @Column(name = "del_flag")
    private Boolean delFlag;

    /**
     * 小程序模块id
     */
    @Column(name = "appmodel_id")
    private String appmodelId;

}