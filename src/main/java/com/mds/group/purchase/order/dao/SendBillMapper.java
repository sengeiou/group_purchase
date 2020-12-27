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
import com.mds.group.purchase.order.model.SendBill;
import com.mds.group.purchase.order.result.SendBillResult;
import com.mds.group.purchase.order.vo.SendbillFindVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 发货单dao
 *
 * @author shuke
 * @date 2019 -2-18
 */
public interface SendBillMapper extends Mapper<SendBill> {

    /**
     * 根据传入的参数查询发货单
     *
     * @param appmodelId the appmodel id
     * @param status     the status
     * @return list list
     */
    List<SendBillResult> selectByFilterVoSelective(@Param("appmodelId") String appmodelId,
                                                   @Param("status") Integer status);

    /**
     * 根据传入的日期查询发货单
     *
     * @param appmodelId   the appmodel id
     * @param generateDate the generate date
     * @return list list
     */
    List<SendBill> selectByDate(@Param("appmodelId") String appmodelId, @Param("generateDate") String generateDate);

    /**
     * 查询最近3条发货单
     *
     * @param appmodelId the appmodel id
     * @return list list
     */
    List<SendBill> select3SendBill(String appmodelId);

    /**
     * 将发货单的对应数据减去参数
     *
     * @param sendBillId the send bill id
     * @param orders     the orders
     * @param amount     the amount
     * @param commission the commission
     */
    void updateInfo(
            @Param("sendBillId") Long sendBillId, @Param("orders") int orders, @Param("amount") double amount,
            @Param("commission") double commission);

    /**
     * 分拣单签收单查询发货单数据
     *
     * @param appmodelId the appmodel id
     * @return list list
     */
    List<SendbillFindVO> selectBySendbillFind(String appmodelId);

    /**
     * 更新指定发货单订单状态
     *
     * @param sendBillId the send bill id
     * @param status     the status
     * @return
     */
    void updateStatus(@Param("sendBillId") Long sendBillId, @Param("status") Integer status);

    /**
     * Delete by id.
     *
     * @param sendBillId the send bill id
     */
    void deleteById(@Param("sendBillId") Long sendBillId);


    //	/**
//	 * 根据appmodelId查询改店铺所有发货单
//	 * @param appmodelId
//	 * @return
//	 */
//	List<SendBill> selectByStatus(String appmodelId);
}