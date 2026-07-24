package com.lzw.blueprint.admin.mapper;

import com.lzw.blueprint.admin.entity.SysMenu;
import com.lzw.blueprint.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 系统菜单 Mapper
 */
public interface SysMenuMapper extends BaseMapper<SysMenu> {

    @Select("""
            SELECT DISTINCT m.permission
            FROM sys_user_role ur
            JOIN sys_role_menu rm ON ur.role_id = rm.role_id
            JOIN sys_menu m ON rm.menu_id = m.id
            WHERE ur.user_id = #{userId}
              AND m.permission IS NOT NULL
            """)
    List<String> findPermissionsByUserId(Long userId);

    @Select("""
            SELECT DISTINCT m.*
            FROM sys_user_role ur
            JOIN sys_role_menu rm ON ur.role_id = rm.role_id
            JOIN sys_menu m ON rm.menu_id = m.id
            WHERE ur.user_id = #{userId}
            ORDER BY m.parent_id, m.sort
            """)
    List<SysMenu> selectByUserId(Long userId);

    @Select("SELECT menu_id FROM sys_role_menu WHERE role_id = #{roleId}")
    List<Long> selectMenuIdsByRoleId(Long roleId);
}