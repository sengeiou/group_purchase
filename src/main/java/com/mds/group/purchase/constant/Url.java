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

package com.mds.group.purchase.constant;

/**
 * The interface Url.
 *
 * @author pavawi
 */
public interface Url {

    /**
     * 订单支付回调
     * 该常量已经被弃用
     * v1.2版本中将该常量提到了application.properties配置文件中
     * 避免了在切换测试和生产环境时需要手动修改的操作
     */
    @Deprecated
    String ORDER_NOTIFY = "https://www.superprism.cn/groupmall/order/v1/notify";

    //String ORDER_NOTIFY = "https://www.superprism.cn/grouptest/order/v1/notify";

    //String ORDER_NOTIFY = "http://whh.ngrok.xiaomiqiu.cn/groupmall/order/v1/notify";
    /**
     * 获取模板消息id
     */
    String GET_TEMPLATID_URL = "https://www.superprism.cn/medusaplatform/rest/mini/templateid";

    /**
     * The Constant GET_MINI_LOGO_URL.
     */
    String GET_MINI_LOGO_URL = "https://www.superprism.cn/medusaplatform/MiniProgramy/getMiniLogo";

}
