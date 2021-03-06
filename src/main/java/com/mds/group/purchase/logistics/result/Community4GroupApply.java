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

import com.mds.group.purchase.logistics.model.Community;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * The type Community 4 group apply.
 *
 * @author pavawi
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class Community4GroupApply extends Community {

    private boolean canApply;

    /**
     * Community to this.
     *
     * @param community the community
     */
    public void communityToThis(Community community) {
        setAppmodelId(community.getAppmodelId());
        setCommunityId(community.getCommunityId());
        setCommunityName(community.getCommunityName());
        setPcaAdr(community.getPcaAdr());
        setStreetName(community.getStreetName());
        setAreaId(community.getAreaId());
        setAreaName(community.getAreaName());
        setCityId(community.getCityId());
        setLocation(community.getLocation());
        setProvinceId(community.getProvinceId());
        setStreetId(community.getStreetId());
    }
}
