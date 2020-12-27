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

package com.mds.group.purchase.order.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.mds.group.purchase.activity.result.ActGoodsInfoResult;
import com.mds.group.purchase.activity.service.ActivityGoodsService;
import com.mds.group.purchase.configurer.GroupMallProperties;
import com.mds.group.purchase.constant.OrderConstant;
import com.mds.group.purchase.constant.RedisPrefix;
import com.mds.group.purchase.constant.enums.AfterSaleOrderStatus;
import com.mds.group.purchase.core.AbstractService;
import com.mds.group.purchase.exception.ServiceException;
import com.mds.group.purchase.goods.model.Goods;
import com.mds.group.purchase.goods.model.GoodsDetail;
import com.mds.group.purchase.goods.service.GoodsDetailService;
import com.mds.group.purchase.goods.service.GoodsService;
import com.mds.group.purchase.order.dao.AfterSaleOrderMapper;
import com.mds.group.purchase.order.dao.CommentMapper;
import com.mds.group.purchase.order.model.AfterSaleOrder;
import com.mds.group.purchase.order.model.Comment;
import com.mds.group.purchase.order.model.Order;
import com.mds.group.purchase.order.model.OrderDetail;
import com.mds.group.purchase.order.result.OrderResult;
import com.mds.group.purchase.order.service.AfterSaleOrderService;
import com.mds.group.purchase.order.service.CommentService;
import com.mds.group.purchase.order.service.OrderDetailService;
import com.mds.group.purchase.order.service.OrderService;
import com.mds.group.purchase.order.vo.SaveCommentVo;
import com.mds.group.purchase.user.model.GroupLeader;
import com.mds.group.purchase.user.model.Wxuser;
import com.mds.group.purchase.user.service.GroupLeaderService;
import com.mds.group.purchase.user.service.WxuserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Condition;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;


/**
 * The type Comment service.
 *
 * @author pavawi
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class CommentServiceImpl extends AbstractService<Comment> implements CommentService {

    @Resource
    private GoodsService goodsService;
    @Resource
    private OrderService orderService;
    @Resource
    private RedisTemplate<String, Map<String, Double>> redisTemplate;
    @Resource
    private WxuserService wxuserService;
    @Resource
    private CommentMapper tCommentMapper;
    @Resource
    private GoodsDetailService goodsDetailService;
    @Resource
    private OrderDetailService orderDetailService;
    @Resource
    private GroupLeaderService groupLeaderService;
    @Resource
    private ActivityGoodsService activityGoodsService;
    @Resource
    private AfterSaleOrderService afterSaleOrderService;
    @Resource
    private AfterSaleOrderMapper afterSaleOrderMapper;

    private Logger logger = LoggerFactory.getLogger(CommentServiceImpl.class);

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveComment(SaveCommentVo saveCommentVo) {
        Order order = orderService.findBy("orderNo", saveCommentVo.getOrderNo());
        if (order == null) {
            throw new ServiceException("用户id和订单不匹配或者订单不存在");
        }
        OrderDetail orderDetail = orderDetailService.findBy("orderId", order.getOrderId());
        //提取出comment
        Comment comment = vo2Po(saveCommentVo);
        GoodsDetail goodsDetail = goodsDetailService.findBy("goodsId", comment.getGoodsId());
        comment.setProviderId(goodsDetail.getProviderId());
        comment.setGoodsNumber(orderDetail.getGoodsNum());
        comment.setActGoodsId(orderDetail.getActGoodsId());
        tCommentMapper.insert(comment);
        //异步刷新商品评价
        CompletableFuture.runAsync(() -> findAvgScoreByGoodsIds(saveCommentVo.getAppmodelId()));
        //修改订单状态为已完成
        if (order.getPayStatus() != OrderConstant.NOT_COMMENT) {
            throw new ServiceException("订单状态错误");
        } else {
            order.setPayStatus(OrderConstant.ORDER_COMPLETE);
            orderService.update(order);
            AfterSaleOrder afterSaleOrder = afterSaleOrderMapper.selectOrderByOriginalOrderId(order.getOrderId());
            if (afterSaleOrder != null && afterSaleOrder.getAfterSaleStatus().equals(AfterSaleOrderStatus.待商家审核.getValue())) {
                afterSaleOrderService.cancel(afterSaleOrder.getAfterSaleOrderId().toString(), false, 1);
            }
        }
    }

    @Override
    public List<Comment> findByUserId(Long wxuserId, String appmodelId) {
        List<Comment> comments = tCommentMapper.selectByCommentUser(wxuserId);
        List<String> orderNos = comments.stream().map(Comment::getOrderNo).collect(Collectors.toList());
        if (!orderNos.isEmpty()) {
            Map<String, OrderResult> details = orderService.findByOrderNos(orderNos);
            comments.forEach(o -> {
                if (o.getGoodsNumber() == null || o.getGoodsNumber() == 0) {
                    OrderResult orderResult = details.get(o.getOrderNo());
                    if (orderResult != null) {
                        o.setGoodsNumber(orderResult.getGoodsNum());
                    }
                }
            });
        }
        return comments;
    }


    @Override
    public List<Comment> findByGroupLeaderIds(List<String> groupleaderId) {
        Condition condition = new Condition(Comment.class);
        condition.createCriteria().andIn("groupLeaderId", groupleaderId).andEqualTo("delFlag", 0);
        return tCommentMapper.selectByCondition(condition);
    }

    @Override
    public Map<String, Double> findAvgScoreByGoodsIds(String appmodelId) {
        Map<String, Double> goodsScore = new HashMap<>(16);
        Condition condition = new Condition(Comment.class);
        condition.createCriteria().andEqualTo("appmodelId", appmodelId);
        List<Comment> commentList = tCommentMapper.selectByCondition(condition);
        if (CollectionUtil.isNotEmpty(commentList)) {
            Map<Long, List<Comment>> collect = commentList.stream().collect(Collectors.groupingBy(Comment::getGoodsId));
            for (Map.Entry<Long, List<Comment>> next : collect.entrySet()) {
                List<Comment> value = next.getValue();
                Double totalScore = 0.0;
                int sum = 0;
                for (Comment o : value) {
                    totalScore = totalScore + o.getGoodsScore();
                    sum++;
                }
                if (sum > 0) {
                    Double avgScore = totalScore / sum;
                    goodsScore.put(next.getKey().toString(), avgScore);
                }
            }
            String key = GroupMallProperties.getRedisPrefix() + appmodelId + RedisPrefix.ALL_GOODS_SCORE;
            redisTemplate.opsForValue().set(key, goodsScore, RedisPrefix.EXPIRATION_TIME, TimeUnit.HOURS);
        }
        return goodsScore;
    }

    @Override
    public List<Comment> findByProviderIds(List<String> providerIdList) {
        Condition condition = new Condition(Comment.class);
        condition.createCriteria().andIn("providerId", providerIdList);
        return tCommentMapper.selectByCondition(condition);
    }

    @Override
    public List<Comment> findByOrderNos(List<String> orderNos) {
        Condition condition = new Condition(Comment.class);
        condition.createCriteria().andIn("orderNo", orderNos);
        return tCommentMapper.selectByCondition(condition);
    }

    private Comment vo2Po(SaveCommentVo saveCommentVo) {
        Comment comment = new Comment();
        String orderNo = saveCommentVo.getOrderNo();
        Order order = orderService.findBy("orderNo", orderNo);
        OrderDetail orderDetail = orderDetailService.findBy("orderId", order.getOrderId());
        GroupLeader groupLeader = groupLeaderService.findById(order.getGroupId());
        comment.setDelFlag(false);
        comment.setCommentTime(new Date());
        comment.setGroupLeaderId(order.getGroupId());
        comment.setOrderNo(saveCommentVo.getOrderNo());
        comment.setAppmodelId(saveCommentVo.getAppmodelId());
        comment.setGoodsScore(saveCommentVo.getGoodsScore());
        comment.setGroupScore(saveCommentVo.getGroupScore());
        comment.setCommentUser(order.getWxuserId().toString());
        comment.setCommentContentGoods(saveCommentVo.getCommentContent4Goods());
        comment.setCommentContentGroup(saveCommentVo.getCommentContent4Group());
        ActGoodsInfoResult activityGoods;
        try {
            activityGoods = activityGoodsService
                    .getActGoodsById(orderDetail.getActGoodsId(), saveCommentVo.getAppmodelId());
        } catch (Exception e) {
            activityGoods = null;
            logger.error(e.getMessage());
        }
        if (activityGoods == null) {
            Goods goods = goodsService.findById(orderDetail.getGoodsId());
            comment.setGoodsId(goods.getGoodsId());
            comment.setGoodsImg(goods.getGoodsImg());
            comment.setGoodsName(goods.getGoodsName());
            int index = goods.getGoodsImg().indexOf(",");
            if (index == -1) {
                comment.setGoodsImg(goods.getGoodsImg());
            } else {
                comment.setGoodsImg(goods.getGoodsImg().substring(0, index));
            }
        } else {
            comment.setGoodsId(activityGoods.getGoodsId());
            comment.setGoodsName(activityGoods.getGoodsName());
            comment.setGoodsPrice(activityGoods.getActPrice());
            int index = activityGoods.getGoodsImg().indexOf(",");
            if (index == -1) {
                comment.setGoodsImg(activityGoods.getGoodsImg());
            } else {
                comment.setGoodsImg(activityGoods.getGoodsImg().substring(0, index));
            }
        }
        if (groupLeader != null) {
            comment.setGroupLeaderName(groupLeader.getGroupName());
            Wxuser groupUser = wxuserService.findByGroupleaderId(groupLeader.getGroupLeaderId());
            if (groupUser != null) {
                comment.setGroupLeaderIcon(groupUser.getIcon());
            }
        }
        return comment;
    }
}
