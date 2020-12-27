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

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * The type General situation vo.
 *
 * @author pavawi
 */
@Data
public class GeneralSituationVO {

    @ApiModelProperty(value = "实时概况,三个数组,[0]今日,[1]昨日,[2]最近七天")
    private List<RealTimeGeneralSituationVO> realTimeGeneralSituationVOList;

    @ApiModelProperty(value = "历史交易额,历史交易量")
    private List<HistoricalTransactionDataVO> historicalTransactionDataVO;

    @ApiModelProperty(value = "订单统计")
    private OrderDataVO orderDataVO;

    @ApiModelProperty(value = "[0]销售量,[1]商品销售额,[2]会员消费统计")
    private List<SalesVolumeVO> salesVolumeVO;
}
