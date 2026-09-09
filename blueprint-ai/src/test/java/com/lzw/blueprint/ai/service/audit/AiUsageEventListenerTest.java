package com.lzw.blueprint.ai.service.audit;

import com.lzw.blueprint.ai.entity.AiUsageRecord;
import com.lzw.blueprint.ai.event.AiUsageEvent;
import com.lzw.blueprint.ai.mapper.AiUsageRecordMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AiUsageEventListenerTest {

    @Mock
    private AiUsageRecordMapper aiUsageRecordMapper;

    private AiUsageEventListener listener;

    @BeforeEach
    void setUp() {
        listener = new AiUsageEventListener(aiUsageRecordMapper);
    }

    @Test
    void skipsWhenNoUser() {
        AiUsageEvent event = new AiUsageEvent();
        event.setUserId(null);
        listener.onUsage(event);
        verify(aiUsageRecordMapper, never()).insertDaily(any());
    }

    @Test
    void insertsDailyRecord() {
        AiUsageEvent event = new AiUsageEvent();
        event.setUserId(1L);
        event.setProviderCode("openai");
        event.setModelId("gpt-4o");
        event.setTokensInput(100);
        event.setTokensOutput(50);
        event.setRequestCount(1);
        listener.onUsage(event);

        ArgumentCaptor<AiUsageRecord> captor = ArgumentCaptor.forClass(AiUsageRecord.class);
        verify(aiUsageRecordMapper).insertDaily(captor.capture());
        AiUsageRecord record = captor.getValue();
        assertThat(record.getUserId()).isEqualTo(1L);
        assertThat(record.getProviderCode()).isEqualTo("openai");
        assertThat(record.getTokensInput()).isEqualTo(100);
        assertThat(record.getTokensOutput()).isEqualTo(50);
    }

    @Test
    void nullTokenFieldsDefaultToZero() {
        AiUsageEvent event = new AiUsageEvent();
        event.setUserId(2L);
        event.setProviderCode("offline");
        listener.onUsage(event);

        ArgumentCaptor<AiUsageRecord> captor = ArgumentCaptor.forClass(AiUsageRecord.class);
        verify(aiUsageRecordMapper).insertDaily(captor.capture());
        assertThat(captor.getValue().getTokensInput()).isZero();
        assertThat(captor.getValue().getRequestCount()).isEqualTo(1);
    }
}
