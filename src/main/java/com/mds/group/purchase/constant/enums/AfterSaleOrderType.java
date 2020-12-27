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

package com.mds.group.purchase.constant.enums;

/**
 * The enum
 *
 * @author pavawi
 */
public enum AfterSaleOrderType {


    /**
     * 团长换货
     */
    团长换货(1, "团长换货"),
    /**
     * 团长退款
     */
    团长退款(2, "团长退款"),
    /**
     * 退款
     */
    退款(3, "退款"),
    /**
     * 换货
     */
    换货(4, "重新发货"),
    /**
     * 退货退款
     */
    退货退款(5, "退货退款");

    private int value;
    private String desc;

    AfterSaleOrderType(final int value, final String desc) {
        this.value = value;
        this.desc = desc;
    }

    /**
     * Gets desc.
     *
     * @param value the value
     * @return the desc
     */
    public static String getDesc(int value) {
        for (AfterSaleOrderType afterSaleOrderType : AfterSaleOrderType.values()) {
            if (afterSaleOrderType.getValue() == value) {
                return afterSaleOrderType.getDesc();
            }
        }
        return "";
    }

    /**
     * Gets value.
     *
     * @return the value
     */
    public int getValue() {
        return value;
    }

    /**
     * Gets desc.
     *
     * @return the desc
     */
    public String getDesc() {
        return desc;
    }

    /**
     * Is leader boolean.
     *
     * @param type the type
     * @return the boolean
     */
    public static boolean isLeader(Integer type) {
        return type == 1 || type == 2;
    }

    /**
     * Is ex change boolean.
     *
     * @param type the type
     * @return the boolean
     */
    public static boolean isExChange(Integer type) {
        return type == 1 || type == 4;
    }
}
