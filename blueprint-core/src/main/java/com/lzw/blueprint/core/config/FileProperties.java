package com.lzw.blueprint.core.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@Data
@ConfigurationProperties(prefix = "file.upload")
public class FileProperties {

    private String path = "./uploads";
    private long maxSize = 10;
    private List<String> allowedTypes = List.of("jpg", "jpeg", "png", "gif", "pdf", "doc", "docx", "xls", "xlsx", "zip");
    private String storage = "local";
    private Minio minio = new Minio();

    @Data
    public static class Minio {
        private String endpoint = "http://localhost:9000";
        private String accessKey = "admin";
        private String secretKey = "admin123456";
        private String bucket = "blueprint";
    }
}