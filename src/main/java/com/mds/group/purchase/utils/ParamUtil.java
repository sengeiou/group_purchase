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

import cn.hutool.core.date.DateUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

/**
 * The type Param util.
 *
 * @author pavawi
 */
@Component
public class ParamUtil {


    /**
     * Init date string string.
     *
     * @param date   the date
     * @param offset the offset
     * @return the string
     */
    public static String initDateString(String date, int offset) {
        return StringUtils.isBlank(date) ?
                DateUtil.formatDateTime(DateUtil.offsetMonth(DateUtil.date(), offset)) :
                offset == 0 ?
                        DateUtil.formatDateTime(DateUtil.offsetDay(DateUtil.parse(date), 1)) :
                        DateUtil.formatDateTime(DateUtil.parse(date));
    }
}
