package com.lzw.blueprint.admin.controller;

import com.lzw.blueprint.admin.dto.UserRoleDTO;
import com.lzw.blueprint.admin.entity.SysUser;
import com.lzw.blueprint.admin.service.SysUserService;
import com.lzw.blueprint.common.PageQuery;
import com.lzw.blueprint.common.PageResult;
import com.lzw.blueprint.common.Result;
import com.lzw.blueprint.core.annotation.RequirePermission;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    @RequirePermission("sys:user:list")
    @GetMapping
    public Result<PageResult<SysUser>> list(PageQuery query) {
        return success(sysUserService.selectPage(query));
    }

    @Operation(summary = "创建用户")
    @RequirePermission("sys:user:create")
    @PostMapping
    public Result<Integer> create(@Valid @RequestBody SysUser user) {
        return success(sysUserService.insert(user));
    }

    @Operation(summary = "获取用户已分配角色ID")
    @RequirePermission("sys:user:role")
    @GetMapping("/{userId}/roles")
    public Result<List<Long>> getRoles(@PathVariable Long userId) {
        return success(sysUserService.getRoleIds(userId));
    }

    @Operation(summary = "更新用户-角色分配")
    @RequirePermission("sys:user:role")
    @PutMapping("/{userId}/roles")
    public Result<Void> updateRoles(@PathVariable Long userId, @RequestBody UserRoleDTO dto) {
        sysUserService.updateRoles(userId, dto.getRoleIds());
        return success();
    }
}
