package com.lzw.blueprint.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.lzw.blueprint.admin.entity.SysUser;
import com.lzw.blueprint.admin.mapper.SysUserMapper;
import com.lzw.blueprint.admin.mapper.SysUserRoleMapper;
import com.lzw.blueprint.admin.service.SysUserService;
import com.lzw.blueprint.core.service.impl.BaseServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 系统用户 Service 实现
 */
@Service
public class SysUserServiceImpl extends BaseServiceImpl<SysUserMapper, SysUser> implements SysUserService {

    @Autowired
    private SysUserRoleMapper sysUserRoleMapper;

    @Override
    public SysUser findByUsername(String username) {
        return baseMapper.selectOne(new LambdaQueryWrapper<SysUser>()
                .eq(SysUser::getUsername, username));
    }

    @Override
    public List<Long> getRoleIds(Long userId) {
        return sysUserRoleMapper.selectRoleIdsByUserId(userId);
    }

    @Override
    @Transactional
    public void updateRoles(Long userId, List<Long> roleIds) {
        sysUserRoleMapper.deleteByUserId(userId);
        for (Long roleId : roleIds) {
            sysUserRoleMapper.insert(userId, roleId);
        }
    }
}
