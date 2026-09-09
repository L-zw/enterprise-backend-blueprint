package com.lzw.blueprint.ai.service.audit;

import com.lzw.blueprint.ai.config.AiProperties;
import com.lzw.blueprint.ai.mapper.AiUsageRecordMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsageLimitTest {

    @Mock
    private AiUsageRecordMapper aiUsageRecordMapper;

    private AiProperties properties() {
        AiProperties p = new AiProperties();
        p.getUsage().setDailyLimitTokens(500000);
        return p;
    }

    @Test
    void belowLimitIsAllowed() {
        AiProperties props = properties();
        AiUsageLimitService service = new AiUsageLimitService(aiUsageRecordMapper, props);
        when(aiUsageRecordMapper.todayTokensByUser(1L)).thenReturn(100L);

        assertThatCode(() -> service.checkLimit(1L)).doesNotThrowAnyException();
    }

    @Test
    void reachingLimitThrows429() {
        AiProperties props = properties();
        AiUsageLimitService service = new AiUsageLimitService(aiUsageRecordMapper, props);
        when(aiUsageRecordMapper.todayTokensByUser(1L)).thenReturn(500000L);

        assertThatThrownBy(() -> service.checkLimit(1L))
                .isInstanceOf(UsageLimitExceededException.class)
                .hasMessageContaining("500000");
    }

    @Test
    void nullUserIdSkipsCheck() {
        AiProperties props = properties();
        AiUsageLimitService service = new AiUsageLimitService(aiUsageRecordMapper, props);

        assertThatCode(() -> service.checkLimit(null)).doesNotThrowAnyException();
        verify(aiUsageRecordMapper, never()).todayTokensByUser(any());
    }

    @Test
    void disabledLimitNeverThrows() {
        AiProperties props = properties();
        props.getUsage().setDailyLimitTokens(0);
        AiUsageLimitService service = new AiUsageLimitService(aiUsageRecordMapper, props);
        when(aiUsageRecordMapper.todayTokensByUser(1L)).thenReturn(999999999L);

        assertThatCode(() -> service.checkLimit(1L)).doesNotThrowAnyException();
    }
}
