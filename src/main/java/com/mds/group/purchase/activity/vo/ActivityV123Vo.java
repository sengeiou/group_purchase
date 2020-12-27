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

import com.mds.group.purchase.activity.model.Activity;
import com.mds.group.purchase.activity.model.ActivityGoods;
import com.mds.group.purchase.core.CodeMsg;
import com.mds.group.purchase.exception.GlobalException;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;

/**
 * The type Activity v 123 vo.
 *
 * @author pavawi
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class ActivityV123Vo extends ActivityVo {

    @ApiModelProperty(value = "活动商品是否自动按销量排序，否则按照手动排序")
    private Boolean actGoodsAutoSort;

    /**
     * 活动商品集合
     * @since v1.2.3
     */
    @ApiModelProperty(value = "活动商品集合")
    private List<ActivityGoodsV123Vo> actGoodsV123List;

    @Override
    public Activity voToAct() {
        Activity activity = super.voToAct();
        activity.setActGoodsAutoSort(actGoodsAutoSort);
        return activity;
    }

    @Override
    public List<ActivityGoods> getActGoodsList() {
        if (this.actGoodsV123List == null || this.actGoodsV123List.size() <= 0) {
            throw new GlobalException(CodeMsg.BIND_ERROR.fillArgs("活动商品列表不能为空"));
        }
        List<ActivityGoods> activityGoodsList = new ArrayList<>();
        int i = 1;
        for (ActivityGoodsV123Vo activityGoodsVo : this.actGoodsV123List) {
            ActivityGoods goods = activityGoodsVo.voToActGoods();
            goods.setAppmodelId(getAppmodelId());
            goods.setActivityId(getActivityId());
            goods.setActivityType(getActivityType());
            goods.setSortPosition(i);
            activityGoodsList.add(goods);
            i++;
        }
        return activityGoodsList;
    }
}
