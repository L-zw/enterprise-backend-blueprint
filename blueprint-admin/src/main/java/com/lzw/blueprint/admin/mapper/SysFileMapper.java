package com.lzw.blueprint.admin.mapper;

import com.lzw.blueprint.admin.entity.SysFile;
import com.lzw.blueprint.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Select;

public interface SysFileMapper extends BaseMapper<SysFile> {

    @Select("SELECT * FROM sys_file WHERE md5 = #{md5} AND deleted = FALSE LIMIT 1")
    SysFile findByMd5(String md5);

    @Select("SELECT * FROM sys_file WHERE stored_name = #{storedName} AND deleted = FALSE LIMIT 1")
    SysFile findByStoredName(String storedName);
}