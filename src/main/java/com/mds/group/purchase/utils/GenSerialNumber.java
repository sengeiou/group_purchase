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
import cn.hutool.core.util.StrUtil;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * The type Gen serial number.
 *
 * @author pavawi
 */
public class GenSerialNumber {

    private long sequence = 0L;
    private long lastTimestamp = -1L;

    /**
     * The Constant FINANCIAL.
     */
    public final static int FINANCIAL = 1;
    /**
     * The Constant GROUPBROKERAGE.
     */
    public final static int GROUPBROKERAGE = 2;

    private GenSerialNumber() {

    }

    private static Map<String, GenSerialNumber> MAP4FINANCIAL = new ConcurrentHashMap<>();
    private static Map<String, GenSerialNumber> MAP4GROUPBROKERAGE = new ConcurrentHashMap<>();

    /**
     * Init gen serial number gen serial number.
     *
     * @param type       the type
     * @param appmodelId the appmodel id
     * @return the gen serial number
     */
    public static GenSerialNumber initGenSerialNumber(int type, String appmodelId) {
        GenSerialNumber genSerialNumber;
        switch (type) {
            case FINANCIAL:
                genSerialNumber = MAP4FINANCIAL.get(appmodelId);
                if (genSerialNumber == null) {
                    genSerialNumber = new GenSerialNumber();
                    MAP4FINANCIAL.put(appmodelId, genSerialNumber);
                }
                break;
            case GROUPBROKERAGE:
                genSerialNumber = MAP4GROUPBROKERAGE.get(appmodelId);
                if (genSerialNumber == null) {
                    genSerialNumber = new GenSerialNumber();
                    MAP4GROUPBROKERAGE.put(appmodelId, genSerialNumber);
                }
                break;
            default:
                genSerialNumber = new GenSerialNumber();
        }
        return genSerialNumber;
    }

    /**
     * 下一个ID
     *
     * @return ID string
     */
    public synchronized String nextId() {
        long timestamp = genTime();
        if (timestamp < lastTimestamp) {
            //如果服务器时间有问题(时钟后退) 报错。
            throw new IllegalStateException(StrUtil.format("Clock moved backwards. Refusing to generate id for {}ms",
                    lastTimestamp - timestamp));
        }
        if (timestamp == lastTimestamp) {
            sequence = (sequence + 1) < 999999L ? (sequence + 1) : 0L;
            if (sequence == 0) {
                timestamp = tilNextMillis(lastTimestamp);
            }
        } else {
            sequence = 0L;
        }

        lastTimestamp = timestamp;
        String seqStr = sequence + "";
        for (int i = 0; i <= 6 - seqStr.length(); i++) {
            seqStr = "0".concat(seqStr);
        }
        String yyyyMMddHHmm = DateUtil.format(DateUtil.date(), "yyyyMMddHHmm");
        return "" + yyyyMMddHHmm + seqStr;
    }

    /**
     * 循环等待下一个时间
     *
     * @param lastTimestamp 上次记录的时间
     * @return 下一个时间
     */
    private long tilNextMillis(long lastTimestamp) {
        long timestamp = genTime();
        while (timestamp <= lastTimestamp) {
            timestamp = genTime();
        }
        return timestamp;
    }

    /**
     * 生成时间戳 分钟级
     * @return 时间戳
     */
    private long genTime() {
        return System.currentTimeMillis() / 1000 / 60;
    }
}
