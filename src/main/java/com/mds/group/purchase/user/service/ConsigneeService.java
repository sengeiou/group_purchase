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

import com.mds.group.purchase.core.Service;
import com.mds.group.purchase.user.model.Consignee;
import com.mds.group.purchase.user.vo.AddersVO;

import java.util.List;


/**
 * Created by CodeGenerator on 2018/11/28.
 *
 * @author pavawi
 */
public interface ConsigneeService extends Service<Consignee> {

    /**
     * 添加用户地址
     *
     * @param map the map
     * @return int int
     */
    int addersCraete(AddersVO map);

    /**
     * 获取用户地址
     *
     * @param wxuserId the wxuser id
     * @return list list
     */
    List<Consignee> addersGet(Long wxuserId);

    /**
     * 更新用户地址|设置为默认
     *
     * @param consignee the consignee
     * @return int int
     */
    int addersUpdate(Consignee consignee);

    /**
     * Adders delete int.
     *
     * @param consigneeId the consignee id
     * @param wxuserId    the wxuser id
     * @return the int
     */
    int addersDelete(Long consigneeId, Long wxuserId);

    /**
     * 根据用户ID查找默认收货地址
     *
     * @param wxuserIds the wxuser ids
     * @return list list
     */
    List<Consignee> findDefaultByWxuserIds(List<Long> wxuserIds);
}
