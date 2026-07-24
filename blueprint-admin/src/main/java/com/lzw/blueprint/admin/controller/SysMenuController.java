package com.lzw.blueprint.admin.controller;

import com.lzw.blueprint.admin.entity.SysMenu;
import com.lzw.blueprint.admin.service.SysMenuService;
import com.lzw.blueprint.admin.vo.MenuVO;
import com.lzw.blueprint.common.Result;
import com.lzw.blueprint.core.annotation.RequirePermission;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "菜单管理", description = "系统菜单增删改查接口")
@RestController
@RequestMapping("/menus")
public class SysMenuController extends BaseController {

    @Autowired
    private SysMenuService sysMenuService;

    @Operation(summary = "获取菜单树")
    @RequirePermission("sys:menu:list")
    @GetMapping
    public Result<List<MenuVO>> tree() {
        return success(sysMenuService.getMenuTree());
    }

    @Operation(summary = "获取单个菜单")
    @RequirePermission("sys:menu:list")
    @GetMapping("/{id}")
    public Result<SysMenu> get(@PathVariable Long id) {
        return success(sysMenuService.getById(id));
    }

    @Operation(summary = "创建菜单")
    @RequirePermission("sys:menu:create")
    @PostMapping
    public Result<Integer> create(@Valid @RequestBody SysMenu menu) {
        return success(sysMenuService.create(menu));
    }

    @Operation(summary = "更新菜单")
    @RequirePermission("sys:menu:update")
    @PutMapping("/{id}")
    public Result<Integer> update(@PathVariable Long id, @Valid @RequestBody SysMenu menu) {
        menu.setId(id);
        return success(sysMenuService.update(menu));
    }

    @Operation(summary = "删除菜单")
    @RequirePermission("sys:menu:delete")
    @DeleteMapping("/{id}")
    public Result<Integer> delete(@PathVariable Long id) {
        return success(sysMenuService.deleteById(id));
    }
}