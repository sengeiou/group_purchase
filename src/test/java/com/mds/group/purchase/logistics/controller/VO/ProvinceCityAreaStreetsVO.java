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

package com.mds.group.purchase.logistics.controller.VO;

import com.mds.group.purchase.logistics.model.Community;
import com.mds.group.purchase.logistics.model.Provinces;
import com.mds.group.purchase.logistics.result.AreaResult;
import com.mds.group.purchase.logistics.result.CityResult;
import com.mds.group.purchase.logistics.result.LineResult;
import com.mds.group.purchase.logistics.result.StreetsResult;
import lombok.Data;

import java.util.List;

@Data
public class ProvinceCityAreaStreetsVO {
    private Provinces provinces;
    private CityResult cityResult;
    private AreaResult areaResult;
    private Community community;
    private LineResult lineResult;
    private List<Community> communities;
    private List<StreetsResult> streetsResults;
}
