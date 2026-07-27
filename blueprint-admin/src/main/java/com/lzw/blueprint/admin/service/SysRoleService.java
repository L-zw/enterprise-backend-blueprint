package com.lzw.blueprint.admin.service;

import com.lzw.blueprint.admin.entity.SysRole;
import com.lzw.blueprint.common.PageQuery;
import com.lzw.blueprint.common.PageResult;

import java.util.List;

public interface SysRoleService {

    PageResult<SysRole> selectPage(PageQuery query);

    SysRole getById(Long id);

    int create(SysRole role);

    int update(SysRole role);

    int deleteById(Long id);

    List<Long> getMenuIds(Long roleId);

    void updateMenus(Long roleId, List<Long> menuIds);
}