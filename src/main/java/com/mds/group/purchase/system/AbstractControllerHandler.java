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

import cn.hutool.core.lang.Assert;
import com.alibaba.fastjson.JSON;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.util.function.Function;

/**
 * The type Abstract controller handler.
 *
 * @author pavawi
 */
public abstract class AbstractControllerHandler {

    private static Logger log = LoggerFactory.getLogger(AbstractControllerHandler.class);

    private static Function<ProceedingJoinPoint, AbstractControllerHandler> build;

    /**
     * The Proceeding join point.
     */
    protected ProceedingJoinPoint proceedingJoinPoint;
    /**
     * The Http servlet request.
     */
    protected HttpServletRequest httpServletRequest;
    /**
     * The Method name.
     */
    protected String methodName;
    /**
     * The Uri.
     */
    protected String uri;
    /**
     * The Request body.
     */
    protected String requestBody;
    /**
     * The Ip.
     */
    protected String ip;
    /**
     * The Method.
     */
    protected Method method;
    /**
     * The In data masking.
     */
    protected boolean inDataMasking;
    /**
     * The Out data masking.
     */
    protected boolean outDataMasking;

    /**
     * Instantiates a new Abstract controller handler.
     *
     * @param proceedingJoinPoint the proceeding join point
     */
    public AbstractControllerHandler(ProceedingJoinPoint proceedingJoinPoint) {
        Assert.notNull(proceedingJoinPoint, "proceedingJoinPoint");

        this.proceedingJoinPoint = proceedingJoinPoint;
        Signature signature = this.proceedingJoinPoint.getSignature();
        this.httpServletRequest = this.getHttpServletRequest(this.proceedingJoinPoint.getArgs());
        this.methodName = signature.getName();
        this.uri = null == this.httpServletRequest ? null : this.httpServletRequest.getRequestURI();
        this.requestBody = this.formatParameters(this.proceedingJoinPoint.getArgs());
        this.ip = null == this.httpServletRequest ? "" : this.httpServletRequest.getHeader("x-forwarded-for");
        this.inDataMasking = false;
        this.outDataMasking = false;
        if (signature instanceof MethodSignature) {
            MethodSignature methodSignature = (MethodSignature) signature;
            try {
                this.method = proceedingJoinPoint.getTarget().getClass().getMethod(this.methodName,
                        methodSignature.getParameterTypes());
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Gets build.
     *
     * @return the build
     */
    public static Function<ProceedingJoinPoint, AbstractControllerHandler> getBuild() {
        return build;
    }

    /**
     * Register build function.
     *
     * @param build the build
     */
    public static void registerBuildFunction(Function<ProceedingJoinPoint, AbstractControllerHandler> build) {
        Assert.notNull(build);

        AbstractControllerHandler.build = build;
    }

    /**
     * Handle object.
     *
     * @return the object
     * @throws Throwable the throwable
     */
    public abstract Object handle() throws Throwable;

    /**
     * Log in.
     */
    protected void logIn() {
        String requestBody = this.requestBody;
        if (this.inDataMasking) {
            requestBody = "Data Masking";
        }
        log.info(String.format("Start-[%s][%s][%s][body: %s]", this.ip, this.uri, this.methodName, requestBody));
    }

    /**
     * Log out api info.
     *
     * @param elapsedMilliseconds the elapsed milliseconds
     * @param success             the success
     * @param responseBody        the response body
     * @return the api info
     */
    protected ApiInfo logOut(long elapsedMilliseconds, boolean success, String responseBody) {
        ApiInfo apiInfo;
        if (success) {
            if (this.outDataMasking) {
                responseBody = "Data Masking";
            }
            apiInfo = new ApiInfo(elapsedMilliseconds, this.uri, this.ip, this.methodName, "success");
            log.info(
                    String.format(
//							"Success(%s)-[%s][%s][%s][response body: %s]",
                            "Success(%s)-[%s][%s][%s]",
                            elapsedMilliseconds,
                            this.ip,
                            this.uri,
                            this.methodName));
//							responseBody));
        } else {
            apiInfo = new ApiInfo(elapsedMilliseconds, this.uri, this.ip, this.methodName, "fail",responseBody);
            log.warn(
                    String.format(
//							"Failed(%s)-[%s][%s][%s][request body: %s]",
                            "Failed(%s)-[%s][%s][%s][request body: %s][response body: %s]",
                            elapsedMilliseconds,
                            this.ip,
                            this.uri,
                            this.methodName,
                            this.requestBody,
                            responseBody));
        }
        return apiInfo;
    }

    /**
     * Gets http servlet request.
     *
     * @param parameters the parameters
     * @return the http servlet request
     */
    protected HttpServletRequest getHttpServletRequest(Object[] parameters) {
        try {
            if (null != parameters) {
                for (Object parameter : parameters) {
                    if (parameter instanceof HttpServletRequest) {
                        return (HttpServletRequest) parameter;
                    }
                }
            }

            return ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        } catch (Exception e) {
            log.error(e.getMessage());

            return null;
        }
    }

    /**
     * Format parameters string.
     *
     * @param parameters the parameters
     * @return the string
     */
    protected String formatParameters(Object[] parameters) {
        if (null == parameters) {
            return null;
        } else {
            StringBuilder stringBuilder = new StringBuilder();
            for (int i = 0; i < parameters.length; i++) {
                Object parameter = parameters[i];
                if (parameter instanceof HttpServletRequest || parameter instanceof HttpServletResponse) {
                    continue;
                }

                stringBuilder.append(String.format("[%s]: %s.", i, JSON.toJSONString(parameter)));
            }

            return stringBuilder.toString();
        }
    }


}
