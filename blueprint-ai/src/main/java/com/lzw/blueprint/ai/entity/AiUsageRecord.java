package com.lzw.blueprint.ai.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("ai_usage_record")
public class AiUsageRecord {

    private Long id;
    private Long userId;
    private String providerCode;
    private String modelId;
    private Integer tokensInput;
    private Integer tokensOutput;
    private Integer requestCount;
    private LocalDate recordDate;
    private LocalDateTime createTime;
}