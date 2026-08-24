package com.lzw.blueprint.ai.dto;

import lombok.Data;

@Data
public class ChatRequestDto {
    private Long userId;
    private String sessionId;
    private String content;
}