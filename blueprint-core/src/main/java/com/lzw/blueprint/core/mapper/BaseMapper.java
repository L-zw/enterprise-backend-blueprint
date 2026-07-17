package com.lzw.blueprint.core.mapper;

/**
 * 通用 Mapper 接口
 * 继承 MyBatis-Plus BaseMapper，所有 Mapper 自动获得 CRUD + 分页方法
 */
public interface BaseMapper<T> extends com.baomidou.mybatisplus.core.mapper.BaseMapper<T> {
}