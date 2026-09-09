package com.lzw.blueprint.ai.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lzw.blueprint.ai.entity.ChatMessage;
import com.lzw.blueprint.ai.mapper.ChatMessageMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ChatMessageServiceImplTest {

    @Mock
    private ChatMessageMapper chatMessageMapper;

    private ChatMessageServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new ChatMessageServiceImpl(chatMessageMapper);
    }

    private ChatMessage message(Long id) {
        ChatMessage m = new ChatMessage();
        m.setId(id);
        m.setRole("user");
        return m;
    }

    @Test
    void pageBySessionId() {
        Page<ChatMessage> page = new Page<>(1, 50);
        page.setRecords(List.of(message(1L)));
        when(chatMessageMapper.selectPage(any(Page.class), any())).thenReturn(page);

        IPage<ChatMessage> result = service.pageBySessionId(1L, 1, 50);
        assertThat(result.getRecords()).hasSize(1);
    }

    @Test
    void listBySessionId() {
        when(chatMessageMapper.findBySessionId(1L)).thenReturn(List.of(message(1L)));
        assertThat(service.listBySessionId(1L)).hasSize(1);
    }

    @Test
    void recentTrimsToLimit() {
        List<ChatMessage> all = new ArrayList<>();
        all.add(message(1L));
        all.add(message(2L));
        all.add(message(3L));
        when(chatMessageMapper.findBySessionId(1L)).thenReturn(all);

        List<ChatMessage> recent = service.listRecentBySessionId(1L, 2);
        assertThat(recent).hasSize(2);
        assertThat(recent.get(0).getId()).isEqualTo(2L);
        assertThat(recent.get(1).getId()).isEqualTo(3L);
    }

    @Test
    void recentKeepsAllWhenUnderLimit() {
        when(chatMessageMapper.findBySessionId(1L)).thenReturn(List.of(message(1L)));
        List<ChatMessage> recent = service.listRecentBySessionId(1L, 10);
        assertThat(recent).hasSize(1);
    }

    @Test
    void saveCallsMapper() {
        service.save(message(1L));
        verify(chatMessageMapper).insert(any(ChatMessage.class));
    }
}
