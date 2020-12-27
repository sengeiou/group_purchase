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

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * The type Line sorting order view vo.
 *
 * @author pavawi
 */
@Data
public class LineSortingOrderViewVo {

    @ApiModelProperty(value = "编号")
    private Integer id;
    @ApiModelProperty(value = "线路名")
    private String lineName;
    @ApiModelProperty(value = "司机名")
    private String driverName;
    @ApiModelProperty(value = "手机号")
    private String phone;
    @ApiModelProperty(value = "发货单名称")
    private String sendBillName;
    @ApiModelProperty("分拣商品")
    private List<LineSortingOrderGoodsVO> lineSortingOrderGoodsVOS;

}
