package com.lzw.blueprint.ai.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(name = "blueprint.ai.enabled", havingValue = "true", matchIfMissing = true)
@ComponentScan("com.lzw.blueprint.ai")
public class AiAutoConfiguration {
}