package com.lzw.blueprint.admin.controller;

import com.lzw.blueprint.admin.dto.RoleMenuDTO;
import com.lzw.blueprint.admin.entity.SysRole;
import com.lzw.blueprint.admin.service.SysRoleService;
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

@Tag(name = "角色管理", description = "系统角色增删改查 + 角色-菜单分配")
@RestController
@RequestMapping("/roles")
public class SysRoleController extends BaseController {

    @Autowired
    private SysRoleService sysRoleService;

    @Operation(summary = "分页查询角色")
    @RequirePermission("sys:role:list")
    @GetMapping
    public Result<PageResult<SysRole>> list(PageQuery query) {
        return success(sysRoleService.selectPage(query));
    }

    @Operation(summary = "创建角色")
    @RequirePermission("sys:role:create")
    @PostMapping
    public Result<Integer> create(@Valid @RequestBody SysRole role) {
        return success(sysRoleService.create(role));
    }

    @Operation(summary = "更新角色")
    @RequirePermission("sys:role:update")
    @PutMapping("/{id}")
    public Result<Integer> update(@PathVariable Long id, @Valid @RequestBody SysRole role) {
        role.setId(id);
        return success(sysRoleService.update(role));
    }

    @Operation(summary = "删除角色")
    @RequirePermission("sys:role:delete")
    @DeleteMapping("/{id}")
    public Result<Integer> delete(@PathVariable Long id) {
        return success(sysRoleService.deleteById(id));
    }

    @Operation(summary = "获取角色已分配菜单ID")
    @RequirePermission("sys:role:menu")
    @GetMapping("/{roleId}/menus")
    public Result<List<Long>> getMenus(@PathVariable Long roleId) {
        return success(sysRoleService.getMenuIds(roleId));
    }

    @Operation(summary = "更新角色-菜单分配")
    @RequirePermission("sys:role:menu")
    @PutMapping("/{roleId}/menus")
    public Result<Void> updateMenus(@PathVariable Long roleId, @RequestBody RoleMenuDTO dto) {
        sysRoleService.updateMenus(roleId, dto.getMenuIds());
        return success();
    }
}