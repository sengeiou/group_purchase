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

package com.mds.group.purchase.activity.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import com.mds.group.purchase.activity.dao.ActivityMapper;
import com.mds.group.purchase.activity.model.Activity;
import com.mds.group.purchase.activity.model.ActivityGoods;
import com.mds.group.purchase.activity.service.ActivityGoodsService;
import com.mds.group.purchase.activity.service.ActivityService;
import com.mds.group.purchase.activity.vo.ActivityV123Vo;
import com.mds.group.purchase.activity.vo.ActivityV12Vo;
import com.mds.group.purchase.activity.vo.ActivityVo;
import com.mds.group.purchase.constant.ActiviMqQueueName;
import com.mds.group.purchase.constant.ActivityConstant;
import com.mds.group.purchase.core.AbstractService;
import com.mds.group.purchase.core.CodeMsg;
import com.mds.group.purchase.exception.GlobalException;
import com.mds.group.purchase.exception.ServiceException;
import com.mds.group.purchase.jobhandler.ActiveDelaySendJobHandler;
import com.mds.group.purchase.order.model.OrderDetail;
import com.mds.group.purchase.order.result.ActivityTurnoverDTO;
import com.mds.group.purchase.order.service.OrderDetailService;
import com.mds.group.purchase.order.service.OrderService;
import com.mds.group.purchase.order.service.SendBillActivityService;
import com.mds.group.purchase.shop.vo.ActivityInfoVO;
import com.mds.group.purchase.utils.ActivityUtil;
import com.mds.group.purchase.utils.GoodsUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


/**
 * The type Activity service.
 *
 * @author shuke
 * @date 2018 /12/03
 */
@Service
public class ActivityServiceImpl extends AbstractService<Activity> implements ActivityService {

    @Resource
    private GoodsUtil goodsUtil;
    @Resource
    private ActivityUtil activityUtil;
    @Resource
    private OrderService orderService;
    @Resource
    private ActivityMapper tActivityMapper;
    @Resource
    private OrderDetailService orderDetailService;
    @Resource
    private ActivityGoodsService activityGoodsService;
    @Resource
    private SendBillActivityService sendBillActivityService;
    @Resource
    private ActiveDelaySendJobHandler activeDelaySendJobHandler;

    @Override
    @Transactional(rollbackFor = Exception.class)
    @SuppressWarnings("unchecked")
    public void createActivity(ActivityVo activityVo) {
        //活动不能创建小于当前时间的活动
        if (DateUtil.parse(activityVo.getStartTime()).getTime() < DateUtil.date().getTime()) {
            throw new ServiceException("活动时间不能小于当前时间");
        }
        //将参数提取出来
        Activity act = activityVo.voToAct();
        act.setStatus(ActivityConstant.ACTIVITY_STATUS_DNS);
        act.setDeleteStatus(false);
        //判断该活动开始时间是否包在已有的活动时间段内
        activityTime(activityVo);
        //一、插入活动表,返回activity_id
        tActivityMapper.insertAct(act);
        activityVo.setActivityId(act.getActivityId());
        //二、插入活动商品
        int i = activityGoodsService.saveActGoods(activityVo.getActGoodsList());
        //活动商品插入成功后更新活动商品数量
        act.setActGoodsNum(i);
        tActivityMapper.updateByPrimaryKey(act);

        //设置发货单生成日期
        String date = activityVo.getSendBillGenerateDate();
        sendBillActivityService.setSendBillGenerateDate(act.getActivityId(), date, activityVo.getAppmodelId());
        //发送准备活动的队列
        activityUtil.startActivityMq(activityVo);
    }

    private void activityTime(ActivityVo activityVo) {
        if (activityVo.getActivityType().equals(1)) {
            //判断时间正确性
            DateTime startTime = DateUtil.parse(activityVo.getStartTime());
            DateTime endTime = DateUtil.parse(activityVo.getEndTime());
            if (!DateUtil.isSameDay(startTime, endTime)) {
                throw new ServiceException("必须是同一天的时间");
            } else if (startTime.getTime() > endTime.getTime()) {
                throw new ServiceException("开始时间必须要大于结束时间");
            } else if (startTime.getTime() == endTime.getTime()) {
                throw new ServiceException("开始时间与结束时间不能相同");
            }
        }
        //查出所有的未开始,预热中和正在进行中的活动
        List<Integer> status = new ArrayList<>();
        status.add(ActivityConstant.ACTIVITY_STATUS_READY);
        status.add(ActivityConstant.ACTIVITY_STATUS_START);
        status.add(ActivityConstant.ACTIVITY_STATUS_DNS);
        List<Activity> activities = tActivityMapper
                .selectAct(activityVo.getAppmodelId(), status, activityVo.getActivityType());
        if (activityVo.getActivityId() != null) {
            for (Activity activity : activities) {
                if (activity.getActivityId().equals(activityVo.getActivityId())) {
                    activities.remove(activity);
                    break;
                }
            }
        }
        if (activities != null && activities.size() != 0) {
            for (Activity activity : activities) {
                boolean b1 =
                        DateUtil.parse(activityVo.getStartTime()).getTime() > DateUtil.parse(activity.getStartTime())
                                .getTime();
                boolean b2 = DateUtil.parse(activityVo.getStartTime()).getTime() < DateUtil.parse(activity.getEndTime())
                        .getTime();
                boolean b3 = DateUtil.parse(activityVo.getEndTime()).getTime() < DateUtil.parse(activity.getEndTime())
                        .getTime();
                boolean b4 = DateUtil.parse(activityVo.getEndTime()).getTime() > DateUtil.parse(activity.getStartTime())
                        .getTime();
                if ((b1 || b4) && (b3 || b2)) {
                    throw new ServiceException("活动时间存在冲突");
                }
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void modifyActivity(ActivityVo activityVo) {
        if (activityVo.getActivityId() == null || activityVo.getActivityId() <= 0) {
            throw new GlobalException(CodeMsg.BIND_ERROR.fillArgs("activityId不能为空"));
        }
        //判断该活动开始时间是否包在已有的活动时间段内
        activityTime(activityVo);
        //将参数提取出来
        Activity act = activityVo.voToAct();
        Activity activity = tActivityMapper.selectByPrimaryKey(act.getActivityId());
        int status = activity.getStatus();
        if (status == ActivityConstant.ACTIVITY_STATUS_START || status == ActivityConstant.ACTIVITY_STATUS_END) {
            throw new ServiceException("活动已经开始或结束，不能修改");
        }

        //批量删除活动商品
        activityGoodsService.deleteByActId(activityVo.getActivityId(), activityVo.getAppmodelId());
        //二、插入活动商品
        int actGoodsNum = activityGoodsService.saveActGoods(activityVo.getActGoodsList());
        //活动商品插入成功后更新活动商品数量
        act.setActGoodsNum(actGoodsNum);
        ////更新活动时将mongodb里的面该活动原有的对应的队列记录删除
        activityUtil.removeMongodbValueById(activityVo.getActivityId());
        //更新数据库中的活动信息
        tActivityMapper.updateByPrimaryKeySelective(act);

        //设置发货单生成日期
        String date = activityVo.getSendBillGenerateDate();
        sendBillActivityService.setSendBillGenerateDate(act.getActivityId(), date, act.getAppmodelId());
        //发送准备活动的队列
        activityUtil.startActivityMq(activityVo);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void modifyActivity(ActivityV12Vo activityVo) {
        if (activityVo.getActivityId() == null || activityVo.getActivityId() <= 0) {
            throw new GlobalException(CodeMsg.BIND_ERROR.fillArgs("activityId不能为空"));
        }
        //将参数提取出来
        Activity act = activityVo.voToAct();
        Activity activity = tActivityMapper.selectByPrimaryKey(activityVo.getActivityId());
        int status = activity.getStatus();
        if (status == ActivityConstant.ACTIVITY_STATUS_START) {
            //已经开始的活动只能修改排序方式和排序顺序，名称和海报
            act.setActivityId(activityVo.getActivityId());
            act.setActivityName(activityVo.getActivityName());
            act.setActivityPoster(activityVo.getActivityPoster());
            act.setActGoodsAutoSort(activityVo.getActGoodsAutoSort());
            if (!activityVo.getActGoodsAutoSort()) {
                //如果设置为手动排序，则更新数据库里数据的排序字段
                List<ActivityGoods> actGoodsList = activityVo.getActGoodsList();
                if (!actGoodsList.isEmpty()) {
                    activityGoodsService.updateActGoodsSortList(actGoodsList);
                } else {
                    throw new GlobalException(CodeMsg.PARAMETER_ERROR.fillArgs("未传入活动商品排序值"));
                }
            }
        }else {
            modifyNotStartAct(activityVo,status,act);
        }
        //更新数据库中的活动信息
        tActivityMapper.updateByPrimaryKeySelective(act);
    }

    @Override
    public void modifyActivity(ActivityV123Vo activityVo) {
        if (activityVo.getActivityId() == null || activityVo.getActivityId() <= 0) {
            throw new GlobalException(CodeMsg.BIND_ERROR.fillArgs("activityId不能为空"));
        }
        //将参数提取出来
        Activity act = activityVo.voToAct();
        Activity activity = tActivityMapper.selectByPrimaryKey(activityVo.getActivityId());
        int status = activity.getStatus();
        if (status == ActivityConstant.ACTIVITY_STATUS_START) {
            //已经开始的活动只能修改排序方式和排序顺序，名称、海报和是否参加接龙
            act.setActivityId(activityVo.getActivityId());
            act.setActivityName(activityVo.getActivityName());
            act.setActivityPoster(activityVo.getActivityPoster());
            act.setActGoodsAutoSort(activityVo.getActGoodsAutoSort());
            List<ActivityGoods> actGoodsList = activityVo.getActGoodsList();
            activityGoodsService.updateActGoodsJoinSolitaire(actGoodsList);
            if (!activityVo.getActGoodsAutoSort()) {
                //如果设置为手动排序，则更新数据库里数据的排序字段
                if (!actGoodsList.isEmpty()) {
                    activityGoodsService.updateActGoodsSortList(actGoodsList);
                } else {
                    throw new GlobalException(CodeMsg.PARAMETER_ERROR.fillArgs("未传入活动商品排序值"));
                }
            }
        }else {
            modifyNotStartAct(activityVo,status,act);
        }
        //更新数据库中的活动信息
        tActivityMapper.updateByPrimaryKeySelective(act);
    }

    private void modifyNotStartAct(ActivityVo activityVo,int status,Activity act){
        //判断该活动开始时间是否包在已有的活动时间段内
        activityTime(activityVo);
        if (status == ActivityConstant.ACTIVITY_STATUS_END) {
            throw new ServiceException("活动已经结束，不能修改");
        }
        //批量删除活动商品
        activityGoodsService.deleteByActId(activityVo.getActivityId(), activityVo.getAppmodelId());
        //二、插入活动商品
        int actGoodsNum = activityGoodsService.saveActGoods(activityVo.getActGoodsList());
        //活动商品插入成功后更新活动商品数量
        act.setActGoodsNum(actGoodsNum);
        ////更新活动时将mongodb里的面该活动原有的对应的队列记录删除
        activityUtil.removeMongodbValueById(activityVo.getActivityId());

        //设置发货单生成日期
        String date = activityVo.getSendBillGenerateDate();
        sendBillActivityService.setSendBillGenerateDate(act.getActivityId(), date, act.getAppmodelId());
        //发送准备活动的队列,并判断是否修改活动状态
        activityUtil.startActivityMq(activityVo);
    }


    @Override
    public List<Activity> findAllAct(String appmodelId, Integer actType) {
        actType = actType == 0 ? null : actType;
        Activity activity = new Activity();
        activity.setDeleteStatus(false);
        activity.setAppmodelId(appmodelId);
        activity.setActivityType(actType);
        List<Activity> activities = tActivityMapper.select(activity);
        if (CollectionUtil.isNotEmpty(activities)) {
            List<Long> activityId = activities.stream().map(Activity::getActivityId).collect(Collectors.toList());
            List<ActivityTurnoverDTO> activityTurnoverDTOS = orderService.findActivityTurnover(activityId, appmodelId);
            Map<Long, List<ActivityTurnoverDTO>> listMap = activityTurnoverDTOS.stream()
                    .collect(Collectors.groupingBy(ActivityTurnoverDTO::getAcitvityId));
            activities.forEach(obj -> {
                List<ActivityTurnoverDTO> turnoverDTOS = listMap.get(obj.getActivityId());
                if (CollectionUtil.isEmpty(turnoverDTOS)) {
                    obj.setParticipants(0);
                    obj.setTurnover(0);
                    obj.setGmv(BigDecimal.valueOf(0));
                } else {
                    obj.setParticipants(turnoverDTOS.size());
                    obj.setGmv(BigDecimal
                            .valueOf(turnoverDTOS.stream().mapToDouble(ActivityTurnoverDTO::getTotleFee).sum())
                            .setScale(2, RoundingMode.HALF_UP));
                    obj.setTurnover(turnoverDTOS.stream().mapToInt(ActivityTurnoverDTO::getTotleSum).sum());
                }
            });
        }
        return activities;
    }

    @Override
    public List<Activity> findAct(String appmodelId, Integer actType) {
        List<Integer> status = new ArrayList<>();
        status.add(ActivityConstant.ACTIVITY_STATUS_READY);
        status.add(ActivityConstant.ACTIVITY_STATUS_START);
        return tActivityMapper.selectAct(appmodelId, status, actType);
    }

    @Override
    public List<Activity> findActs(String appmodelId) {
        List<Integer> status = new ArrayList<>();
        status.add(ActivityConstant.ACTIVITY_STATUS_READY);
        status.add(ActivityConstant.ACTIVITY_STATUS_START);
        return tActivityMapper.selectAllStartAndReadyAct(appmodelId, status);
    }

    @Override
    public List<ActivityInfoVO> findAssignActivitys(String appmodelId, Integer searchType) {
        return tActivityMapper.selectAssignActivitys(appmodelId, searchType);
    }

    @Override
    public Activity findByActId(Long actId) {
        return tActivityMapper.selectByActId(actId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteOrCloseActivity(String activityIds, String appmodelId) {
        List<Long> ids = Arrays.stream(activityIds.split(",")).map(Long::parseLong).collect(Collectors.toList());
        //删除活动下的活动商品
        for (Long id : ids) {
            Activity activity = tActivityMapper.selectByActId(id);
            if (activity == null) {
                continue;
            }
            if (activity.getStatus().equals(ActivityConstant.ACTIVITY_STATUS_START)) {
                //活动状态为正在进行中,结束活动,并执行活动结束时的相关操作
                activeDelaySendJobHandler
                        .savaTask(activity.getActivityId().toString(), ActiviMqQueueName.END_ACTIVITY_V1, 0L,
                                activity.getAppmodelId(), true);
            } else if (activity.getStatus().equals(ActivityConstant.ACTIVITY_STATUS_READY) || activity.getStatus()
                    .equals(ActivityConstant.ACTIVITY_STATUS_DNS)) {
                activity.setDeleteStatus(true);
                //活动未开始,或者预热中,需要归还库存
                activityGoodsService.deleteByActId(id, appmodelId);
                activeDelaySendJobHandler
                        .savaTask(id.toString(), ActiviMqQueueName.DEL_ONE_ACTIVITY_CACHE, 0L, appmodelId, true);
            } else {
                //直接删除活动
                //删除已结束的活动，如果有未发货订单，则给出提示且不能删除
                List<OrderDetail> orderDetails = orderDetailService
                        .findByNoSendActivityGoods(activity.getActivityId(), 1);
                if (CollectionUtil.isNotEmpty(orderDetails)) {
                    throw new ServiceException("该活动存在未发货的订单，不能删除！");
                }
                activity.setDeleteStatus(true);
            }
            tActivityMapper.updateByPrimaryKeySelective(activity);
        }
        //刷新活动和商品缓存
        goodsUtil.flushGoodsCache(appmodelId);
    }
}
