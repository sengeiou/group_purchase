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

package com.mds.group.purchase.activity.result;

import cn.hutool.core.date.DateUtil;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * 首页秒杀商品结果对象
 *
 * @author shuke
 * @date 2018 -12-20
 */
@Data
public class IndexSeckillResult implements Comparable<IndexSeckillResult> {

    @ApiModelProperty("结束时间")
    private String endTime;

    @ApiModelProperty("活动id")
    private Long activityId;

    @ApiModelProperty("开始时间")
    private String startTime;

    @ApiModelProperty("活动类型，1秒杀 2拼团")
    private Long activityType;

    @ApiModelProperty("活动名称")
    private String activityName;

    @ApiModelProperty("活动海报")
    private String activityPoster;

    @ApiModelProperty("活动状态（0:未开始 1：预热中 2：进行中 3：已经结束）")
    private Integer activityStatus;

    @ApiModelProperty("活动商品")
    private List<ActGoodsInfoResult> actGoodsInfoResults;

    @Override
    public int compareTo(@NotNull IndexSeckillResult o) {
        if (StringUtils.isNotBlank(this.startTime) && StringUtils.isNotBlank(o.getStartTime())) {
            return DateUtil.parse(this.startTime).compareTo(DateUtil.parse(o.getStartTime()));
        }else {
            return 0;
        }

    }
}
