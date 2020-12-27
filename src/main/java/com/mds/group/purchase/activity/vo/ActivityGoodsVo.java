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

package com.mds.group.purchase.activity.vo;

import com.mds.group.purchase.activity.model.ActivityGoods;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotNull;

/**
 * 活动商品参数类
 *
 * @author shuke
 * @date 2018 -12-4
 */
@Validated
@Data
@Deprecated
public class ActivityGoodsVo {

    /**
     * 活动商品id
     */
    @ApiModelProperty(value = "活动商品id")
    private Long activityGoodsId;

    /**
     * 商品id
     */
    @NotNull
    @ApiModelProperty(value = "商品id")
    private Long goodsId;

    /**
     * 活动id
     */
    @ApiModelProperty(value = "活动id")
    private Long activityId;

    /**
     * 活动折扣
     */
    @ApiModelProperty(value = "活动折扣")
    private Double activityDiscount;

    /**
     * 活动库存
     */
    @NotNull
    @ApiModelProperty(value = "活动库存")
    private Integer activityStock;

    /**
     * 限购
     */
    @ApiModelProperty(value = "限购")
    private Integer maxQuantity;

    /**
     * 是否在主页显示
     */
    @NotNull
    @ApiModelProperty(value = "是否在主页显示")
    private Boolean indexDisplay;

    /**
     * 活动商品参数对象转换成活动商品对象
     *
     * @return ActivityGoods activity goods
     */
    public ActivityGoods voToActGoods() {
        ActivityGoods actGoods = new ActivityGoods();
        if (this.activityGoodsId != null) {
            actGoods.setActivityGoodsId(this.activityGoodsId);
        }
        if (this.activityId != null) {
            actGoods.setActivityId(this.activityId);
        }
        actGoods.setActivityDiscount(this.activityDiscount);
        actGoods.setGoodsId(this.goodsId);
        actGoods.setActivityStock(this.activityStock);
        if (maxQuantity != null) {
            actGoods.setMaxQuantity(this.maxQuantity);
        }
        actGoods.setIndexDisplay(this.indexDisplay==null?Boolean.FALSE:indexDisplay);
        return actGoods;
    }
}
