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

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;


/**
 * The type Save comment vo.
 *
 * @author pavawi
 */
@Data
public class SaveCommentVo {

    /**
     * 订单编号
     */
    @NotNull
    @ApiModelProperty("订单编号")
    private String orderNo;

    /**
     * 评论团长的内容
     */
    @NotNull
    @ApiModelProperty("评论团长的内容")
    private String commentContent4Group;

    /**
     * 评论商品的内容
     */
    @NotNull
    @ApiModelProperty("评论商品的内容")
    private String commentContent4Goods;

//	/**
//	 * 团长id
//	 */
//	@NotNull
//	@ApiModelProperty("团长id")
//	private String groupLeaderId;
//
//	/**
//	 * 活动商品id
//	 */
//	@NotNull
//	@ApiModelProperty("活动商品id")
//	private Long goodsId;
//
//	/**
//	 * 评论者openid
//	 */
//	@NotNull
//	@ApiModelProperty("评论者id")
//	private Long wxuserId;

    /**
     * 团长分数
     */
    @NotNull
    @ApiModelProperty("团长分数")
    private Double groupScore;

    /**
     * 商品分数
     */
    @NotNull
    @ApiModelProperty("商品分数")
    private Double goodsScore;

    /**
     * 小程序模块id
     */
    @ApiModelProperty("小程序模块id")
    private String appmodelId;
}
