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

import com.mds.group.purchase.order.model.GoodsSortingOrder;
import com.mds.group.purchase.order.model.GoodsSortingOrderDetail;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * The type Goods sorting order view vo.
 *
 * @author pavawi
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class GoodsSortingOrderViewVO extends GoodsSortingOrder {

    @ApiModelProperty(value = "分拣明细")
    private List<GoodsSortingOrderDetail> goodsSortingOrderDetailList;

    @ApiModelProperty(value = "序号")
    private Integer id;
}
