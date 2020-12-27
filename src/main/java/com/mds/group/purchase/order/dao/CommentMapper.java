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
import com.mds.group.purchase.order.model.Comment;

import java.util.List;

/**
 * The interface Comment mapper.
 *
 * @author pavawi
 */
public interface CommentMapper extends Mapper<Comment> {

    /**
     * Select goods comment by order no string.
     *
     * @param orderNo the order no
     * @return the string
     */
    String selectGoodsCommentByOrderNo(String orderNo);

    /**
     * 查询订单评分
     *
     * @param orderNo the order no
     * @return double double
     */
    Double selectGoodsScoreByOrderNo(String orderNo);

    /**
     * 根据商品id获取评价列表
     *
     * @param goodsId the goods id
     * @return list list
     */
    List<Comment> selectAcgGoodsScoreByGoodsIds(List<Long> goodsId);

    /**
     * 查询用户个人评价
     *
     * @param wxuserId the wxuser id
     * @return list list
     */
    List<Comment> selectByCommentUser(Long wxuserId);

}