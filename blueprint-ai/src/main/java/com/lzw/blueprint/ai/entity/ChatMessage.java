package com.lzw.blueprint.ai.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.lzw.blueprint.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("chat_message")
public class ChatMessage extends BaseEntity {

    private Long sessionId;
    private String role;
    private String content;
    private String model;
    private Integer tokensInput;
    private Integer tokensOutput;
    private String finishReason;
}