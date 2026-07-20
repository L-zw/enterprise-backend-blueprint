package com.lzw.blueprint.admin.service;

import com.lzw.blueprint.admin.entity.SysUser;
import com.lzw.blueprint.core.service.BaseService;

/**
 * 系统用户 Service
 */
public interface SysUserService extends BaseService<SysUser> {

    SysUser findByUsername(String username);
}
