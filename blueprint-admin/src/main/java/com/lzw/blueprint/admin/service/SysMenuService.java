package com.lzw.blueprint.admin.service;

import com.lzw.blueprint.admin.entity.SysMenu;
import com.lzw.blueprint.admin.vo.MenuVO;

import java.util.List;
import java.util.Set;

/**
 * 系统菜单 Service
 */
public interface SysMenuService {

    List<String> getPermissionsByUserId(Long userId);

    List<MenuVO> getMenuTree();

    List<MenuVO> getRouterTree(Long userId);

    Set<String> getPermissionSet(Long userId);

    SysMenu getById(Long id);

    int create(SysMenu menu);

    int update(SysMenu menu);

    int deleteById(Long id);
}