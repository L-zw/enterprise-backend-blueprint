package com.lzw.blueprint.ai.service.audit;

import com.lzw.blueprint.ai.config.AiProperties;
import com.lzw.blueprint.ai.mapper.AiUsageRecordMapper;
import org.springframework.stereotype.Service;

@Service
public class AiUsageLimitService {

    private final AiUsageRecordMapper aiUsageRecordMapper;
    private final AiProperties aiProperties;

    public AiUsageLimitService(AiUsageRecordMapper aiUsageRecordMapper, AiProperties aiProperties) {
        this.aiUsageRecordMapper = aiUsageRecordMapper;
        this.aiProperties = aiProperties;
    }

    public void checkLimit(Long userId) {
        if (userId == null) {
            return;
        }
        long used = aiUsageRecordMapper.todayTokensByUser(userId);
        long limit = aiProperties.getUsage().getDailyLimitTokens();
        if (limit > 0 && used >= limit) {
            throw new UsageLimitExceededException(limit);
        }
    }
}
