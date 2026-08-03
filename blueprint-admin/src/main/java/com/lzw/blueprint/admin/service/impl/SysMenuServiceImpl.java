package com.lzw.blueprint.admin.service.impl;

import com.lzw.blueprint.admin.entity.SysMenu;
import com.lzw.blueprint.admin.mapper.SysMenuMapper;
import com.lzw.blueprint.admin.service.PermissionCacheService;
import com.lzw.blueprint.admin.service.SysMenuService;
import com.lzw.blueprint.admin.vo.MenuVO;
import com.lzw.blueprint.common.constant.CacheNames;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 系统菜单 Service 实现
 */
@Service
public class SysMenuServiceImpl implements SysMenuService {

    @Autowired
    private SysMenuMapper sysMenuMapper;

    @Autowired
    private PermissionCacheService permissionCacheService;

    @Override
    public List<String> getPermissionsByUserId(Long userId) {
        return sysMenuMapper.findPermissionsByUserId(userId);
    }

    @Override
    public Set<String> getPermissionSet(Long userId) {
        return permissionCacheService.getPermissions(userId);
    }

    @Override
    public void clearPermissionCache(Long userId) {
        permissionCacheService.clearPermissions(userId);
    }

    @Cacheable(value = CacheNames.ROUTERS, key = "#userId")
    @Override
    public List<MenuVO> getRouterTree(Long userId) {
        List<SysMenu> menus = sysMenuMapper.selectByUserId(userId);
        List<SysMenu> filtered = menus.stream()
                .filter(m -> m.getType() != null && m.getType() != 3)
                .collect(Collectors.toList());
        return buildTree(filtered, 0L);
    }

    @Override
    public List<MenuVO> getMenuTree() {
        List<SysMenu> all = sysMenuMapper.selectList(null);
        return buildTree(all, 0L);
    }

    @Override
    public SysMenu getById(Long id) {
        return sysMenuMapper.selectById(id);
    }

    @Override
    public int create(SysMenu menu) {
        return sysMenuMapper.insert(menu);
    }

    @Override
    @CacheEvict(value = CacheNames.ROUTERS, allEntries = true)
    public int update(SysMenu menu) {
        return sysMenuMapper.updateById(menu);
    }

    @Override
    @CacheEvict(value = CacheNames.ROUTERS, allEntries = true)
    public int deleteById(Long id) {
        return sysMenuMapper.deleteById(id);
    }

    private List<MenuVO> buildTree(List<SysMenu> menus, Long parentId) {
        List<MenuVO> tree = new ArrayList<>();
        for (SysMenu menu : menus) {
            if (menu.getParentId() == null) {
                menu.setParentId(0L);
            }
            if (menu.getParentId().equals(parentId)) {
                tree.add(convert(menu, menus));
            }
        }
        tree.sort(Comparator.comparingInt(MenuVO::getSort));
        return tree;
    }

    private MenuVO convert(SysMenu menu, List<SysMenu> all) {
        MenuVO vo = new MenuVO();
        vo.setId(menu.getId());
        vo.setName(menu.getName());
        vo.setPermission(menu.getPermission());
        vo.setPath(menu.getPath());
        vo.setComponent(menu.getComponent());
        vo.setParentId(menu.getParentId());
        vo.setType(menu.getType());
        vo.setSort(menu.getSort());
        vo.setIcon(menu.getIcon());
        vo.setStatus(menu.getStatus());
        vo.setHidden(menu.getHidden());
        vo.setRedirect(menu.getRedirect());
        vo.setChildren(buildTree(all, menu.getId()));
        return vo;
    }
}