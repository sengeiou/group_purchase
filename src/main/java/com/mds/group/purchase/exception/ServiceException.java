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

/**
 * The type Service exception.
 *
 * @author shuke  服务（业务）异常如“ 账号或密码错误 ”，该异常只做INFO级别的日志记录 @see WebMvcConfigurer
 * @since v1
 */
public class ServiceException extends RuntimeException {

    /**
     * 自定义错误讯息.
     */
    private String customErrorMsg;

    /**
     * Instantiates a new Service exception.
     */
    public ServiceException() {
    }

    /**
     * Instantiates a new Service exception.
     *
     * @param message   the message
     * @param throwable the throwable
     */
    public ServiceException(String message, Throwable throwable) {
        super(message, throwable);
        this.customErrorMsg = message;
    }

    /**
     * Instantiates a new Service exception.
     *
     * @param message the message
     */
    public ServiceException(String message) {
        super(message);
    }

}
