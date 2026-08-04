package com.lzw.blueprint.core.storage;

import com.lzw.blueprint.core.config.FileProperties;
import jakarta.annotation.PostConstruct;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Component
@ConditionalOnProperty(name = "file.upload.storage", havingValue = "local", matchIfMissing = true)
public class LocalFileStorage implements FileStorage {

    private final Path uploadPath;

    public LocalFileStorage(FileProperties properties) {
        this.uploadPath = Paths.get(properties.getPath()).toAbsolutePath().normalize();
    }

    @PostConstruct
    public void init() throws IOException {
        Files.createDirectories(uploadPath);
    }

    @Override
    public String store(MultipartFile file, String storedName) {
        try {
            Path target = uploadPath.resolve(storedName).normalize();
            Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);
            return "/api/files/" + storedName;
        } catch (IOException e) {
            throw new RuntimeException("文件存储失败", e);
        }
    }

    @Override
    public void delete(String storedName) {
        try {
            Files.deleteIfExists(uploadPath.resolve(storedName));
        } catch (IOException e) {
            // ignore
        }
    }

    @Override
    public String getUrl(String storedName) {
        return "/api/files/" + storedName;
    }

    @Override
    public String getType() {
        return "LOCAL";
    }
}