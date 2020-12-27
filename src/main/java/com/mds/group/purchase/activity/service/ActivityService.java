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

package com.mds.group.purchase.activity.service;

import com.mds.group.purchase.activity.model.Activity;
import com.mds.group.purchase.activity.vo.ActivityV123Vo;
import com.mds.group.purchase.activity.vo.ActivityV12Vo;
import com.mds.group.purchase.activity.vo.ActivityVo;
import com.mds.group.purchase.core.Service;
import com.mds.group.purchase.shop.vo.ActivityInfoVO;

import java.util.List;


/**
 * 活动的业务接口类
 *
 * @author CodeGenerator
 * @date 2018 /12/03
 */
public interface ActivityService extends Service<Activity> {

    /**
     * 创建一个活动
     *
     * @param activityVo 活动参数对象
     */
    void createActivity(ActivityVo activityVo);

    /**
     * 修改一个活动
     *
     * @param activityVo 活动参数对象
     */
    void modifyActivity(ActivityVo activityVo);

    /**
     * 修改一个活动
     * 已经开始的活动也能够修改商品排序
     *
     * @param activityVo 活动参数对象
     * @since v1.2
     */
    void modifyActivity(ActivityV12Vo activityVo);

    /**
     * 修改一个活动
     * 修改活动商品是否参加接龙活动
     *
     * @param activityVo 活动参数对象
     * @since v1.2.3
     */
    void modifyActivity(ActivityV123Vo activityVo);

    /**
     * 查找所有的活动
     *
     * @param appmodelId 小程序模板id
     * @param actType    活动类型（1、秒杀 2、拼团）
     * @return 活动对象列表 list
     */
    List<Activity> findAllAct(String appmodelId,Integer actType);

    /**
     * 查找预热中和进行中的不同类型的活动
     *
     * @param appmodelId 小程序模板id
     * @param actType    活动类型（1、秒杀 2、拼团）
     * @return 活动对象列表 list
     */
    List<Activity> findAct(String appmodelId,Integer actType);

    /**
     * 查找预热中和进行中的所有活动
     *
     * @param appmodelId 小程序模板id
     * @return 活动对象列表 list
     */
    List<Activity> findActs(String appmodelId);

    /**
     * 查询指定类型的活动(店铺装修查询商品)
     *
     * @param appmodelId 小程序模板id
     * @param searchType 类型
     * @return 活动对象列表 list
     */
    List<ActivityInfoVO> findAssignActivitys(String appmodelId, Integer searchType);

    /**
     * 查询置顶id的未删除活动
     *
     * @param actId 活动id
     * @return 活动对象 activity
     */
    Activity findByActId(Long actId);

    /**
     * 活动已经开始则结束活动
     * * 其他状态则删除活动
     *
     * @param activityIds 活动id，多个id之间用逗号分隔
     * @param appmodelId  小程序模板id
     */
    void deleteOrCloseActivity(String activityIds, String appmodelId);
}
