package com.lzw.blueprint.admin.service;

import com.lzw.blueprint.admin.entity.SysFile;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

public interface FileService {

    SysFile upload(MultipartFile file);

    SysFile getById(Long id);

    Resource download(Long id);

    Resource preview(Long id);

    String getFileUrl(Long id);

    void delete(Long id);
}