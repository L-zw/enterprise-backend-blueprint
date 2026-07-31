package com.lzw.blueprint.core.aspect;

import com.lzw.blueprint.common.exception.ApiException;
import com.lzw.blueprint.core.annotation.RateLimiter;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.concurrent.TimeUnit;

@Aspect
@Component
public class RateLimiterAspect {

    @Autowired
    private StringRedisTemplate redisTemplate;

    private final ExpressionParser parser = new SpelExpressionParser();

    @Around("@annotation(limiter)")
    public Object check(ProceedingJoinPoint pjp, RateLimiter limiter) throws Throwable {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String ip = request.getRemoteAddr();

        StandardEvaluationContext ctx = new StandardEvaluationContext();
        ctx.setVariable("ip", ip);
        String key = "ratelimit:" + parser.parseExpression(limiter.key()).getValue(ctx, String.class);

        Long count = redisTemplate.opsForValue().increment(key);
        if (count == 1) {
            redisTemplate.expire(key, limiter.period(), TimeUnit.SECONDS);
        }
        if (count > limiter.max()) {
            throw new ApiException(429, "请求过于频繁，请稍后再试");
        }
        return pjp.proceed();
    }
}