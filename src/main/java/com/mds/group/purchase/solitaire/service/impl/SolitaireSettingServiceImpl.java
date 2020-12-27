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
import com.mds.group.purchase.solitaire.dao.SolitaireSettingMapper;
import com.mds.group.purchase.solitaire.model.SolitaireRecordSetting;
import com.mds.group.purchase.solitaire.model.SolitaireSetting;
import com.mds.group.purchase.solitaire.service.SolitaireRecordSettingService;
import com.mds.group.purchase.solitaire.service.SolitaireSettingService;
import com.mds.group.purchase.solitaire.vo.SolitaireSettingVo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Condition;

import javax.annotation.Resource;


/**
 * The type Solitaire setting service.
 *
 * @author shuke
 * @date 2019 /05/16
 */
@Service
public class SolitaireSettingServiceImpl extends AbstractService<SolitaireSetting> implements SolitaireSettingService {
    @Resource
    private SolitaireSettingMapper tSolitaireSettingMapper;
    @Resource
    private SolitaireRecordSettingService recordSettingService;

    @Override
    public int openSolitaire(String appmodelId) {
        int res;
        //1·查询接龙设置是否存在
        SolitaireSetting setting = findBy("appmodelId", appmodelId);
        if (null == setting) {
            //2·还不存在接龙设置，插入一条新纪录
            setting = new SolitaireSetting();
            setting.setAppmodelId(appmodelId);
            setting.setCreateTime(System.currentTimeMillis());
            setting.setStatus(SolitaireSetting.Status.OPEN);
            res = save(setting);
        } else {
            //3·已存在则修改开启状态
            setting.setAppmodelId(appmodelId);
            setting.setStatus(SolitaireSetting.Status.OPEN);
            setting.setModifyTime(System.currentTimeMillis());
            res = update(setting);
        }
        return res;
    }

    @Override
    public int closeSolitaire(String appmodelId) {
        int res;
        //1·查询接龙设置是否存在
        SolitaireSetting setting = findBy("appmodelId", appmodelId);
        if (null == setting) {
            res = 0;
        } else {
            setting.setModifyTime(System.currentTimeMillis());
            setting.setStatus(SolitaireSetting.Status.CLOSE);
            res = update(setting);
        }
        return res;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void setSolitaireInfo(SolitaireSettingVo solitaireSettingVo) {
        Condition condition = new Condition(SolitaireSetting.class);
        condition.createCriteria().andEqualTo("appmodelId", solitaireSettingVo.getAppmodelId());
        SolitaireSetting setting = solitaireSettingVo.voToSetting();
        setting.setModifyTime(System.currentTimeMillis());
        SolitaireSetting solitaireSetting = findByOneCondition(condition);
        if (null == solitaireSetting) {
            setting.setCreateTime(System.currentTimeMillis());
            save(setting);
        } else {
            tSolitaireSettingMapper.updateByConditionSelective(setting, condition);
        }
        //
        SolitaireRecordSetting recordSetting = recordSettingService
                .findBy("appmodelId", solitaireSettingVo.getAppmodelId());
        if (null == recordSetting) {
            recordSetting = solitaireSettingVo.voToRecordSetting();
            recordSetting.setCreateTime(System.currentTimeMillis());
            recordSetting.setLastDeleteTime(System.currentTimeMillis());
            recordSettingService.save(recordSetting);
        } else {
            recordSetting.fillFromVo(solitaireSettingVo);
            recordSetting.setModifyTime(System.currentTimeMillis());
            recordSettingService.update(recordSetting);
        }
    }

    @Override
    public SolitaireSettingVo getSolitaireInfo(String appmodelId) {
        SolitaireSetting setting = findBy("appmodelId", appmodelId);
        SolitaireRecordSetting recordSetting = recordSettingService.findBy("appmodelId", appmodelId);
        return null == setting ? new SolitaireSettingVo() : new SolitaireSettingVo(setting, recordSetting);
    }
}
