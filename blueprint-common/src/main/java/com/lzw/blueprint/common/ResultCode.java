package com.lzw.blueprint.common;

import lombok.Getter;

/**
 * 响应状态码枚举
 * 统一管理所有业务状态码，避免魔法数字
 */
@Getter
public enum ResultCode {

    SUCCESS(200, "success"),
    FAIL(500, "fail"),
    UNAUTHORIZED(401, "unauthorized"),
    FORBIDDEN(403, "forbidden"),
    NOT_FOUND(404, "not found");

    private final Integer code;
    private final String message;

    ResultCode(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}
