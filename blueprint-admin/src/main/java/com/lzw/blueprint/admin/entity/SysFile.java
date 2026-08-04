package com.lzw.blueprint.admin.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("sys_file")
public class SysFile {

    private Long id;
    private String originalName;
    private String storedName;
    private String extension;
    private Long size;
    private String md5;
    private String mimeType;
    private String url;
    private String thumbnailUrl;
    private String storageType;
    private String bucket;
    private String objectName;
    private LocalDateTime createTime;
    private String createBy;
    private LocalDateTime updateTime;
    private String updateBy;
    private Boolean deleted;
}