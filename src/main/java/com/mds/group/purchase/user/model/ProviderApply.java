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
 * The type Provider apply.
 *
 * @author pavawi
 */
@Data
@Table(name = "t_provider_apply")
public class ProviderApply {

    /**
     * 供应商申请表
     */
    @Id
    @Column(name = "provider_apply_id")
    private String providerApplyid;

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
     * 产品类目
     */
    @Column(name = "goods_class")
    private String goodsClass;

    /**
     * 申请时间
     */
    @Column(name = "apply_time")
    private Date applyTime;

    /**
     * 申请时间审核状态 0-待审核   1-已通过  2-已拒绝
     */
    @Column(name = "apply_state")
    private Integer applyState;

    /**
     * 小程序模板id
     */
    @Column(name = "appmodel_id")
    private String appmodelId;

}