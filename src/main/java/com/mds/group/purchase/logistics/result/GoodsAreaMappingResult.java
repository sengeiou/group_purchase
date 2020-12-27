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

package com.mds.group.purchase.logistics.result;

import lombok.Data;

import java.util.List;

/**
 * 商品投放区域结果类
 *
 * @author shuke
 * @date 2018 -12-20
 */
@Data
public class GoodsAreaMappingResult {

    /**
     * 商品id
     */
    private Long goodsId;

    /**
     * 商品名称
     */
    private String goodsName;

    /**
     * 商品主图
     */
    private String goodsImg;

    /**
     *
     */
    private Long goodsAreaId;

    /**
     *
     */
    private List<LineAreaResult> lineAreaResults;

    /**
     * 小程序模板id
     */
    private String appmodelId;
}
