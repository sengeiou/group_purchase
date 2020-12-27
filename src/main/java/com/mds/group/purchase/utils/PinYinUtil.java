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

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

/**
 * 拼音工具
 *
 * @author shuke
 */
public class PinYinUtil {

    /**
     * 获取中文字符串首字母缩写
     *
     * @param str 中文字符串
     * @return 首字母缩写 ping yin
     * @throws BadHanyuPinyinOutputFormatCombination 拼音转换错误
     */
    public static String getPingYin(String str) throws BadHanyuPinyinOutputFormatCombination {
        HanyuPinyinOutputFormat defaultFormat = new HanyuPinyinOutputFormat();
        // 设置大小写格式
        defaultFormat.setCaseType(HanyuPinyinCaseType.UPPERCASE);
        char[] charArray = str.toCharArray();
        StringBuilder pinyin = new StringBuilder();
        for (char aCharArray : charArray) {
            //匹配中文,非中文转换会转换成null
            if (Character.toString(aCharArray).matches("[\\u4E00-\\u9FA5]+")) {
                String[] hanyuPinyinStringArray = PinyinHelper.toHanyuPinyinStringArray(aCharArray, defaultFormat);
                if (hanyuPinyinStringArray != null) {
                    pinyin.append(hanyuPinyinStringArray[0].charAt(0));
                }
            }
        }
        return pinyin.toString();
    }

    /**
     * 获取中文字符串首字母
     *
     * @param str 中文字符串
     * @return 首字母 ping yin first
     * @throws BadHanyuPinyinOutputFormatCombination 拼音转换错误
     */
    public static String getPingYinFirst(String str) throws BadHanyuPinyinOutputFormatCombination {
        HanyuPinyinOutputFormat defaultFormat = new HanyuPinyinOutputFormat();
        // 设置大小写格式
        defaultFormat.setCaseType(HanyuPinyinCaseType.UPPERCASE);
        char[] charArray = str.toCharArray();
        StringBuilder pinyin = new StringBuilder();
        //匹配中文,非中文转换会转换成null
        if (Character.toString(charArray[0]).matches("[\\u4E00-\\u9FA5]+")) {
            String[] hanyuPinyinStringArray = PinyinHelper.toHanyuPinyinStringArray(charArray[0], defaultFormat);
            if (hanyuPinyinStringArray != null) {
                pinyin.append(hanyuPinyinStringArray[0].charAt(0));
            }
        }
        return pinyin.toString();
    }
}
