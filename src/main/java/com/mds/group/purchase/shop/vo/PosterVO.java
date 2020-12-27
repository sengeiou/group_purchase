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

package com.mds.group.purchase.shop.vo;

import com.mds.group.purchase.goods.model.Goods;
import com.mds.group.purchase.shop.model.Poster;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * The type Poster vo.
 *
 * @author pavawi
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class PosterVO extends Poster {

    @ApiModelProperty(value = "商品信息,前端判断根据delete/shelfState判断商品是否下架")
    private Goods goodsInfo;
}
