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
public enum AfterSaleOrderStatus {


    /**
     * 待商家审核
     */
    待商家审核(1, "待商家审核"),
    /**
     * 商家拒绝
     */
    商家拒绝(2, "商家拒绝"),
    /**
     * 待退货
     */
    待退货(3, "待退货"),
    /**
     * 待团长确认收货
     */
    待团长确认收货(4, "待团长确认收货"),
    /**
     * 待发货
     */
    待发货(5, "待发货"),
    /**
     * 待签收
     */
    待签收(8, "待签收"),
    /**
     * 待提货
     */
    待提货(6, "待提货"),
    /**
     * 成功
     */
    成功(7, "成功"),
    /**
     * 关闭
     */
    关闭(99, "关闭");

    private int value;
    private String desc;

    AfterSaleOrderStatus(final int value, final String desc) {
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
        for (AfterSaleOrderStatus afterSaleOrderStatus : AfterSaleOrderStatus.values()) {
            if (afterSaleOrderStatus.getValue() == value) {
                return afterSaleOrderStatus.getDesc();
            }
        }
        return "";
    }

    /**
     * 是否是已结束的售后单
     *
     * @param afterSaleStatus the after sale status
     * @return the boolean
     */
    public static boolean isEnd(Integer afterSaleStatus) {
        return afterSaleStatus == 2 || afterSaleStatus == 7 || afterSaleStatus == 99;
    }

    /**
     * 是否可以签收
     *
     * @param afterSaleStatus the after sale status
     * @return the boolean
     */
    public static boolean canReceipt(Integer afterSaleStatus) {
        return afterSaleStatus == 1 || afterSaleStatus == 3 || isEnd(afterSaleStatus);
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
}
