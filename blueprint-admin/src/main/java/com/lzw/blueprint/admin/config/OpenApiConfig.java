package com.lzw.blueprint.admin.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * OpenAPI / Swagger 配置
 * knife4j 依赖在 blueprint-admin 模块，故配置类放在此处
 * 访问地址：http://localhost:8080/swagger-ui/index.html
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Enterprise Backend Blueprint API")
                        .version("1.0.0")
                        .description("Enterprise AI Backend Blueprint - 企业级AI后端开发底座")
                        .contact(new Contact()
                                .name("L-zw")
                                .email("lzw@example.com")));
    }
}