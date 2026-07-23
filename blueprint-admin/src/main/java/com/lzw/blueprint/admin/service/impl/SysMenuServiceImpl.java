package com.lzw.blueprint.admin.service.impl;

import com.lzw.blueprint.admin.mapper.SysMenuMapper;
import com.lzw.blueprint.admin.service.SysMenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 系统菜单 Service 实现
 */
@Service
public class SysMenuServiceImpl implements SysMenuService {

    @Autowired
    private SysMenuMapper sysMenuMapper;

    @Override
    public List<String> getPermissionsByUserId(Long userId) {
        return sysMenuMapper.findPermissionsByUserId(userId);
    }
}