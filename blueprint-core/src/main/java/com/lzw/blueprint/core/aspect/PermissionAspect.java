package com.lzw.blueprint.core.aspect;

import com.lzw.blueprint.common.ResultCode;
import com.lzw.blueprint.common.exception.ApiException;
import com.lzw.blueprint.core.annotation.RequirePermission;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.List;

/**
 * 权限校验切面
 * 拦截 @RequirePermission 注解，检查当前用户是否拥有指定权限
 * 超级管理员（admin 角色）跳过所有权限校验
 */
@Aspect
@Component
public class PermissionAspect {

    @Around("@annotation(requirePermission)")
    public Object checkPermission(ProceedingJoinPoint pjp, RequirePermission requirePermission) throws Throwable {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        Long userId = (Long) request.getAttribute("userId");

        if (userId == null) {
            throw new ApiException(ResultCode.UNAUTHORIZED);
        }

        List<String> roles = (List<String>) request.getAttribute("roles");
        if (roles != null && roles.contains("admin")) {
            return pjp.proceed();
        }

        List<String> permissions = (List<String>) request.getAttribute("permissions");
        if (permissions == null) {
            throw new ApiException(ResultCode.UNAUTHORIZED);
        }

        if (permissions.contains(requirePermission.value())) {
            return pjp.proceed();
        }

        throw new ApiException(ResultCode.FORBIDDEN);
    }
}