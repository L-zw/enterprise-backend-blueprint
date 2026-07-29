package com.lzw.blueprint.admin.config;

import com.lzw.blueprint.admin.entity.SysOperationLog;
import com.lzw.blueprint.admin.service.SysOperationLogService;
import com.lzw.blueprint.core.annotation.OperationLog;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.expression.Expression;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.LocalDateTime;

@Aspect
@Component
public class OperationLogAspect {

    @Autowired
    private SysOperationLogService operationLogService;

    private final SpelExpressionParser parser = new SpelExpressionParser();

    @Around("@annotation(operationLog)")
    public Object log(ProceedingJoinPoint pjp, OperationLog operationLog) throws Throwable {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        long start = System.currentTimeMillis();

        String target = parseTarget(operationLog.target(), pjp);

        Object result;
        String resultStr = "SUCCESS";
        String errorMsg = null;
        try {
            result = pjp.proceed();
            resultStr = "SUCCESS";
        } catch (Throwable e) {
            resultStr = "FAIL";
            errorMsg = e.getMessage();
            throw e;
        } finally {
            long duration = System.currentTimeMillis() - start;
            saveLog(request, operationLog, target, resultStr, errorMsg, duration);
        }
        return result;
    }

    private void saveLog(HttpServletRequest request, OperationLog operationLog,
                         String target, String result, String errorMsg, long duration) {
        SysOperationLog log = new SysOperationLog();
        log.setUserId((Long) request.getAttribute("userId"));
        log.setUsername((String) request.getAttribute("username"));
        log.setModule(operationLog.module());
        log.setOperation(operationLog.operation());
        log.setTarget(target);
        log.setRequestUrl(request.getRequestURI());
        log.setRequestMethod(request.getMethod());
        log.setResult(result);
        log.setErrorMsg(errorMsg);
        log.setDuration(duration);
        log.setIp(request.getRemoteAddr());
        log.setCreateTime(LocalDateTime.now());
        operationLogService.saveAsync(log);
    }

    private String parseTarget(String expression, ProceedingJoinPoint pjp) {
        if (expression.isEmpty()) {
            return "";
        }
        MethodSignature sig = (MethodSignature) pjp.getSignature();
        String[] paramNames = sig.getParameterNames();
        Object[] args = pjp.getArgs();
        StandardEvaluationContext ctx = new StandardEvaluationContext();
        for (int i = 0; i < args.length; i++) {
            ctx.setVariable(paramNames[i], args[i]);
        }
        try {
            Expression exp = parser.parseExpression(expression);
            return exp.getValue(ctx, String.class);
        } catch (Exception e) {
            return expression;
        }
    }
}