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

import java.math.BigDecimal;


/**
 * The type Real time general situation vo.
 *
 * @author pavawi
 */
@Data
public class RealTimeGeneralSituationVO {

    @ApiModelProperty(value = "今日访问数")
    private Integer todayAccessNumber;
    @ApiModelProperty(value = "今日浏览量")
    private Integer todayVisitorVolume;
    @ApiModelProperty(value = "今日交易量")
    private Integer todayVolumeOfBusinessNumber;
    @ApiModelProperty(value = "今日交易额")
    private BigDecimal todayVolumeOfBusiness;

}
