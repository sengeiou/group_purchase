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

package com.mds.group.purchase.user.service;

import com.mds.group.purchase.core.Result;
import com.mds.group.purchase.core.Service;
import com.mds.group.purchase.user.model.Wxuser;
import com.mds.group.purchase.user.vo.*;

import java.util.List;


/**
 * The interface Wxuser service.
 *
 * @author CodeGenerator
 * @date 2018 /11/27
 */
public interface WxuserService extends Service<Wxuser> {

    /**
     * 用户登录
     *
     * @param code       the code
     * @param appmodelId the appmodel id
     * @return the login result vo
     */
    LoginResultVO wxLogin(String code, String appmodelId);


    /**
     * 用户手机号解密
     *
     * @param decodeUserInfoVO the decode user info vo
     * @return string string
     */
    String decodeUserInfo(DecodeUserInfoVO decodeUserInfoVO);

    /**
     * 获取用户最新的信息
     *
     * @param wxuserId   the wxuser id
     * @param appmodelId the appmodel id
     * @return user info
     */
    UserInfoVO getUserInfo(Long wxuserId, String appmodelId);

    /**
     * 更新用户信息
     *
     * @param infoUpdateVO the info update vo
     * @return integer integer
     */
    Integer updateUserInfo(WxuserInfoUpdateVO infoUpdateVO);

    /**
     * 批量更新用户备注
     *
     * @param userRemarkVO the user remark vo
     * @return the int
     */
    int updateRemark(RemarkVO userRemarkVO);

    /**
     * 用户查询
     *
     * @param pageNum    the page num
     * @param pageSize   the page size
     * @param nickName   the nick name
     * @param community  the community
     * @param createTime the create time
     * @param appmodelId the appmodel id
     * @return list list
     */
    List<WxuserManagerVO> searchWxuserManager(Integer pageNum, Integer pageSize, String nickName, String community,
                                              String createTime, String appmodelId);

    /**
     * Qrcode long.
     *
     * @param code       the code
     * @param state      the state
     * @param appmodelId the appmodel id
     * @return the long
     */
    Long qrcode(String code, String state, String appmodelId);

    /**
     * 根据团长id查询用户信息
     *
     * @param groupLeaderId the group leader id
     * @return wxuser wxuser
     */
    Wxuser findByGroupleaderId(String groupLeaderId);

    /**
     * 更新某个团长下的所属用户小区为空
     *
     * @param appmodelId  the appmodel id
     * @param communityId the community id
     */
    void updateByComminuty(String appmodelId, Long communityId);

    /**
     * Gets user info 2.
     *
     * @param wxuserId   the wxuser id
     * @param appmodelId the appmodel id
     * @return the user info 2
     */
    UserInfoVO getUserInfo2(Long wxuserId, String appmodelId);

    /**
     * 根据团长ID查询团长旗下的团员
     *
     * @param groupLeaderId the group leader id
     * @param search        the search
     * @return list list
     */
    List<Wxuser> findCustomersByGroupLeaderId(String groupLeaderId, String search);

    /**
     * 获取用户所属的团长信息
     *
     * @param wxuserId   用户id
     * @param appmodelId 小程序模板id
     * @return string string
     */
    String finUserGroupById(Long wxuserId, String appmodelId);

    /**
     * wxH5登录
     *
     * @param code       the code
     * @param appmodelId the appmodel id
     * @return the wxuser
     */
    Wxuser wxH5Login(String code, String appmodelId);

    /**
     * wxH5登录
     *
     * @param code       the code
     * @param appmodelId the appmodel id
     * @return the result
     */
    Result wxH5LoginV13(String code, String appmodelId);

    /**
     * 绑定
     *
     * @param wxuserId   the wxuser id
     * @param mdsUnionId the mds union id
     * @param mpOpenid   the mp openid
     * @param appmodelId the appmodel id
     * @return the result
     */
    Result bindH5Mini(Long wxuserId, String mdsUnionId, String mpOpenid, String appmodelId);

    /**
     * 取消绑定
     *
     * @param wxuserId   the wxuser id
     * @param appmodelId the appmodel id
     * @return the result
     */
    Result unBind(Long wxuserId, String appmodelId);
}
