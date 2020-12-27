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

package com.mds.group.purchase.utils;

import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * The type File upload util.
 *
 * @author pavawi
 */
public class FileUploadUtil {

    /**
     * Upload image string.
     *
     * @param file       the file
     * @param appmodelId the appmodel id
     * @return the string
     */
    public static String uploadImage(File file, String appmodelId) {
        String url = "https://www.bgniao.cn/medusafile/common/v1/uploads/1001/groupmall/" + appmodelId;
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("files", file);
        String result = HttpUtil.post(url, paramMap);
        JSONObject jsonObject = new JSONObject(result);
        if ("SUCCESS".equalsIgnoreCase(jsonObject.getStr("message"))) {
            return jsonObject.getStr("data");
        } else {
            return "";
        }

    }

    /**
     * The entry point of application.
     *
     * @param args the input arguments
     */
    public static void main(String[] args) {

        //File file = FileUtil.file("E:\\11.jpg");
        //System.out.println(uploadImage(file, "S00050001wx17c66eb4da0ef6ab"));
    }
}
