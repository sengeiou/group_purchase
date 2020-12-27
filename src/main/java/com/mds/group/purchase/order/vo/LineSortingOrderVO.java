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

import com.mds.group.purchase.order.model.LineSortingOrder;
import com.mds.group.purchase.order.model.LineSortingOrderDetail;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * The type Line sorting order vo.
 *
 * @author pavawi
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class LineSortingOrderVO extends LineSortingOrder {

    @ApiModelProperty("线路分拣单小区详情")
    private List<LineSortingOrderDetail> lineSortingOrderDetailList;

    @ApiModelProperty("商品数量")
    private Integer goodsSum;
}
