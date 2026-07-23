package com.lzw.blueprint.admin.service;

import java.util.List;

/**
 * 系统菜单 Service
 */
public interface SysMenuService {

    List<String> getPermissionsByUserId(Long userId);
}