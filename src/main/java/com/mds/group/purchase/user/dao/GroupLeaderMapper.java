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

package com.mds.group.purchase.user.dao;

import com.mds.group.purchase.core.Mapper;
import com.mds.group.purchase.user.model.GroupLeader;
import com.mds.group.purchase.user.vo.GroupManagerVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * The interface Group leader mapper.
 *
 * @author pavawi
 */
public interface GroupLeaderMapper extends Mapper<GroupLeader> {


    /**
     * 更新团长数据逻辑删除
     *
     * @param idsList the ids list
     * @return int int
     */
    int updateDelete(List<String> idsList);

    /**
     * 团长管理搜索
     *
     * @param map the map
     * @return list list
     */
    List<GroupManagerVO> searchWxuserManager(Map<String, Object> map);

    /**
     * 根据app modelid查询未删除的团长信息
     *
     * @param appmodelId the appmodel id
     * @return list list
     */
    List<GroupLeader> selectByAppmodelId(String appmodelId);

    /**
     * 根据团长状态和模板id查询团长
     *
     * @param status     the status
     * @param appmodelId the appmodel id
     * @return list list
     */
    List<GroupLeader> selectByStatusAppmodelId(@Param("status") Integer status, @Param("appmodelId") String appmodelId);

    /**
     * Select by community id group leader.
     *
     * @param groupLeader the group leader
     * @return group leader
     */
    GroupLeader selectByCommunityId(@Param("groupLeader") GroupLeader groupLeader);

    /**
     * Select by wx user id list.
     *
     * @param wxuserId the wxuser id
     * @return list list
     */
    List<GroupLeader> selectByWxUserId(Long wxuserId);

    /**
     * Select apply list.
     *
     * @param appmodelId the appmodel id
     * @return list list
     */
    List<GroupManagerVO> selectApply(String appmodelId);

    /**
     * Select by phone group leader.
     *
     * @param groupPhone the group phone
     * @param appmodelId the appmodel id
     * @return the group leader
     */
    GroupLeader selectByPhone(@Param("groupPhone") String groupPhone, @Param("appmodelId") String appmodelId);

    /**
     * Find customers by wx user id integer.
     *
     * @param wxuserId the wxuser id
     * @return the integer
     */
    Integer findCustomersByWxUserId(@Param("wxuserId") Long wxuserId);
}