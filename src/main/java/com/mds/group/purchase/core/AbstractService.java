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


import com.mds.group.purchase.exception.GlobalException;
import org.apache.ibatis.exceptions.TooManyResultsException;
import org.springframework.beans.factory.annotation.Autowired;
import tk.mybatis.mapper.entity.Condition;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.Arrays;
import java.util.List;

/**
 * 基于通用MyBatis Mapper插件的Service接口的实现
 *
 * @param <T> the type parameter
 * @author Administrator *
 */
public abstract class AbstractService<T> implements Service<T> {

    /**
     * The Mapper.
     */
    @Autowired
    protected Mapper<T> mapper;

    /**
     * 当前泛型真实类型的Class
     */
    private Class<T> modelClass;

    /**
     * Instantiates a new Abstract service.
     */
    public AbstractService() {
        ParameterizedType pt = (ParameterizedType) this.getClass().getGenericSuperclass();
        modelClass = (Class<T>) pt.getActualTypeArguments()[0];
    }

    @Override
    public int save(T model) {
        return mapper.insertSelective(model);
    }

    @Override
    public int save(List<T> models) {
        return mapper.insertList(models);
    }

    @Override
    public int deleteById(Integer id) {
        return mapper.deleteByPrimaryKey(id);
    }

    @Override
    public int deleteById(Long id) {
        return mapper.deleteByPrimaryKey(id);
    }

    @Override
    public int deleteByIds(String ids) {
        return mapper.deleteByIds(ids);
    }

    @Override
    public int update(T model) {
        return mapper.updateByPrimaryKeySelective(model);
    }

    @Override
    public T findById(Object id) {
        return mapper.selectByPrimaryKey(id);
    }

    @Override
    public T findBy(String fieldName, Object value) throws TooManyResultsException {
        try {
            T model = modelClass.newInstance();
            Field field = modelClass.getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(model, value);
            return mapper.selectOne(model);
        } catch (ReflectiveOperationException e) {
            throw new GlobalException(e.getMessage(),e.getCause());
        }
    }

    @Override
    public T findBy(String[] fieldNames, Object[] value) throws TooManyResultsException {
        try {
            T model = modelClass.newInstance();
            for (int i = 0; i < fieldNames.length; i++) {
                Field field = modelClass.getDeclaredField(fieldNames[i]);
                field.setAccessible(true);
                field.set(model, value[i]);
            }
            return mapper.selectOne(model);
        } catch (ReflectiveOperationException e) {
            throw new GlobalException(e.getMessage(), e);
        }
    }

    @Override
    public List<T> findByList(String fieldName, Object value) throws TooManyResultsException {
        try {
            String[] split = value.toString().split(",");
            if (split.length == 0) {
                throw new GlobalException(CodeMsg.NULL_VALUE);
            }
            T model = modelClass.newInstance();
            Condition condition = new Condition(model.getClass());
            condition.createCriteria().andIn(fieldName, Arrays.asList(split));
            return mapper.selectByCondition(condition);
        } catch (ReflectiveOperationException e) {
            throw new GlobalException(e.getMessage(), e);
        }
    }

    @Override
    public List<T> findByIds(String ids) {
        return mapper.selectByIds(ids);
    }

    @Override
    public List<T> findByCondition(Condition condition) {
        return mapper.selectByCondition(condition);
    }

    @Override
    public T findByOneCondition(Condition condition) {
        List<T> ts = mapper.selectByCondition(condition);
        if (ts != null && ts.size() == 1) {
            return ts.get(0);
        }
        return null;
    }

    @Override
    public List<T> findAll() {
        return mapper.selectAll();
    }
}
