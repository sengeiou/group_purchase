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

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 响应结果生成工具
 *
 * @author pavawi
 */
public class ResultGenerator {
    private static final Logger logger = LoggerFactory.getLogger(ResultGenerator.class);

    /**
     * Gen success result result.
     *
     * @return the result
     */
    public static Result genSuccessResult() {
        return new Result();
    }

    /**
     * Gen success result result.
     *
     * @param <T>  the type parameter
     * @param data the data
     * @return the result
     */
    public static <T> Result<T> genSuccessResult(T data) {
        return Result.success(data);
    }

    /**
     * Gen fail result result.
     *
     * @param message the message
     * @return the result
     */
    public static Result genFailResult(String message) {
        return Result.error(CodeMsg.SERVER_ERROR);
    }

    /**
     * Response result.
     *
     * @param response the response
     * @param result   the result
     */
    public static void responseResult(HttpServletResponse response, Result result) {
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Content-type", "application/json;charset=UTF-8");
        response.setStatus(200);
        try {
            response.getWriter().write(JSON.toJSONString(result, SerializerFeature.WriteMapNullValue));
        } catch (IOException ex) {
            logger.error(ex.getMessage());
        }
    }
}
