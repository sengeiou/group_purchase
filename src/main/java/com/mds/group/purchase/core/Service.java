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

package com.mds.group.purchase.core;

import org.apache.ibatis.exceptions.TooManyResultsException;
import tk.mybatis.mapper.entity.Condition;

import java.util.List;

/**
 * Service 层 基础接口，其他Service 接口 请继承该接口
 *
 * @param <T> the type parameter
 * @author pavawi *
 */
public interface Service<T> {
    /**
     * Save int.
     *
     * @param model the model
     * @return the int
     */
    int save(T model);//持久化

    /**
     * Save int.
     *
     * @param models the models
     * @return the int
     */
    int save(List<T> models);//批量持久化

    /**
     * Delete by id int.
     *
     * @param id the id
     * @return the int
     */
    int deleteById(Integer id);//通过主鍵刪除

    /**
     * Delete by id int.
     *
     * @param id the id
     * @return the int
     */
    int deleteById(Long id);//通过主鍵刪除

    /**
     * Delete by ids int.
     *
     * @param ids the ids
     * @return the int
     */
    int deleteByIds(String ids);//批量刪除 eg：ids -> “1,2,3,4”

    /**
     * Update int.
     *
     * @param model the model
     * @return the int
     */
    int update(T model);//更新

    /**
     * Find by id t.
     *
     * @param id the id
     * @return the t
     */
    T findById(Object id);//通过ID查找

    /**
     * 通过Model中某个成员变量名称（非数据表中column的名称）查找,value需符合unique约束
     *
     * @param fieldName the field name
     * @param value     the value
     * @return t
     * @throws TooManyResultsException the too many results exception
     */
    T findBy(String fieldName, Object value) throws TooManyResultsException;

    /**
     * Find by t.
     *
     * @param fieldNames the field names
     * @param value      the value
     * @return the t
     * @throws TooManyResultsException the too many results exception
     */
    T findBy(String[] fieldNames, Object[] value) throws TooManyResultsException;


    /**
     * 通过Model中某个成员变量名称（非数据表中column的名称）查找,value为逗号分隔
     *
     * @param fieldName the field name
     * @param value     the value
     * @return list
     * @throws TooManyResultsException the too many results exception
     */
    List<T> findByList(String fieldName, Object value) throws TooManyResultsException;

    /**
     * Find by ids list.
     *
     * @param ids the ids
     * @return the list
     */
    List<T> findByIds(String ids);//通过多个ID查找//eg：ids -> “1,2,3,4”

    /**
     * Find by condition list.
     *
     * @param condition the condition
     * @return the list
     */
    List<T> findByCondition(Condition condition);//根据条件查找(返回多个)

    /**
     * Find by one condition t.
     *
     * @param condition the condition
     * @return the t
     */
    T findByOneCondition(Condition condition);//根据条件查找(返回一个)

    /**
     * Find all list.
     *
     * @return the list
     */
    List<T> findAll();//获取所有
}

