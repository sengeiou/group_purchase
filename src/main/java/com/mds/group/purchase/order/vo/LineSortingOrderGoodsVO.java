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

package com.mds.group.purchase.order.vo;

import com.mds.group.purchase.order.model.LineSortingOrderGoodsDetail;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * The type Line sorting order goods vo.
 *
 * @author pavawi
 */
@Data
public class LineSortingOrderGoodsVO {


    @ApiModelProperty("商品图片")
    private String goodsImage;

    @ApiModelProperty("商品名称")
    private String goodsName;

    @ApiModelProperty("商品总数量")
    private Integer goodsSum;

    @ApiModelProperty("分拣商品小区详情")
    private List<LineSortingOrderGoodsDetail> lineSortingOrderDetailList;


}
