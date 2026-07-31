package com.lzw.blueprint.core.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class RedisLockUtil {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    private static final String PREFIX = "lock:";

    public boolean tryLock(String key, long timeout, TimeUnit unit) {
        return Boolean.TRUE.equals(redisTemplate.opsForValue().setIfAbsent(PREFIX + key, "1", timeout, unit));
    }

    public void unlock(String key) {
        redisTemplate.delete(PREFIX + key);
    }
}