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

package com.mds.group.purchase.shop.dao;

import com.mds.group.purchase.shop.model.Shop;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * The interface Shop repository.
 *
 * @author pavawi
 */
public interface ShopRepository extends MongoRepository<Shop, String> {

    /**
     * 根据appmodelId查询店铺信息
     *
     * @param appmodelId the appmodel id
     * @return Shop by appmodel id
     */
    Shop getByAppmodelId(String appmodelId);

}