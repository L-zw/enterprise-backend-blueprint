package com.lzw.blueprint.admin.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("sys_operation_log")
public class SysOperationLog {

    private Long id;
    private Long userId;
    private String username;
    private String operation;
    private String module;
    private String target;
    private String requestUrl;
    private String requestMethod;
    private String requestParams;
    private String result;
    private String errorMsg;
    private Long duration;
    private String ip;
    private LocalDateTime createTime;
}