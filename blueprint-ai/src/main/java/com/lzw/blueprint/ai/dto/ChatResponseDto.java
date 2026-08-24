package com.lzw.blueprint.ai.dto;

import lombok.Data;

@Data
public class ChatResponseDto {
    private Long sessionId;
    private String reply;
    private String providerCode;
}