package com.lzw.blueprint.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.lzw.blueprint.admin.entity.SysUser;
import com.lzw.blueprint.admin.mapper.SysUserMapper;
import com.lzw.blueprint.admin.service.SysUserService;
import com.lzw.blueprint.core.service.impl.BaseServiceImpl;
import org.springframework.stereotype.Service;

/**
 * 系统用户 Service 实现
 */
@Service
public class SysUserServiceImpl extends BaseServiceImpl<SysUserMapper, SysUser> implements SysUserService {

    @Override
    public SysUser findByUsername(String username) {
        return baseMapper.selectOne(new LambdaQueryWrapper<SysUser>()
                .eq(SysUser::getUsername, username));
    }
}
