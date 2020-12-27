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

package com.mds.group.purchase.order.service;

import com.mds.group.purchase.core.Service;
import com.mds.group.purchase.order.model.Comment;
import com.mds.group.purchase.order.vo.SaveCommentVo;

import java.util.List;
import java.util.Map;


/**
 * 订单评价service
 *
 * @author CodeGenerator
 * @date 2018 /12/13
 */
public interface CommentService extends Service<Comment> {

    /**
     * 用户评价订单
     *
     * @param saveCommentVo the save comment vo
     */
    void saveComment(SaveCommentVo saveCommentVo);

    /**
     * 获取用户自己的所有评价
     *
     * @param wxuserId   用户id
     * @param appmodelId the appmodel id
     * @return list list
     */
    List<Comment> findByUserId(Long wxuserId, String appmodelId);

    /**
     * 查询指定团长评分记录
     *
     * @param groupleaderId the groupleader id
     * @return list list
     */
    List<Comment> findByGroupLeaderIds(List<String> groupleaderId);

    /**
     * 获取商品评价信息,并存入缓存
     *
     * @param appmodelId 小程序模板id
     * @return map map
     */
    Map<String, Double> findAvgScoreByGoodsIds(String appmodelId);


    /**
     * 获取指定供应商的的评价
     *
     * @param providerIdList the provider id list
     * @return list list
     */
    List<Comment> findByProviderIds(List<String> providerIdList);

    /**
     * 查询指定订单评价
     *
     * @param orderOns the order ons
     * @return list list
     */
    List<Comment> findByOrderNos(List<String> orderOns);
}
