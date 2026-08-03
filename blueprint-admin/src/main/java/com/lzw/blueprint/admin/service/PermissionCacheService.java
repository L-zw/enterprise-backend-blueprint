package com.lzw.blueprint.admin.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lzw.blueprint.admin.mapper.SysMenuMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Service
public class PermissionCacheService {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private SysMenuMapper sysMenuMapper;

    private final ObjectMapper mapper = new ObjectMapper();

    private static final String PREFIX = "cache:permissions:";
    private static final long BASE_TTL = 300;
    private static final long NULL_TTL = 30;

    public Set<String> getPermissions(Long userId) {
        String key = PREFIX + userId;
        String cached = redisTemplate.opsForValue().get(key);
        if (cached != null) {
            if ("NULL".equals(cached)) {
                return new HashSet<>();
            }
            try {
                return mapper.readValue(cached, new TypeReference<Set<String>>() {});
            } catch (Exception e) {
                // fall through to DB
            }
        }
        List<String> list = sysMenuMapper.findPermissionsByUserId(userId);
        Set<String> result = new HashSet<>(list);
        long ttl = result.isEmpty() ? NULL_TTL : BASE_TTL + randomOffset();
        try {
            redisTemplate.opsForValue().set(key,
                    result.isEmpty() ? "NULL" : mapper.writeValueAsString(result),
                    ttl, TimeUnit.SECONDS);
        } catch (Exception e) {
            // log and continue
        }
        return result;
    }

    public void clearPermissions(Long userId) {
        redisTemplate.delete(PREFIX + userId);
    }

    private long randomOffset() {
        return (long) (BASE_TTL * 0.2 * (Math.random() * 2 - 1));
    }
}