package com.lzw.blueprint.admin.mapper;

import com.lzw.blueprint.admin.entity.SysUser;
import com.lzw.blueprint.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Select;

/**
 * 系统用户 Mapper
 */
public interface SysUserMapper extends BaseMapper<SysUser> {

    @Select("SELECT * FROM sys_user WHERE username = #{username}")
    SysUser findByUsername(String username);
}
