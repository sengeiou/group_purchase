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
import com.mds.group.purchase.order.model.SendBillTimeSet;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 设置发货单生成时间Dao
 *
 * @author shuke
 * @date 2019 -2-18
 */
public interface SendBillTimeSetMapper extends Mapper<SendBillTimeSet> {

    /**
     * 根据appmodelId查询发货单生成时间个数
     * 且数据的删除状态为未删除状态
     *
     * @param appmodelId the appmodel id
     * @return 发货单生成时间个数 integer
     */
    Integer selectSetTimeNumberByAppmodelId(@Param("appmodelId") String appmodelId);

    /**
     * 根据appmodelId删除记录
     *
     * @param appmodelId the appmodel id
     */
    void deleteByAppmodelId(@Param("appmodelId") String appmodelId);

    /**
     * 根据appmodelId查询发货单生成时间
     * 且数据的删除状态为未删除状态
     *
     * @param appmodelId the appmodel id
     * @return list list
     */
    List<SendBillTimeSet> selectSetTimeByAppmodelId(String appmodelId);
}