package com.lzw.blueprint.ai.dto;

import lombok.Data;

/**
 * 请求体：对话输入。
 */
@Data
public class ChatRequestDto {
    /** 会话 ID（可选，未使用时可为空） */
    private String sessionId;
    /** 用户输入内容 */
    private String content;
}
