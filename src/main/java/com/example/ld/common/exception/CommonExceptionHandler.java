package com.example.ld.common.exception;

import cn.hutool.core.exceptions.ExceptionUtil;
import cn.hutool.http.HttpException;
import com.example.ld.common.result.CommonResult;
import com.example.ld.common.result.ResultCode;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.Objects;

/**
 * @author HeYiyu
 * @date 2023/1/4
 */
@Slf4j
@RestControllerAdvice
public class CommonExceptionHandler {
    /**
     * 自定义异常
     */
    @ExceptionHandler(CommonException.class)
    public CommonResult<String> handleSuiteException(CommonException e) {
        log.error(e.getMessage());
        return new CommonResult<>(e.getResultCode(), e.getMessage());
    }

    /**
     * 请求参数错误，错误码 400
     */
    @ExceptionHandler({MissingServletRequestParameterException.class, MethodArgumentTypeMismatchException.class, HttpMessageNotReadableException.class})
    public CommonResult<String> handleRequestParameterException(Exception e) {
        log.error(e.getMessage());
        return new CommonResult<>(ResultCode.REQUEST_PARAMETER, e.getMessage());
    }

    /**
     * 未知URL异常，错误码 404
     */
    @ExceptionHandler(NoHandlerFoundException.class)
    public CommonResult<String> handlerNoHandlerException(NoHandlerFoundException e) {
        log.error(e.getMessage());
        return new CommonResult<>(ResultCode.NO_HANDLER_FOUND, e.getMessage());
    }

    /**
     * 请求方式异常，错误码 405
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public CommonResult<String> handleHttpRequestMethodNotSupportedException(Exception e) {
        log.error(e.getMessage());
        return new CommonResult<>(ResultCode.REQUEST_METHOD, e.getMessage());
    }

    /**
     * 请求媒体类型错误，错误码 415
     */
    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public CommonResult<String> handlerHttpMediaTypeNotSupportedException(Exception e) {
        log.error(e.getMessage());
        return new CommonResult<>(ResultCode.MEDIA_TYPE, e.getMessage());
    }

    /**
     * 空指针异常，错误码 500
     */
    @ExceptionHandler(NullPointerException.class)
    public CommonResult<String> handlerNullPointerException(Exception e) {
        log.error(ExceptionUtil.stacktraceToString(e));
        return new CommonResult<>(ResultCode.SYSTEM_ERROR, "系统内部错误，空指针异常");
    }

    /**
     * HTTP请求超时异常，错误码 504
     */
    @ExceptionHandler({HttpException.class, SocketTimeoutException.class})
    public CommonResult<String> handleTimeOutException(Exception e) {
        log.error(ExceptionUtil.stacktraceToString(e));
        return new CommonResult<>(ResultCode.TIMEOUT, e.getMessage());
    }

    /**
     * 参数异常，错误码 511
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public CommonResult<String> handleIllegalArgumentException(Exception e) {
        log.error(ExceptionUtil.stacktraceToString(e));
        return new CommonResult<>(ResultCode.ILLEGAL_ARGUMENT, e.getMessage());
    }

    /**
     * 接口对象参数校验异常，错误码511
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public CommonResult<String> handleValidException(MethodArgumentNotValidException e) {
        log.error(e.getMessage());
        return new CommonResult<>(ResultCode.ILLEGAL_ARGUMENT, Objects.requireNonNull(e.getBindingResult().getFieldError()).getDefaultMessage());
    }

    /**
     * JSON解析异常，错误码 512
     */
    @ExceptionHandler(JsonProcessingException.class)
    public CommonResult<String> handleJsonException(Exception e) {
        log.error(ExceptionUtil.stacktraceToString(e));
        return new CommonResult<>(ResultCode.JSON_EXCEPTION, "json解析异常，请确认数据格式填写正确");
    }

    /**
     * 文件读写异常，错误码 513
     */
    @ExceptionHandler(IOException.class)
    public CommonResult<String> handleFileException(Exception e) {
        log.error(ExceptionUtil.stacktraceToString(e));
        return new CommonResult<>(ResultCode.IO_EXCEPTION, e.getMessage());
    }

    /**
     * 文件不存在或权限不足，错误码 514
     */
    @ExceptionHandler(FileNotFoundException.class)
    public CommonResult<String> handleFileNotFoundException(Exception e) {
        log.error(ExceptionUtil.stacktraceToString(e));
        return new CommonResult<>(ResultCode.FILE_NOT_FOUND, e.getMessage());
    }
}
