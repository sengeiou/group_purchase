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
import com.mds.group.purchase.solitaire.model.SolitaireSetting;
import com.mds.group.purchase.solitaire.vo.SolitaireSettingVo;


/**
 * The interface Solitaire setting service.
 *
 * @author shuke
 * @date 2019 /05/16
 */
public interface SolitaireSettingService extends Service<SolitaireSetting> {

    /**
     * 开启对应小程序的接龙
     *
     * @param appmodelId 小程序模板id
     * @return 记录更新个数 1表示开启成功 0表示未成功
     */
    int openSolitaire(String appmodelId);

    /**
     * 关闭对应小程序的接龙
     *
     * @param appmodelId 小程序模板id
     * @return 记录更新个数 1表示关闭成功 0表示未成功
     */
    int closeSolitaire(String appmodelId);

    /**
     * 设置接龙相关参数
     *
     * @param solitaireSettingVo 设置参数
     * @return 记录更新个数 1表示设置成功 0表示未成功
     */
    void setSolitaireInfo(SolitaireSettingVo solitaireSettingVo);

    /**
     * 获取相关小程序的接龙设置信息
     *
     * @param appmodelId 小程序模板id
     * @return SolitaireSettingVo solitaire info
     */
    SolitaireSettingVo getSolitaireInfo(String appmodelId);
}
