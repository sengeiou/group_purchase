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

/**
 * The type Code msg.
 *
 * @author pavawi
 */
public class CodeMsg {

    private int code;

    private String msg;

    /**
     * 通用错误代码
     */
    public final static CodeMsg SUCCESS = new CodeMsg(200, "success");
    /**
     * The Constant FAIL.
     */
    public final static CodeMsg FAIL = new CodeMsg(400, "%s");
    /**
     * The Constant SERVER_ERROR.
     */
    public final static CodeMsg SERVER_ERROR = new CodeMsg(100100, "网络异常");
    /**
     * The Constant BIND_ERROR.
     */
    public final static CodeMsg BIND_ERROR = new CodeMsg(100101, "参数异常：%s");
    /**
     * The Constant PARAMETER_ERROR.
     */
    public final static CodeMsg PARAMETER_ERROR = new CodeMsg(100102, "参数格式错误：%s");
    /**
     * The Constant NULL_PARAM.
     */
    public final static CodeMsg NULL_PARAM = new CodeMsg(100103, "参数为空");
    /**
     * The Constant NULL_VALUE.
     */
    public final static CodeMsg NULL_VALUE = new CodeMsg(100104, "查询字段value 不能为空");
    /**
     * The Constant NOT_FOUND.
     */
    public final static CodeMsg NOT_FOUND = new CodeMsg(404, "接口: %s 未找到");
    /**
     * The Constant MISS_PATHVARIBLE.
     */
    public final static CodeMsg MISS_PATHVARIBLE = new CodeMsg(100105, "%s");
    /**
     * The Constant MISS_PARAM.
     */
    public final static CodeMsg MISS_PARAM = new CodeMsg(100106, "%s");
    /**
     * The Constant MISS_HEADER.
     */
    public final static CodeMsg MISS_HEADER = new CodeMsg(100107, "%s");
    /**
     * The Constant REQUEST_METHOD_ERROR.
     */
    public final static CodeMsg REQUEST_METHOD_ERROR = new CodeMsg(100108, "%s");
    /**
     * The Constant REQUEST_LIMIT.
     */
    public final static CodeMsg REQUEST_LIMIT = new CodeMsg(100109, "HTTP请求次数超出限制");

    /**
     * 订单模块错误码
     */
    public final static CodeMsg GOODS_UNDERSTOCK = new CodeMsg(300100, "%s");
    /**
     * The Constant MAX_QUANTITY.
     */
    public final static CodeMsg MAX_QUANTITY = new CodeMsg(300101, "购买数量已超过限购量");

    /**
     * Instantiates a new Code msg.
     */
    public CodeMsg() {
    }

    /**
     * Instantiates a new Code msg.
     *
     * @param msg the msg
     */
    public CodeMsg(String msg) {
        this.msg = msg;
    }

    /**
     * Instantiates a new Code msg.
     *
     * @param code the code
     * @param msg  the msg
     */
    public CodeMsg(int code, String msg) {
        this.code = code;
        this.msg = msg;
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
     * Fill args code msg.
     *
     * @param args the args
     * @return the code msg
     */
    public CodeMsg fillArgs(Object... args) {
        int code = this.code;
        String message = String.format(this.msg, args);
        return new CodeMsg(code, message);
    }
}
