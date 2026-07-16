package com.lzw.blueprint.admin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 启动类
 * scanBasePackages 扫描 com.lzw.blueprint 下所有模块，
 * 使 blueprint-common / blueprint-core 中的 @Component 能被注册
 */
@SpringBootApplication(scanBasePackages = "com.lzw.blueprint")
public class BlueprintApplication {

    public static void main(String[] args) {
        SpringApplication.run(BlueprintApplication.class, args);
    }
}
