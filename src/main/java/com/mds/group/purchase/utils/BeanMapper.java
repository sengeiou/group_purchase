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

import cn.hutool.core.util.ArrayUtil;
import com.alibaba.fastjson.JSON;
import com.github.dozermapper.core.DozerBeanMapperBuilder;
import com.github.dozermapper.core.Mapper;

import java.util.ArrayList;
import java.util.List;


/**
 * 实现深度的BeanOfClasssA<->BeanOfClassB复制
 * <p>
 * 不要使用Apache Common BeanUtils进行类复制，每次就行反射查询对象的属性列表, 非常缓慢.
 * <p>
 * orika性能比Dozer快近十倍，也不需要Getter函数与无参构造函数
 * <p>
 * 但我们内部修复了的bug，社区版没有修复: https://github.com/orika-mapper/orika/issues/252
 * <p>
 * 如果应用启动时有并发流量进入，可能导致两个不同类型的同名属性间(如Order的User user属性，与OrderVO的UserVO user)的复制失败，只有重启才能解决。
 * <p>
 * 因此安全起见，在vjkit的开源版本中仍然使用Dozer。
 * <p>
 * Dozer最新是6.x版，但only for JDK8，为兼容JDK7这里仍使用5.x版本。
 * <p>
 * 注意: 需要参考POM文件，显式引用Dozer.
 *
 * @author pavawi
 */
public class BeanMapper {

    private static Mapper mapper = DozerBeanMapperBuilder.create().build();

    /**
     * 简单的复制出新类型对象.
     *
     * @param <S>              the type parameter
     * @param <D>              the type parameter
     * @param source           the source
     * @param destinationClass the destination class
     * @return the d
     */
    public static <S, D> D map(S source, Class<D> destinationClass) {
        return mapper.map(source, destinationClass);
    }

    /**
     * 简单的复制出新对象ArrayList
     *
     * @param <S>              the type parameter
     * @param <D>              the type parameter
     * @param sourceList       the source list
     * @param destinationClass the destination class
     * @return the list
     */
    public static <S, D> List<D> mapList(Iterable<S> sourceList, Class<D> destinationClass) {
        List<D> destionationList = new ArrayList<D>();
        for (S source : sourceList) {
            if (source != null) {
                destionationList.add(mapper.map(source, destinationClass));
            }
        }
        return destionationList;
    }

    /**
     * 简单复制出新对象数组
     *
     * @param <S>              the type parameter
     * @param <D>              the type parameter
     * @param sourceArray      the source array
     * @param destinationClass the destination class
     * @return the d [ ]
     */
    public static <S, D> D[] mapArray(final S[] sourceArray, final Class<D> destinationClass) {
        D[] destinationArray = ArrayUtil.newArray(destinationClass, sourceArray.length);

        int i = 0;
        for (S source : sourceArray) {
            if (source != null) {
                destinationArray[i] = mapper.map(sourceArray[i], destinationClass);
                i++;
            }
        }

        return destinationArray;
    }

    /**
     * 对象转json字符串
     *
     * @param <T>   the type parameter
     * @param value the value
     * @return string
     */
    public static <T> String beanToString(T value) {
        if (value == null) {
            return null;
        }
        Class<?> clazz = value.getClass();
        if (clazz == int.class || clazz == Integer.class) {
            return "" + value;
        } else if (clazz == String.class) {
            return (String) value;
        } else if (clazz == long.class || clazz == Long.class) {
            return "" + value;
        } else {
            return JSON.toJSONString(value);
        }
    }

    /**
     * json字符串转对象
     *
     * @param <T>   the type parameter
     * @param str   the str
     * @param clazz the clazz
     * @return t
     */
    @SuppressWarnings("unchecked")
    public static <T> T stringToBean(String str, Class<T> clazz) {
        if (str == null || str.length() <= 0 || clazz == null) {
            return null;
        }
        if (clazz == int.class || clazz == Integer.class) {
            return (T) Integer.valueOf(str);
        } else if (clazz == String.class) {
            return (T) str;
        } else if (clazz == long.class || clazz == Long.class) {
            return (T) Long.valueOf(str);
        }else {
            return JSON.toJavaObject(JSON.parseObject(str), clazz);
        }
    }
}