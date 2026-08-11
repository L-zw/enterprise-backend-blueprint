package com.lzw.blueprint.ai.service;

import com.lzw.blueprint.ai.dto.ChatResponseDto;
import com.lzw.blueprint.ai.dto.ModelInfoDto;
import reactor.core.publisher.Flux;
import java.util.List;

/**
 * AI 对话核心服务。
 */
public interface AiChatService {
    /**
     * 非流式对话。
     */
    ChatResponseDto chat(String sessionId, String content);

    /**
     * 流式对话（文本块）。
     */
    Flux<String> chatStream(String sessionId, String content);

    /**
     * 列出可用模型信息。
     */
    List<ModelInfoDto> listModels();
}
