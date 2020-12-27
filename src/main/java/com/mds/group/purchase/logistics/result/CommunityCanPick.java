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
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * The type Community can pick.
 *
 * @author pavawi
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class CommunityCanPick extends Community implements Comparable<CommunityCanPick> {

    @ApiModelProperty("用户与小区的距离")
    private Double distance;

    /**
     * Instantiates a new Community can pick.
     *
     * @param c the c
     */
    public CommunityCanPick(Community c) {
        setAppmodelId(c.getAppmodelId());
        setCommunityId(c.getCommunityId());
        setCommunityName(c.getCommunityName());
        setPcaAdr(c.getPcaAdr());
        setStreetName(c.getStreetName());
        setAreaId(c.getAreaId());
        setAreaName(c.getAreaName());
        setCityId(c.getCityId());
        setLocation(c.getLocation());
        setProvinceId(c.getProvinceId());
        setStreetId(c.getStreetId());
    }

    /**
     * Instantiates a new Community can pick.
     */
    public CommunityCanPick() {

    }

    @Override
    public int compareTo(CommunityCanPick o) {
        if (o == null) {
            return 0;
        }
        if (Double.valueOf(this.getDistance()) != null && Double.valueOf(o.getDistance()) != null) {
            return (int) (this.getDistance() - o.getDistance());
        }
        return 0;
    }
}
