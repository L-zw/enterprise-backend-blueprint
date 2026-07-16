package com.lzw.blueprint.common.exception;

import com.lzw.blueprint.common.Result;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.StringJoiner;

/**
 * 全局异常处理器
 * 统一返回 Result 格式，避免直接抛出 500
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ApiException.class)
    public Result<Void> handleApiException(ApiException e) {
        log.warn("Api exception: {}", e.getMessage());
        return Result.fail(e.getCode(), e.getMessage());
    }

    @ExceptionHandler(BusinessException.class)
    public Result<Void> handleBusinessException(BusinessException e) {
        log.warn("Business exception: {}", e.getMessage());
        return Result.fail(e.getCode(), e.getMessage());
    }

    /** @Validated + @RequestBody 校验失败 */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result<Void> handleValidation(MethodArgumentNotValidException e) {
        StringJoiner sj = new StringJoiner("; ");
        for (FieldError fe : e.getBindingResult().getFieldErrors()) {
            sj.add(fe.getDefaultMessage());
        }
        return Result.fail(400, sj.toString());
    }

    /** @ModelAttribute 或普通参数绑定失败 */
    @ExceptionHandler(BindException.class)
    public Result<Void> handleBind(BindException e) {
        StringJoiner sj = new StringJoiner("; ");
        for (FieldError fe : e.getBindingResult().getFieldErrors()) {
            sj.add(fe.getDefaultMessage());
        }
        return Result.fail(400, sj.toString());
    }

    /** @Validated + @RequestParam 参数校验失败 */
    @ExceptionHandler(ConstraintViolationException.class)
    public Result<Void> handleConstraintViolation(ConstraintViolationException e) {
        StringJoiner sj = new StringJoiner("; ");
        for (ConstraintViolation<?> cv : e.getConstraintViolations()) {
            sj.add(cv.getMessage());
        }
        return Result.fail(400, sj.toString());
    }

    @ExceptionHandler(Exception.class)
    public Result<Void> handleException(Exception e) {
        log.error("Unexpected exception", e);
        return Result.fail("Internal server error");
    }
}
