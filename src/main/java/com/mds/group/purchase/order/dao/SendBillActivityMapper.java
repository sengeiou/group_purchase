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

package com.mds.group.purchase.order.dao;

import com.mds.group.purchase.core.Mapper;
import com.mds.group.purchase.order.model.SendBillActivity;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 活动设置的发货单生成时间DAO
 *
 * @author shuke
 * @date 2019 -2-19
 */
public interface SendBillActivityMapper extends Mapper<SendBillActivity> {

    /**
     * 查找匹配当前actid的未删除数据
     * del_flag = 0
     *
     * @param actId the act id
     * @return send bill activity
     */
    SendBillActivity selectByActId(@Param("actId") Long actId);

    /**
     * 查找匹配当前actid的未删除数据
     * del_flag = 0
     *
     * @param actIdList the act id list
     * @return list list
     */
    List<SendBillActivity> selectByActIds(@Param("actIdList") List<Long> actIdList);
}