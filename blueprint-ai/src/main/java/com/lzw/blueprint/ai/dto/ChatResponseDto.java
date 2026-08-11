package com.lzw.blueprint.ai.dto;

import lombok.Data;

/**
 * 响应体：AI 回复。
 */
@Data
public class ChatResponseDto {
    /** AI 返回的文本内容 */
    private String reply;
    /** 触发的供应商代码（可选） */
    private String providerCode;
}
