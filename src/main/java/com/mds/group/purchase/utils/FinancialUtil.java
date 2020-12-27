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

package com.mds.group.purchase.utils;

import cn.hutool.core.collection.CollectionUtil;
import com.mds.group.purchase.financial.model.FinancialDetails;
import com.mds.group.purchase.financial.result.FinancialResult;
import com.mds.group.purchase.user.model.Wxuser;
import com.mds.group.purchase.user.service.WxuserService;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * The type Financial util.
 *
 * @author pavawi
 */
@Component
public class FinancialUtil {

    @Resource
    private WxuserService wxuserService;

    /**
     * Package result list.
     *
     * @param financialDetails the financial details
     * @return the list
     */
    public List<FinancialResult> packageResult(List<FinancialDetails> financialDetails) {
        List<FinancialResult> financialResults = new ArrayList<>();
        if (CollectionUtil.isNotEmpty(financialDetails)) {
            String userIds = financialDetails.stream().map(o -> o.getWxuserId().toString())
                    .collect(Collectors.joining(","));
            Map<Long, Wxuser> map = wxuserService.findByIds(userIds).stream()
                    .collect(Collectors.toMap(Wxuser::getWxuserId, v -> v));
            financialDetails.forEach(o -> {
                Wxuser user = map.get(o.getWxuserId());
                if (user != null) {
                    FinancialResult res = new FinancialResult(o);
                    res.setIcon(user.getIcon());
                    res.setNickName(user.getWxuserName());
                    financialResults.add(res);
                }
            });
        }
        return financialResults;
    }


}
