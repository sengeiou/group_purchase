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

import javax.persistence.*;

/**
 * The type Wxmp.
 *
 * @author pavawi
 */
@Table(name = "t_wxmp")
public class Wxmp {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 公众号appid
     */
    @Column(name = "mp_appid")
    private String mpAppid;

    /**
     * 公众号秘钥
     */
    @Column(name = "mp_secret")
    private String mpSecret;

    /**
     * 公众号aeskey
     */
    @Column(name = "mp_aeskey")
    private String mpAeskey;

    /**
     * 公众号token
     */
    @Column(name = "mp_token")
    private String mpToken;

    /**
     * 公众号绑定的小程序id
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
     * 获取公众号appid
     *
     * @return mp_appid - 公众号appid
     */
    public String getMpAppid() {
        return mpAppid;
    }

    /**
     * 设置公众号appid
     *
     * @param mpAppid 公众号appid
     */
    public void setMpAppid(String mpAppid) {
        this.mpAppid = mpAppid;
    }

    /**
     * 获取公众号秘钥
     *
     * @return mp_secret - 公众号秘钥
     */
    public String getMpSecret() {
        return mpSecret;
    }

    /**
     * 设置公众号秘钥
     *
     * @param mpSecret 公众号秘钥
     */
    public void setMpSecret(String mpSecret) {
        this.mpSecret = mpSecret;
    }

    /**
     * 获取公众号aeskey
     *
     * @return mp_aeskey - 公众号aeskey
     */
    public String getMpAeskey() {
        return mpAeskey;
    }

    /**
     * 设置公众号aeskey
     *
     * @param mpAeskey 公众号aeskey
     */
    public void setMpAeskey(String mpAeskey) {
        this.mpAeskey = mpAeskey;
    }

    /**
     * 获取公众号token
     *
     * @return mp_token - 公众号token
     */
    public String getMpToken() {
        return mpToken;
    }

    /**
     * 设置公众号token
     *
     * @param mpToken 公众号token
     */
    public void setMpToken(String mpToken) {
        this.mpToken = mpToken;
    }

    /**
     * 获取公众号绑定的小程序id
     *
     * @return appmodel_id - 公众号绑定的小程序id
     */
    public String getAppmodelId() {
        return appmodelId;
    }

    /**
     * 设置公众号绑定的小程序id
     *
     * @param appmodelId 公众号绑定的小程序id
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
}