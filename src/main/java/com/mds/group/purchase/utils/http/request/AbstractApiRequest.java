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

package com.mds.group.purchase.utils.http.request;

import com.mds.group.purchase.constant.HttpConstant;
import com.mds.group.purchase.utils.http.response.ApiResponse;
import lombok.Getter;
import okhttp3.Headers;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

/**
 * 基础请求
 *
 * @param <T> the type parameter
 * @param <R> the type parameter
 * @author biezhi *
 */
@Getter
public abstract class AbstractApiRequest<T extends AbstractApiRequest, R extends ApiResponse> {

    /**
     * The This as t.
     */
    @SuppressWarnings("unchecked")
    protected final T thisAsT = (T) this;
    /**
     * The Timeout.
     */
    protected int timeout = 10;
    /**
     * The No redirect.
     */
    protected boolean noRedirect;
    /**
     * The Json body.
     */
    protected boolean jsonBody;
    /**
     * The Multipart.
     */
    protected boolean multipart;
    /**
     * The Url.
     */
    protected String url;
    /**
     * The Method.
     */
    protected String method = "GET";
    /**
     * The File name.
     */
    protected String fileName;
    /**
     * The Content type.
     */
    protected String contentType = "application/x-www-form-urlencoded";
    /**
     * The Headers.
     */
    protected Headers headers;
    private final Class<? extends R> responseClass;
    private final Map<String, Object> parameters;

    /**
     * Instantiates a new Abstract api request.
     *
     * @param url           the url
     * @param responseClass the response class
     */
    public AbstractApiRequest(String url, Class<? extends R> responseClass) {
        this.url = url;
        this.responseClass = responseClass;
        this.parameters = new HashMap<>();
        this.headers = Headers.of("User-Agent", HttpConstant.USER_AGENT, "Content-Type", this.contentType);
    }

    /**
     * Header t.
     *
     * @param name  the name
     * @param value the value
     * @return the t
     */
    public T header(String name, String value) {
        this.headers = this.headers.newBuilder().set(name, value).build();
        return thisAsT;
    }

    /**
     * Add t.
     *
     * @param name the name
     * @param val  the val
     * @return the t
     */
    public T add(String name, Object val) {
        parameters.put(name, val);
        return thisAsT;
    }

    /**
     * No redirect t.
     *
     * @return the t
     */
    public T noRedirect() {
        this.noRedirect = true;
        return thisAsT;
    }

    /**
     * Multipart t.
     *
     * @return the t
     */
    public T multipart() {
        this.multipart = true;
        return thisAsT;
    }

    /**
     * Gets response type.
     *
     * @return the response type
     */
    public Type getResponseType() {
        return responseClass;
    }

    /**
     * Url t.
     *
     * @param url the url
     * @return the t
     */
    public T url(String url) {
        this.url = url;
        return thisAsT;
    }

    /**
     * Timeout t.
     *
     * @param seconds the seconds
     * @return the t
     */
    public T timeout(int seconds) {
        this.timeout = seconds;
        return thisAsT;
    }

    /**
     * File name t.
     *
     * @param fileName the file name
     * @return the t
     */
    public T fileName(String fileName) {
        this.fileName = fileName;
        return thisAsT;
    }

    /**
     * Post t.
     *
     * @return the t
     */
    public T post() {
        this.method = "POST";
        return thisAsT;
    }

    /**
     * Json body t.
     *
     * @return the t
     */
    public T jsonBody() {
        this.jsonBody = true;
        this.contentType = "application/json; charset=UTF-8";
        this.header("Content-Type", this.contentType);
        return thisAsT;
    }
}
