package com.lzw.blueprint.ai.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.lzw.blueprint.ai.dto.ChatRequestDto;
import com.lzw.blueprint.ai.entity.ChatMessage;
import com.lzw.blueprint.ai.entity.ChatSession;
import com.lzw.blueprint.ai.service.chat.ChatMessageService;
import com.lzw.blueprint.ai.service.chat.ChatSessionService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/ai/chat/sessions")
public class SessionController {

    private final ChatSessionService chatSessionService;
    private final ChatMessageService chatMessageService;

    public SessionController(ChatSessionService chatSessionService, ChatMessageService chatMessageService) {
        this.chatSessionService = chatSessionService;
        this.chatMessageService = chatMessageService;
    }

    @GetMapping
    public List<ChatSession> list(@RequestParam Long userId) {
        return chatSessionService.listByUserId(userId);
    }

    @PostMapping
    public ChatSession create(@RequestBody ChatRequestDto request) {
        return chatSessionService.create(request.getUserId(), request.getContent(), null);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        chatSessionService.delete(id);
    }

    @GetMapping("/{id}/messages")
    public List<ChatMessage> messages(@PathVariable Long id,
                                      @RequestParam(defaultValue = "1") int page,
                                      @RequestParam(defaultValue = "50") int size) {
        IPage<ChatMessage> p = chatMessageService.pageBySessionId(id, page, size);
        return p.getRecords();
    }
}