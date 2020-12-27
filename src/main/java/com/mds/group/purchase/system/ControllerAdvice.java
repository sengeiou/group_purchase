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

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.function.Function;

/**
 * The type Controller advice.
 *
 * @author pavawi
 */
@Component
//@Aspect
public class ControllerAdvice {

    private static Logger logger = LoggerFactory.getLogger(ControllerAdvice.class);

    /**
     * Handle object.
     *
     * @param proceedingJoinPoint the proceeding join point
     * @return the object
     * @throws Throwable the throwable
     */
    @Around("execution(public * *..*controller.*.*(..))")
    public Object handle(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        Object result;
        try {
            Function<ProceedingJoinPoint, AbstractControllerHandler> build = AbstractControllerHandler.getBuild();
            if (null == build) {
                AbstractControllerHandler.registerBuildFunction(DefaultControllerHandler::new);
            }
            build = AbstractControllerHandler.getBuild();
            AbstractControllerHandler controllerHandler = build.apply(proceedingJoinPoint);
            if (null == controllerHandler) {
                logger.warn(String.format("The method(%s) do not be handle by controller handler.", proceedingJoinPoint.getSignature().getName()));
                result = proceedingJoinPoint.proceed();
            } else {
                result = controllerHandler.handle();
            }
        } catch (Throwable throwable) {
            RuntimeHealthIndicator.failedRequestCount++;
            logger.error(new Exception(throwable).getMessage(), "Unknown exception- -!");

            throw throwable;
        }

        return result;
    }
}
