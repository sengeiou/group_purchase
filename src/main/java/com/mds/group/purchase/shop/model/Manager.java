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

package com.mds.group.purchase.shop.model;

import lombok.Data;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

/**
 * The type Manager.
 *
 * @author pavawi
 */
@Table(name = "t_manager")
@Data
public class Manager {
    /**
     * 编号
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * 用户模板ID
     */
    @Column(name = "appmodel_id")
    private String appmodelId;


    /**
     * 创建用户时间
     */
    @Column(name = "create_time")
    private String createTime;


    /**
     * 是否开启企业支付
     */
    @Column(name = "enterprise_pay_state")
    private Boolean enterprisePayState;


    @Column(name = "app_id")
    private String appId;

    @Column(name = "mch_id")
    private String mchId;

    @Column(name = "mch_key")
    private String mchKey;

    /**
     * 商户支付证书路径
     */
    @Column(name = "certificate_path")
    private String certificatePath;

    /**
     * 小程序名称
     */
    @Column(name = "mini_name")
    private String miniName;


    /**
     * 小程序二维码
     */
    @Column(name = "mini_code")
    private String miniCode;

    /**
     * 删除标志
     */
    @Column(name = "del_state")
    private Integer delState;

    /**
     * 小程序log
     */
    private String logo;

    //  以下数据暂时无用


    /**
     * 是否已经到期通知
     */
    @Column(name = "expiry_date_notify")
    private Long expiryDateNotify;

    /**
     * 版本下标
     */
    @Column(name = "version_subscript")
    private String versionSubscript;

    /**
     * 余额
     */
    private BigDecimal bpavawice;

    /**
     * 是否缴纳保证金
     */
    private Boolean flag;

    /**
     * 版本 1-基础班,2-标准版,3-营销版
     */
    private Integer version;
    /**
     * 截止日期
     */
    @Column(name = "expiry_date")
    private Date expiryDate;

}