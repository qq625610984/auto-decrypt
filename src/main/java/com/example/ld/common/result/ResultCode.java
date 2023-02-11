package com.example.ld.common.result;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author HeYiyu
 * @date 2023/1/4
 */
@Getter
@AllArgsConstructor
public enum ResultCode {
    // 请求成功
    SUCCESS(200),
    // 请求参数错误
    REQUEST_PARAMETER(400),
    // 认证失败
    UNAUTHORIZED(401),
    // 找不到处理方法
    NO_HANDLER_FOUND(404),
    // 请求方法错误
    REQUEST_METHOD(405),
    // 媒体类型错误
    MEDIA_TYPE(415),
    // 系统错误
    SYSTEM_ERROR(500),
    // HTTP请求超时
    TIMEOUT(504),
    // 参数异常
    ILLEGAL_ARGUMENT(511),
    // JSON解析异常
    JSON_EXCEPTION(512),
    // 文件读写异常
    IO_EXCEPTION(513),
    // 文件不存在或权限不足
    FILE_NOT_FOUND(514),
    // 权限异常
    LACK_AUTH(515),
    // 数据库异常
    DATABASE_ERROR(516);

    // 错误码
    private final int code;
}
