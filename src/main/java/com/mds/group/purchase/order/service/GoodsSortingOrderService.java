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

package com.mds.group.purchase.order.service;

import com.mds.group.purchase.core.Service;
import com.mds.group.purchase.order.model.GoodsSortingOrder;
import com.mds.group.purchase.order.vo.GoodsSortingOrderViewVO;

import javax.servlet.http.HttpServletResponse;
import java.util.List;


/**
 * The interface Goods sorting order service.
 *
 * @author CodeGenerator
 * @date 2019 /02/23
 */
public interface GoodsSortingOrderService extends Service<GoodsSortingOrder> {

    /**
     * 查询商品分拣单
     *
     * @param appmodelId the appmodel id
     * @param sendBillId the send bill id
     * @return list list
     */
    List<GoodsSortingOrderViewVO> goodsSortingOrder(String appmodelId, Long sendBillId);

    /**
     * 导出线路分拣单
     *
     * @param appmodelId the appmodel id
     * @param pageNum    the page num
     * @param pageSize   the page size
     * @param sendBillId the send bill id
     * @param response   the response
     */
    void export(String appmodelId, Integer pageNum, Integer pageSize, Long sendBillId, HttpServletResponse response);

}
