package com.lzw.blueprint.ai.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.lzw.blueprint.ai.dto.ChatRequestDto;
import com.lzw.blueprint.ai.entity.ChatMessage;
import com.lzw.blueprint.ai.entity.ChatSession;
import com.lzw.blueprint.ai.service.chat.ChatMessageService;
import com.lzw.blueprint.ai.service.chat.ChatSessionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "AI 会话管理", description = "会话与消息 CRUD")
@RestController
@RequestMapping("/v1/ai/chat/sessions")
public class SessionController {

    private final ChatSessionService chatSessionService;
    private final ChatMessageService chatMessageService;

    public SessionController(ChatSessionService chatSessionService, ChatMessageService chatMessageService) {
        this.chatSessionService = chatSessionService;
        this.chatMessageService = chatMessageService;
    }

    @Operation(summary = "会话列表")
    @GetMapping
    public List<ChatSession> list(@RequestParam Long userId) {
        return chatSessionService.listByUserId(userId);
    }

    @Operation(summary = "创建会话")
    @PostMapping
    public ChatSession create(@RequestBody ChatRequestDto request) {
        return chatSessionService.create(request.getUserId(), request.getContent(), null);
    }

    @Operation(summary = "删除会话")
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        chatSessionService.delete(id);
    }

    @Operation(summary = "会话消息列表（分页）")
    @GetMapping("/{id}/messages")
    public List<ChatMessage> messages(@PathVariable Long id,
                                      @RequestParam(defaultValue = "1") int page,
                                      @RequestParam(defaultValue = "50") int size) {
        IPage<ChatMessage> p = chatMessageService.pageBySessionId(id, page, size);
        return p.getRecords();
    }
}