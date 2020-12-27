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

import com.mds.group.purchase.core.AbstractService;
import com.mds.group.purchase.order.dao.GoodsSortingOrderDetailMapper;
import com.mds.group.purchase.order.model.GoodsSortingOrderDetail;
import com.mds.group.purchase.order.service.GoodsSortingOrderDetailService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;


/**
 * The type Goods sorting order detail service.
 *
 * @author CodeGenerator
 * @date 2019 /02/23
 */
@Service
public class GoodsSortingOrderDetailServiceImpl extends AbstractService<GoodsSortingOrderDetail> implements
        GoodsSortingOrderDetailService {
    @Resource
    private GoodsSortingOrderDetailMapper tGoodsSortingOrderDetailMapper;

}
