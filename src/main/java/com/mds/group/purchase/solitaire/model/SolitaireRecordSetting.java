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

package com.mds.group.purchase.solitaire.model;

import com.mds.group.purchase.solitaire.vo.SolitaireSettingVo;

import javax.persistence.*;

/**
 * The type Solitaire record setting.
 *
 * @author pavawi
 */
@Table(name = "t_solitaire_record_setting")
public class SolitaireRecordSetting {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 自动删除接龙记录
     */
    @Column(name = "auto_delete_record")
    private Boolean autoDeleteRecord;

    /**
     * 接龙记录删除方式1:定时删除  2：按记录条数删除
     */
    @Column(name = "record_delete_method")
    private Integer recordDeleteMethod;

    /**
     * 上一次定时删除的时间 单位ms
     */
    @Column(name = "last_delete_time")
    private Long lastDeleteTime;

    /**
     * 定时删除时间 单位：天
     */
    @Column(name = "delete_time")
    private Integer deleteTime;

    /**
     * 按记录条数删除时，满足多少条记录时删除
     */
    @Column(name = "attain_record_count")
    private Integer attainRecordCount;

    /**
     * 自动删除多少条记录
     */
    @Column(name = "delete_count")
    private Integer deleteCount;

    /**
     * 小程序模板id
     */
    @Column(name = "appmodel_id")
    private String appmodelId;

    /**
     * 创建时间
     */
    @Column(name = "create_time")
    private Long createTime;

    /**
     * 修改时间
     */
    @Column(name = "modify_time")
    private Long modifyTime;

    /**
     * Gets id.
     *
     * @return id id
     */
    public Long getId() {
        return id;
    }

    /**
     * Sets id.
     *
     * @param id the id
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * 获取自动删除接龙记录
     *
     * @return auto_delete_record - 自动删除接龙记录
     */
    public Boolean getAutoDeleteRecord() {
        return autoDeleteRecord;
    }

    /**
     * 设置自动删除接龙记录
     *
     * @param autoDeleteRecord 自动删除接龙记录
     */
    public void setAutoDeleteRecord(Boolean autoDeleteRecord) {
        this.autoDeleteRecord = autoDeleteRecord;
    }

    /**
     * 获取接龙记录删除方式1:定时删除  2：按记录条数删除
     *
     * @return record_delete_method - 接龙记录删除方式1:定时删除  2：按记录条数删除
     */
    public Integer getRecordDeleteMethod() {
        return recordDeleteMethod;
    }

    /**
     * 设置接龙记录删除方式1:定时删除  2：按记录条数删除
     *
     * @param recordDeleteMethod 接龙记录删除方式1:定时删除  2：按记录条数删除
     */
    public void setRecordDeleteMethod(Integer recordDeleteMethod) {
        this.recordDeleteMethod = recordDeleteMethod;
    }

    /**
     * 获取定时删除时间 单位：天
     *
     * @return delete_time - 定时删除时间 单位：天
     */
    public Integer getDeleteTime() {
        return deleteTime;
    }

    /**
     * 设置定时删除时间 单位：天
     *
     * @param deleteTime 定时删除时间 单位：天
     */
    public void setDeleteTime(Integer deleteTime) {
        this.deleteTime = deleteTime;
    }

    /**
     * 获取按记录条数删除时，满足多少条记录时删除
     *
     * @return attain_record_count - 按记录条数删除时，满足多少条记录时删除
     */
    public Integer getAttainRecordCount() {
        return attainRecordCount;
    }

    /**
     * 设置按记录条数删除时，满足多少条记录时删除
     *
     * @param attainRecordCount 按记录条数删除时，满足多少条记录时删除
     */
    public void setAttainRecordCount(Integer attainRecordCount) {
        this.attainRecordCount = attainRecordCount;
    }

    /**
     * 获取自动删除多少条记录
     *
     * @return delete_count - 自动删除多少条记录
     */
    public Integer getDeleteCount() {
        return deleteCount;
    }

    /**
     * 设置自动删除多少条记录
     *
     * @param deleteCount 自动删除多少条记录
     */
    public void setDeleteCount(Integer deleteCount) {
        this.deleteCount = deleteCount;
    }

    /**
     * 获取小程序模板id
     *
     * @return appmodel_id - 小程序模板id
     */
    public String getAppmodelId() {
        return appmodelId;
    }

    /**
     * 设置小程序模板id
     *
     * @param appmodelId 小程序模板id
     */
    public void setAppmodelId(String appmodelId) {
        this.appmodelId = appmodelId;
    }

    /**
     * 获取创建时间
     *
     * @return create_time - 创建时间
     */
    public Long getCreateTime() {
        return createTime;
    }

    /**
     * 设置创建时间
     *
     * @param createTime 创建时间
     */
    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }

    /**
     * 获取修改时间
     *
     * @return modify_time - 修改时间
     */
    public Long getModifyTime() {
        return modifyTime;
    }

    /**
     * 设置修改时间
     *
     * @param modifyTime 修改时间
     */
    public void setModifyTime(Long modifyTime) {
        this.modifyTime = modifyTime;
    }

    /**
     * Gets last delete time.
     *
     * @return the last delete time
     */
    public Long getLastDeleteTime() {
        return lastDeleteTime;
    }

    /**
     * Sets last delete time.
     *
     * @param lastDeleteTime the last delete time
     */
    public void setLastDeleteTime(Long lastDeleteTime) {
        this.lastDeleteTime = lastDeleteTime;
    }

    /**
     * Fill from vo.
     *
     * @param vo the vo
     */
    public void fillFromVo(SolitaireSettingVo vo) {
        this.setAppmodelId(vo.getAppmodelId());
        this.setAttainRecordCount(vo.getAttainRecordCount());
        this.setAutoDeleteRecord(vo.getAutoDeleteRecord());
        this.setDeleteCount(vo.getDeleteCount());
        this.setRecordDeleteMethod(vo.getRecordDeleteMethod());
        this.setDeleteTime(vo.getDeleteTime());
    }
}