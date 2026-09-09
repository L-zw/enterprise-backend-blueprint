package com.lzw.blueprint.ai.service.impl;

import com.lzw.blueprint.ai.entity.ChatSession;
import com.lzw.blueprint.ai.mapper.ChatSessionMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ChatSessionServiceImplTest {

    @Mock
    private ChatSessionMapper chatSessionMapper;

    private ChatSessionServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new ChatSessionServiceImpl(chatSessionMapper);
    }

    @Test
    void listByUserId() {
        ChatSession s = new ChatSession();
        s.setId(1L);
        when(chatSessionMapper.findByUserId(1L)).thenReturn(List.of(s));
        assertThat(service.listByUserId(1L)).hasSize(1);
    }

    @Test
    void getById() {
        ChatSession s = new ChatSession();
        s.setId(1L);
        when(chatSessionMapper.selectById(1L)).thenReturn(s);
        assertThat(service.getById(1L).getId()).isEqualTo(1L);
    }

    @Test
    void createSetsDefaults() {
        when(chatSessionMapper.insert(any())).thenReturn(1);

        ChatSession result = service.create(1L, null, "sys");

        assertThat(result.getUserId()).isEqualTo(1L);
        assertThat(result.getTitle()).isEqualTo("新会话");
        assertThat(result.getSystemPrompt()).isEqualTo("sys");
        assertThat(result.getTemperature()).isEqualByComparingTo(BigDecimal.valueOf(0.7));
        assertThat(result.getContextSize()).isEqualTo(10);
        assertThat(result.getStatus()).isEqualTo("active");
    }

    @Test
    void createKeepsProvidedTitle() {
        when(chatSessionMapper.insert(any())).thenReturn(1);
        ChatSession result = service.create(1L, "我的会话", null);
        assertThat(result.getTitle()).isEqualTo("我的会话");
    }

    @Test
    void deleteCallsMapper() {
        service.delete(1L);
        verify(chatSessionMapper).deleteById(1L);
    }
}
