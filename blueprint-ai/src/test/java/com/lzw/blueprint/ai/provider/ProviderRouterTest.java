package com.lzw.blueprint.ai.provider;

import com.lzw.blueprint.ai.entity.AiProvider;
import com.lzw.blueprint.ai.mapper.AiProviderMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProviderRouterTest {

    @Mock
    private AiProviderMapper aiProviderMapper;

    private ProviderRouter router;

    private AiProvider high;
    private AiProvider low;

    @BeforeEach
    void setUp() {
        router = new ProviderRouter(aiProviderMapper);
        high = provider("openai", 10);
        low = provider("ollama", 1);
    }

    private AiProvider provider(String code, int priority) {
        AiProvider p = new AiProvider();
        p.setProviderCode(code);
        p.setPriority(priority);
        p.setEnabled(true);
        return p;
    }

    @Test
    void resolveDefaultReturnsHighestPriority() {
        when(aiProviderMapper.findEnabled()).thenReturn(Arrays.asList(high, low));
        assertThat(router.resolveProvider(null)).isSameAs(high);
    }

    @Test
    void resolveByCodeReturnsMatchingProvider() {
        when(aiProviderMapper.findEnabled()).thenReturn(Arrays.asList(high, low));
        assertThat(router.resolveProvider("ollama")).isSameAs(low);
    }

    @Test
    void resolveByUnknownCodeReturnsNull() {
        when(aiProviderMapper.findEnabled()).thenReturn(Arrays.asList(high, low));
        assertThat(router.resolveProvider("unknown")).isNull();
    }

    @Test
    void resolveWhenEmptyReturnsNull() {
        when(aiProviderMapper.findEnabled()).thenReturn(Collections.emptyList());
        assertThat(router.resolveProvider(null)).isNull();
    }

    @Test
    void cacheHitDoesNotReload() {
        when(aiProviderMapper.findEnabled()).thenReturn(Arrays.asList(high, low));
        router.getEnabledProviders();
        router.getEnabledProviders();
        verify(aiProviderMapper, times(1)).findEnabled();
    }

    @Test
    void clearCacheForcesReload() {
        when(aiProviderMapper.findEnabled()).thenReturn(Arrays.asList(high, low));
        router.getEnabledProviders();
        router.clearCache();
        router.getEnabledProviders();
        verify(aiProviderMapper, times(2)).findEnabled();
    }

    @Test
    void emptyCacheLoadsOnCall() {
        List<AiProvider> providers = router.getEnabledProviders();
        assertThat(providers).isEmpty();
        verify(aiProviderMapper, times(1)).findEnabled();
    }
}
