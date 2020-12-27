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

package com.mds.group.purchase.system;

import lombok.Data;

import java.io.Serializable;

/**
 * The type Api info.
 *
 * @author pavawi
 */
@Data
public class ApiInfo implements Serializable {

    private String requestResult;
    private Long elapsedMilliseconds;
    private String url;
    private String ip;
    private String methodName;
    private String errorResponse;

    /**
     * Instantiates a new Api info.
     */
    public ApiInfo() {

    }


    /**
     * Instantiates a new Api info.
     *
     * @param elapsedMilliseconds the elapsed milliseconds
     * @param url                 the url
     * @param ip                  the ip
     * @param methodName          the method name
     * @param requestResult       the request result
     */
    ApiInfo(long elapsedMilliseconds, String url, String ip, String methodName, String requestResult) {
        this.elapsedMilliseconds = elapsedMilliseconds;
        this.ip = ip;
        this.methodName = methodName;
        this.requestResult = requestResult;
        this.url = url;
    }

    /**
     * Instantiates a new Api info.
     *
     * @param elapsedMilliseconds the elapsed milliseconds
     * @param url                 the url
     * @param ip                  the ip
     * @param methodName          the method name
     * @param requestResult       the request result
     * @param errorResponse       the error response
     */
    ApiInfo(long elapsedMilliseconds, String url, String ip, String methodName, String requestResult,
            String errorResponse) {
        this.elapsedMilliseconds = elapsedMilliseconds;
        this.ip = ip;
        this.methodName = methodName;
        this.requestResult = requestResult;
        this.url = url;
        this.errorResponse = errorResponse;
    }
}
