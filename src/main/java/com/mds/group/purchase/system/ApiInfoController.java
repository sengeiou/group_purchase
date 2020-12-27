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

import com.mds.group.purchase.core.Result;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.lang.management.ManagementFactory;
import java.util.HashMap;
import java.util.Map;

/**
 * The type Api info controller.
 *
 * @author pavawi
 */
@RestController
@RequestMapping("/api")
public class ApiInfoController {

    @Resource
    private RedisTemplate redisTemplate;
    @Value("${server.servlet.context-path}")
    private String project;

    @Value(("${groupmall.version}"))
    private String version;

    /**
     * Gets api info.
     *
     * @return the api info
     */
    @GetMapping("/info")
    public Result getApiInfo() {
        Map map = new HashMap();
        map.put("apiCountMap", DefaultControllerHandler.apiCountMap);
        map.put("apiMap", DefaultControllerHandler.apiMap);
        return Result.success(map);
    }

    /**
     * Get project info result.
     *
     * @return the result
     */
    @GetMapping("/project")
    public Result getProjectInfo() {
        long uptime = ManagementFactory.getRuntimeMXBean().getUptime();
        long day = uptime / (1000 * 60 * 60 * 24);
        long hour = (uptime % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60);
        long minute = ((uptime % (1000 * 60 * 60 * 24)) % (1000 * 60 * 60)) / (1000 * 60);
        Map<String, String> map = new HashMap<>(8);
        map.put("project", project.replace("/", ""));
        map.put("version", version);
        map.put("upTime", day + "天" + hour + "小时" + minute + "分钟");
        return Result.success(map);
    }

}
