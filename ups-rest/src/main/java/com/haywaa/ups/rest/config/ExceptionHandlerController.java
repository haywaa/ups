package com.haywaa.ups.rest.config;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.alibaba.fastjson.JSON;
import com.haywaa.ups.domain.constants.ErrorCode;
import com.haywaa.ups.domain.exception.BizException;
import com.haywaa.ups.rest.web.HttpResult;

/**
 * @description
 * @author: haywaa
 * @create: 2019-11-26 14:46
 */
@RestControllerAdvice
public class ExceptionHandlerController {

    private static final Logger logger = LoggerFactory.getLogger(ExceptionHandlerController.class);

    @ResponseStatus(HttpStatus.OK)
    @ExceptionHandler(BizException.class)
    public HttpResult handleBizException(BizException ex) {
        return HttpResult.Failure(ex.getErrorCode(), ex.getMessage());
    }

    @ResponseStatus(HttpStatus.OK)
    @ExceptionHandler(Exception.class)
    public HttpResult handleException(Exception ex, HttpServletRequest request) {
        logger.error("controller execption => path: [" + request.getServletPath() + "], params: [" + JSON.toJSONString(request.getParameterMap()) + "]", ex);
        return HttpResult.Failure(ErrorCode.SERVICE_ERROR.getErrorNo(), ErrorCode.SERVICE_ERROR.getErrorMsg());
    }


}