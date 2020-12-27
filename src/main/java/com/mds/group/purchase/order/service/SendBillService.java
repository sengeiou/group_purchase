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
import com.mds.group.purchase.order.model.SendBill;
import com.mds.group.purchase.order.result.OrderResult;
import com.mds.group.purchase.order.result.SendBillResult;
import com.mds.group.purchase.order.vo.SendBillFilterVo;
import com.mds.group.purchase.order.vo.SendbillFindVO;

import javax.servlet.http.HttpServletResponse;
import java.util.List;


/**
 * 发货单业务接口类
 *
 * @author shuke
 * @date 2019 /02/18
 */
public interface SendBillService extends Service<SendBill> {

    /**
     * 生成发货单的方法
     *
     * @param appmodelId the appmodel id
     */
    void generateSendBill(String appmodelId);

    /**
     * 发货单里发货功能
     * 改变属于该发货单的所有订单状态为 已发货状态
     *
     * @param appmodelId the appmodel id
     * @param sendBillId the send bill id
     */
    void doSend(String appmodelId, Long sendBillId);

    /**
     * 根据发货单id获取对应的所有订单
     *
     * @param appmodelId the appmodel id
     * @param sendBillId the send bill id
     * @return detail detail
     */
    List<OrderResult> getDetail(String appmodelId, Long sendBillId);

    /**
     * 根据传入的参数筛选发货单
     *
     * @param appmodelId       the appmodel id
     * @param sendBillFilterVo the send bill filter vo
     * @return list list
     */
    List<SendBillResult> filter(String appmodelId, SendBillFilterVo sendBillFilterVo);

    /**
     * 导出发货单
     *
     * @param response   the response
     * @param appmodelId the appmodel id
     * @param sendBillId the send bill id
     */
    void downloadSendBill(HttpServletResponse response, String appmodelId, Long sendBillId);

    /**
     * 根据传入的日期筛选发货单
     *
     * @param appmodelId   the appmodel id
     * @param generateDate the generate date
     * @return by date
     */
    List<SendBill> getByDate(String appmodelId, String generateDate);

    /**
     * 获取最近3条发货单
     *
     * @param appmodelId the appmodel id
     * @return list list
     */
    List<SendBill> find3SendBill(String appmodelId);


    /**
     * 吧订单从发货单移除时，更新发货单时汇总信息
     *
     * @param sendBillId the send bill id
     * @param orders     the orders
     * @param amount     the amount
     * @param commission the commission
     */
    void updateInfo(Long sendBillId, int orders, double amount, double commission);

    /**
     * 根据发货单ID删除发货单
     *
     * @param sendBillId the send bill id
     */
    void removeById(Long sendBillId);

    /**
     * 查询最新生成的发货单
     *
     * @param appmodelId the appmodel id
     * @return recently send bill
     */
    SendBill getRecentlySendBill(String appmodelId);

    /**
     * 分拣单和签收单查询发货单数据
     *
     * @param appmodelId the appmodel id
     * @param type       the type
     * @return list list
     */
    List<SendbillFindVO> sendbillFind(String appmodelId, Integer type);

}
