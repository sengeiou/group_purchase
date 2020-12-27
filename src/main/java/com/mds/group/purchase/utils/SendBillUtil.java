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

/**
 * 发货单工具类
 *
 * @author shuke
 * @date 2019 -2-18
 */
public class SendBillUtil {

    /**
     * 对传入的时间进行格式验证
     * 1、必须满足 hh:mm 格式
     * 2、小时在0~23的区间内
     * 3、分钟再0~59的区间内
     *
     * @param setTime the set time
     * @return the boolean
     */
    public static Boolean setTimeVerify(String setTime) {
        int indexOf = setTime.indexOf(":");
        if (indexOf == -1 || indexOf != 2) {
            return false;
        }
        if (setTime.length() != 5) {
            return false;
        }
        try {
            Integer hh = Integer.valueOf(setTime.substring(0, indexOf));
            Integer mm = Integer.valueOf(setTime.substring(indexOf + 1));
            if (hh < 0 || hh > 23) {
                return false;
            }
            if (mm < 0 || mm > 59) {
                return false;
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }


    /**
     * 根据当前时间生成发货单名称
     *
     * @return the send bill name
     */
    public static String getSendBillName() {
        String time = DateUtil.format(DateUtil.date(), "yyyy年MM月dd日HH:mm");
        return time + "发货单";
    }


}
