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

package com.mds.group.purchase.order.dao;

import com.mds.group.purchase.core.Mapper;
import com.mds.group.purchase.order.model.GoodsSortingOrder;
import com.mds.group.purchase.order.vo.GoodsSortingOrderViewVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * The interface Goods sorting order mapper.
 *
 * @author pavawi
 */
public interface GoodsSortingOrderMapper extends Mapper<GoodsSortingOrder> {

    /**
     * 查询指定发货单的商品分拣单
     *
     * @param sendBillId the send bill id
     * @param appmodelId the appmodel id
     * @return list list
     */
    List<GoodsSortingOrderViewVO> selectByGoodsSortingOrder(@Param("sendBillId") Long sendBillId,
                                                            @Param("appmodelId") String appmodelId);
}