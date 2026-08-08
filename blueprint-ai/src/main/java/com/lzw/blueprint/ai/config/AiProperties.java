package com.lzw.blueprint.ai.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "blueprint.ai")
public class AiProperties {

    private boolean enabled = true;
    private String defaultProvider = "openai";

    private Retry retry = new Retry();
    private Usage usage = new Usage();

    @Data
    public static class Retry {
        private int maxAttempts = 3;
        private long backoffMs = 1000;
    }

    @Data
    public static class Usage {
        private long dailyLimitTokens = 500000;
    }
}