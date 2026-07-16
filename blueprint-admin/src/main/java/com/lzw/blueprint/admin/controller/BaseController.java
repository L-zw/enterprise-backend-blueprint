package com.lzw.blueprint.admin.controller;

import com.lzw.blueprint.common.Result;

/**
 * Controller 基类
 * 封装 success / fail 快捷方法，子类直接调用
 */
public abstract class BaseController {

    protected <T> Result<T> success() {
        return Result.success();
    }

    protected <T> Result<T> success(T data) {
        return Result.success(data);
    }

    protected <T> Result<T> fail(String message) {
        return Result.fail(message);
    }

    protected <T> Result<T> fail(Integer code, String message) {
        return Result.fail(code, message);
    }
}