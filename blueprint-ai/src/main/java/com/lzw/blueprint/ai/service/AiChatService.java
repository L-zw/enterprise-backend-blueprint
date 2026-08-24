package com.lzw.blueprint.ai.service;

import com.lzw.blueprint.ai.dto.ChatResponseDto;
import com.lzw.blueprint.ai.dto.ModelInfoDto;
import reactor.core.publisher.Flux;
import java.util.List;

public interface AiChatService {
    ChatResponseDto chat(Long userId, String sessionId, String content);
    Flux<String> chatStream(Long userId, String sessionId, String content);
    List<ModelInfoDto> listModels();
}