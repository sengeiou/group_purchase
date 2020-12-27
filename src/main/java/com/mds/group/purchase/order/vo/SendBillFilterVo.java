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

/**
 * 发货单过滤功能参数类
 *
 * @author shuke
 * @date 2019 -2-20
 */
@Data
public class SendBillFilterVo {

    /**
     * 自定义的筛选时间
     * 手动选择的开始和结束时间，两个时间之间请使用逗号(,)分隔，时间格式为yyyy-MM-dd或者yyyy
     * 2019-01-01,2019-02-01或者2019/01/01,2019/02/01
     */
    @ApiModelProperty(name = "自定义的筛选时间", value = "手动选择的开始和结束时间，两个时间之间请使用逗号(,)分隔，时间格式为yyyy-MM-dd或者yyyy/MM/dd",
            dataType = "String", example = "2019-01-01,2019-02-01或者2019/01/01,2019/02/01")
    private String inputDate;

    /**
     * 发货单状态
     * 传入发货单状态筛选，（0、全部  1、待发货  2、配送中（暂时不用，先做预留） 3、待提货  4、已完成 5、（已关闭，暂时不用，预留）
     */
    @ApiModelProperty(name = "发货单状态", value = "传入发货单状态筛选，（0、全部  1、待发货  2、配送中（暂时不用，先做预留） 3、待提货  4、已完成 5、（已关闭，暂时不用，预留）"
            , example = "0")
    private Integer sendBillStatus;

    /**
     * 快速时间筛选
     * 0、全部  1、近一个月  2、近三个月
     */
    @ApiModelProperty(name = "快速时间筛选", value = "快速时间筛选,0、全部  1、近一个月  2、近三个月", example = "0")
    private Integer quicklyDate;

    /**
     * 分页页码，传入0查询所有
     */
    @ApiModelProperty(value = "分页页码，传入0查询所有", example = "0")
    private Integer page;

    /**
     * 分页数据量，传入0查询所有
     */
    @ApiModelProperty(value = "分页数据量，传入0查询所有", example = "0")
    private Integer size;
}
