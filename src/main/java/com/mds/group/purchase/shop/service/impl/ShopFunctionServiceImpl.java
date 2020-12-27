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

import com.mds.group.purchase.core.AbstractService;
import com.mds.group.purchase.exception.ServiceException;
import com.mds.group.purchase.shop.dao.ShopFunctionMapper;
import com.mds.group.purchase.shop.model.Manager;
import com.mds.group.purchase.shop.model.ShopFunction;
import com.mds.group.purchase.shop.service.ManagerService;
import com.mds.group.purchase.shop.service.ShopFunctionService;
import com.mds.group.purchase.shop.vo.ShopCreateUpdateVO;
import com.mds.group.purchase.utils.BeanMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;


/**
 * The type Shop function service.
 *
 * @author CodeGenerator
 * @date 2018 /11/27
 */
@Service
public class ShopFunctionServiceImpl extends AbstractService<ShopFunction> implements ShopFunctionService {

    @Resource
    private ManagerService managerService;
    @Resource
    private ShopFunctionMapper tShopFunctionMapper;

    @Override
    public int shopCreateUpdate(ShopCreateUpdateVO shopCreateUpdateVO) {
        ShopFunction map = BeanMapper.map(shopCreateUpdateVO, ShopFunction.class);
        if (map.getShopFunctionId() == null) {
            ShopFunction shopFunction = new ShopFunction();
            shopFunction.setAppmodelId(shopCreateUpdateVO.getAppmodelId());
            ShopFunction shopFunction1 = tShopFunctionMapper.selectOne(shopFunction);
            if (shopFunction1 != null) {
                throw new ServiceException("不能重复添加");
            }
            tShopFunctionMapper.insert(map);
        }
        if (map.getShopFunctionId() != null) {
            tShopFunctionMapper.updateByPrimaryKeySelective(map);
        }
        managerService.updateEnterprisePayState(shopCreateUpdateVO.getAppmodelId(),
                shopCreateUpdateVO.getEnterprisePayState());
        return 1;
    }

    @Override
    public ShopCreateUpdateVO findByAppmodelId(String appmodelId) {
        ShopFunction shopFunction = new ShopFunction();
        shopFunction.setAppmodelId(appmodelId);
        ShopFunction shopFunction1 = tShopFunctionMapper.selectOne(shopFunction);
        ShopCreateUpdateVO map = BeanMapper.map(shopFunction1, ShopCreateUpdateVO.class);
        if (map != null) {
            Manager manager = managerService.findBy("appmodelId", appmodelId);
            map.setEnterprisePayState(manager.getEnterprisePayState());
        }
        return map;
    }
}
