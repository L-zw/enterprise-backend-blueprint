package com.lzw.blueprint.ai.mapper;

import com.lzw.blueprint.ai.entity.ChatMessage;
import com.lzw.blueprint.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface ChatMessageMapper extends BaseMapper<ChatMessage> {

    @Select("SELECT * FROM chat_message WHERE session_id = #{sessionId} ORDER BY create_time ASC")
    List<ChatMessage> findBySessionId(Long sessionId);
}