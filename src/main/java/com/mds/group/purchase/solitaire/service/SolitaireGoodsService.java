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

import com.mds.group.purchase.activity.result.ActGoodsInfoResult;

import java.util.List;

/**
 * The interface Solitaire goods service.
 *
 * @author pavawi
 */
public interface SolitaireGoodsService {

    /**
     * 获取接龙的商品信息
     *
     * @param appmodelId the appmodel id
     * @param wxuserId   the wxuser id
     * @return the solitaire goods list
     */
    List<ActGoodsInfoResult> getSolitaireGoodsList(String appmodelId, Long wxuserId);
}
