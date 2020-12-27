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

package com.mds.group.purchase.utils.http;

import com.mds.group.purchase.utils.http.request.AbstractApiRequest;
import com.mds.group.purchase.utils.http.response.ApiResponse;

import java.io.IOException;

/**
 * The interface Callback.
 *
 * @param <T> the type parameter
 * @param <R> the type parameter
 * @author pavawi *
 */
public interface Callback<T extends AbstractApiRequest, R extends ApiResponse> {

    /**
     * On response.
     *
     * @param request  the request
     * @param response the response
     */
    void onResponse(T request, R response);

    /**
     * On failure.
     *
     * @param request the request
     * @param e       the e
     */
    void onFailure(T request, IOException e);

}