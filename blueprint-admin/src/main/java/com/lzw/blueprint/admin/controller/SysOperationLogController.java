package com.lzw.blueprint.admin.controller;

import com.lzw.blueprint.admin.entity.SysOperationLog;
import com.lzw.blueprint.admin.service.SysOperationLogService;
import com.lzw.blueprint.common.PageQuery;
import com.lzw.blueprint.common.PageResult;
import com.lzw.blueprint.common.Result;
import com.lzw.blueprint.core.annotation.RequirePermission;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "操作日志", description = "系统操作日志查询接口")
@RestController
@RequestMapping("/operation-logs")
public class SysOperationLogController extends BaseController {

    @Autowired
    private SysOperationLogService sysOperationLogService;

    @Operation(summary = "分页查询操作日志")
    @RequirePermission("sys:log:list")
    @GetMapping
    public Result<PageResult<SysOperationLog>> list(PageQuery query) {
        return success(sysOperationLogService.selectPage(query));
    }
}