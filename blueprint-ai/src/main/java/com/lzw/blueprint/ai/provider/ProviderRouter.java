package com.lzw.blueprint.ai.provider;

import com.lzw.blueprint.ai.entity.AiProvider;
import com.lzw.blueprint.ai.mapper.AiProviderMapper;
import org.springframework.stereotype.Component;
import java.util.Collections;
import java.util.List;

/**
 * 供应商路由器，按 priority 排序缓存已启用的供应商。
 * 缓存 TTL 5 分钟，任何 CRUD 操作后请调用 {@link #clearCache()} 刷新。
 */
@Component
public class ProviderRouter {

    private static final long CACHE_TTL_MS = 5 * 60 * 1000L;
    private volatile List<AiProvider> cache = Collections.emptyList();
    private volatile long cacheTimestamp = 0L;

    private final AiProviderMapper aiProviderMapper;

    public ProviderRouter(AiProviderMapper aiProviderMapper) {
        this.aiProviderMapper = aiProviderMapper;
    }

    private List<AiProvider> loadProviders() {
        return aiProviderMapper.findEnabled();
    }

    /**
     * 获取已启用的供应商列表，带有 5 分钟 TTL 缓存。
     */
    public List<AiProvider> getEnabledProviders() {
        long now = System.currentTimeMillis();
        if (now - cacheTimestamp > CACHE_TTL_MS || cache.isEmpty()) {
            synchronized (this) {
                if (now - cacheTimestamp > CACHE_TTL_MS || cache.isEmpty()) {
                    cache = loadProviders();
                    cacheTimestamp = now;
                }
            }
        }
        return cache;
    }

    /**
     * 根据 providerCode（可空）解析出要使用的供应商。
     * 若 providerCode 为 null/empty，返回 priority 最高的已启用供应商。
     */
    public AiProvider resolveProvider(String providerCode) {
        List<AiProvider> providers = getEnabledProviders();
        if (providers.isEmpty()) {
            return null;
        }
        if (providerCode != null && !providerCode.isBlank()) {
            return providers.stream()
                    .filter(p -> providerCode.equals(p.getProviderCode()))
                    .findFirst()
                    .orElse(null);
        }
        // 默认返回 priority 最高的（列表已按 priority DESC 排序）
        return providers.get(0);
    }

    /**
     * 清除缓存，让下次调用重新加载。
     */
    public void clearCache() {
        cache = Collections.emptyList();
        cacheTimestamp = 0L;
    }
}
