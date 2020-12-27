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

package com.mds.group.purchase.listener;

import com.alibaba.fastjson.JSONObject;
import com.mds.group.purchase.GroupPurchaseApplicationTests;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class SendBillListenerTest extends GroupPurchaseApplicationTests {
    @Autowired
    private SendBillListener sendBillListener;

    @Test
    public void goodsSortingOrderCache() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("sendBillId", "29");
        jsonObject.put("appmodelId", "S00050001wx17c66eb4da0ef6ab");
        sendBillListener.goodsSortingOrderCache(jsonObject.toJSONString());
    }
}