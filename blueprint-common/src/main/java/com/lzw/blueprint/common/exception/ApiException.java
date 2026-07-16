package com.lzw.blueprint.common.exception;

import com.lzw.blueprint.common.ResultCode;
import lombok.Getter;

/**
 * 通用API异常
 * 业务层抛出此异常，由 GlobalExceptionHandler 统一处理返回 Result
 */
@Getter
public class ApiException extends RuntimeException {

    private final Integer code;

    public ApiException(String message) {
        super(message);
        this.code = ResultCode.FAIL.getCode();
    }

    public ApiException(Integer code, String message) {
        super(message);
        this.code = code;
    }

    public ApiException(ResultCode resultCode) {
        super(resultCode.getMessage());
        this.code = resultCode.getCode();
    }
}