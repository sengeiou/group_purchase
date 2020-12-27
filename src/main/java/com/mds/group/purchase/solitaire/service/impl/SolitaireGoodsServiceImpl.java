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

package com.mds.group.purchase.solitaire.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.mds.group.purchase.activity.model.Activity;
import com.mds.group.purchase.activity.model.ActivityGoods;
import com.mds.group.purchase.activity.result.ActGoodsInfoResult;
import com.mds.group.purchase.activity.service.ActivityGoodsService;
import com.mds.group.purchase.activity.service.ActivityService;
import com.mds.group.purchase.constant.ActivityConstant;
import com.mds.group.purchase.solitaire.service.SolitaireGoodsService;
import com.mds.group.purchase.utils.ActivityGoodsUtil;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * The type Solitaire goods service.
 *
 * @author pavawi
 */
@Service
public class SolitaireGoodsServiceImpl implements SolitaireGoodsService {

    @Resource
    private ActivityGoodsService activityGoodsService;
    @Resource
    private ActivityService activityService;
    @Resource
    private ActivityGoodsUtil activityGoodsUtil;

    @Override
    public List<ActGoodsInfoResult> getSolitaireGoodsList(String appmodelId, Long wxuserId) {
        List<ActGoodsInfoResult> actGoodsInfoResults = new ArrayList<>();
        /*获取已经开始的活动*/
        List<Activity> acts = activityService.findActs(appmodelId);
        List<Long> startActIds = acts.stream().filter(o -> ActivityConstant.ACTIVITY_STATUS_START.equals(o.getStatus()))
                .map(Activity::getActivityId).collect(Collectors.toList());
        if (CollectionUtil.isEmpty(startActIds)) {
            return actGoodsInfoResults;
        }
        /*根据活动id获取活动商品，筛选出接龙商品*/
        List<ActivityGoods> byActIds = activityGoodsService.findByActIds(startActIds);
        if (CollectionUtil.isNotEmpty(byActIds)) {
            byActIds = byActIds.stream()
                    .filter(o -> o.getJoinSolitaire() != null && o.getJoinSolitaire()).collect(Collectors.toList());
            actGoodsInfoResults = activityGoodsUtil.getResultByActGoodsList4Wx(byActIds, wxuserId, appmodelId);
        }
        return actGoodsInfoResults;
    }
}
