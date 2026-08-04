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
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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
    public ResponseEntity<Resource> download(@PathVariable Long id) {
        SysFile sysFile = fileService.getById(id);
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
    public ResponseEntity<Resource> preview(@PathVariable Long id) {
        SysFile sysFile = fileService.getById(id);
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
        SysFile sysFile = fileService.getById(id);
        return success(sysFile.getUrl());
    }

    @Operation(summary = "删除文件")
    @RequirePermission("sys:file:delete")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        fileService.delete(id);
        return success();
    }
}