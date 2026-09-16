package com.lzw.blueprint.workflow.config;

import org.flowable.engine.ProcessEngine;
import org.flowable.spring.SpringProcessEngineConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

@Configuration
@ConditionalOnProperty(name = "blueprint.workflow.enabled", havingValue = "true", matchIfMissing = true)
public class FlowableConfig {

    @Bean(destroyMethod = "close")
    public ProcessEngine processEngine(DataSource dataSource, PlatformTransactionManager transactionManager) {
        SpringProcessEngineConfiguration configuration = new SpringProcessEngineConfiguration();
        configuration.setDataSource(dataSource);
        configuration.setTransactionManager(transactionManager);
        configuration.setDatabaseSchemaUpdate("true");
        return configuration.buildProcessEngine();
    }
}
