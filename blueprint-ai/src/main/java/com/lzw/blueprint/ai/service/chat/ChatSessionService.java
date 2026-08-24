package com.lzw.blueprint.ai.service.chat;

import com.lzw.blueprint.ai.entity.ChatSession;

import java.util.List;

public interface ChatSessionService {
    List<ChatSession> listByUserId(Long userId);
    ChatSession getById(Long id);
    ChatSession create(Long userId, String title, String systemPrompt);
    void delete(Long id);
}