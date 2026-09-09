package com.lzw.blueprint.ai.service.impl;

import com.lzw.blueprint.ai.entity.AiProvider;
import com.lzw.blueprint.ai.mapper.AiProviderMapper;
import com.lzw.blueprint.ai.provider.ProviderRouter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AiProviderServiceImplTest {

    @Mock
    private AiProviderMapper aiProviderMapper;
    @Mock
    private ProviderRouter providerRouter;

    private AiProviderServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new AiProviderServiceImpl(aiProviderMapper, providerRouter);
    }

    private AiProvider provider(Long id) {
        AiProvider p = new AiProvider();
        p.setId(id);
        p.setProviderCode("openai");
        return p;
    }

    @Test
    void insertClearsCache() {
        when(aiProviderMapper.insert(any())).thenReturn(1);
        assertThat(service.insert(provider(1L))).isEqualTo(1);
        verify(providerRouter).clearCache();
    }

    @Test
    void updateClearsCache() {
        when(aiProviderMapper.updateById(any())).thenReturn(1);
        assertThat(service.updateById(provider(1L))).isEqualTo(1);
        verify(providerRouter).clearCache();
    }

    @Test
    void deleteClearsCache() {
        when(aiProviderMapper.deleteById(1L)).thenReturn(1);
        assertThat(service.deleteById(1L)).isEqualTo(1);
        verify(providerRouter).clearCache();
    }

    @Test
    void selectById() {
        when(aiProviderMapper.selectById(1L)).thenReturn(provider(1L));
        assertThat(service.selectById(1L).getId()).isEqualTo(1L);
    }

    @Test
    void selectList() {
        when(aiProviderMapper.selectList(any())).thenReturn(List.of(provider(1L)));
        assertThat(service.selectList()).hasSize(1);
    }
}
