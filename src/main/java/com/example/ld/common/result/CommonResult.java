package com.example.ld.common.result;

import lombok.Data;

import java.io.Serializable;

/**
 * @author HeYiyu
 * @date 2023/2/11
 */
@Data
public class CommonResult<T> implements Serializable {
    // 错误码
    private final int code;
    // 简略信息
    private final String msg;
    // 返回数据
    private final Object data;

    /**
     * 构造方法
     *
     * @param resultCode 错误码
     * @param message    简略信息
     * @param data       返回数据
     */
    public CommonResult(ResultCode resultCode, String message, T data) {
        this.code = resultCode.getCode();
        this.msg = message;
        this.data = data;
    }

    /**
     * 构造方法，data为null
     *
     * @param resultCode 错误码
     * @param message    简略信息
     */
    public CommonResult(ResultCode resultCode, String message) {
        this(resultCode, message, null);
    }

    /**
     * 构造方法，默认成功结果
     *
     * @param data 返回数据
     */
    public CommonResult(T data) {
        this.code = ResultCode.SUCCESS.getCode();
        this.msg = "Success";
        this.data = data;
    }

    /**
     * 构造方法，默认成功结果
     */
    public CommonResult() {
        this(null);
    }

    public static <T> CommonResult<T> success() {
        return new CommonResult<>();
    }

    public static <T> CommonResult<T> success(T data) {
        return new CommonResult<>(data);
    }
}
