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

package com.mds.group.purchase.logistics.service;

import com.mds.group.purchase.core.Service;
import com.mds.group.purchase.logistics.dto.CommunityLineInfoDTO;
import com.mds.group.purchase.logistics.model.Line;
import com.mds.group.purchase.logistics.result.LineResult;
import com.mds.group.purchase.logistics.vo.LineAddCommunityV12Vo;
import com.mds.group.purchase.logistics.vo.LineGetVo;
import com.mds.group.purchase.logistics.vo.LineV12Vo;
import com.mds.group.purchase.logistics.vo.LineVo;

import java.util.List;
import java.util.Map;


/**
 * The interface Line service.
 *
 * @author CodeGenerator
 * @date 2018 /11/27
 */
public interface LineService extends Service<Line> {

    /**
     * 新建线路
     *
     * @param lineVo the line vo
     */
    void saveLine(LineVo lineVo);

    /**
     * 新建线路
     *
     * @param lineVo the line vo
     * @return the long
     * @since v1.2
     */
    Long saveLineV12(LineV12Vo lineVo);

    /**
     * 为线路中添加/更改小区
     *
     * @param lineVo the line vo
     * @since v1.2
     */
    void lineAddCommunity(LineAddCommunityV12Vo lineVo);

    /**
     * 模糊查询线路
     *
     * @param lineGetVo  the line get vo
     * @param appmodelId the appmodel id
     * @return line line
     */
    Map<String, Object> getLine(LineGetVo lineGetVo, String appmodelId);

    /**
     * 模糊查询线路
     *
     * @param lineGetVo  the line get vo
     * @param appmodelId the appmodel id
     * @return line v 12
     * @since v1.2
     */
    Map<String, Object> getLineV12(LineGetVo lineGetVo, String appmodelId);

    /**
     * 更新线路
     *
     * @param lineVo the line vo
     */
    void updateLine(LineVo lineVo);

    /**
     * 更新线路
     *
     * @param lineVo the line vo
     * @since v1.2
     */
    void updateLineV12(LineV12Vo lineVo);

    /**
     * 根据id批量删除线路
     *
     * @param lineIds    the line ids
     * @param appmodelId the appmodel id
     */
    void deleteLineByIds(String lineIds, String appmodelId);

    /**
     * 根据appmodeliD获取所有线路和街道和小区
     *
     * @param appmodelId the appmodel id
     * @return the line street community
     */
    List<LineResult> getLineStreetCommunity(String appmodelId);

    /**
     * 查询指定区域的线路
     *
     * @param areaid     the areaid
     * @param appmodelId the appmodel id
     * @return list list
     */
    List<Line> findByAreaid(String areaid, String appmodelId);

    /**
     * 查询指定小区线路信息
     *
     * @param communityIds the community ids
     * @return the list
     */
    List<CommunityLineInfoDTO> findCommunityLine(List<Long> communityIds);

    /**
     * Find by appmodel id list.
     *
     * @param appmodelId the appmodel id
     * @return list list
     */
    List<Line> findByAppmodelId(String appmodelId);

    /**
     * 获取该商家的所有线路
     *
     * @param appmodelId the appmodel id
     * @return all all
     */
    Object getAll(String appmodelId);

    /**
     * 查询包含小区的线路
     *
     * @param appmodelId the appmodel id
     * @return list list
     */
    List<Line> findByAppmodelIdV2(String appmodelId);


}
