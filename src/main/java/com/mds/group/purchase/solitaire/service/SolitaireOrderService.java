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

package com.mds.group.purchase.solitaire.service;

import com.github.binarywang.wxpay.bean.notify.WxPayOrderNotifyResult;
import com.mds.group.purchase.order.vo.SolitaireOrderVo;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * The interface Solitaire order service.
 *
 * @author pavawi
 */
public interface SolitaireOrderService {

    /**
     * H 5 pay map.
     *
     * @param orderNo the order no
     * @param request the request
     * @param ip      the ip
     * @return the map
     */
    Map<String, String> h5Pay(String orderNo, HttpServletRequest request, String ip);

    /**
     * 保存接龙订单
     *
     * @param vo the vo
     * @return the string
     */
    String saveSolitaireOrder(SolitaireOrderVo vo);

    /**
     * h5接龙活动订单回调
     * 此版本支付成功后会生成对应该订单的订单发货单关系映射数据
     * 生成
     *
     * @param xmlResult the xml result
     * @return string string
     * @throws Exception the exception
     */
    String notifyH5(WxPayOrderNotifyResult xmlResult) throws Exception;
}
