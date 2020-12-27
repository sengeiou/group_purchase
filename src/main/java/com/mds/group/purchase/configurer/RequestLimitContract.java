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

package com.mds.group.purchase.configurer;

import com.mds.group.purchase.constant.Common;
import com.mds.group.purchase.constant.RedisPrefix;
import com.mds.group.purchase.exception.RequestLimitException;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.concurrent.TimeUnit;

/**
 * The type Request limit contract.
 *
 * @author Administrator
 */
@Aspect
@Component
public class RequestLimitContract {

    private static final Logger logger = LoggerFactory.getLogger("requestLimitLogger");

    @Resource
    private RedisTemplate redisTemplate;

    /**
     * Web point cut.
     */
    @Pointcut("execution(public * com.mds.group.purchase.*.controller.*.*(..))")
    public void webPointCut() {
    }

    /**
     * Request limit.
     *
     * @param joinPoint the join point
     * @param limit     the limit
     * @throws RequestLimitException the request limit exception
     */
    @Before("webPointCut()&& @annotation(limit)")
    public void requestLimit(final JoinPoint joinPoint, RequestLimit limit) throws RequestLimitException {

        Object[] args = joinPoint.getArgs();
        HttpServletRequest request = null;
        for (Object arg : args) {
            if (arg instanceof HttpServletRequest) {
                request = (HttpServletRequest) arg;
                break;
            }
        }
        if (request == null) {
            throw new RequestLimitException("方法中缺失HttpServletRequest参数");
        }
        String ip = request.getHeader("x-forwarded-for");
        if(ip == null || ip.length() == 0) {
            ip = request.getRemoteAddr();
        }
        logger.info("$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$用户ip" + ip);

        String url = request.getRequestURL().toString();
        String key = GroupMallProperties.getRedisPrefix().concat(RedisPrefix.HTTP_REQUEST_LIMIT).concat(url).concat(ip);
        long count = redisTemplate.opsForValue().increment(key, 1);
        if (count == 1) {
            redisTemplate.expire(key, limit.time(), TimeUnit.MILLISECONDS);
        }
        if (count > limit.count()) {
            request.setAttribute(Common.REQUEST_LIMT, Common.REQUEST_OVER_LIMIT);
            logger.info("用户IP[" + ip + "]访问地址[" + url + "]超过了限定的次数[" + limit.count() + "]");
            throw new RequestLimitException();
        }else {
            request.setAttribute(Common.REQUEST_LIMT, Common.REQUEST_IN_LIMIT);
        }
    }
}
