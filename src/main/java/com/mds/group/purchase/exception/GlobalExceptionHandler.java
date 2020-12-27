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

package com.mds.group.purchase.exception;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.mds.group.purchase.core.CodeMsg;
import com.mds.group.purchase.core.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;


/**
 * The type Global exception handler.
 *
 * @author pavawi
 */
@ControllerAdvice
@ResponseBody
public class GlobalExceptionHandler {

    private Logger logger = LoggerFactory.getLogger(GlobalException.class);

    /**
     * Exception handler result.
     *
     * @param request the request
     * @param e       the e
     * @return the result
     */
    @ExceptionHandler(value = Exception.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public Result<String> exceptionHandler(HttpServletRequest request, Exception e) {
        e.printStackTrace();
        StackTraceElement stackTraceElement = e.getStackTrace()[0];
        String target = "错误类:".concat(stackTraceElement.getClassName()).concat("错误方法:")
                .concat(stackTraceElement.getMethodName()).concat(",错误行数:")
                .concat(stackTraceElement.getLineNumber() + "");
        if (e instanceof GlobalException) {
            GlobalException globalException = (GlobalException) e;
            logger.info(e.getMessage());
            logger.info(target);
            return Result.error(globalException.getCodeMsg());
        } else {
            logger.error(e.getMessage(), e);
            logger.error(target);
            return Result.error(CodeMsg.SERVER_ERROR);
        }
    }

    /**
     * 方法参数校验
     *
     * @param ex the ex
     * @return result
     */
    @ExceptionHandler(value = {ConstraintViolationException.class})
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public Result<String> handleValidationFailure(ConstraintViolationException ex) {
        for (ConstraintViolation<?> violation : ex.getConstraintViolations()) {
            return Result.error(CodeMsg.PARAMETER_ERROR.fillArgs(violation.getMessage()));
        }
        return Result.error(CodeMsg.SERVER_ERROR);
    }

    /**
     * 方法对象校验
     *
     * @param request the request
     * @param e       the e
     * @return result
     */
    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public Result<String> methodArgumentNotValidExceptionHandler(HttpServletRequest request,
                                                                 MethodArgumentNotValidException e) {
        BindingResult bindingResult = e.getBindingResult();
        ObjectError objectError = bindingResult.getAllErrors().get(0);
        JSONObject jsonObject = JSONObject.parseObject(JSON.toJSONString(objectError));
        return Result.error(CodeMsg.BIND_ERROR
                .fillArgs(jsonObject.getString("defaultMessage")));
    }

    /**
     * 表单对象参数违反约束校验
     *
     * @param e the e
     * @return result
     */
    @ExceptionHandler(value = BindException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public Result<String> bindExceptionHandler(BindException e) {
        BindingResult bindingResult = e.getBindingResult();
        ObjectError objectError = bindingResult.getAllErrors().get(0);
        JSONObject jsonObject = JSONObject.parseObject(JSON.toJSONString(objectError));
        return Result.error(CodeMsg.BIND_ERROR
                .fillArgs(jsonObject.getString("defaultMessage")));
    }

    /**
     * 参数格式错误异常
     *
     * @param request the request
     * @param e       the e
     * @return result
     */
    @ExceptionHandler(value = HttpMessageNotReadableException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public Result<String> httpMessageNotReadableExceptionHandler(HttpServletRequest request, Exception e) {
        HttpMessageNotReadableException e1 = (HttpMessageNotReadableException) e;
        return Result.error(CodeMsg.PARAMETER_ERROR.fillArgs(e1.getMessage()));
    }

    /**
     * 业务异常
     *
     * @param request the request
     * @param e       the e
     * @return result
     */
    @ExceptionHandler(value = ServiceException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public Result<String> serviceExceptionHandler(HttpServletRequest request, Exception e) {
        StackTraceElement stackTraceElement = e.getStackTrace()[0];
        String target = "错误类:".concat(stackTraceElement.getClassName()).concat("错误方法:")
                .concat(stackTraceElement.getMethodName()).concat(",错误行数:")
                .concat(stackTraceElement.getLineNumber() + "");
        logger.info(target);
        return Result.error(CodeMsg.FAIL.fillArgs(e.getMessage()));
    }

    /**
     * Missing path variable exception result.
     *
     * @param request the request
     * @param e       the e
     * @return result
     */
    @ExceptionHandler(value = MissingPathVariableException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public Result<String> missingPathVariableException(HttpServletRequest request, Exception e) {
        return Result.error(CodeMsg.MISS_PATHVARIBLE);
    }

    /**
     * Missing servlet request parameter exception result.
     *
     * @param request the request
     * @param e       the e
     * @return result
     */
    @ExceptionHandler(value = MissingServletRequestParameterException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public Result<String> missingServletRequestParameterException(HttpServletRequest request, Exception e) {
        System.out.println(JSON.toJSONString(request.getParameterMap()));
        System.out.println(JSON.toJSONString(request.getParameterNames()));
        return Result.error(CodeMsg.MISS_PARAM.fillArgs(e.getMessage()));
    }

    /**
     * 请求头参数不匹配
     *
     * @param request the request
     * @param e       the e
     * @return result
     */
    @ExceptionHandler(value = MissingRequestHeaderException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public Result<String> missingRequestHeaderException(HttpServletRequest request, Exception e) {
        String[] strings = e.getMessage().split("'");
        return Result.error(CodeMsg.MISS_HEADER.fillArgs(strings[1] + "不能为空"));
    }

    /**
     * 请求方法错误
     *
     * @param request the request
     * @param e       the e
     * @return result
     */
    @ExceptionHandler(value = HttpRequestMethodNotSupportedException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public Result<String> httpRequestMethodNotSupportedException(HttpServletRequest request, Exception e) {
        return Result.error(CodeMsg.REQUEST_METHOD_ERROR.fillArgs(e.getMessage()));
    }

    /**
     * 请求次数超出限制
     *
     * @param request the request
     * @param e       the e
     * @return result
     */
    @ExceptionHandler(value = RequestLimitException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public Result<String> requestLimitException(HttpServletRequest request, Exception e) {
        return Result.error(CodeMsg.REQUEST_LIMIT.fillArgs(e.getMessage()));
    }


}
