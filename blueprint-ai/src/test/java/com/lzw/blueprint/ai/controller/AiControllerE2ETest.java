package com.lzw.blueprint.ai.controller;

import com.lzw.blueprint.ai.dto.ChatResponseDto;
import com.lzw.blueprint.ai.entity.ChatMessage;
import com.lzw.blueprint.ai.entity.ChatSession;
import com.lzw.blueprint.ai.entity.PromptTemplate;
import com.lzw.blueprint.ai.service.AiChatService;
import com.lzw.blueprint.ai.service.AiProviderService;
import com.lzw.blueprint.ai.service.chat.ChatMessageService;
import com.lzw.blueprint.ai.service.chat.ChatSessionService;
import com.lzw.blueprint.ai.service.chat.SseEmitterManager;
import com.lzw.blueprint.ai.service.prompt.PromptTemplateService;
import com.lzw.blueprint.common.exception.GlobalExceptionHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import reactor.core.publisher.Flux;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

@ExtendWith(MockitoExtension.class)
class AiControllerE2ETest {

    @Mock
    private AiChatService aiChatService;
    @Mock
    private SseEmitterManager sseEmitterManager;
    @Mock
    private ChatSessionService chatSessionService;
    @Mock
    private ChatMessageService chatMessageService;
    @Mock
    private PromptTemplateService promptTemplateService;
    @Mock
    private AiProviderService aiProviderService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        AiChatController aiChatController = new AiChatController(aiChatService, sseEmitterManager);
        SessionController sessionController = new SessionController(chatSessionService, chatMessageService);
        PromptController promptController = new PromptController(promptTemplateService);
        AiProviderController providerController = new AiProviderController();
        ReflectionTestUtils.setField(providerController, "aiProviderService", aiProviderService);
        mockMvc = standaloneSetup(aiChatController, sessionController, promptController, providerController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    private ChatResponseDto reply(String text) {
        ChatResponseDto dto = new ChatResponseDto();
        dto.setSessionId(1L);
        dto.setReply(text);
        return dto;
    }

    @Test
    void chatReturnsReply() throws Exception {
        when(aiChatService.chat(1L, "1", "hello")).thenReturn(reply("hi"));

        mockMvc.perform(post("/v1/ai/chat")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"userId\":1,\"sessionId\":\"1\",\"content\":\"hello\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sessionId").value(1))
                .andExpect(jsonPath("$.reply").value("hi"));
    }

    @Test
    void streamReturnsEventStream() throws Exception {
        when(sseEmitterManager.register(any())).thenReturn(new org.springframework.web.servlet.mvc.method.annotation.SseEmitter());
        when(aiChatService.chatStream(any(), any(), any())).thenReturn(Flux.just("chunk1", "chunk2"));

        MvcResult result = mockMvc.perform(post("/v1/ai/chat/stream")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"userId\":1,\"content\":\"hi\"}"))
                .andExpect(request().asyncStarted())
                .andReturn();

        mockMvc.perform(asyncDispatch(result))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_EVENT_STREAM_VALUE))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("data:chunk1")));
    }

    @Test
    void listSessions() throws Exception {
        ChatSession session = new ChatSession();
        session.setId(1L);
        when(chatSessionService.listByUserId(1L)).thenReturn(List.of(session));

        mockMvc.perform(get("/v1/ai/chat/sessions").param("userId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));
    }

    @Test
    void createSession() throws Exception {
        ChatSession session = new ChatSession();
        session.setId(2L);
        when(chatSessionService.create(1L, "title", null)).thenReturn(session);

        mockMvc.perform(post("/v1/ai/chat/sessions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"userId\":1,\"content\":\"title\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(2));
    }

    @Test
    void deleteSession() throws Exception {
        mockMvc.perform(delete("/v1/ai/chat/sessions/1"))
                .andExpect(status().isOk());
    }

    @Test
    void listMessages() throws Exception {
        ChatMessage message = new ChatMessage();
        message.setId(1L);
        message.setRole("user");
        com.baomidou.mybatisplus.core.metadata.IPage<ChatMessage> page =
                new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(1, 50);
        page.setRecords(List.of(message));
        when(chatMessageService.pageBySessionId(1L, 1, 50)).thenReturn(page);

        mockMvc.perform(get("/v1/ai/chat/sessions/1/messages"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].role").value("user"));
    }

    @Test
    void listPrompts() throws Exception {
        PromptTemplate template = new PromptTemplate();
        template.setId(1L);
        template.setName("代码解释");
        when(promptTemplateService.list()).thenReturn(List.of(template));

        mockMvc.perform(get("/v1/ai/prompts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].name").value("代码解释"));
    }

    @Test
    void renderPrompt() throws Exception {
        when(promptTemplateService.render(1L, Map.of("code", "int x;"))).thenReturn("请解释 int x;");

        mockMvc.perform(post("/v1/ai/prompts/1/render")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"code\":\"int x;\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value("请解释 int x;"));
    }

    @Test
    void deleteBuiltinPromptReturns400() throws Exception {
        when(promptTemplateService.delete(1L)).thenThrow(new com.lzw.blueprint.common.exception.BusinessException(400, "内置模板不可删除"));

        mockMvc.perform(delete("/v1/ai/prompts/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(400));
    }

    @Test
    void listProviders() throws Exception {
        com.lzw.blueprint.ai.entity.AiProvider provider = new com.lzw.blueprint.ai.entity.AiProvider();
        provider.setId(1L);
        provider.setProviderCode("openai");
        when(aiProviderService.selectList()).thenReturn(List.of(provider));

        mockMvc.perform(get("/v1/ai/providers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].providerCode").value("openai"));
    }

    @Test
    void disableProvider() throws Exception {
        com.lzw.blueprint.ai.entity.AiProvider provider = new com.lzw.blueprint.ai.entity.AiProvider();
        provider.setId(1L);
        provider.setEnabled(true);
        when(aiProviderService.selectById(1L)).thenReturn(provider);
        when(aiProviderService.updateById(provider)).thenReturn(1);

        mockMvc.perform(patch("/v1/ai/providers/1/disable"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value(1));
    }
}
