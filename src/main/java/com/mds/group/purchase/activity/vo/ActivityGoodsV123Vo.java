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
 * The type Activity goods v 123 vo.
 *
 * @author shuke
 * @date 2019 /5/17
 * @since v1.2.3
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ActivityGoodsV123Vo extends ActivityGoodsVo {
    /**
     * 活动价格
     */
    @ApiModelProperty(value = "活动价格")
    private BigDecimal activityPrice;

    /**
     * 是否参加接龙活动
     */
    @ApiModelProperty(value = "是否参加接龙活动")
    private Boolean joinSolitaire;

    @Override
    public ActivityGoods voToActGoods() {
        ActivityGoods activityGoods = super.voToActGoods();
        activityGoods.setActivityPrice(activityPrice);
        activityGoods.setJoinSolitaire(joinSolitaire == null ? false : joinSolitaire);
        return activityGoods;
    }
}
