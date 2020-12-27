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

import com.google.gson.Gson;
import com.mds.group.purchase.constant.HttpConstant;
import com.mds.group.purchase.exception.GlobalException;
import com.mds.group.purchase.utils.http.request.AbstractApiRequest;
import com.mds.group.purchase.utils.http.response.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * 微信机器人 HTTP 发送端
 *
 * @author biezhi
 * @date 2018 /1/18
 */
@Slf4j
public class HttpClient {

    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    private static Map<String, List<Cookie>> cookieStore = new ConcurrentHashMap<>();

    private OkHttpClient client;
    private OkHttpClient clientWithTimeout;

    /**
     * Instantiates a new Http client.
     *
     * @param client the client
     */
    public HttpClient(OkHttpClient client) {
        this.client = client;
        System.setProperty("https.protocols", "TLSv1");
        System.setProperty("jsse.enableSNIExtension", "false");
    }

    /**
     * Get client http client.
     *
     * @return the http client
     */
    public static HttpClient getClient() {
        return new HttpClient(OkHttpUtil.client(null));
    }

    /**
     * 重新恢复Cookie
     *
     * @param cookieStore the cookie store
     */
    public static void recoverCookie(Map<String, List<Cookie>> cookieStore) {
        HttpClient.cookieStore.clear();
        HttpClient.cookieStore = cookieStore;
    }

    /**
     * Cookie store map.
     *
     * @return the map
     */
    public static Map<String, List<Cookie>> cookieStore() {
        return cookieStore;
    }

    /**
     * Send.
     *
     * @param <T>      the type parameter
     * @param <R>      the type parameter
     * @param request  the request
     * @param callback the callback
     */
    public <T extends AbstractApiRequest, R extends ApiResponse> void send(final T request,
                                                                           final Callback<T, R> callback) {
        OkHttpClient client = getOkHttpClient(request);
        client.newCall(createRequest(request)).enqueue(new okhttp3.Callback() {
            @Override
            public void onResponse(Call call, Response response) {
                try {
                    String body = response.body().string();
                    if (log.isDebugEnabled()) {
                        log.debug("Response:\r\n{}", body);
                    }
                    callback.onResponse(request, (R) new ApiResponse(body));
                } catch (Exception e) {
                    IOException ioEx = e instanceof IOException ? (IOException) e : new IOException(e);
                    callback.onFailure(request, ioEx);
                }
            }

            @Override
            public void onFailure(Call call, IOException e) {
                callback.onFailure(request, e);
            }
        });
    }

    /**
     * Send r.
     *
     * @param <T>     the type parameter
     * @param <R>     the type parameter
     * @param request the request
     * @return the r
     */
    public <T extends AbstractApiRequest, R extends ApiResponse> R send(final AbstractApiRequest<T, R> request) {
        try {
            OkHttpClient client = getOkHttpClient(request);
            Request okHttpRequest = createRequest(request);
            Response response = client.newCall(okHttpRequest).execute();
            String body = response.body().string();

            if (log.isDebugEnabled()) {
                log.debug("Response :\r\n{}", body);
            }
            return (R) new ApiResponse(body);
        } catch (IOException e) {
            throw new GlobalException(e.getMessage(),e.getCause());
        }
    }

    private OkHttpClient getOkHttpClient(AbstractApiRequest request) {
        OkHttpClient client = timeout(request);
        if (request.isNoRedirect()) {
            return client.newBuilder().followRedirects(false).followSslRedirects(false).build();
        }
        return cookie(client);
    }

    /**
     * 设置超时
     *
     * @param request
     * @return
     */
    private OkHttpClient timeout(AbstractApiRequest request) {
        int timeoutMillis = request.getTimeout() * 1000;
        if (client.readTimeoutMillis() == 0 || client.readTimeoutMillis() > timeoutMillis) {
            return client;
        }
        if (null != clientWithTimeout && clientWithTimeout.readTimeoutMillis() > timeoutMillis) {
            return clientWithTimeout;
        }
        clientWithTimeout = client.newBuilder().readTimeout(timeoutMillis + 1000, TimeUnit.MILLISECONDS).build();
        return clientWithTimeout;
    }

    private OkHttpClient cookie(OkHttpClient client) {
        return client.newBuilder().cookieJar(new CookieJar() {
            @Override
            public void saveFromResponse(HttpUrl httpUrl, List<Cookie> cookies) {
            }

            @Override
            public List<Cookie> loadForRequest(HttpUrl httpUrl) {
                List<Cookie> cookies = cookieStore.get(httpUrl.host());
                return cookies != null ? cookies : new ArrayList<Cookie>();
            }
        }).build();
    }

    /**
     * Cookies list.
     *
     * @return the list
     */
    public List<Cookie> cookies() {
        List<Cookie> cookies = new ArrayList<Cookie>();
        Collection<List<Cookie>> values = cookieStore.values();
        for (List<Cookie> value : values) {
            cookies.addAll(value);
        }
        return cookies;
    }

    /**
     * Cookie string.
     *
     * @param name the name
     * @return the string
     */
    public String cookie(String name) {
        for (Cookie cookie : cookies()) {
            if (cookie.name().equalsIgnoreCase(name)) {
                return cookie.value();
            }
        }
        return null;
    }

    private Request createRequest(AbstractApiRequest request) {
        Request.Builder builder = new Request.Builder();
        if (HttpConstant.GET.equalsIgnoreCase(request.getMethod())) {
            builder.get();
            if (null != request.getParameters() && request.getParameters().size() > 0) {
                Set<String>   keys = request.getParameters().keySet();
                StringBuilder sbuf = new StringBuilder(request.getUrl());
                if (request.getUrl().contains("=")) {
                    sbuf.append("&");
                } else {
                    sbuf.append("?");
                }
                for (String key : keys) {
                    sbuf.append(key).append('=').append(request.getParameters().get(key)).append('&');
                }
                request.url(sbuf.substring(0, sbuf.length() - 1));
            }
        } else {
            builder.method(request.getMethod(), createRequestBody(request));
        }
        builder.url(request.getUrl());
        if (log.isDebugEnabled()) {
            log.debug("Request : {}", request.getUrl());
        }
        if (null != request.getHeaders()) {
            builder.headers(request.getHeaders());
        }
        return builder.build();
    }

    private RequestBody createRequestBody(AbstractApiRequest<?, ?> request) {
        if (request.isMultipart()) {
            MediaType             contentType = MediaType.parse(request.getContentType());
            MultipartBody.Builder builder     = new MultipartBody.Builder().setType(MultipartBody.FORM);

            for (Map.Entry<String, Object> parameter : request.getParameters().entrySet()) {
                String name  = parameter.getKey();
                Object value = parameter.getValue();
                if (value instanceof byte[]) {
                    builder.addFormDataPart(name, request.getFileName(), RequestBody.create(contentType, (byte[]) value));
                } else if (value instanceof File) {
                    builder.addFormDataPart(name, request.getFileName(), RequestBody.create(contentType, (File) value));
                } else if (value instanceof RequestBody) {
                    builder.addFormDataPart(name, request.getFileName(), (RequestBody) value);
                } else {
                    builder.addFormDataPart(name, String.valueOf(value));
                }
            }
            return builder.build();
        } else {
            if (request.isJsonBody()) {
                String json =new Gson().toJson(request.getParameters());
                if (log.isDebugEnabled()) {
                    log.debug("Request Body:\r\n{}", json);
                }
                return RequestBody.create(JSON, json);
            } else {
                FormBody.Builder builder = new FormBody.Builder();
                for (Map.Entry<String, Object> parameter : request.getParameters().entrySet()) {
                    builder.add(parameter.getKey(), String.valueOf(parameter.getValue()));
                }
                return builder.build();
            }

        }
    }

}
