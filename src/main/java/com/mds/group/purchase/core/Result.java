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

import java.io.Serializable;

/**
 * The type Result.
 *
 * @param <T> the type parameter
 * @author pavawi *
 */
public class Result<T> implements Serializable {

    private static final long serialVersionUID = -7620613299343787892L;

    private int code;

    private String msg;

    private T data;


    /**
     * Instantiates a new Result.
     */
    public Result() {
        this.code = 200;
        this.msg = "success";
    }

    /**
     * Instantiates a new Result.
     *
     * @param data the data
     */
    public Result(T data) {
        this.code = 200;
        this.msg = "success";
        this.data = data;
    }

    /**
     * Instantiates a new Result.
     *
     * @param code the code
     * @param msg  the msg
     */
    public Result(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    /**
     * 成功时候的调用
     *
     * @param <T> the type parameter
     * @return result
     */
    public static <T> Result<T> success() {
        return new Result();
    }

    /**
     * 成功时候的调用
     *
     * @param <T>  the type parameter
     * @param data the data
     * @return result
     */
    public static <T> Result<T> success(T data) {
        return new Result<>(data);
    }

    /**
     * 失败时候的调用
     *
     * @param <T>     the type parameter
     * @param codeMsg the code msg
     * @return result
     */
    public static <T> Result<T> error(CodeMsg codeMsg) {
        return new Result<>(codeMsg);
    }

    private Result(CodeMsg codeMsg) {
        if (codeMsg != null) {
            this.code = codeMsg.getCode();
            this.msg = codeMsg.getMsg();
        }
    }

    /**
     * Gets code.
     *
     * @return the code
     */
    public int getCode() {
        return code;
    }

    /**
     * Sets code.
     *
     * @param code the code
     */
    public void setCode(int code) {
        this.code = code;
    }

    /**
     * Gets msg.
     *
     * @return the msg
     */
    public String getMsg() {
        return msg;
    }

    /**
     * Sets msg.
     *
     * @param msg the msg
     */
    public void setMsg(String msg) {
        this.msg = msg;
    }

    /**
     * Gets data.
     *
     * @return the data
     */
    public T getData() {
        return data;
    }

    /**
     * Sets data.
     *
     * @param data the data
     */
    public void setData(T data) {
        this.data = data;
    }
}
