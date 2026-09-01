package com.lzw.blueprint.ai.service.audit;

import com.lzw.blueprint.ai.entity.AiUsageRecord;
import com.lzw.blueprint.ai.event.AiUsageEvent;
import com.lzw.blueprint.ai.mapper.AiUsageRecordMapper;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class AiUsageEventListener {

    private final AiUsageRecordMapper aiUsageRecordMapper;

    public AiUsageEventListener(AiUsageRecordMapper aiUsageRecordMapper) {
        this.aiUsageRecordMapper = aiUsageRecordMapper;
    }

    @Async
    @EventListener
    public void onUsage(AiUsageEvent event) {
        if (event.getUserId() == null) {
            return;
        }
        AiUsageRecord record = new AiUsageRecord();
        record.setUserId(event.getUserId());
        record.setProviderCode(event.getProviderCode());
        record.setModelId(event.getModelId());
        record.setTokensInput(event.getTokensInput() == null ? 0 : event.getTokensInput());
        record.setTokensOutput(event.getTokensOutput() == null ? 0 : event.getTokensOutput());
        record.setRequestCount(event.getRequestCount() == null ? 1 : event.getRequestCount());
        aiUsageRecordMapper.insertDaily(record);
    }
}
