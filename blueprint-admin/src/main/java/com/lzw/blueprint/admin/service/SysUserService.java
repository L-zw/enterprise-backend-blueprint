package com.lzw.blueprint.admin.service;

import com.lzw.blueprint.admin.entity.SysUser;
import com.lzw.blueprint.core.service.BaseService;

import java.util.List;

/**
 * 系统用户 Service
 */
public interface SysUserService extends BaseService<SysUser> {

    SysUser findByUsername(String username);

    List<Long> getRoleIds(Long userId);

    void updateRoles(Long userId, List<Long> roleIds);
}
