package com.lzw.blueprint.workflow.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(name = "blueprint.workflow.enabled", havingValue = "true", matchIfMissing = true)
@ComponentScan("com.lzw.blueprint.workflow")
public class WorkflowAutoConfiguration {
}
