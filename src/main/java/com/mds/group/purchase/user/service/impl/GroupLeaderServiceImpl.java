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

package com.mds.group.purchase.user.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.NumberUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.mds.group.purchase.activity.model.Activity;
import com.mds.group.purchase.activity.service.ActivityService;
import com.mds.group.purchase.constant.ActiviMqQueueName;
import com.mds.group.purchase.constant.Common;
import com.mds.group.purchase.constant.GroupLeaderStatus;
import com.mds.group.purchase.constant.OrderConstant;
import com.mds.group.purchase.core.AbstractService;
import com.mds.group.purchase.exception.ServiceException;
import com.mds.group.purchase.jobhandler.ActiveDelaySendJobHandler;
import com.mds.group.purchase.logistics.dto.CommunityMoreDTO;
import com.mds.group.purchase.logistics.model.Community;
import com.mds.group.purchase.logistics.model.Line;
import com.mds.group.purchase.logistics.model.LineDetail;
import com.mds.group.purchase.logistics.service.*;
import com.mds.group.purchase.order.model.Comment;
import com.mds.group.purchase.order.model.Order;
import com.mds.group.purchase.order.model.OrderDetail;
import com.mds.group.purchase.order.result.GroupOrderDTO;
import com.mds.group.purchase.order.result.MyCommissionResult;
import com.mds.group.purchase.order.service.CommentService;
import com.mds.group.purchase.order.service.OrderDetailService;
import com.mds.group.purchase.order.service.OrderService;
import com.mds.group.purchase.shop.model.Manager;
import com.mds.group.purchase.shop.service.ManagerService;
import com.mds.group.purchase.shop.service.ShopFunctionService;
import com.mds.group.purchase.shop.vo.ShopCreateUpdateVO;
import com.mds.group.purchase.user.dao.GroupLeaderMapper;
import com.mds.group.purchase.user.model.GroupBpavawiceOrder;
import com.mds.group.purchase.user.model.GroupLeader;
import com.mds.group.purchase.user.model.Wxuser;
import com.mds.group.purchase.user.service.GroupBpavawiceOrderService;
import com.mds.group.purchase.user.service.GroupLeaderService;
import com.mds.group.purchase.user.service.WxuserService;
import com.mds.group.purchase.user.vo.*;
import com.mds.group.purchase.utils.*;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Condition;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;


/**
 * The type Group leader service.
 *
 * @author CodeGenerator
 * @date 2018 /11/27
 */
@Service
@Log4j2
public class GroupLeaderServiceImpl extends AbstractService<GroupLeader> implements GroupLeaderService {

    @Resource
    private UserUtil userUtil;
    @Resource
    private LineService lineService;
    @Resource
    private AreasService areasService;
    @Resource
    private OrderService orderService;
    @Resource
    private OrderDetailService orderDetailService;
    @Resource
    private WxuserService wxuserService;
    @Resource
    private CitiesService citiesService;
    @Resource
    private CommentService commentService;
    @Resource
    private ManagerService managerService;
    @Resource
    private ActivityService activityService;
    @Resource
    private OrderDetailService detailService;
    @Resource
    private ProvincesService provincesService;
    @Resource
    private CommunityService communityService;
    @Resource
    private LineDetailService lineDetailService;
    @Resource
    private GroupLeaderMapper tGroupLeaderMapper;
    @Resource
    private ShopFunctionService shopFunctionService;
    @Resource
    private GroupBpavawiceOrderService groupBpavawiceOrderService;
    @Resource
    private ActiveDelaySendJobHandler activeDelaySendJobHandler;

    @Override
    public int groupApplyRegister(GroupApplyForVO groupLeaderApply) {
        //判断团长id不等于空
        boolean flag = StringUtils.isNotBlank(groupLeaderApply.getGroupLeaderId());
        //判断是否可注册
        this.isMayRegister(groupLeaderApply.getAppmodelId(), groupLeaderApply.getCommunityId(),
                groupLeaderApply.getGroupLeaderId(), groupLeaderApply.getGroupPhone(), flag);

        GroupLeader isExist2 = new GroupLeader();
        isExist2.setWxuserId(groupLeaderApply.getWxuserId());
        //0-待审核 1-正常 2-拒绝 3-禁用中
        isExist2.setDeleteState(false);
        isExist2.setAppmodelId(groupLeaderApply.getAppmodelId());
        GroupLeader isExist3 = tGroupLeaderMapper.selectOne(isExist2);
        if (isExist3 != null) {
            //0-待审核 1-正常 2-拒绝 3-禁用中
            if (isExist3.getStatus().equals(GroupLeader.Status.WAITAUDIT)) {
                throw new ServiceException("您的账号已在待审核中");
            }
        }
        GroupLeader groupLeader = new GroupLeader();
        groupLeader.setAppmodelId(groupLeaderApply.getAppmodelId());
        groupLeader.setGroupPhone(groupLeaderApply.getGroupPhone());
        groupLeader.setDeleteState(false);
        groupLeader.setGroupName(groupLeaderApply.getGroupName());
        groupLeader.setCommunityId(groupLeaderApply.getCommunityId());
        groupLeader.setAddress(groupLeaderApply.getAddress());
        if (groupLeaderApply.getOptionType().equals(GroupApplyForVO.OptionType.ADDORMODIFY)) {
            //新增
            if (!flag) {
                groupLeader.setCreateTime(DateUtil.date());
                groupLeader.setStatus(1);
                groupLeader.setDeleteState(false);
                groupLeader.setBrokerage(new BigDecimal(0.0));
                groupLeader.setGroupLeaderId("TZ" + IdGenerateUtils.getItemId());
                groupLeader.setWxuserId(groupLeaderApply.getWxuserId());
                Condition condition = new Condition(LineDetail.class);
                condition.createCriteria().andEqualTo("communityId", groupLeader.getCommunityId())
                        .andEqualTo("delFlag", 0);
                LineDetail lineDetail = lineDetailService.findByOneCondition(condition);
                groupLeader.setLineId(lineDetail.getLineId());

                //修改用户状态为
                Wxuser wxuser = new Wxuser();
                wxuser.setWxuserId(groupLeaderApply.getWxuserId());
                wxuser.setUserStatus(Wxuser.UserStatus.GROUPLEADER);
                wxuserService.update(wxuser);
            } else {
                //更新
                groupLeader.setGroupLeaderId(groupLeaderApply.getGroupLeaderId());
                return tGroupLeaderMapper.updateByPrimaryKeySelective(groupLeader);
            }
        } else {
            groupLeader.setGroupLeaderId("TZ" + IdGenerateUtils.getItemId());
            groupLeader.setStatus(0);
            groupLeader.setWxuserId(groupLeaderApply.getWxuserId());
            groupLeader.setFormId(groupLeaderApply.getFormId());
        }
        return tGroupLeaderMapper.insertSelective(groupLeader);
    }

    private void isMayRegister(String appmodelId, Long communityId, String groupleaderId, String groupPhone,
                               boolean flag) {
        //判断手机号是否注册
        GroupLeader groupLeader = new GroupLeader();
        groupLeader.setAppmodelId(appmodelId);
        groupLeader.setGroupPhone(groupPhone);
        groupLeader.setDeleteState(false);
        GroupLeader isExist = tGroupLeaderMapper.selectOne(groupLeader);
        if (isExist != null) {
            //如果是添加提醒手机号已存在
            if (!flag) {
                throw new ServiceException("手机号已被注册");
            }
            //如果更新判断手机号是否是当前用户注册的
            if (!isExist.getGroupLeaderId().equals(groupleaderId)) {
                throw new ServiceException("手机号已存在申请");
            }
        }
        //判断小区是否存在团长
        Condition condition = new Condition(GroupLeader.class);
        condition.createCriteria().andEqualTo("deleteState", false).andEqualTo("appmodelId", appmodelId)
                .andEqualTo("communityId", communityId).andIn("status", Arrays.asList(1, 3));
        List<GroupLeader> groupLeaders = tGroupLeaderMapper.selectByCondition(condition);
        if (CollectionUtil.isNotEmpty(groupLeaders)) {
            if (!flag) {
                throw new ServiceException("小区已存在团长");
            }
            GroupLeader isExsit = groupLeaders.get(0);
            if (!isExsit.getGroupLeaderId().equals(groupleaderId)) {
                throw new ServiceException("小区已存在团长");
            }
        }
    }


    private void isMayRegister119(String appmodelId, String groupleaderId, String groupPhone, boolean flag) {
        //判断手机号是否注册
        GroupLeader isExist = tGroupLeaderMapper.selectByPhone(groupPhone, appmodelId);
        if (isExist != null) {
            //如果是添加提醒手机号已存在
            if (!flag) {
                throw new ServiceException("手机号已被注册");
            }
            //如果更新判断手机号是否是当前用户注册的
            if (!isExist.getGroupLeaderId().equals(groupleaderId)) {
                throw new ServiceException("手机号已存在申请");
            }
        }
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public int groupApply(GroupApplyVO groupApplyVO) {
        GroupLeader groupLeader = tGroupLeaderMapper.selectByPrimaryKey(groupApplyVO.getId());
        if (groupLeader == null) {
            throw new ServiceException("团长不存在");
        }
        //0-拒绝 1-同意
        if (groupApplyVO.getOptionType().equals(0) && groupLeader.getStatus().equals(GroupLeaderStatus.STATUS_AWAIT)) {
            groupLeader.setStatus(GroupLeaderStatus.STATUS_REPULSE);
        } else if (groupApplyVO.getOptionType().equals(1) && groupLeader.getStatus()
                .equals(GroupLeaderStatus.STATUS_AWAIT)) {
            //同意需要判断小区是否已存在团长,手机号是否已注册
            this.isMayRegister(groupLeader.getAppmodelId(), groupLeader.getCommunityId(),
                    groupLeader.getGroupLeaderId(), groupLeader.getGroupPhone(), true);
            groupLeader.setStatus(GroupLeaderStatus.STATUS_NORMAL);
            //更新用户状态为团长
            Wxuser wxuser = wxuserService.findById(groupLeader.getWxuserId());
            wxuser.setUserStatus(2);
            wxuserService.update(wxuser);
            //更新用户缓存
            userUtil.depositCache(wxuser, 1);
            //2-禁用 3-开启 todo 需要其他操作时增加
        } else if (groupApplyVO.getOptionType().equals(2) && groupLeader.getStatus()
                .equals(GroupLeaderStatus.STATUS_NORMAL)) {
            groupLeader.setStatus(GroupLeaderStatus.STATUS_DISABLE);
        } else if (groupApplyVO.getOptionType().equals(3) && groupLeader.getStatus()
                .equals(GroupLeaderStatus.STATUS_DISABLE)) {
            groupLeader.setStatus(GroupLeaderStatus.STATUS_NORMAL);
        } else {
            throw new ServiceException("非法操作");
        }
        int i = tGroupLeaderMapper.updateByPrimaryKeySelective(groupLeader);

        //发送审核通过模板消息
        Map<String, Object> map = new HashMap<>();
        map.put("appmodelId", groupLeader.getAppmodelId());
        map.put("wxuserId", groupLeader.getWxuserId());
        map.put("communityId", groupLeader.getCommunityId());
        map.put("formId", groupLeader.getFormId());
        map.put("time", DateUtil.date().toString());
        map.put("type", 101);
        if (groupLeader.getStatus() == 1) {
            map.put("applyResult", "您的申请已通过");
        } else if (GroupLeaderStatus.STATUS_REPULSE.equals(groupLeader.getStatus())) {
            map.put("applyResult", "您的申请未通过");
        }
        activeDelaySendJobHandler
                .savaTask(JSON.toJSONString(map), ActiviMqQueueName.ORDER_MINIPROGRAM_TEMPLATE_MESSAGE, 0L,
                        groupLeader.getAppmodelId(), false);
        return i;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int groupDelete(DeleteVO deleteVO) {
        List<String> idsList = Arrays.asList(deleteVO.getIds().split(Common.REGEX));
        Condition condition = new Condition(GroupLeader.class);
        condition.createCriteria().andIn("groupLeaderId", idsList);
        List<GroupLeader> groupLeaders = tGroupLeaderMapper.selectByCondition(condition);
        if (groupLeaders != null) {
            List<GroupLeader> havaBpavawice = groupLeaders.stream().filter(obj -> obj.getBrokerage().doubleValue() > 0)
                    .collect(Collectors.toList());
            if (CollectionUtil.isNotEmpty(havaBpavawice)) {
                if (havaBpavawice.size() > 1) {
                    throw new ServiceException("部分团长账号有佣金金额,请先结算清金额");
                } else {
                    throw new ServiceException("此团长账号有佣金金额,请先结算清金额");
                }
            }
            if (tGroupLeaderMapper.updateDelete(idsList) > 0) {
                //团长删除后  todo 需要其他操作时增加
                String wxuserIds = groupLeaders.stream().map(obj -> obj.getWxuserId().toString())
                        .collect(Collectors.joining(","));
                List<Wxuser> wxuserList = wxuserService.findByIds(wxuserIds);
                for (Wxuser wxuser : wxuserList) {
                    //更新用户团长状态
                    if (wxuser.getUserStatus().equals(2)) {
                        wxuser.setUserStatus(1);
                    }
                    wxuser.setCommunityId(null);
                    wxuserService.update(wxuser);
                    userUtil.depositCache(wxuser, 1);
                }
                //保证团长必须要有一个
                Condition condition1 = new Condition(GroupLeader.class);
                condition1.createCriteria().andEqualTo("deleteState", 0)
                        .andEqualTo("appmodelId", deleteVO.getAppmodelId());
                int sum = tGroupLeaderMapper.selectCountByCondition(condition1);
                if (sum == 0) {
                    throw new ServiceException("必须存在一个团长");
                }
                return 1;
            }
        }
        return 0;
    }


    @Override
    public List<GroupManagerVO> searchGroupManager(Integer pageNum, Integer pageSize, Integer searchType,
                                                   String lineName, String groupLeaderId, String communityName,
                                                   String area, String appmodelId) {
        if (searchType == GroupManagerVO.WAITAUDIT) {
            //查询待审核的信息
            PageHelper.startPage(pageNum, pageSize, "create_time desc");
            List<GroupManagerVO> groupManagerVOS = tGroupLeaderMapper.selectApply(appmodelId);
            groupManagerVOS.forEach(o -> {
                String groupLocation = o.getGroupLocation();
                groupLocation = groupLocation == null ? "" : groupLocation;
                int i = groupLocation.indexOf(",");
                if (i > 0) {
                    String[] split = o.getGroupLocation().split(",");
                    JSONObject jsonObject = JSONObject.parseObject(GeoCodeUtil.getArea(split[1], split[0], false));
                    String cityName = jsonObject.getJSONObject("regeocode").getJSONObject("addressComponent")
                            .getString("city");
                    String province = jsonObject.getJSONObject("regeocode").getJSONObject("addressComponent")
                            .getString("province");
                    String areas = jsonObject.getJSONObject("regeocode").getJSONObject("addressComponent")
                            .getString("district");
                    String provincesId = provincesService.findIdLikeName(province);
                    String cityId = citiesService.findIdLikeName(provincesId, cityName);
                    String areaId = areasService.findIdLikeName(cityId, areas);
                    o.setProvinceId(provincesId);
                    o.setCityId(cityId);
                    o.setAreaId(areaId);
                    o.setArea(province + cityName + areas);
                }
            });
            return groupManagerVOS;
        }
        Map<String, Object> map = new HashMap<>(8);
        map.put("appmodelId", appmodelId);
        map.put("searchType", searchType);
        StringBuilder stringBuilder = new StringBuilder();
        if (StringUtils.isNotBlank(lineName)) {
            char[] chars = lineName.toCharArray();
            for (char aChar : chars) {
                stringBuilder.append(aChar).append("%");
            }
            map.put("lineName", "%" + stringBuilder.toString() + "%");
            stringBuilder.delete(0, stringBuilder.length());
        }
        if (StringUtils.isNotBlank(groupLeaderId)) {
            map.put("groupLeaderId", groupLeaderId);
        }
        if (StringUtils.isNotBlank(communityName)) {
            char[] chars = communityName.toCharArray();
            for (char aChar : chars) {
                stringBuilder.append(aChar).append("%");
            }
            map.put("communityName", "%" + stringBuilder.toString() + "%");
            stringBuilder.delete(0, stringBuilder.length());
        }
        if (StringUtils.isNotBlank(area)) {
            char[] chars = area.toCharArray();
            for (char aChar : chars) {
                stringBuilder.append(aChar).append("%");
            }
            map.put("area", "%" + stringBuilder.toString() + "%");
            stringBuilder.delete(0, stringBuilder.length());
        }
        PageHelper.startPage(pageNum, pageSize, "create_time desc");
        List<GroupManagerVO> groupManagerVOS = tGroupLeaderMapper.searchWxuserManager(map);
        if (groupManagerVOS.size() > 0) {
            List<Long> communityIds = groupManagerVOS.stream().map(GroupManagerVO::getCommunityId).distinct()
                    .collect(Collectors.toList());
            List<CommunityMoreDTO> communityAll = communityService.getCommunityAll(communityIds);
            Map<Long, CommunityMoreDTO> communityMoreDTOMap = communityAll.stream()
                    .collect(Collectors.toMap(Community::getCommunityId, v -> v));
            //查询用户线路名称
            Condition condition = new Condition(LineDetail.class);
            condition.createCriteria().andIn("communityId", communityIds).andEqualTo("delFlag", 0)
                    .andIsNotNull("lineId");
            List<LineDetail> lineDetails = lineDetailService.findByCondition(condition);
            String lineIds = lineDetails.stream().map(obj -> obj.getLineId().toString())
                    .collect(Collectors.joining(","));
            Map<Long, LineDetail> lineDetailMap = lineDetails.stream()
                    .collect(Collectors.toMap(LineDetail::getCommunityId, v -> v, (oldValue, newValue) -> oldValue));
            Map<Long, Line> lineMap = new HashMap<>(8);
            if (StringUtils.isNotBlank(lineIds)) {
                List<Line> lines = lineService.findByIds(lineIds);
                lineMap = lines.stream().collect(Collectors.toMap(Line::getLineId, v -> v));
            }
            //查询团长评分 只有审核通过的团长才有评分
            Map<String, String> groupComment = new HashMap<>(16);
            boolean ifSelect = searchType.equals(GroupManagerVO.NORMAL) || searchType.equals(GroupManagerVO.LOCK);
            if (ifSelect) {
                List<String> groupleaderIds = groupManagerVOS.stream().map(GroupManagerVO::getGroupLeaderId)
                        .collect(Collectors.toList());
                List<Comment> commentList = commentService.findByGroupLeaderIds(groupleaderIds);
                Map<String, List<Comment>> commentListMap = commentList.stream().filter(Objects::nonNull)
                        .collect(Collectors.groupingBy(Comment::getGroupLeaderId));
                commentListMap.forEach((groupleaderId, comments) -> {
                    if (CollectionUtil.isNotEmpty(comments)) {
                        double asDouble = comments.stream().mapToDouble(Comment::getGroupScore).average().getAsDouble();
                        groupComment.put(groupleaderId, NumberUtil.round(asDouble, 1).toString());
                    } else {
                        groupComment.put(groupleaderId, "5.0");
                    }
                });
            }
            for (GroupManagerVO obj : groupManagerVOS) {
                if (ifSelect) {
                    //没有任何评价时需要再次判断是否为空
                    String grade = groupComment.get(obj.getGroupLeaderId());
                    if (StringUtils.isNotBlank(grade)) {
                        obj.setGrade(grade);
                    } else {
                        obj.setGrade("5.0");
                    }
                }
                CommunityMoreDTO communityMoreDTO = communityMoreDTOMap.get(obj.getCommunityId());
                if (communityMoreDTO == null) {
                    obj.setCommunityName("无所属小区");
                    obj.setArea("无所属小区");
                } else {
                    obj.setCommunityName(communityMoreDTO.getCommunityName());
                    obj.setArea(communityMoreDTO.getPcaAdr());
                    obj.setStreetId(communityMoreDTO.getStreetId());
                    obj.setStreetName(communityMoreDTO.getStreetName());
                    obj.setAreaId(communityMoreDTO.getAreaId());
                    obj.setCityId(communityMoreDTO.getCityId());
                    obj.setProvinceId(communityMoreDTO.getProvinceId());
                }
                LineDetail lineDetail = lineDetailMap.get(obj.getCommunityId());
                if (lineDetail != null) {
                    Line line = lineMap.get(lineDetail.getLineId());
                    obj.setLineName(line.getLineName());
                    obj.setLineId(line.getLineId());
                } else {
                    obj.setLineName("无所属线路");
                }
            }
        }
        return groupManagerVOS;
    }

    @Override
    public int withdrawMoneyApply(WithdrawMoneyApplyVO withdrawMoneyVO) {
        ShopCreateUpdateVO shopCreateUpdateVO = shopFunctionService.findByAppmodelId(withdrawMoneyVO.getAppmodelId());
        if (shopCreateUpdateVO == null) {
            throw new ServiceException("商家ID有误");
        }
        if (withdrawMoneyVO.getWithdrawMoney().doubleValue() < shopCreateUpdateVO.getWithdrawLimit().doubleValue()) {
            throw new ServiceException(
                    "提现余额不满MONEY元,继续努力吧~".replace("MONEY", shopCreateUpdateVO.getWithdrawLimit().doubleValue() + ""));
        }
        if (withdrawMoneyVO.getOptionType().equals(1)) {
            //检测商家是否支持企业付款
            Manager manager = managerService.findBy("appmodelId", withdrawMoneyVO.getAppmodelId());
            if (manager == null) {
                throw new ServiceException("商家不存在");
            }
            if (manager.getEnterprisePayState().equals(false)) {
                throw new ServiceException("商家不满足提现条件,请联系商家线下核销");
            }
        }
        if (withdrawMoneyVO.getOptionType().equals(WithdrawMoneyApplyVO.OptionType.WX) || withdrawMoneyVO
                .getOptionType().equals(WithdrawMoneyApplyVO.OptionType.OFFLINE)) {
            //检测团长佣金是否足够
            GroupLeader groupLeader = tGroupLeaderMapper.selectByPrimaryKey(withdrawMoneyVO.getGroupLeaderId());
            if (groupLeader.getBrokerage().doubleValue() < withdrawMoneyVO.getWithdrawMoney().doubleValue()) {
                throw new ServiceException("佣金不足");
            }
            List<GroupBpavawiceOrder> groupBpavawiceOrders = groupBpavawiceOrderService
                    .findByGroupLeaderId(groupLeader.getGroupLeaderId());
            if (CollectionUtil.isNotEmpty(groupBpavawiceOrders)) {
                double sum = groupBpavawiceOrders.stream()
                        .mapToDouble(obj -> obj.getOutBpavawice().setScale(2, RoundingMode.HALF_UP).doubleValue()).sum();
                if (groupLeader.getBrokerage().doubleValue() - sum < 0) {
                    throw new ServiceException("佣金申请金额已达上限");
                }
            }
            GroupBpavawiceOrder bpavawiceOrder = new GroupBpavawiceOrder();
            bpavawiceOrder.setAppmodelId(withdrawMoneyVO.getAppmodelId());
            bpavawiceOrder.setApplyforState(0);
            bpavawiceOrder.setGroupLeaderId(withdrawMoneyVO.getGroupLeaderId());
            bpavawiceOrder.setOutBpavawice(withdrawMoneyVO.getWithdrawMoney());
            bpavawiceOrder.setCreateTime(DateUtil.date());
            bpavawiceOrder.setFormId(withdrawMoneyVO.getFormId());
            bpavawiceOrder.setOutType(withdrawMoneyVO.getOptionType());
            bpavawiceOrder.setGroupBpavawiceOrderId(IdGenerateUtils.getItemId());
            return groupBpavawiceOrderService.save(bpavawiceOrder);
        } else {
            throw new ServiceException("非法操作");
        }
    }

    @Override
    public ResultPage<List<GroupBackstageVO>> groupBackstage(String groupLeaderId, Integer searchType,
                                                             String appmodelId, Integer pageNum, Integer pageSize) {
        List<GroupOrderDTO> orderDTOS = orderService
                .getGroupOrder(groupLeaderId, searchType, appmodelId, pageNum, pageSize);
        List<GroupBackstageVO> groupBackstageVOS = new ArrayList<>();
        ResultPage<List<GroupBackstageVO>> resultPage = new ResultPage<>();
        resultPage.setTotle(0L);
        if (orderDTOS != null && orderDTOS.size() > 0) {
            resultPage.setTotle(PageInfo.of(orderDTOS).getTotal());
            Map<Long, List<GroupOrderDTO>> orderMap = orderDTOS.stream()
                    .collect(Collectors.groupingBy(Order::getWxuserId));
            //查询用户信息
            String wxuserIds = orderDTOS.stream().map(obj -> obj.getWxuserId().toString())
                    .collect(Collectors.joining(","));
            Map<Long, Wxuser> wxuserMap = wxuserService.findByIds(wxuserIds).stream()
                    .collect(Collectors.toMap(Wxuser::getWxuserId, v -> v));
            //查询活动
            List<Long> orderId = orderDTOS.stream().map(Order::getOrderId).collect(Collectors.toList());
            Condition condition = new Condition(OrderDetail.class);
            condition.createCriteria().andIn("orderId", orderId);
            List<OrderDetail> orderDetails = detailService.findByCondition(condition);
            String activityIds = orderDetails.stream().map(obj -> obj.getActivityId().toString()).distinct()
                    .collect(Collectors.joining(","));
            List<Activity> activities = activityService.findByIds(activityIds);
            Map<String, Activity> activityMap = new HashMap<>(8);
            for (OrderDetail order : orderDetails) {
                for (Activity activity : activities) {
                    if (order.getActivityId().equals(activity.getActivityId())) {
                        activityMap.put(order.getOrderId().toString(), activity);
                    }
                }
            }
            orderMap.forEach((k, v) -> {
                Wxuser wxuser = wxuserMap.get(k);
                if (wxuser != null) {
                    //按地址分组
                    Map<String, List<GroupOrderDTO>> addressMap = v.stream()
                            .collect(Collectors.groupingBy(GroupOrderDTO::getBuyerAddress));
                    addressMap.forEach((adders, dtos) -> {
                        List<GroupOrderVO> groupOrderVOS = new ArrayList<>();
                        dtos.forEach(dto -> {
                            GroupOrderVO groupOrderVO = BeanMapper.map(dto, GroupOrderVO.class);
                            groupOrderVO.setForecastReceiveTime(DateUtil.parseDateTime(
                                    activityMap.get(groupOrderVO.getOrderId().toString()).getForecastReceiveTime()));
                            groupOrderVO.setBuyerName(dto.getBuyerName());
                            groupOrderVO.setBuyerPhone(dto.getBuyerPhone());
                            //封装订单详情
                            List<OrderDetail> orderDetailList = dto.getOrderDetailList();
                            groupOrderVO.setTotleFee(new BigDecimal(0));
                            orderDetailList.forEach(obj -> {
                                BigDecimal sub = NumberUtil.sub(obj.getGoodsPrice(), obj.getPreferential());
                                if (sub.doubleValue() < OrderConstant.MIN_PAYFEE) {
                                    obj.setPreferential(new BigDecimal(OrderConstant.MIN_PAYFEE));
                                } else {
                                    obj.setPreferential(sub);
                                }
                                BigDecimal value = NumberUtil.add(groupOrderVO.getTotleFee(), sub)
                                        .setScale(2, BigDecimal.ROUND_HALF_UP);
                                groupOrderVO.setTotleFee(value);
                            });
                            List<GroupOrderDtailVO> groupOrderDtailVOS = BeanMapper
                                    .mapList(orderDetailList, GroupOrderDtailVO.class);
                            groupOrderVO.setGroupOrderDtailVOList(groupOrderDtailVOS);
                            groupOrderVOS.add(groupOrderVO);
                        });
                        GroupBackstageVO backstageVO = new GroupBackstageVO();
                        if (StringUtils.isNotBlank(wxuser.getIcon())) {
                            backstageVO.setIcon(wxuser.getIcon());
                        }
                        if (StringUtils.isNotBlank(wxuser.getWxuserName())) {
                            backstageVO.setWxuserName(wxuser.getWxuserName());
                        }
                        backstageVO.setUserAdders(groupOrderVOS.get(0).getBuyerAddress());
                        backstageVO.setWxuserId(wxuser.getWxuserId());
                        backstageVO.setGroupOrderVOList(groupOrderVOS);
                        backstageVO.setOrderTotlePayfee(new BigDecimal(0));
                        groupOrderVOS.forEach(obj -> {
                            BigDecimal value = NumberUtil.add(obj.getTotleFee(), backstageVO.getOrderTotlePayfee());
                            backstageVO.setOrderTotlePayfee(value);
                        });
                        groupBackstageVOS.add(backstageVO);
                    });
                }
            });
        }
        resultPage.setList(groupBackstageVOS);
        return resultPage;
    }

    @Override
    public int groupPostil(GroupPostilVO withdrawMoneyVO) {
        List<Long> orderIds = Arrays.stream(withdrawMoneyVO.getOrderIds().split(",")).map(Long::valueOf)
                .collect(Collectors.toList());
        if (orderIds.size() == 0) {
            throw new ServiceException("订单id未空");
        }
        return orderService.updateGroupPostil(orderIds, withdrawMoneyVO.getPostilText());
    }


    @Override
    public List<GroupLeader> findByGroupleaderIds(List<String> groupleaderIdList) {
        if (CollectionUtil.isEmpty(groupleaderIdList)) {
            return new ArrayList<>();
        }
        Condition condition = new Condition(GroupLeader.class);
        condition.createCriteria().andIn("groupLeaderId", groupleaderIdList);
        List<GroupLeader> groupLeaders = tGroupLeaderMapper.selectByCondition(condition);
        String communityIds =
                groupLeaders.stream().map(o -> o.getCommunityId().toString()).collect(Collectors.joining(","));
        List<LineDetail> byCommunityIds = lineDetailService.findByCommunityIds(communityIds);
        Map<Long, Long> collect = byCommunityIds.stream()
                .collect(Collectors.toMap(o -> o.getCommunityId(), v -> v.getLineId()));
        groupLeaders.stream().forEach(o -> o.setLineId(collect.get(o.getCommunityId())));
        return groupLeaders;
    }

    @Override
    public List<GroupLeader> findByAppmodelId(String appmodelId) {
        return tGroupLeaderMapper.selectByAppmodelId(appmodelId);
    }

    @Override
    public GroupLeader findByWxuserId(Long wxuserId) {
        GroupLeader groupLeader = new GroupLeader();
        groupLeader.setWxuserId(wxuserId);
        groupLeader.setDeleteState(false);
        return tGroupLeaderMapper.selectOne(groupLeader);
    }

    @Override
    public List<GroupLeader> findByStatusAppmodelId(Integer status, String appmodelId) {
        return tGroupLeaderMapper.selectByStatusAppmodelId(status, appmodelId);
    }

    @Override
    public GroupLeader findBySoleGroupLeader(Long communityId) {
        GroupLeader groupLeader = new GroupLeader();
        groupLeader.setCommunityId(communityId);
        groupLeader.setDeleteState(Boolean.FALSE);
        groupLeader.setStatus(1);
        return tGroupLeaderMapper.selectByCommunityId(groupLeader);
    }

    @Override
    public void groupApplyRegisterV119(GroupApplyRegisterV119VO groupLeaderApply) {
        List<GroupLeader> groupLeaders = tGroupLeaderMapper.selectByWxUserId(groupLeaderApply.getWxuserId());
        Condition condition = new Condition(Community.class);
        condition.createCriteria().andEqualTo("communityName", groupLeaderApply.getCommunityName())
                .andEqualTo("appmodelId", groupLeaderApply.getAppmodelId()).andEqualTo("delFlag", 0);
        List<Community> communities = communityService.findByCondition(condition);
        if (CollectionUtil.isNotEmpty(communities)) {
            //存在同名小区则不能申请
            throw new ServiceException("已经存在一个与你申请小区同名的小区");
        }
        if (!groupLeaders.isEmpty()) {
            List<GroupLeader> collect = groupLeaders.stream()
                    .filter(groupLeader -> groupLeader.getStatus() == GroupLeader.Status.WAITAUDIT)
                    .collect(Collectors.toList());
            if (!collect.isEmpty()) {
                throw new ServiceException("您的账号已在待审核中");
            }
            List<GroupLeader> collect1 = groupLeaders.stream()
                    .filter(groupLeader -> groupLeader.getStatus() == GroupLeader.Status.NORMAL)
                    .collect(Collectors.toList());
            if (!collect1.isEmpty()) {
                throw new ServiceException("您的账号已经是团长");
            }
            List<GroupLeader> collect2 = groupLeaders.stream()
                    .filter(groupLeader -> groupLeader.getStatus() == GroupLeader.Status.LOCK)
                    .collect(Collectors.toList());
            if (!collect2.isEmpty()) {
                throw new ServiceException("您的账号已经是团长，且已被商家禁用");
            }
        }
        GroupLeader groupLeader = new GroupLeader();
        groupLeader.setStatus(0);
        groupLeader.setDeleteState(false);
        groupLeader.setWxuserId(groupLeaderApply.getWxuserId());
        groupLeader.setAppmodelId(groupLeaderApply.getAppmodelId());
        groupLeader.setGroupPhone(groupLeaderApply.getGroupPhone());
        groupLeader.setGroupName(groupLeaderApply.getGroupName());
        groupLeader.setAddress(groupLeaderApply.getPickUpAddr());
        groupLeader.setFormId(groupLeaderApply.getFormId());
        groupLeader.setApplyCommunityName(groupLeaderApply.getCommunityName());
        groupLeader.setGroupLocation(groupLeaderApply.getGroupLocation());
        groupLeader.setGroupLeaderId("TZ" + IdGenerateUtils.getItemId());
        tGroupLeaderMapper.insertSelective(groupLeader);
    }

    @Override
    public void groupApplyV119(GroupApplyV119VO groupApplyVO) {
        GroupLeader groupLeader = tGroupLeaderMapper.selectByPrimaryKey(groupApplyVO.getId());
        if (groupLeader == null) {
            throw new ServiceException("团长不存在");
        }
        //0-拒绝 1-同意
        if (groupApplyVO.getOptionType().equals(GroupApplyV119VO.OptionType.REFUSE) && groupLeader.getStatus()
                .equals(GroupLeaderStatus.STATUS_AWAIT)) {
            groupLeader.setStatus(GroupLeaderStatus.STATUS_REPULSE);
        } else if (groupApplyVO.getOptionType().equals(1) && groupLeader.getStatus()
                .equals(GroupLeaderStatus.STATUS_AWAIT)) {
            //同意需要判断小区是否已存在团长,手机号是否已注册
            this.isMayRegister119(groupLeader.getAppmodelId(), groupLeader.getGroupLeaderId(),
                    groupLeader.getGroupPhone(), true);
            groupLeader.setStatus(GroupLeaderStatus.STATUS_NORMAL);
            //更新用户状态为团长
            Wxuser wxuser = wxuserService.findById(groupLeader.getWxuserId());
            wxuser.setUserStatus(2);
            //将用户申请的小区添加到小区表中
            Long communityId = communityService
                    .saveCommunityV2(groupApplyVO.voToCommunityVo(), groupApplyVO.getAppmodelId());
            if (communityId == null) {
                throw new ServiceException("创建小区失败，请联系系统管理员");
            }
            //将小区添加到线路中
            List<Long> communityIds = new ArrayList<>();
            communityIds.add(communityId);
            lineDetailService.addLineDetails(groupApplyVO.getLineId(), groupApplyVO.getAppmodelId(), communityIds);
            groupLeader.setLineId(groupApplyVO.getLineId());
            groupLeader.setCommunityId(communityId);
            groupLeader.setBrokerage(new BigDecimal(0.0));
            groupLeader.setCreateTime(new Date());
            groupLeader.setGroupName(groupApplyVO.getGroupName());
            groupLeader.setGroupPhone(groupApplyVO.getGroupPhone());
            groupLeader.setAddress(groupApplyVO.getPickUpAddr());
            wxuserService.update(wxuser);
            //更新用户缓存
            userUtil.depositCache(wxuser, 1);
            //2-禁用 3-开启
        } else if (groupApplyVO.getOptionType().equals(GroupApplyV119VO.OptionType.LOCK) && groupLeader.getStatus()
                .equals(GroupLeaderStatus.STATUS_NORMAL)) {
            groupLeader.setStatus(GroupLeaderStatus.STATUS_DISABLE);
        } else if (groupApplyVO.getOptionType().equals(GroupApplyV119VO.OptionType.OPEN) && groupLeader.getStatus()
                .equals(GroupLeaderStatus.STATUS_DISABLE)) {
            groupLeader.setStatus(GroupLeaderStatus.STATUS_NORMAL);
        } else {
            throw new ServiceException("非法操作");
        }
        tGroupLeaderMapper.updateByPrimaryKeySelective(groupLeader);

        //发送审核通过模板消息
        Map<String, Object> map = new HashMap<>(8);
        map.put("appmodelId", groupLeader.getAppmodelId());
        map.put("wxuserId", groupLeader.getWxuserId());
        map.put("communityId", groupLeader.getCommunityId());
        map.put("formId", groupLeader.getFormId());
        map.put("time", DateUtil.date().toString());
        map.put("type", 101);
        if (groupLeader.getStatus() == 1) {
            map.put("applyResult", "您的申请已通过");
        } else if (GroupLeaderStatus.STATUS_REPULSE.equals(groupLeader.getStatus())) {
            map.put("applyResult", "您的申请未通过");
        }
        activeDelaySendJobHandler
                .savaTask(JSON.toJSONString(map), ActiviMqQueueName.ORDER_MINIPROGRAM_TEMPLATE_MESSAGE, 0L,
                        groupLeader.getAppmodelId(), false);
    }

    @Override
    public void groupUpdateV119(GroupUpdateV119VO groupLeader) {
        GroupLeader groupLeader1 = tGroupLeaderMapper.selectByPrimaryKey(groupLeader.getId());
        if (groupLeader1 == null) {
            throw new ServiceException("团长不存在");
        }
        groupLeader1.setLineId(groupLeader.getLineId());
        groupLeader1.setAddress(groupLeader.getPickUpAddr());
        groupLeader1.setGroupPhone(groupLeader.getGroupPhone());
        groupLeader1.setGroupName(groupLeader.getGroupName());
        groupLeader1.setGroupLocation(groupLeader.getLocation());
        tGroupLeaderMapper.updateByPrimaryKey(groupLeader1);

        //修改小区信息
        Community byId = communityService.findById(groupLeader1.getCommunityId());
        byId.setPcaAdr(groupLeader.getPcaAdr());
        byId.setStreetName(groupLeader.getStreetName());
        byId.setCommunityName(groupLeader.getCommunityName());
        byId.setStreetId(groupLeader.getStreetId());
        byId.setCityId(groupLeader.getCityId());
        byId.setProvinceId(groupLeader.getProvinceId());
        byId.setAreaName(areasService.findBy("areaid", groupLeader.getAreaId()).getArea());
        byId.setLocation(groupLeader.getLocation());
        communityService.update(byId);

        //删除原线路下的小区信息,将小区投放到新线路
        List<Long> community = Collections.singletonList(groupLeader1.getCommunityId());
        lineDetailService.deleteByCommunityIds(community);
        lineDetailService.addLineDetails(groupLeader.getLineId(), groupLeader.getAppmodelId(), community);
    }

    @Override
    public Integer findCustomersByWxUserId(Long wxuserId) {
        return tGroupLeaderMapper.findCustomersByWxUserId(wxuserId);
    }

    @Override
    public MyCommissionResult groupLeaderCommission(Long wxuserId) {
        List<GroupLeader> groupLeaders = tGroupLeaderMapper.selectByWxUserId(wxuserId);
        GroupLeader leader =
                groupLeaders.stream().filter(groupLeader -> groupLeader.getStatus() == 1).findFirst().get();
        MyCommissionResult myCommissionResult = new MyCommissionResult();
        //待提现佣金
        myCommissionResult.setNotWithdraw(leader.getBrokerage());
        //已提现佣金
        myCommissionResult.setWithdraw(groupBpavawiceOrderService.countCumulativeCashWithdrawal(leader.getGroupLeaderId()));
        //待结算
        BigDecimal settlement = orderDetailService.countSettlementCommission(leader.getGroupLeaderId());
        myCommissionResult.setNotSettlement(settlement);
        return myCommissionResult;
    }
}
