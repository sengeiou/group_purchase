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
import com.mds.group.purchase.order.model.NegotiateHistory;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * The interface Negotiate history mapper.
 *
 * @author pavawi
 */
public interface NegotiateHistoryMapper extends Mapper<NegotiateHistory> {
    /**
     * Select by order id list.
     *
     * @param orderId the order id
     * @param type    the type
     * @return the list
     */
    List<NegotiateHistory> selectByOrderId(@Param("orderId") Long orderId, @Param("type") String type);
}