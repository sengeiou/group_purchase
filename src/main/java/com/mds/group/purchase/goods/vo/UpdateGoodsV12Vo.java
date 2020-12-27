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

import com.mds.group.purchase.goods.model.Goods;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * The type Update goods v 12 vo.
 *
 * @author shuke
 * @since v1.2
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class UpdateGoodsV12Vo extends UpdateGoodsV119Vo {

    @ApiModelProperty(value = "商品主图视频链接")
    private String goodsVideoUrl;

    @Override
    public Goods resolveGoodsFromVo() {
        Goods goods = super.resolveGoodsFromVo();
        goods.setGoodsVideoUrl(goodsVideoUrl);
        return goods;
    }
}
