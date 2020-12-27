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

import com.mds.group.purchase.core.AbstractService;
import com.mds.group.purchase.solitaire.dao.SolitaireRecordSettingMapper;
import com.mds.group.purchase.solitaire.model.SolitaireRecordSetting;
import com.mds.group.purchase.solitaire.service.SolitaireRecordSettingService;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Condition;

import javax.annotation.Resource;
import java.util.List;


/**
 * The type Solitaire record setting service.
 *
 * @author shuke
 * @date 2019 /05/16
 */
@Service
public class SolitaireRecordSettingServiceImpl extends AbstractService<SolitaireRecordSetting> implements SolitaireRecordSettingService {
    @Resource
    private SolitaireRecordSettingMapper tSolitaireRecordSettingMapper;


    @Override
    public void updateBatchLastDeleteTime(List<String> canDeleteAppmodelIds, Long lastDeleteTime) {
        Condition condition = new Condition(SolitaireRecordSetting.class);
        condition.createCriteria().andIn("appmodelId", canDeleteAppmodelIds);
        SolitaireRecordSetting setting = new SolitaireRecordSetting();
        setting.setLastDeleteTime(lastDeleteTime);
        tSolitaireRecordSettingMapper.updateByConditionSelective(setting, condition);
    }
}
