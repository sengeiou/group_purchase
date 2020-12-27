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

package com.mds.group.purchase.goods.result;

import com.mds.group.purchase.goods.model.GoodsClass;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 条件查询商品时的结果类
 *
 * @author Administrator
 * @date 2019 /3/21
 * @since v1
 */
@Data
public class GoodsFuzzyResult {

    @ApiModelProperty(value = "商品名称")
    private String goodsName;

    @ApiModelProperty(value = "商品标题")
    private String goodsTitle;

    @ApiModelProperty(value = "商品描述")
    private String goodsDesc;

    @ApiModelProperty(value = "供应商id")
    private String providerId;

    @ApiModelProperty(value = "供应商名称")
    private String providerName;

    @ApiModelProperty(value = "商品分类")
    private List<GoodsClass> goodsClass;

    @ApiModelProperty(value = "手动虚假销量")
    private Integer shamVolume;

    /**
     * 销量
     */
    @ApiModelProperty(value = "销量")
    private Integer salesVolume;

    @ApiModelProperty(value = "评分")
    private String score;

    /**
     * Gets score.
     *
     * @return the score
     */
    public String getScore() {
        String d = this.score;
        if (d == null) {
            d = "5.0";
        }
        BigDecimal b = new BigDecimal(d);
        d = b.setScale(1, BigDecimal.ROUND_HALF_UP).toString();
        return d;
    }

    /**
     * 创建时间
     */
    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    @ApiModelProperty(value = "市场价（划线价）")
    private BigDecimal markPrice;

    @ApiModelProperty(value = "销售价")
    private BigDecimal price;

    @ApiModelProperty(value = "商品图片地址，多个地址用逗号分隔")
    private String goodsImg;

    @ApiModelProperty(value = "商品主图视频url")
    private String goodsVideoUrl;

    @ApiModelProperty(value = "库存")
    private Integer stock;

    @ApiModelProperty(value = "商品属性，json字符串")
    private String goodsProperty;

    @ApiModelProperty(value = "保质期")
    private String expirationDate;

    @ApiModelProperty(value = "团长佣金")
    private BigDecimal groupLeaderCommission;

    @ApiModelProperty(value = "团长佣金类型（1：比率 2：固定金额）")
    private Integer commissionType;

    /**
     * The type Commission type.
     *
     * @author pavawi
     */
    public static class CommissionType {
        //佣金类型常量
        /**
         * 比率
         */
        public final static int RATE = 1;
        /**
         * 固定金额
         */
        public final static int FIXED_AMOUNT = 2;
    }

    @ApiModelProperty(value = "文本介绍")
    private String text;

    @ApiModelProperty(value = "小程序模板id")
    private String appmodelId;

    @ApiModelProperty(value = "商品状态，0为直接发布，1为发布至仓库")
    private Integer goodsStatus;

    @ApiModelProperty(value = "商品id")
    private Long goodsId;

    @ApiModelProperty(value = "删除标志")
    private Boolean goodsDelFlag;

    @ApiModelProperty(value = "该商品是否自动添加到新建小区")
    private boolean autoAdd;

    @ApiModelProperty(value = "已选投放区域的数量")
    private Integer selectedAreaNum;

    @ApiModelProperty(value = "投放区域总数")
    private Integer allAreaNum;

}
