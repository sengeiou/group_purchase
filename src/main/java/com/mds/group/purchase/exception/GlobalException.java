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

package com.mds.group.purchase.exception;


import com.mds.group.purchase.core.CodeMsg;

/**
 * The type Global exception.
 *
 * @author pavawi
 */
public class GlobalException extends RuntimeException {

    private static final long serialVersionUID = 19131L;

    private CodeMsg codeMsg;

    /**
     * Instantiates a new Global exception.
     *
     * @param codeMsg the code msg
     */
    public GlobalException(CodeMsg codeMsg) {
        super(codeMsg.toString());
        this.codeMsg = codeMsg;
    }

    /**
     * Instantiates a new Global exception.
     *
     * @param message the message
     * @param cause   the cause
     */
    public GlobalException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Gets code msg.
     *
     * @return the code msg
     */
    public CodeMsg getCodeMsg() {
        return codeMsg;
    }
}
