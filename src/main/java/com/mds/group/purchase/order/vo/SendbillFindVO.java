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
 * The type Sendbill find vo.
 *
 * @author pavawi
 */
@Data
public class SendbillFindVO {

    @ApiModelProperty(value = "发货单Id")
    private Long sendBillId;

    @ApiModelProperty(value = "发货单名称")
    private String sendBillName;

    @ApiModelProperty(value = "发货单下的线路,只有在查询团长签收单的时候返回数据")
    private List<SendbillFindLineVO> sendbillFindLineVOList;


    /**
     * Create sendbill find line vo sendbill find line vo.
     *
     * @return the sendbill find line vo
     */
    public SendbillFindLineVO createSendbillFindLineVO() {
        return new SendbillFindLineVO();
    }

    /**
     * The type Sendbill find line vo.
     *
     * @author pavawi
     */
    @Data
    public static class SendbillFindLineVO {
        @ApiModelProperty(value = "线路Id")
        private Long lineId;

        @ApiModelProperty(value = "线路名称")
        private String lineName;
    }

}
