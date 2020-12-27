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

package com.mds.group.purchase.goods.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 获取商品参数类
 *
 * @author shuke
 */
@Data
public class GetGoodsVo {

    @ApiModelProperty(value = "商品名称")
    private String goodsName;

    @ApiModelProperty(value = "商品状态(0--上架，1--下架（仓库中），2--已售完)")
    private Integer goodsStatus;

    @ApiModelProperty(value = "供应商id")
    private String providerId;

    @ApiModelProperty(value = "当前页码，默认为0")
    private Integer page;

    @ApiModelProperty(value = "每页显示数量，默认为0获取所有")
    private Integer size;

    @ApiModelProperty(value = "商品分类id，默认获取所有商品")
    private Long goodsClassId;


    /**
     * Get goods name string.
     *
     * @return the string
     */
    public String getGoodsName() {
        return goodsName == null || "".equals(goodsName) ? null : goodsName;
    }

    /**
     * Gets provider id.
     *
     * @return the provider id
     */
    public String getProviderId() {
        return providerId == null || "".equals(providerId) ? null : providerId;
    }

    /**
     * Gets page.
     *
     * @return the page
     */
    public Integer getPage() {
        return page == null ? 0 : page;
    }

    /**
     * Sets page.
     *
     * @param page the page
     */
    public void setPage(Integer page) {
        this.page = page;
    }

    /**
     * Gets size.
     *
     * @return the size
     */
    public Integer getSize() {
        return size == null ? 0 : size;
    }

    /**
     * Sets size.
     *
     * @param size the size
     */
    public void setSize(Integer size) {
        this.size = size;
    }

    private Integer pageStart;

    private Integer pageEnd;

    /**
     * Gets page start.
     *
     * @return the page start
     */
    public Integer getPageStart() {
        if (getPage() > 1) {
            return (getPage() - 1) * getSize();
        } else if (getPage() == 1) {
            return 0;
        } else {
            return null;
        }
    }

    /**
     * Gets page end.
     *
     * @return the page end
     */
    public Integer getPageEnd() {
        if (getPage() == 0 || getSize() == 0) {
            return null;
        } else {
            return getPage() * getSize();
        }
    }
}
