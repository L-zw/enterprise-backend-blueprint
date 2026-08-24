package com.lzw.blueprint.ai.service.chat;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.lzw.blueprint.ai.entity.ChatMessage;

import java.util.List;

public interface ChatMessageService {
    IPage<ChatMessage> pageBySessionId(Long sessionId, int page, int size);
    List<ChatMessage> listBySessionId(Long sessionId);
    List<ChatMessage> listRecentBySessionId(Long sessionId, int limit);
    void save(ChatMessage message);
}