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

package com.mds.group.purchase.shop.service.impl;

import cn.hutool.http.HttpRequest;
import com.alibaba.fastjson.JSONObject;
import com.mds.group.purchase.core.AbstractService;
import com.mds.group.purchase.shop.dao.ManagerMapper;
import com.mds.group.purchase.shop.model.Manager;
import com.mds.group.purchase.shop.service.ManagerService;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Condition;

import javax.annotation.Resource;


/**
 * The type Manager service.
 *
 * @author CodeGenerator
 * @date 2018 /11/27
 */
@Service
public class ManagerServiceImpl extends AbstractService<Manager> implements ManagerService {

    @Resource
    private ManagerMapper tManagerMapper;

    @Override
    public void updateEnterprisePayState(String appmodelId, Boolean enterprisePayState) {
        Manager manager = new Manager();
        manager.setEnterprisePayState(enterprisePayState);
        Condition condition = new Condition(Manager.class);
        condition.createCriteria().andEqualTo("appmodelId", appmodelId);
        tManagerMapper.updateByConditionSelective(manager, condition);
    }

    @Override
    public Integer updateSecretKey(String appId, String certificatePath, String mchId, String mchKey) {
        Manager manager = new Manager();
        manager.setMchKey(mchKey);
        manager.setMchId(mchId);
        int index = certificatePath.indexOf("certificate");
        certificatePath = certificatePath.substring(index - 9);
        manager.setCertificatePath(certificatePath);
        Condition condition = new Condition(Manager.class);
        condition.createCriteria().andEqualTo("appId", appId);
        if (tManagerMapper.updateByConditionSelective(manager, condition) > 0) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("appId", appId);
            jsonObject.put("mchId", mchId);
            jsonObject.put("mchKey", mchKey);
            jsonObject.put("certificatePath", certificatePath);
            HttpRequest.put("https://www.superprism.cn/medusaplatform/MiniProgramy/update/secret/key")
                    .contentType("application/json;charset=UTF-8").body(jsonObject.toJSONString()).execute();
            return 1;
        }
        return 0;
    }

    @Override
    public Manager findByAppmodelId(String appmodelId) {
        Manager manager = new Manager();
        manager.setAppmodelId(appmodelId);
        return tManagerMapper.selectOne(manager);
    }
}
