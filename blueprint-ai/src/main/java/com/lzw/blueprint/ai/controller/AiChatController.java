package com.lzw.blueprint.ai.controller;

import com.lzw.blueprint.ai.dto.ChatRequestDto;
import com.lzw.blueprint.ai.dto.ChatResponseDto;
import com.lzw.blueprint.ai.service.AiChatService;
import com.lzw.blueprint.ai.service.chat.SseEmitterManager;
import com.lzw.blueprint.core.annotation.OperationLog;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Tag(name = "AI 对话", description = "AI 聊天与流式回复")
@RestController
@RequestMapping("/v1/ai")
public class AiChatController {

    private final AiChatService aiChatService;
    private final SseEmitterManager sseEmitterManager;

    public AiChatController(AiChatService aiChatService, SseEmitterManager sseEmitterManager) {
        this.aiChatService = aiChatService;
        this.sseEmitterManager = sseEmitterManager;
    }

    @Operation(summary = "非流式对话")
    @OperationLog(module = "AI对话", operation = "发送消息")
    @PostMapping("/chat")
    public ChatResponseDto chat(@RequestBody ChatRequestDto request) {
        return aiChatService.chat(request.getUserId(), request.getSessionId(), request.getContent());
    }

    @Operation(summary = "流式对话（SSE）")
    @PostMapping(value = "/chat/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter chatStream(@RequestBody ChatRequestDto request) {
        String sid = request.getSessionId() != null ? request.getSessionId() : String.valueOf(System.currentTimeMillis());
        SseEmitter emitter = sseEmitterManager.register(sid);
        aiChatService.chatStream(request.getUserId(), request.getSessionId(), request.getContent())
                .subscribe(
                        chunk -> {
                            try {
                                emitter.send(SseEmitter.event().data(chunk));
                            } catch (Exception ignored) {
                            }
                        },
                        emitter::completeWithError,
                        emitter::complete);
        return emitter;
    }
}