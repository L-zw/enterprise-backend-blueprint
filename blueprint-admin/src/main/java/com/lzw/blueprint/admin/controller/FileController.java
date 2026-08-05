package com.lzw.blueprint.admin.controller;

import com.lzw.blueprint.admin.entity.SysFile;
import com.lzw.blueprint.admin.service.FileService;
import com.lzw.blueprint.common.Result;
import com.lzw.blueprint.core.annotation.RequirePermission;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.URI;

@Tag(name = "文件管理", description = "文件上传/下载/预览接口")
@RestController
@RequestMapping("/files")
public class FileController extends BaseController {

    @Autowired
    private FileService fileService;

    @Operation(summary = "上传文件")
    @RequirePermission("sys:file:upload")
    @PostMapping("/upload")
    public Result<SysFile> upload(@RequestParam("file") MultipartFile file) {
        return success(fileService.upload(file));
    }

    @Operation(summary = "下载文件")
    @RequirePermission("sys:file:list")
    @GetMapping("/{id}")
    public ResponseEntity<Object> download(@PathVariable Long id) {
        SysFile sysFile = fileService.getById(id);
        if ("MINIO".equals(sysFile.getStorageType())) {
            String url = fileService.getFileUrl(id);
            return ResponseEntity.status(HttpStatus.FOUND)
                    .location(URI.create(url))
                    .build();
        }
        Resource resource = fileService.download(id);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + sysFile.getOriginalName() + "\"")
                .body(resource);
    }

    @Operation(summary = "预览文件（图片）")
    @RequirePermission("sys:file:list")
    @GetMapping("/{id}/preview")
    public ResponseEntity<Object> preview(@PathVariable Long id) {
        SysFile sysFile = fileService.getById(id);
        if ("MINIO".equals(sysFile.getStorageType())) {
            if (!isImage(sysFile.getMimeType())) {
                return ResponseEntity.badRequest().body("非图片文件无法预览");
            }
            String url = fileService.getFileUrl(id);
            return ResponseEntity.status(HttpStatus.FOUND)
                    .location(URI.create(url))
                    .build();
        }
        Resource resource = fileService.preview(id);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(sysFile.getMimeType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline")
                .body(resource);
    }

    @Operation(summary = "获取文件访问 URL")
    @RequirePermission("sys:file:list")
    @GetMapping("/{id}/url")
    public Result<String> getUrl(@PathVariable Long id) {
        return success(fileService.getFileUrl(id));
    }

    @Operation(summary = "删除文件")
    @RequirePermission("sys:file:delete")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        fileService.delete(id);
        return success();
    }

    private boolean isImage(String mimeType) {
        return mimeType != null && mimeType.startsWith("image/");
    }
}