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
import com.mds.group.purchase.solitaire.model.SolitaireRecordSetting;

import java.util.List;


/**
 * The interface Solitaire record setting service.
 *
 * @author shuke
 * @date 2019 /05/16
 */
public interface SolitaireRecordSettingService extends Service<SolitaireRecordSetting> {

    /**
     * 批量更新接龙记录的最近删除时间
     * 当程序执行自动删除接龙记录时，会更新对应接龙设置里面的最近删除时间
     *
     * @param canDeleteAppmodelIds 小程序模板id
     * @param lastDeleteTime       最近删除时间  时间戳 单位ms
     */
    void updateBatchLastDeleteTime(List<String> canDeleteAppmodelIds, Long lastDeleteTime);
}
