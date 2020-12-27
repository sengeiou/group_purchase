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

import com.sun.management.OperatingSystemMXBean;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.actuate.health.AbstractHealthIndicator;
import org.springframework.boot.actuate.health.Health;
import org.springframework.stereotype.Component;

import java.lang.management.ManagementFactory;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * The type Runtime health indicator.
 *
 * @author pavawi
 */
@Component
public class RuntimeHealthIndicator extends AbstractHealthIndicator {
    private static Logger log = LoggerFactory.getLogger(RuntimeHealthIndicator.class);
    private static Map<String, RestApiInvokeStatus> restApiInvokeStatuses = new HashMap<>();
    /**
     * The Constant failedRequestCount.
     */
    public static long failedRequestCount = 0;
    /**
     * The Constant successRequestCount.
     */
    public static long successRequestCount = 0;

    /**
     * The History request recode.
     */
    public static Map<String, Object> historyRequestRecode;
    private Map<String, Object> details;

    /**
     * Instantiates a new Runtime health indicator.
     */
    public RuntimeHealthIndicator() {
        this.details = new HashMap<>();
        RuntimeHealthIndicator.historyRequestRecode = new HashMap<>();
        this.details.put("startTime", new Date(ManagementFactory.getRuntimeMXBean().getStartTime()));
        this.details.put("path", RuntimeHealthIndicator.class.getClassLoader().getResource("").getPath());
        this.details.put("osName", System.getProperty("os.name"));
        this.details.put("osVersion", System.getProperty("os.version"));
        this.details.put("javaVersion", System.getProperty("java.version"));
        this.details.put("ip", "127.0.0.1");
    }

    @Override
    protected void doHealthCheck(Health.Builder builder) throws Exception {

        ThreadGroup threadGroup = Thread.currentThread().getThreadGroup();
        while (null != threadGroup.getParent()) {
            threadGroup = threadGroup.getParent();
        }
        this.details.put("threadCount", threadGroup.activeCount());
        OperatingSystemMXBean operatingSystemMXBean = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
        this.details.put("cpuUsageRate", operatingSystemMXBean.getSystemCpuLoad());
        this.details.put(
                "memoryUsageRate",
                (float) (operatingSystemMXBean.getTotalPhysicalMemorySize() - operatingSystemMXBean.getFreePhysicalMemorySize()) / (float) operatingSystemMXBean.getTotalPhysicalMemorySize());
        this.details.put("failedRequestCount", RuntimeHealthIndicator.failedRequestCount);
        this.details.put("successRequestCount", RuntimeHealthIndicator.successRequestCount);
        this.details.put("restApiInvokeStatuses", RuntimeHealthIndicator.restApiInvokeStatuses);
        this.details.put("historyRequestRecode", RuntimeHealthIndicator.historyRequestRecode);

        for (Map.Entry<String, Object> detail : this.details.entrySet()) {
            builder.withDetail(detail.getKey(), detail.getValue());
        }
        builder.up();
    }

    /**
     * Mark rest api invoked.
     *
     * @param name     the name
     * @param duration the duration
     */
    public static void markRestApiInvoked(String name, int duration) {
        if (StringUtils.isBlank(name)) {
            return;
        }

        if (!RuntimeHealthIndicator.restApiInvokeStatuses.containsKey(name)) {
            RuntimeHealthIndicator.restApiInvokeStatuses.put(name, new RestApiInvokeStatus(name));
        }

        RestApiInvokeStatus restApiInvokeStatus = RuntimeHealthIndicator.restApiInvokeStatuses.get(name);
        restApiInvokeStatus.setDuration(duration);
    }
}