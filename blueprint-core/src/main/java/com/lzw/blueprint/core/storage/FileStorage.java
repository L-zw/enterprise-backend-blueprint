package com.lzw.blueprint.core.storage;

import org.springframework.web.multipart.MultipartFile;

public interface FileStorage {

    String store(MultipartFile file, String storedName);

    void delete(String storedName);

    String getUrl(String storedName);

    String getType();
}