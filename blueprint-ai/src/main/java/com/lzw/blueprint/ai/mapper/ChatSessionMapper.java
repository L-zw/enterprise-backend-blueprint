package com.lzw.blueprint.ai.mapper;

import com.lzw.blueprint.ai.entity.ChatSession;
import com.lzw.blueprint.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface ChatSessionMapper extends BaseMapper<ChatSession> {

    @Select("SELECT * FROM chat_session WHERE user_id = #{userId} AND status = 'active' AND deleted = FALSE ORDER BY update_time DESC")
    List<ChatSession> findByUserId(Long userId);
}