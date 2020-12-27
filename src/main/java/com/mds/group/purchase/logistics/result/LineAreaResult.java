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

/**
 * The type Line area result.
 *
 * @author shuke
 * @date 2018 -12-20
 */
@Data
public class LineAreaResult {

    /**
     * 线路id
     */
    private Long lineId;

    /**
     * 线路名称
     */
    private String lineName;

    /**
     * 县区Id
     */
    private Long areaId;

    /**
     * 县区名称
     */
    private String areaName;

    /**
     * 区域id（街道）
     */
    private Long streetId;

    /**
     * 区域名称（街道）
     */
    private String streetName;

    /**
     * 小区id
     */
    private Long communityId;

    /**
     * 小区名称
     */
    private String communityName;
}
