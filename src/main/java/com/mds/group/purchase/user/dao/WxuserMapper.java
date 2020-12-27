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
import com.mds.group.purchase.user.model.Wxuser;
import com.mds.group.purchase.user.vo.WxuserManagerVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * The interface Wxuser mapper.
 *
 * @author pavawi
 */
public interface WxuserMapper extends Mapper<Wxuser> {

    /**
     * 通过openId获取用户信息
     *
     * @param openid the openid
     * @return wxuser wxuser
     */
    Wxuser selectByOpenId(@Param("openid") String openid);

    /**
     * 通过wxuserId获取用户信息
     *
     * @param wxuserId   the wxuser id
     * @param appmodelId the appmodel id
     * @return wxuser wxuser
     */
    Wxuser selectByWxuserIdAndAppmodelId(@Param("wxuserId") Long wxuserId, @Param("appmodelId") String appmodelId);

    /**
     * 按条件查询用户
     *
     * @param paramMap the param map
     * @return list list
     */
    List<WxuserManagerVO> selectWxuserManager(Map<String, Object> paramMap);

    /**
     * 根据unionid查询
     *
     * @param unionid the unionid
     * @return wxuser wxuser
     */
    Wxuser selectByUnionid(String unionid);


    /**
     * 查询团长用户信息
     *
     * @param groupLeaderId the group leader id
     * @return wxuser wxuser
     */
    Wxuser selectByGroupleaderId(String groupLeaderId);

    /**
     * 查询用户头像
     *
     * @param wxuserId the wxuser id
     * @return string string
     */
    String selectIconByUserId(Long wxuserId);

    /**
     * 查询用户名
     *
     * @param wxuserId the wxuser id
     * @return string string
     */
    String selectNameByUserId(Long wxuserId);

    /**
     * 根据openid查询用户
     *
     * @param openId the open id
     * @return wxuser wxuser
     */
    Wxuser selectOneByPcOpenId(String openId);

    /**
     * 更新某个团长下的所属用户小区为空
     *
     * @param appmodelId  the appmodel id
     * @param communityId the community id
     */
    void updateByComminuty(@Param("appmodelId") String appmodelId, @Param("communityId") Long communityId);

    /**
     * Update mp open id by primary key.
     *
     * @param wxuserId the wxuser id
     * @param mpOpenId the mp open id
     */
    void updateMpOpenIdByPrimaryKey(@Param("wxuserId") Long wxuserId, @Param("mpOpenId") String mpOpenId);

    /**
     * Find customers by group leader id list.
     *
     * @param groupLeaderId the group leader id
     * @param search        the search
     * @return the list
     */
    List<Wxuser> findCustomersByGroupLeaderId(@Param("groupLeaderId") String groupLeaderId,
                                              @Param("search") String search);

    /**
     * Unbind.
     *
     * @param wxuserId the wxuser id
     */
    void unbind(@Param("wxuserId") Long wxuserId);
}