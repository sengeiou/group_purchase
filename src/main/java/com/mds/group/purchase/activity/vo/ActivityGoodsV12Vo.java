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
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 1.2版本新建活动时的活动商品参数，新增加属性 '活动价格'
 *
 * @author shuke
 * @since v1.2
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class ActivityGoodsV12Vo extends ActivityGoodsVo {

    /**
     * 活动价格
     */
    @ApiModelProperty(value = "活动价格")
    private BigDecimal activityPrice;

    @Override
    public ActivityGoods voToActGoods() {
        ActivityGoods activityGoods = super.voToActGoods();
        activityGoods.setActivityPrice(activityPrice);
        return activityGoods;
    }
}
