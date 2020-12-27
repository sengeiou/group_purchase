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

package com.mds.group.purchase.utils.http.response;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.lang.reflect.Type;

/**
 * The type Api response.
 *
 * @author pavawi
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApiResponse {

    private static final Gson gson = new Gson();

    /**
     * The Raw body.
     */
    protected String rawBody;

    /**
     * Parse t.
     *
     * @param <T>  the type parameter
     * @param type the type
     * @return the t
     */
    public <T> T parse(Class<T> type) {
        return gson.fromJson(rawBody, type);
    }

    /**
     * Parse t.
     *
     * @param <T>  the type parameter
     * @param type the type
     * @return the t
     */
    public <T> T parse(Type type) {
        return gson.fromJson(rawBody, type);
    }

    /**
     * To json object json object.
     *
     * @return the json object
     */
    public JsonObject toJsonObject() {
        return new JsonParser().parse(rawBody).getAsJsonObject();
    }

}