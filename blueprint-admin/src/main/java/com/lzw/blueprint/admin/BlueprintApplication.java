package com.lzw.blueprint.admin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.lzw.blueprint")
public class BlueprintApplication {

    public static void main(String[] args) {
        SpringApplication.run(BlueprintApplication.class, args);
    }
}
