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

package com.mds.group.purchase.logistics.dao;

import com.mds.group.purchase.core.Mapper;
import com.mds.group.purchase.logistics.dto.CommunityLineInfoDTO;
import com.mds.group.purchase.logistics.model.Line;
import com.mds.group.purchase.logistics.vo.LineGetVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * The interface Line mapper.
 *
 * @author pavawi
 */
public interface LineMapper extends Mapper<Line> {

    /**
     * Select by vo list.
     *
     * @param lineGetVo  the line get vo
     * @param appmodelId the appmodel id
     * @return the list
     */
    List<Line> selectByVo(@Param("lineGetVo") LineGetVo lineGetVo, @Param("appmodelId") String appmodelId);

    /**
     * Select by v 12 vo list.
     *
     * @param lineGetVo  the line get vo
     * @param appmodelId the appmodel id
     * @return the list
     */
    List<Line> selectByV12Vo(@Param("lineGetVo") LineGetVo lineGetVo, @Param("appmodelId") String appmodelId);

    /**
     * 根据appmodelId查询
     *
     * @param appmodelId the appmodel id
     * @return list list
     */
    List<Line> selectByAppmodelId(String appmodelId);

    /**
     * 根据线路id批量逻辑删除线路
     *
     * @param lineIds the line ids
     */
    void deleteByLineIds(List<Long> lineIds);

    /**
     * 查询指定小区线路信息
     *
     * @param communityIds the community ids
     * @return the list
     */
    List<CommunityLineInfoDTO> selectByCommunityLine(@Param("communityIds") List<Long> communityIds);

    /**
     * Select by area id list.
     *
     * @param areaid     the areaid
     * @param appmodelId the appmodel id
     * @return the list
     */
    List<Line> selectByAreaId(@Param("areaid") String areaid, @Param("appmodelId") String appmodelId);


}