package com.example.ld.common.exception;

import com.example.ld.common.result.ResultCode;
import lombok.Getter;

/**
 * @author HeYiyu
 * @date 2023/1/4
 */
@Getter
public class CommonException extends RuntimeException {
    // 错误码
    private final ResultCode resultCode;

    /**
     * 构造方法
     *
     * @param message 异常信息
     */
    public CommonException(String message) {
        this(ResultCode.SYSTEM_ERROR, message);
    }

    /**
     * 构造方法
     *
     * @param resultCode 错误码
     * @param message    异常信息
     */
    public CommonException(ResultCode resultCode, String message) {
        super(message);
        this.resultCode = resultCode;
    }
}
