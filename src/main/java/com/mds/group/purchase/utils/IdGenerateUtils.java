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
import cn.hutool.core.util.RandomUtil;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * 各种id生成策略
 *
 * @author pavawi
 */
public class IdGenerateUtils {


    /**
     * id生成(16位id)
     *
     * @return the item id
     */
    public static long getItemId() {
        //取当前时间的长整形值包含毫秒
        long millis = System.currentTimeMillis();
        //加上三位随机数
        int end3 = RandomUtil.randomInt(999);
        //如果不足三位前面补0
        String str = millis + String.format("%03d", end3);
        return Long.parseLong(str);
    }

    /**
     * 订单号id生成  2018082314852725
     *
     * @return order num
     */
    public static String getOrderNum() {
        String s = DateUtil.format(DateUtil.date(), "yyMMddHHss");
        int i = RandomUtil.randomInt(10, 99);
        int i1 = RandomUtil.randomInt(1000, 9999);
        return s + i + i1;
    }

    /**
     * 售后订单号id生成  2018082314852725-101
     *
     * @param orderNo            the order no
     * @param afterSaleOrderType the after sale order type
     * @param count              the count
     * @return after sale order num
     */
    public static String getAfterSaleOrderNum(String orderNo, Integer afterSaleOrderType, Integer count) {
        StringBuffer sb = new StringBuffer();
        if (afterSaleOrderType == 1 || afterSaleOrderType == 2) {
            sb.append(getItemId());
            return sb.toString();
        }
        sb.append(orderNo);
        sb.append("-");
        sb.append(afterSaleOrderType);
        sb.append(String.format("%02d", count));
        return sb.toString();
    }

    /**
     * 生成会员卡编号 810207104737
     *
     * @return the membership number
     */
    public static String getMembershipNumber() {
        //生成前四位
        LocalDate date = LocalDate.now();
        String s1 = new StringBuffer(Integer.toString(date.getYear())).reverse().toString();
        int s1Temp = Integer.parseInt(s1);
        s1 = s1Temp < 1000 ? String.valueOf(s1Temp += 1000) : s1;

        //生成中间4位
        LocalTime localTime = LocalTime.now();
        String hour = Integer.toString(date.getMonth().getValue());
        hour = generateNum(hour, 2);
        String minute = Integer.toString(localTime.getMinute());
        minute = generateNum(minute, 2);
        String s2 = hour + minute;

        //生成后四位
        String s3 = String.valueOf(localTime.getNano());
        s3 = generateNum(s3.substring(0, 2), 2);
        s3 = localTime.getSecond() + s3;
        s3 = generateNum(s3, 4);
        return s1 + s2 + s3;
    }

    /**
     * @param num  传入的字符串
     * @param size 规定大小
     * @return
     */
    private static String generateNum(String num, int size) {
        if (num.length() < size) {
            StringBuilder numBuilder = new StringBuilder(num);
            for (int i = numBuilder.length(); i < size; i++) {
                numBuilder.insert(0, "0");
            }
            num = numBuilder.toString();
        }
        return num;
    }
}
