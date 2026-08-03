package com.lzw.blueprint.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lzw.blueprint.admin.entity.SysRole;
import com.lzw.blueprint.admin.mapper.SysRoleMapper;
import com.lzw.blueprint.admin.mapper.SysRoleMenuMapper;
import com.lzw.blueprint.admin.mapper.SysUserRoleMapper;
import com.lzw.blueprint.admin.service.SysMenuService;
import com.lzw.blueprint.admin.service.SysRoleService;
import com.lzw.blueprint.common.PageQuery;
import com.lzw.blueprint.common.PageResult;
import com.lzw.blueprint.common.constant.CacheNames;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SysRoleServiceImpl implements SysRoleService {

    @Autowired
    private SysRoleMapper sysRoleMapper;

    @Autowired
    private SysRoleMenuMapper sysRoleMenuMapper;

    @Autowired
    private SysUserRoleMapper sysUserRoleMapper;

    @Autowired
    private SysMenuService sysMenuService;

    @Override
    public PageResult<SysRole> selectPage(PageQuery query) {
        Page<SysRole> page = Page.of(query.getPage(), query.getSize());
        Page<SysRole> result = sysRoleMapper.selectPage(page, null);

        PageResult<SysRole> pr = new PageResult<>();
        pr.setRecords(result.getRecords());
        pr.setTotal(result.getTotal());
        pr.setPage((int) result.getCurrent());
        pr.setSize((int) result.getSize());
        pr.setPages(result.getPages());
        return pr;
    }

    @Override
    public SysRole getById(Long id) {
        return sysRoleMapper.selectById(id);
    }

    @Override
    public int create(SysRole role) {
        return sysRoleMapper.insert(role);
    }

    @Override
    public int update(SysRole role) {
        return sysRoleMapper.updateById(role);
    }

    @Override
    public int deleteById(Long id) {
        return sysRoleMapper.deleteById(id);
    }

    @Override
    @Cacheable(value = CacheNames.ROLE_MENUS, key = "#roleId")
    public List<Long> getMenuIds(Long roleId) {
        return sysRoleMenuMapper.selectMenuIdsByRoleId(roleId);
    }

    @Override
    @Transactional
    @CacheEvict(value = CacheNames.ROLE_MENUS, key = "#roleId")
    public void updateMenus(Long roleId, List<Long> menuIds) {
        sysRoleMenuMapper.deleteByRoleId(roleId);
        for (Long menuId : menuIds) {
            sysRoleMenuMapper.insert(roleId, menuId);
        }
        List<Long> userIds = sysUserRoleMapper.selectUserIdsByRoleId(roleId);
        for (Long uid : userIds) {
            sysMenuService.clearPermissionCache(uid);
        }
    }
}