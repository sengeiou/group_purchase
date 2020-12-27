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

package com.mds.group.purchase.logistics.vo;

import com.mds.group.purchase.logistics.model.GoodsAreaMapping;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * The type Goods area search vo.
 *
 * @author pavawi
 */
@Data
public class GoodsAreaSearchVo {

    @ApiModelProperty("商品名称")
    private String goodsName;

    @ApiModelProperty("商品投放区域名称")
    private String goodsAreaName;

    @ApiModelProperty("页码")
    private Integer page;

    @ApiModelProperty("页面大小")
    private Integer size;

    @ApiModelProperty("小程序模板id")
    private String appmodelId;

    /**
     * Sets goods name.
     *
     * @param s the s
     */
    public void setGoodsName(String s) {
        goodsName = "".equalsIgnoreCase(s) ? null : s;
    }

    /**
     * Sets goods area name.
     *
     * @param s the s
     */
    public void setGoodsAreaName(String s) {
        goodsAreaName = "".equalsIgnoreCase(s) ? null : s;
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
     * Gets size.
     *
     * @return the size
     */
    public Integer getSize() {
        return size == null ? 0 : size;
    }

    /**
     * Vo to po goods area mapping.
     *
     * @return the goods area mapping
     */
    public GoodsAreaMapping voToPo() {
        GoodsAreaMapping goodsAreaMapping = new GoodsAreaMapping();
        goodsAreaMapping.setAppmodelId(appmodelId);
        goodsAreaMapping.setGoodsAreaName(goodsAreaName);
        goodsAreaMapping.setGoodsName(goodsName);
        return goodsAreaMapping;
    }
}
