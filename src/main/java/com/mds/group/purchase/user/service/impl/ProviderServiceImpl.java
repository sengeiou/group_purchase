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
import com.github.pagehelper.PageHelper;
import com.google.common.collect.Maps;
import com.mds.group.purchase.configurer.GroupMallProperties;
import com.mds.group.purchase.constant.ActiviMqQueueName;
import com.mds.group.purchase.constant.RedisPrefix;
import com.mds.group.purchase.core.AbstractService;
import com.mds.group.purchase.exception.ServiceException;
import com.mds.group.purchase.goods.service.GoodsService;
import com.mds.group.purchase.jobhandler.ActiveDelaySendJobHandler;
import com.mds.group.purchase.order.model.Comment;
import com.mds.group.purchase.order.service.CommentService;
import com.mds.group.purchase.user.dao.ProviderMapper;
import com.mds.group.purchase.user.model.Provider;
import com.mds.group.purchase.user.service.ProviderService;
import com.mds.group.purchase.user.vo.*;
import com.mds.group.purchase.utils.BeanMapper;
import com.mds.group.purchase.utils.IdGenerateUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Condition;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;


/**
 * The type Provider service.
 *
 * @author CodeGenerator
 * @date 2018 /11/27
 */
@Service
public class ProviderServiceImpl extends AbstractService<Provider> implements ProviderService {

    @Resource
    private GoodsService goodsService;
    @Resource
    private CommentService commentService;
    @Resource
    private ProviderMapper tProviderMapper;
    @Resource
    private ActiveDelaySendJobHandler activeDelaySendJobHandler;
    @Resource
    private RedisTemplate redisTemplate;

    @Override
    public List<ProviderManagerVO> providerApplySearch(Integer searchType, String appmodelId, String providerName,
                                                       String providerPhone, String providerId, Integer pageNum,
                                                       Integer pageSize) {
        Map<String, Object> map = new HashMap<>(8);
        map.put("appmodelId", appmodelId);
        map.put("searchType", searchType);
        if (StringUtils.isNotBlank(providerName)) {
            String s = "%";
            char[] chars = providerName.toCharArray();
            for (char str : chars) {
                s = s.concat(String.valueOf(str)).concat("%");
            }
            map.put("providerName", s.concat("%"));
        }
        if (StringUtils.isNotBlank(providerPhone)) {
            map.put("providerPhone", providerPhone);
        }
        if (StringUtils.isNotBlank(providerId)) {
            map.put("providerId", providerId);
        }
        PageHelper.startPage(pageNum, pageSize, "create_time desc");
        List<ProviderManagerVO> managerVOS = tProviderMapper.selectProviderApplySearch(map);
        if (CollectionUtil.isNotEmpty(managerVOS)) {
            List<String> providerIdList =
                    managerVOS.stream().map(ProviderManagerVO::getProviderId).collect(Collectors.toList());
            List<Comment> commentList = commentService.findByProviderIds(providerIdList);
            Map<String, List<Comment>> commentMap = new HashMap<>(16);
            if (CollectionUtil.isNotEmpty(commentList)) {
                commentMap = commentList.stream()
                        .collect(Collectors.groupingBy(Comment::getProviderId));
            }
            for (ProviderManagerVO managerVO : managerVOS) {
                List<Comment> comments = commentMap.get(managerVO.getProviderId());
                if (CollectionUtil.isNotEmpty(comments)) {
                    double grade = comments.stream().mapToDouble(Comment::getGroupScore).average().getAsDouble();
                    managerVO.setGrade(NumberUtil.round(grade, 1).toString());
                } else {
                    managerVO.setGrade("5.0");
                }
            }
        }
        return managerVOS;
    }

    /**
     * 商品
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<String> providerDelete(DeleteVO deleteVO) {
        //删除供货商,如供应商有商品则不提示用户下架商品再删除
        List<String> providerIds = Arrays.asList(deleteVO.getIds().split(","));
        List<String> putaway = goodsService.findByProviderPutawayOfGoods(providerIds, deleteVO.getAppmodelId());
        if (CollectionUtil.isNotEmpty(putaway)) {
            return putaway;
        }
        int i = tProviderMapper.deleteProviderDelete(providerIds);
        if (i > 0) {
            Map<String, Object> map = Maps.newHashMapWithExpectedSize(2);
            map.put("providerIds", providerIds);
            map.put("optionType", 1);
            goodsService.updateProviderGoods(map);
        }
        return new ArrayList<>();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int providerStatu(ProviderStatuVO providerStatuVO) {
        Provider provider = new Provider();
        provider.setProviderId(providerStatuVO.getId());
        provider.setProviderStatus(providerStatuVO.getOptionType());
        //禁用供应商,商品需要全部下架
        if (providerStatuVO.getOptionType().equals(0)) {
            Map<String, Object> map = new HashMap<>();
            map.put("providerId", providerStatuVO.getId());
            map.put("optionType", 2);
            goodsService.updateProviderGoods(map);
        } else if (providerStatuVO.getOptionType().equals(1)) {

        } else {
            throw new ServiceException("非法操作");
        }
        return tProviderMapper.updateByPrimaryKeySelective(provider);
    }

    @Override
    public int providerApplyRegister(ProviderApplyRegisterVO providerApplyRegisterVO) {
        Provider condition = new Provider();
        condition.setProviderPhone(providerApplyRegisterVO.getProviderPhone());
        condition.setAppmodelId(providerApplyRegisterVO.getAppmodelId());
        condition.setDeleteState(false);
        Provider exit = tProviderMapper.selectOne(condition);
        if (exit != null) {
            if (exit.getApplyState() == null) {
                //申请状态为空，表示供应商手动增加
                //改团长已存在
                throw new ServiceException("该手机号已是供应商");
            } else {
                switch (exit.getApplyState()) {
                    case 0:
                        throw new ServiceException("该手机号已在申请中");
                    case 1:
                        throw new ServiceException("该手机号已是供应商");
                    default:
                        break;
                }
            }
        }
        Provider provider = BeanMapper.map(providerApplyRegisterVO, Provider.class);
        provider.setCreateTime(DateUtil.date());
        provider.setApplyState(0);
        provider.setProviderStatus(0);
        provider.setDeleteState(false);
        provider.setProviderId("GY" + IdGenerateUtils.getItemId());
        return tProviderMapper.insertSelective(provider);
    }

    @Override
    public int providerApply(ProviderApplyVO providerApplyVO) {
        if (providerApplyVO.getOptionType().equals(1)) {
            Condition condition = new Condition(Provider.class);
            condition.createCriteria().andEqualTo("appmodelId", providerApplyVO.getAppmodelId())
                    .andEqualTo("providerPhone", providerApplyVO.getProviderPhone()).andEqualTo("applyState", 1)
                    .andIn("providerStatus", Arrays.asList(0, 1));
            if (tProviderMapper.selectCountByCondition(condition) > 0) {
                throw new ServiceException("该手机号已注册为供应商");
            }
        }
        if (providerApplyVO.getOptionType().equals(0) || providerApplyVO.getOptionType().equals(1)) {
            Provider provider = tProviderMapper.selectByPrimaryKey(providerApplyVO.getId());
            if (provider == null) {
                throw new ServiceException("申请记录不存在");
            }
            if (providerApplyVO.getOptionType().equals(0)) {
                provider.setApplyState(2);
            } else if (providerApplyVO.getOptionType().equals(1)) {
                provider.setApplyState(1);
                provider.setProviderStatus(1);
                provider.setProviderArea(providerApplyVO.getProviderArea());
                provider.setProviderAddress(providerApplyVO.getProviderAddress());
            }
            //发送审核通过模板消息
            if (StringUtils.isNotBlank(provider.getWxuserId())) {
                String key =
                        GroupMallProperties.getRedisPrefix() + provider.getAppmodelId() + RedisPrefix.FROMID + provider.getWxuserId();
                List<String> formIds = (List<String>) redisTemplate.opsForValue().get(key);
                if (CollectionUtil.isNotEmpty(formIds)) {
                    String formId = formIds.get(0);
                    formIds.remove(0);
                    redisTemplate.opsForValue().set(key, formIds);
                    Map<String, Object> map = new HashMap<>(8);
                    map.put("appmodelId", provider.getAppmodelId());
                    map.put("wxuserId", provider.getWxuserId());
                    map.put("formId", formId);
                    map.put("time", DateUtil.date().toString());
                    map.put("type", 101);
                    if (providerApplyVO.getOptionType() == 1) {
                        map.put("applyResult", "您的申请已通过");
                    } else {
                        map.put("applyResult", "您的申请未通过");
                    }
                    activeDelaySendJobHandler
                            .savaTask(JSON.toJSONString(map), ActiviMqQueueName.ORDER_MINIPROGRAM_TEMPLATE_MESSAGE, 0L,
                                    provider.getAppmodelId(), false);
                }
            }
            return tProviderMapper.updateByPrimaryKeySelective(provider);
        } else if (providerApplyVO.getOptionType().equals(2)) {
            Provider provider = BeanMapper.map(providerApplyVO, Provider.class);
            provider.setCreateTime(DateUtil.date());
            provider.setDeleteState(false);
            provider.setProviderStatus(1);
            provider.setProviderId("GY" + IdGenerateUtils.getItemId());
            return tProviderMapper.insert(provider);
        } else if (providerApplyVO.getOptionType().equals(3)) {
            Provider provider = BeanMapper.map(providerApplyVO, Provider.class);
            if (!StringUtils.isNotBlank(providerApplyVO.getId())) {
                throw new ServiceException("id不能为空");
            }
            provider.setProviderId(providerApplyVO.getId());
            return tProviderMapper.updateByPrimaryKeySelective(provider);
        }
        throw new ServiceException("非法操作");
    }

    @Override
    public List<Provider> findByProviderId(List<String> providerIdList) {
        return tProviderMapper.selectByProviderIds(providerIdList);
    }
}
