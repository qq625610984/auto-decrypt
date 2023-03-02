package com.example.decrypt.common.exception;

import com.example.decrypt.common.result.ResultCode;
import lombok.Getter;

/**
 * @author HeYiyu
 * @date 2023/2/11
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
