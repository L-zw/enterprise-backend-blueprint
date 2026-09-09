package com.lzw.blueprint.ai.service.impl;

import com.lzw.blueprint.ai.entity.ChatMessage;
import com.lzw.blueprint.ai.entity.ChatSession;
import com.lzw.blueprint.ai.event.AiUsageEvent;
import com.lzw.blueprint.ai.provider.ProviderRouter;
import com.lzw.blueprint.ai.service.AiChatService;
import com.lzw.blueprint.ai.service.audit.AiUsageLimitService;
import com.lzw.blueprint.ai.service.chat.ChatMessageService;
import com.lzw.blueprint.ai.service.chat.ChatSessionService;
import com.lzw.blueprint.ai.dto.ChatResponseDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AiChatServiceTest {

    @Mock
    private ProviderRouter providerRouter;
    @Mock
    private ChatSessionService chatSessionService;
    @Mock
    private ChatMessageService chatMessageService;
    @Mock
    private AiUsageLimitService aiUsageLimitService;
    @Mock
    private ApplicationEventPublisher eventPublisher;

    private AiChatService aiChatService;

    @BeforeEach
    void setUp() {
        aiChatService = new AiChatServiceImpl(providerRouter, chatSessionService, chatMessageService,
                aiUsageLimitService, eventPublisher);
    }

    private ChatSession session(Long id) {
        ChatSession s = new ChatSession();
        s.setId(id);
        s.setUserId(1L);
        s.setStatus("active");
        return s;
    }

    @Test
    void offlineWhenNoProviderConfigured() {
        when(providerRouter.resolveProvider(any())).thenReturn(null);
        when(chatSessionService.create(any(), any(), any())).thenReturn(session(1L));

        ChatResponseDto response = aiChatService.chat(1L, null, "hello");

        assertThat(response.getReply()).contains("[offline]");
        assertThat(response.getSessionId()).isEqualTo(1L);
    }

    @Test
    void invalidSessionIdCreatesNewSession() {
        when(providerRouter.resolveProvider(any())).thenReturn(null);
        when(chatSessionService.create(any(), any(), any())).thenReturn(session(2L));

        aiChatService.chat(1L, "not-a-number", "hello");

        verify(chatSessionService).create(eq(1L), any(), any());
    }

    @Test
    void existingSessionIsReused() {
        ChatSession existing = session(5L);
        when(providerRouter.resolveProvider(any())).thenReturn(null);
        when(chatSessionService.getById(5L)).thenReturn(existing);

        aiChatService.chat(1L, "5", "hello");

        verify(chatSessionService, never()).create(any(), any(), any());
    }

    @Test
    void messagesAreSaved() {
        when(providerRouter.resolveProvider(any())).thenReturn(null);
        when(chatSessionService.create(any(), any(), any())).thenReturn(session(1L));

        aiChatService.chat(1L, null, "hello");

        verify(chatMessageService, times(2)).save(any(ChatMessage.class));
    }

    @Test
    void usageEventPublishedForUser() {
        when(providerRouter.resolveProvider(any())).thenReturn(null);
        when(chatSessionService.create(any(), any(), any())).thenReturn(session(1L));

        aiChatService.chat(1L, null, "hello world");

        ArgumentCaptor<AiUsageEvent> captor = ArgumentCaptor.forClass(AiUsageEvent.class);
        verify(eventPublisher).publishEvent(captor.capture());
        assertThat(captor.getValue().getUserId()).isEqualTo(1L);
        assertThat(captor.getValue().getProviderCode()).isEqualTo("offline");
    }

    @Test
    void usageEventSkippedWhenNoUser() {
        when(providerRouter.resolveProvider(any())).thenReturn(null);
        when(chatSessionService.create(any(), any(), any())).thenReturn(session(1L));

        aiChatService.chat(null, null, "hello");

        verify(eventPublisher, never()).publishEvent(any());
    }
}
