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
import com.mds.group.purchase.core.AbstractService;
import com.mds.group.purchase.exception.ServiceException;
import com.mds.group.purchase.user.dao.ConsigneeMapper;
import com.mds.group.purchase.user.model.Consignee;
import com.mds.group.purchase.user.service.ConsigneeService;
import com.mds.group.purchase.user.vo.AddersVO;
import com.mds.group.purchase.utils.BeanMapper;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Condition;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;


/**
 * The type Consignee service.
 *
 * @author CodeGenerator
 * @date 2018 /11/28
 */
@Service
public class ConsigneeServiceImpl extends AbstractService<Consignee> implements ConsigneeService {

    @Resource
    private ConsigneeMapper tConsigneeMapper;

    @Override
    public int addersCraete(AddersVO addersVO) {
        Consignee map = BeanMapper.map(addersVO, Consignee.class);
        List<Consignee> consignees = addersGet(addersVO.getWxuserId());
        if (consignees != null && consignees.size() > 0) {
            map.setDefaultAdderss(false);
        } else {
            map.setDefaultAdderss(true);
        }
        setDefaultAdderss(addersVO.getWxuserId());
        return tConsigneeMapper.insert(map);
    }

    @Override
    public List<Consignee> addersGet(Long wxuserId) {
        Consignee consignee = new Consignee();
        consignee.setWxuserId(wxuserId);
        return tConsigneeMapper.select(consignee);
    }

//	@Override
//	public int addersUpdate(Consignee consignee) {
//		if (consignee.getConsigneeId() == null) {
//			throw new ServiceException("id为空");
//		}
//		consignee.setAppmodelId(null);
//		consignee.setWxuserId(null);
//		tConsigneeMapper.updateByPrimaryKeySelective(consignee);
//		if (consignee.getDefaultAdderss() != null) {
//			if (consignee.getDefaultAdderss().equals(true)) {
//				Consignee temp = new Consignee();
//				temp.setDefaultAdderss(false);
//				Condition condition = new Condition(Consignee.class);
//				condition.createCriteria().andEqualTo("wxuserId", consignee.getWxuserId())
//						.andNotEqualTo("consigneeId", consignee.getConsigneeId());
//				tConsigneeMapper.updateByConditionSelective(temp, condition);
//			}
//		}
//		return 1;
//	}

    @Override
    public int addersUpdate(Consignee consignee) {
        if (consignee.getConsigneeId() == null) {
            throw new ServiceException("id为空");
        }
        if (consignee.getDefaultAdderss() != null) {
            if (consignee.getDefaultAdderss().equals(true)) {
                Consignee temp = new Consignee();
                temp.setDefaultAdderss(false);
                Condition condition = new Condition(Consignee.class);
                condition.createCriteria().andEqualTo("wxuserId", consignee.getWxuserId())
                        .andNotEqualTo("consigneeId", consignee.getConsigneeId());
                tConsigneeMapper.updateByConditionSelective(temp, condition);
            }
            tConsigneeMapper.updateByPrimaryKeySelective(consignee);
        }
        return 1;
    }

    @Override
    public int addersDelete(Long consigneeId, Long wxuserId) {
        Consignee consignee = tConsigneeMapper.selectByPrimaryKey(consigneeId);
        if (tConsigneeMapper.deleteByPrimaryKey(consigneeId) > 0) {
            if (consignee.getDefaultAdderss().equals(true)) {
                setDefaultAdderss(wxuserId);
            }
            return 1;
        }
        return 0;
    }

    @Override
    public List<Consignee> findDefaultByWxuserIds(List<Long> wxuserIds) {
        Condition condition = new Condition(Consignee.class);
        condition.createCriteria().orIn("wxuserId", wxuserIds);
        return tConsigneeMapper.selectByCondition(condition);
    }

    private void setDefaultAdderss(Long wxuserId) {
        Condition condition = new Condition(Consignee.class);
        condition.createCriteria().andEqualTo("wxuserId", wxuserId);
        List<Consignee> consignees = tConsigneeMapper.selectByCondition(condition);
        if (CollectionUtil.isNotEmpty(consignees)) {
            List<Consignee> collect = consignees.stream().filter(obj -> obj.getDefaultAdderss().equals(true))
                    .collect(Collectors.toList());
            if (CollectionUtil.isEmpty(collect)) {
                Consignee consignee1 = consignees.get(0);
                consignee1.setDefaultAdderss(true);
                tConsigneeMapper.updateByPrimaryKeySelective(consignee1);
            }
        }
    }
}
