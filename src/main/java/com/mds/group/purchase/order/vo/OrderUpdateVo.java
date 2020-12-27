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

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * The type Order update vo.
 *
 * @author pavawi
 */
@Data
public class OrderUpdateVo {

    //修改类型
    /**
     * 发货
     */
    public final static int SEND = 1;
    /**
     * 备注
     */
    public final static int REMARK = 2;
    /**
     * 关闭
     */
    public final static int CLOSE = 3;
    /**
     * 删除
     */
    public final static int DELETE = 4;
    /**
     * 导出
     */
    public final static int EXPORT = 5;


    @NotBlank
    @ApiModelProperty(value = "订单id")
    private String orderIds;

    @ApiModelProperty(value = "商家备注内容")
    private String shopDesc;

    @ApiModelProperty(hidden = true)
    private String appmodelId;

    @NotNull
    @ApiModelProperty(value = "修改类型（1：发货，2备注,3 关闭 4.删除 5导出）")
    private Integer updateStatus;
}
