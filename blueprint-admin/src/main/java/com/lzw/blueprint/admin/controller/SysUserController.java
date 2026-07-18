package com.lzw.blueprint.admin.controller;

import com.lzw.blueprint.admin.entity.SysUser;
import com.lzw.blueprint.admin.service.SysUserService;
import com.lzw.blueprint.common.PageQuery;
import com.lzw.blueprint.common.PageResult;
import com.lzw.blueprint.common.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 系统用户管理接口
 */
@Tag(name = "用户管理", description = "系统用户增删改查接口")
@RestController
@RequestMapping("/users")
public class SysUserController extends BaseController {

    @Autowired
    private SysUserService sysUserService;

    @Operation(summary = "分页查询用户")
    @GetMapping
    public Result<PageResult<SysUser>> list(PageQuery query) {
        return success(sysUserService.selectPage(query));
    }

    @Operation(summary = "创建用户")
    @PostMapping
    public Result<Integer> create(@Valid @RequestBody SysUser user) {
        return success(sysUserService.insert(user));
    }
}
