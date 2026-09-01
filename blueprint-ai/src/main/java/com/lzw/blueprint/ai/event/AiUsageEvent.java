package com.lzw.blueprint.ai.event;

import lombok.Data;

@Data
public class AiUsageEvent {

    private Long userId;
    private String providerCode;
    private String modelId;
    private Integer tokensInput;
    private Integer tokensOutput;
    private Integer requestCount;
}
