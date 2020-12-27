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

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateUtil;
import com.github.dozermapper.core.DozerBeanMapperBuilder;
import com.github.pagehelper.PageHelper;
import com.mds.group.purchase.constant.Common;
import com.mds.group.purchase.core.AbstractService;
import com.mds.group.purchase.exception.ServiceException;
import com.mds.group.purchase.user.dao.ProviderApplyMapper;
import com.mds.group.purchase.user.model.Provider;
import com.mds.group.purchase.user.model.ProviderApply;
import com.mds.group.purchase.user.service.ProviderApplyService;
import com.mds.group.purchase.user.service.ProviderService;
import com.mds.group.purchase.user.vo.ProviderApplyRegisterVO;
import com.mds.group.purchase.user.vo.ProviderApplyVO;
import com.mds.group.purchase.user.vo.ProviderManagerVO;
import com.mds.group.purchase.utils.IdGenerateUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Condition;

import javax.annotation.Resource;
import java.util.*;


/**
 * The type Provider apply service.
 *
 * @author CodeGenerator
 * @date 2018 /11/27
 */
@Service
public class ProviderApplyServiceImpl extends AbstractService<ProviderApply> implements ProviderApplyService {

    @Resource
    private ProviderApplyMapper tProviderApplyMapper;
    @Resource
    private ProviderService providerService;

    @Override
    public int providerApplyRegister(ProviderApplyRegisterVO providerApplyRegisterVO) {
        ProviderApply providerApply = new ProviderApply();
        providerApply.setProviderPhone(providerApplyRegisterVO.getProviderPhone());
        providerApply.setAppmodelId(providerApplyRegisterVO.getAppmodelId());
        ProviderApply isExist = tProviderApplyMapper.selectOne(providerApply);
        if (isExist != null) {
            throw new ServiceException("已提交申请");
        }
        providerApply.setProviderName(providerApplyRegisterVO.getProviderName());
        providerApply.setGoodsClass(providerApplyRegisterVO.getGoodsClass());
        providerApply.setApplyTime(DateUtil.date());
        providerApply.setApplyState(0);
        providerApply.setProviderApplyid("GY" + IdGenerateUtils.getItemId());
        return tProviderApplyMapper.insert(providerApply);
    }

    @Override
    public List<ProviderManagerVO> providerApplySearch(Integer searchType, String appmodelId, String providerName,
                                                       String providerPhone, String providerId, Integer pageNum,
                                                       Integer pageSize) {
        Map<String, Object> map = new HashMap<>(8);
        map.put("appmodelId", appmodelId);
        map.put("searchType", searchType);
        if (StringUtils.isNotBlank(providerName)) {
            map.put("providerName", providerName);
        }
        if (StringUtils.isNotBlank(providerPhone)) {
            map.put("providerPhone", providerPhone);
        }
        if (StringUtils.isNotBlank(providerId)) {
            map.put("providerApplyId", providerId);
        }
        PageHelper.startPage(pageNum, pageSize);
        List<ProviderApply> providerApplies = tProviderApplyMapper.selectProviderApplySearch(map);
        List<ProviderManagerVO> managerVOS = new ArrayList<>(providerApplies.size());
        DozerBeanMapperBuilder.buildDefault().map(providerApplies, managerVOS);
        return managerVOS;
    }

    @Override
    public int providerApplyDelete(String ids) {
        List<String> idsList = Arrays.asList(StringUtils.split(Common.REGEX));
        return tProviderApplyMapper.deleteIds(idsList);
    }

    @Override
    public int providerApply(ProviderApplyVO providerApplyVO) {
        if (providerApplyVO.getOptionType().equals(0) || providerApplyVO.getOptionType().equals(1)) {
            ProviderApply providerApply = tProviderApplyMapper.selectByPrimaryKey(providerApplyVO.getId());
            if (providerApply == null) {
                throw new ServiceException("申请记录不存在");
            }
            if (providerApplyVO.getOptionType().equals(0)) {
                providerApply.setApplyState(2);
            } else if (providerApplyVO.getOptionType().equals(1)) {
                insertNewProvider(providerApplyVO);
                providerApply.setApplyState(1);
            }
            return tProviderApplyMapper.updateByPrimaryKeySelective(providerApply);
        } else if (providerApplyVO.getOptionType().equals(ProviderApplyVO.ADD)) {
            return insertNewProvider(providerApplyVO);
        }
        throw new ServiceException("非法操作");
    }


    private int insertNewProvider(ProviderApplyVO providerApplyVO) {
        Condition condition = new Condition(Provider.class);
        condition.createCriteria().andEqualTo("appmodelId", providerApplyVO.getAppmodelId())
                .andEqualTo("providerPhone", providerApplyVO.getProviderPhone());
        Provider exist = providerService.findByOneCondition(condition);
        if (exist != null) {
            throw new ServiceException("该手机号已注册为团长");
        }
        Provider provider = BeanUtil.toBean(providerApplyVO, Provider.class);
        provider.setCreateTime(DateUtil.date());
        provider.setDeleteState(false);
        provider.setProviderId("GY" + IdGenerateUtils.getItemId());
        provider.setProviderStatus(1);
        return providerService.save(provider);
    }


}
