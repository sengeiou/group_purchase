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

package com.mds.group.purchase.solitaire.service;

import com.mds.group.purchase.core.Service;
import com.mds.group.purchase.solitaire.model.SolitaireRecord;
import com.mds.group.purchase.solitaire.result.PrivateSolitaireRecord;

import java.util.List;
import java.util.Map;


/**
 * The interface Solitaire record service.
 *
 * @author shuke
 * @date 2019 /05/16
 */
public interface SolitaireRecordService extends Service<SolitaireRecord> {

    /**
     * 付款成功时新增订单对应的接龙记录
     * 需要判断订单的活动商品是否参加接龙活动
     * 参加的生成记录，未参加的不做处理
     *
     * @param orderNo 订单编号
     */
    void addRecord(String orderNo);

    /**
     * 根据appmodelId删除对应的接龙记录
     *
     * @param canDeleteAppmodelIds appmodelId集合
     */
    void deleteByAppmodelIds(List<String> canDeleteAppmodelIds);

    /**
     * 获取用户购买记录
     *
     * @param appmodelId 小程序模板id
     * @param buyerId    用户wxuserid
     * @return 购买记录 list
     */
    List<PrivateSolitaireRecord> findUserBuyRecord(String appmodelId, String buyerId);

    /**
     * 获取用户统计数据
     *
     * @param appmodelId the appmodel id
     * @return person count
     */
    Map<String, String> getPersonCount(String appmodelId);
}
