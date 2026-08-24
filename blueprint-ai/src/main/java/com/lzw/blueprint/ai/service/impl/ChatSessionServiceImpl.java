package com.lzw.blueprint.ai.service.impl;

import com.lzw.blueprint.ai.entity.ChatSession;
import com.lzw.blueprint.ai.mapper.ChatSessionMapper;
import com.lzw.blueprint.ai.service.chat.ChatSessionService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class ChatSessionServiceImpl implements ChatSessionService {

    private final ChatSessionMapper chatSessionMapper;

    public ChatSessionServiceImpl(ChatSessionMapper chatSessionMapper) {
        this.chatSessionMapper = chatSessionMapper;
    }

    @Override
    public List<ChatSession> listByUserId(Long userId) {
        return chatSessionMapper.findByUserId(userId);
    }

    @Override
    public ChatSession getById(Long id) {
        return chatSessionMapper.selectById(id);
    }

    @Override
    public ChatSession create(Long userId, String title, String systemPrompt) {
        ChatSession session = new ChatSession();
        session.setUserId(userId);
        session.setTitle(title != null ? title : "新会话");
        session.setSystemPrompt(systemPrompt);
        session.setTemperature(BigDecimal.valueOf(0.7));
        session.setContextSize(10);
        session.setStatus("active");
        chatSessionMapper.insert(session);
        return session;
    }

    @Override
    public void delete(Long id) {
        chatSessionMapper.deleteById(id);
    }
}