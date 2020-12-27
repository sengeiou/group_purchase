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

package com.mds.group.purchase.shop.service;

import com.mds.group.purchase.core.Service;
import com.mds.group.purchase.shop.model.ShopFunction;
import com.mds.group.purchase.shop.vo.ShopCreateUpdateVO;


/**
 * Created by CodeGenerator on 2018/11/27.
 *
 * @author pavawi
 */
public interface ShopFunctionService extends Service<ShopFunction> {

    /**
     * 更新商铺设置
     *
     * @param shopCreateUpdateVO the shop create update vo
     * @return the int
     */
    int shopCreateUpdate(ShopCreateUpdateVO shopCreateUpdateVO);

    /**
     * 获取商铺设置
     *
     * @param appmodelId the appmodel id
     * @return the shop create update vo
     */
    ShopCreateUpdateVO findByAppmodelId(String appmodelId);
}
