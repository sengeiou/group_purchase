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

package com.mds.group.purchase.solitaire.vo;

import com.mds.group.purchase.solitaire.model.SolitaireRecordSetting;
import com.mds.group.purchase.solitaire.model.SolitaireSetting;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

/**
 * The type Solitaire setting vo.
 *
 * @author pavawi
 */
@Data
public class SolitaireSettingVo {

    /**
     * 状态 0 关闭  1：开启
     */
    @ApiModelProperty("状态 0 关闭  1：开启")
    private Integer status;

    /**
     * 接龙活动名称
     */
    @ApiModelProperty("接龙活动名称")
    private String name;

    /**
     * 接龙活动描述
     */
    @ApiModelProperty("接龙活动描述")
    private String desc;

    /**
     * 接龙活动海报
     */
    @ApiModelProperty("接龙活动海报")
    private String postUrl;

    /**
     * 活动提醒描述
     */
    @ApiModelProperty("活动提醒描述")
    private String notify;
    /**
     * 自动删除接龙记录
     */
    @ApiModelProperty("是否自动删除接龙记录")
    private Boolean autoDeleteRecord;
    /**
     * 接龙记录删除方式1:定时删除  2：按记录条数删除
     */
    @ApiModelProperty("接龙记录删除方式1:定时删除  2：按记录条数删除")
    private Integer recordDeleteMethod;
    /**
     * 定时删除时间 单位：天
     */
    @ApiModelProperty("定时删除时间 单位：天")
    private Integer deleteTime;
    /**
     * 按记录条数删除时，满足多少条记录时删除
     */
    @ApiModelProperty("按记录条数删除时，满足多少条记录时删除")
    private Integer attainRecordCount;
    /**
     * 自动删除多少条记录
     */
    @ApiModelProperty("自动删除多少条记录")
    private Integer deleteCount;
    /**
     * 小程序模板id
     */
    @ApiModelProperty("小程序模板id")
    private String appmodelId;

    /**
     * Instantiates a new Solitaire setting vo.
     */
    public SolitaireSettingVo() {
    }

    /**
     * Instantiates a new Solitaire setting vo.
     *
     * @param setting the setting
     * @param po      the po
     */
    public SolitaireSettingVo(SolitaireSetting setting, SolitaireRecordSetting po) {
        if (setting != null) {
            this.appmodelId = setting.getAppmodelId();
            this.status = setting.getStatus();
            this.desc = setting.getSolitaireDesc();
            this.name = setting.getName();
            this.notify = setting.getNotify();
            this.postUrl = setting.getPostUrl();
        }
        if (po != null) {
            this.autoDeleteRecord = po.getAutoDeleteRecord();
            this.recordDeleteMethod = po.getRecordDeleteMethod();
            this.deleteTime = po.getDeleteTime();
            this.attainRecordCount = po.getAttainRecordCount();
            this.deleteCount = po.getDeleteCount();
            this.appmodelId = po.getAppmodelId();
        }
    }

    /**
     * Gets auto delete record.
     *
     * @return the auto delete record
     */
    public Boolean getAutoDeleteRecord() {
        return autoDeleteRecord == null ? false : autoDeleteRecord;
    }

    /**
     * Vo to record setting solitaire record setting.
     *
     * @return the solitaire record setting
     */
    public SolitaireRecordSetting voToRecordSetting() {
        SolitaireRecordSetting setting = new SolitaireRecordSetting();
        setting.setAppmodelId(appmodelId);
        setting.setAttainRecordCount(attainRecordCount);
        setting.setAutoDeleteRecord(autoDeleteRecord);
        setting.setDeleteCount(deleteCount);
        setting.setRecordDeleteMethod(recordDeleteMethod);
        setting.setDeleteTime(deleteTime);
        return setting;
    }

    /**
     * Vo to setting solitaire setting.
     *
     * @return the solitaire setting
     */
    public SolitaireSetting voToSetting() {
        SolitaireSetting setting = new SolitaireSetting();
        setting.setAppmodelId(appmodelId);
        setting.setSolitaireDesc(StringUtils.isNotBlank(desc) ? desc : null);
        setting.setName(StringUtils.isNotBlank(name) ? name : null);
        setting.setNotify(StringUtils.isNotBlank(notify) ? notify : null);
        setting.setPostUrl(StringUtils.isNotBlank(postUrl) ? postUrl : null);
        return setting;
    }
}
