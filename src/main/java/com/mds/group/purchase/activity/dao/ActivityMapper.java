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

package com.mds.group.purchase.activity.dao;

import com.mds.group.purchase.activity.model.Activity;
import com.mds.group.purchase.core.Mapper;
import com.mds.group.purchase.shop.vo.ActivityInfoVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 活动数据操作接口
 *
 * @author shuke on 2018-12-4
 */
public interface ActivityMapper extends Mapper<Activity> {

    /**
     * 插入一条活动信息
     *
     * @param act 活动对象
     * @return 插入记录的条数 long
     */
    Long insertAct(Activity act);

    /**
     * 根据appmodelid和 活动状态查询活动
     *
     * @param appmodelId 小程序模板id
     * @param status     活动状态
     * @param actType    活动类型（1、秒杀  2、拼团）
     * @return 活动列表 list
     */
    List<Activity> selectAct(@Param("appmodelId") String appmodelId, @Param("status") List<Integer> status,
                             @Param("actType") Integer actType);

    /**
     * 查询指定活动
     *
     * @param appmodelId 小程序模板id
     * @param searchType 查询类型
     * @return 活动结果对象 list
     */
    List<ActivityInfoVO> selectAssignActivitys(@Param("appmodelId") String appmodelId,
                                               @Param("searchType") Integer searchType);

    /**
     * 根据活动状态查询所有预热和开始的活动
     *
     * @param appmodelId 小程序模板id
     * @param status     活动状态（0:未开始 1：预热中 2：进行中 3：已经结束）
     * @return 活动列表 list
     */
    List<Activity> selectAllStartAndReadyAct(@Param("appmodelId") String appmodelId,
                                             @Param("status") List<Integer> status);

    /**
     * 根据活动id查询
     *
     * @param actId 活动id
     * @return 活动对象 activity
     */
    Activity selectByActId(Long actId);
}