package com.lzw.blueprint.core.storage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class FileStorageFactory {

    private final Map<String, FileStorage> storageMap;

    @Autowired
    public FileStorageFactory(List<FileStorage> storages) {
        this.storageMap = storages.stream()
                .collect(Collectors.toMap(FileStorage::getType, Function.identity()));
    }

    public FileStorage getStorage(String type) {
        FileStorage storage = storageMap.get(type.toUpperCase());
        if (storage == null) {
            return storageMap.get("LOCAL");
        }
        return storage;
    }
}