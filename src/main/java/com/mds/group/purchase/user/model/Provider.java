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

package com.mds.group.purchase.user.model;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * The type Provider.
 *
 * @author pavawi
 */
@Data
@Table(name = "t_provider")
public class Provider {

    /**
     * 供应商id
     */
    @Id
    @Column(name = "provider_id")
    private String providerId;


    /**
     * 供应商名称
     */
    @Column(name = "provider_name")
    private String providerName;

    /**
     * 供应商电话
     */
    @Column(name = "provider_phone")
    private String providerPhone;

    /**
     * 供应商区域
     */
    @Column(name = "provider_area")
    private String providerArea;

    /**
     * 供应商地址
     */
    @Column(name = "provider_address")
    private String providerAddress;

    /**
     * 产品类目
     */
    @Column(name = "goods_class")
    private String goodsClass;


    /**
     * 团长状态（0：禁用，1：正常）
     */
    @Column(name = "provider_status")
    private Integer providerStatus;

    /**
     * 审核状态 0-待审核   1-已通过  2-已拒绝
     */
    @Column(name = "apply_state")
    private Integer applyState;


    /**
     * 备注
     */
    private String remark;

    /**
     * 注册时间
     */
    @Column(name = "create_time")
    private Date createTime;

    /**
     * 删除状态
     */
    @Column(name = "delete_state")
    private Boolean deleteState;

    /**
     * 小程序模板id
     */
    @Column(name = "appmodel_id")
    private String appmodelId;

    @Column(name = "wxuser_id")
    private String wxuserId;

}