package com.lzw.blueprint.ai.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.lzw.blueprint.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("ai_provider")
public class AiProvider extends BaseEntity {

    private String providerCode;
    private String providerName;
    private String baseUrl;
    private String apiKey;
    private String authHeader;
    private Integer timeoutMs;
    private Integer maxRetries;
    private Boolean enabled;
    private Integer priority;
}