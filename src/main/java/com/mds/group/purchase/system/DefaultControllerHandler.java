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

import com.alibaba.fastjson.JSON;
import com.mds.group.purchase.configurer.ApplicationContextHelper;
import com.mds.group.purchase.core.CodeMsg;
import com.mds.group.purchase.core.Result;
import com.mds.group.purchase.exception.ServiceException;
import org.aspectj.lang.ProceedingJoinPoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.*;

/**
 * The type Default controller handler.
 *
 * @author pavawi
 */
public class DefaultControllerHandler extends AbstractControllerHandler {

    /**
     * The Api map.
     */
    static Map<String, List<ApiInfo>> apiMap = new HashMap<>(16);
    /**
     * The Api count map.
     */
    static Map<String, Integer> apiCountMap = new HashMap<>(16);

    private RedisTemplate redisTemplate = (RedisTemplate) ApplicationContextHelper.getBean("redisTemplate");

    private static Logger log = LoggerFactory.getLogger(DefaultControllerHandler.class);
    private static int currentMonth = Calendar.getInstance().get(Calendar.MONTH) + 1;

    /**
     * Instantiates a new Default controller handler.
     *
     * @param proceedingJoinPoint the proceeding join point
     */
    public DefaultControllerHandler(ProceedingJoinPoint proceedingJoinPoint) {
        super(proceedingJoinPoint);
    }

    @Override
    public Object handle() throws Throwable {
        long timestamp = System.currentTimeMillis();
        this.logIn();

        Result response;
        boolean success = false;
        try {
            Object result = proceedingJoinPoint.proceed();
            if (result instanceof Result) {
                response = (Result) result;
            } else {
                response = Result.success(result);
            }
            success = true;
            RuntimeHealthIndicator.successRequestCount++;
        } catch (ServiceException e) {
            //            RuntimeHealthIndicator.failedRequestCount++;
            if (this.isDebugLogLevel()) {
                log.error(e.toString());
            }

            response = new Result(400, e.getMessage());
        } catch (Exception e) {
            RuntimeHealthIndicator.failedRequestCount++;

            if (this.isDebugLogLevel()) {
                log.error(e.getMessage());
            }

            response = Result.error(CodeMsg.FAIL.fillArgs(e.getMessage()));
        } finally {
            Calendar cale = Calendar.getInstance();
            if (currentMonth != (cale.get(Calendar.MONTH) + 1)) {
                String recodeKey = String.format("%d年%d月", cale.get(Calendar.YEAR), cale.get(Calendar.MONTH) + 1);
                String recodeValue = "successCount:" + RuntimeHealthIndicator.successRequestCount + " failedCount:"
                        + RuntimeHealthIndicator.failedRequestCount;
                RuntimeHealthIndicator.historyRequestRecode.put(recodeKey, recodeValue);
                RuntimeHealthIndicator.successRequestCount = 0;
                RuntimeHealthIndicator.failedRequestCount = 0;
                currentMonth = cale.get(Calendar.MONTH);
            }
        }

        long duration = System.currentTimeMillis() - timestamp;
        RuntimeHealthIndicator.markRestApiInvoked(this.methodName, (int) duration);
        ApiInfo apiInfo = this.logOut(duration, success, JSON.toJSONString(response));
        //		String redisKey = GroupMallProperties.getRedisPrefix().concat("APIINFO:").concat(uri);
        //		redisTemplate.opsForList().leftPush(redisKey, apiInfo);
        Integer count = apiCountMap.get(apiInfo.getUrl());
        count = count == null ? 0 : count;
        apiCountMap.put(apiInfo.getUrl(), count + 1);
        if (!success) {
            List<ApiInfo> apiInfos = apiMap.get(apiInfo.getUrl());
            if (apiInfos == null) {
                apiInfos = new ArrayList<>();
            }
            apiInfos.add(apiInfo);
            apiMap.put(apiInfo.getUrl(), apiInfos);
        }
        return response;
    }

    /**
     * Is debug log level boolean.
     *
     * @return the boolean
     */
    public boolean isDebugLogLevel() {
        return log.isDebugEnabled();
    }
}