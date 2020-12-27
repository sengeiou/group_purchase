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
import org.springframework.validation.annotation.Validated;

import java.util.ArrayList;
import java.util.List;

/**
 * 活动参数类
 *
 * @author shuke
 * @date 2018 -12-4
 */
@Data
@Validated
public class ActivityVo {

    /**
     * 活动id
     */
    @ApiModelProperty(value = "活动id")
    private Long activityId;

    /**
     * 活动类型（1：秒杀 2拼团）
     */
    @ApiModelProperty(value = "活动类型（1：秒杀 2拼团）")
    private Integer activityType;

    /**
     * 活动名称
     */
    @ApiModelProperty(value = "活动名称")
    private String activityName;

    /**
     * 活动海报
     */
    @ApiModelProperty(value = "活动海报")
    private String activityPoster;

    /**
     * 预热时间（小时）
     */
    @ApiModelProperty(value = "预热时间（小时）")
    private String readyTime;

    /**
     * 开始时间
     */
    @ApiModelProperty(value = "开始时间")
    private String startTime;

    /**
     * 结束时间
     */
    @ApiModelProperty(value = "结束时间")
    private String endTime;

    /**
     * 预计提货时间
     */
    @ApiModelProperty(value = "预计提货时间")
    private String forecastReceiveTime;

    /**
     * 小程序模板id
     */
    @ApiModelProperty(value = "小程序模板id")
    private String appmodelId;

    /**
     * 活动商品集合
     */
    @ApiModelProperty(value = "活动商品集合")
    private List<ActivityGoodsVo> actGoodsList;

    /**
     * 发货单生成日期，格式为 yyyy-MM-dd或者 yyyy/MM/dd
     */
    @ApiModelProperty(value = "发货单生成日期，格式为 yyyy-MM-dd")
    private String sendBillGenerateDate;

    /**
     * 活动参数对象转换成活动对象
     *
     * @return Activity activity
     */
    public Activity voToAct() {
        Activity act = new Activity();
        if (this.activityId != null && this.activityId > 0) {
            act.setActivityId(this.activityId);
        }
        act.setActivityName(this.activityName);
        act.setActivityPoster(this.activityPoster);
        act.setAppmodelId(this.appmodelId);
        act.setActivityType(this.activityType);
        String startTime = this.startTime.replaceAll("T", " ").replaceAll("t", " ").replaceAll("z", "")
                .replaceAll("Z", "");
        String endTime = this.endTime.replaceAll("T", " ").replaceAll("t", " ").replaceAll("z", "").replaceAll("Z", "");
        String forecastReceiveTime = this.forecastReceiveTime.replaceAll("T", " ").replaceAll("t", " ")
                .replaceAll("z", "").replaceAll("Z", "");
        act.setStartTime(startTime);
        act.setEndTime(endTime);
        act.setReadyTime(readyTime);
        act.setForecastReceiveTime(forecastReceiveTime);
        return act;
    }

    /**
     * Gets act goods list.
     *
     * @return the act goods list
     */
    public List<ActivityGoods> getActGoodsList() {
        if (this.actGoodsList == null || this.actGoodsList.size() <= 0) {
            throw new GlobalException(CodeMsg.BIND_ERROR.fillArgs("活动商品列表不能为空"));
        }
        List<ActivityGoods> activityGoodsList = new ArrayList<>();
        int i = 1;
        for (ActivityGoodsVo activityGoodsVo : this.actGoodsList) {
            ActivityGoods goods = activityGoodsVo.voToActGoods();
            goods.setAppmodelId(this.appmodelId);
            goods.setActivityId(this.activityId);
            goods.setActivityType(activityType);
            goods.setSortPosition(i);
            activityGoodsList.add(goods);
            i++;
        }
        return activityGoodsList;
    }

}
