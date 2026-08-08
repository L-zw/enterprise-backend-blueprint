package com.lzw.blueprint.ai.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.lzw.blueprint.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("chat_session")
public class ChatSession extends BaseEntity {

    private Long userId;
    private String title;
    private Long modelId;
    private String systemPrompt;
    private BigDecimal temperature;
    private Integer contextSize;
    private String status;
}