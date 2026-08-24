package com.lzw.blueprint.ai.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lzw.blueprint.ai.entity.ChatMessage;
import com.lzw.blueprint.ai.mapper.ChatMessageMapper;
import com.lzw.blueprint.ai.service.chat.ChatMessageService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ChatMessageServiceImpl implements ChatMessageService {

    private final ChatMessageMapper chatMessageMapper;

    public ChatMessageServiceImpl(ChatMessageMapper chatMessageMapper) {
        this.chatMessageMapper = chatMessageMapper;
    }

    @Override
    public IPage<ChatMessage> pageBySessionId(Long sessionId, int page, int size) {
        return chatMessageMapper.selectPage(new Page<>(page, size),
                new LambdaQueryWrapper<ChatMessage>()
                        .eq(ChatMessage::getSessionId, sessionId)
                        .orderByAsc(ChatMessage::getCreateTime));
    }

    @Override
    public List<ChatMessage> listBySessionId(Long sessionId) {
        return chatMessageMapper.findBySessionId(sessionId);
    }

    @Override
    public List<ChatMessage> listRecentBySessionId(Long sessionId, int limit) {
        List<ChatMessage> all = chatMessageMapper.findBySessionId(sessionId);
        int size = all.size();
        return size <= limit ? all : all.subList(size - limit, size);
    }

    @Override
    public void save(ChatMessage message) {
        chatMessageMapper.insert(message);
    }
}