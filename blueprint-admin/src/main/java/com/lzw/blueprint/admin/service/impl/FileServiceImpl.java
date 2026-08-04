package com.lzw.blueprint.admin.service.impl;

import com.lzw.blueprint.admin.entity.SysFile;
import com.lzw.blueprint.admin.mapper.SysFileMapper;
import com.lzw.blueprint.admin.service.FileService;
import com.lzw.blueprint.common.exception.ApiException;
import com.lzw.blueprint.core.config.FileProperties;
import com.lzw.blueprint.core.storage.FileStorage;
import com.lzw.blueprint.core.storage.FileStorageFactory;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class FileServiceImpl implements FileService {

    @Autowired
    private SysFileMapper sysFileMapper;

    @Autowired
    private FileStorageFactory storageFactory;

    @Autowired
    private FileProperties fileProperties;

    @Autowired
    private HttpServletRequest request;

    private static final List<String> IMAGE_TYPES = List.of("image/jpeg", "image/png", "image/gif", "image/webp");

    private static final byte[][] MAGIC_BYTES = {
        {(byte) 0xFF, (byte) 0xD8, (byte) 0xFF},           // jpg
        {(byte) 0x89, 'P', 'N', 'G'},                       // png
        {'G', 'I', 'F', '8'},                                // gif
        {'%', 'P', 'D', 'F'}                                 // pdf
    };

    private static final String[] MAGIC_EXTS = {"jpg", "png", "gif", "pdf"};

    @Override
    @Transactional
    public SysFile upload(MultipartFile file) {
        String originalName = file.getOriginalFilename();
        String ext = "";
        if (originalName != null && originalName.contains(".")) {
            ext = originalName.substring(originalName.lastIndexOf(".") + 1).toLowerCase();
        }

        if (!fileProperties.getAllowedTypes().contains(ext)) {
            throw new ApiException("文件类型 [" + ext + "] 不允许上传");
        }

        validateMagicBytes(file, ext);

        String md5;
        try {
            md5 = DigestUtils.md5DigestAsHex(file.getInputStream());
        } catch (IOException e) {
            throw new ApiException("读取文件失败");
        }

        SysFile exist = sysFileMapper.findByMd5(md5);
        if (exist != null) {
            return exist;
        }

        String storedName = UUID.randomUUID().toString().replace("-", "") + "." + ext;
        FileStorage storage = storageFactory.getStorage(fileProperties.getStorage());

        long size = file.getSize();
        if (size > fileProperties.getMaxSize() * 1024 * 1024) {
            throw new ApiException("文件大小不能超过 " + fileProperties.getMaxSize() + "MB");
        }

        String url = storage.store(file, storedName);

        SysFile sysFile = new SysFile();
        sysFile.setOriginalName(originalName);
        sysFile.setStoredName(storedName);
        sysFile.setExtension(ext);
        sysFile.setSize(size);
        sysFile.setMd5(md5);
        sysFile.setMimeType(file.getContentType());
        sysFile.setUrl(url);
        sysFile.setStorageType(storage.getType());
        sysFile.setObjectName(storedName);
        sysFile.setCreateTime(LocalDateTime.now());
        sysFile.setCreateBy((String) request.getAttribute("username"));
        sysFile.setDeleted(false);
        sysFileMapper.insert(sysFile);
        sysFile.setUrl("/api/files/" + sysFile.getId());
        sysFileMapper.updateById(sysFile);
        return sysFile;
    }

    @Override
    public SysFile getById(Long id) {
        SysFile sysFile = sysFileMapper.selectById(id);
        if (sysFile == null || Boolean.TRUE.equals(sysFile.getDeleted())) {
            throw new ApiException("文件不存在");
        }
        return sysFile;
    }

    @Override
    public Resource download(Long id) {
        SysFile sysFile = getById(id);
        FileStorage storage = storageFactory.getStorage(sysFile.getStorageType());
        if ("LOCAL".equals(storage.getType())) {
            Path filePath = Paths.get(fileProperties.getPath()).resolve(sysFile.getStoredName()).normalize();
            try {
                Resource resource = new UrlResource(filePath.toUri());
                if (resource.exists()) {
                    return resource;
                }
            } catch (MalformedURLException e) {
                throw new ApiException("文件读取失败");
            }
        }
        throw new ApiException("文件读取失败，请使用签名 URL 下载");
    }

    @Override
    public Resource preview(Long id) {
        SysFile sysFile = getById(id);
        if (!IMAGE_TYPES.contains(sysFile.getMimeType())) {
            throw new ApiException("非图片文件无法预览");
        }
        FileStorage storage = storageFactory.getStorage(sysFile.getStorageType());
        if ("LOCAL".equals(storage.getType())) {
            return download(id);
        }
        throw new ApiException("图片预览不支持 MinIO 存储");
    }

    @Override
    @Transactional
    public void delete(Long id) {
        SysFile sysFile = getById(id);
        FileStorage storage = storageFactory.getStorage(sysFile.getStorageType());
        storage.delete(sysFile.getStoredName());
        sysFile.setDeleted(true);
        sysFileMapper.updateById(sysFile);
    }

    private void validateMagicBytes(MultipartFile file, String ext) {
        try {
            byte[] header = new byte[8];
            int read = file.getInputStream().read(header);
            if (read <= 0) return;

            for (int i = 0; i < MAGIC_EXTS.length; i++) {
                if (MAGIC_EXTS[i].equals(ext)) {
                    byte[] expected = MAGIC_BYTES[i];
                    for (int j = 0; j < expected.length; j++) {
                        if (header[j] != expected[j]) {
                            throw new ApiException("文件内容与扩展名不匹配");
                        }
                    }
                    return;
                }
            }
        } catch (ApiException e) {
            throw e;
        } catch (Exception e) {
            // ignore magic byte check failure
        }
    }
}