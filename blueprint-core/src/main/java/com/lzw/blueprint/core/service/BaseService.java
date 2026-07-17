package com.lzw.blueprint.core.service;

import com.lzw.blueprint.common.PageQuery;
import com.lzw.blueprint.common.PageResult;

import java.util.List;

/**
 * 通用 Service 接口
 * 所有 Service 继承此接口，统一 CRUD + 分页
 */
public interface BaseService<T> {

    int insert(T entity);

    int deleteById(Long id);

    int updateById(T entity);

    T selectById(Long id);

    List<T> selectList();

    PageResult<T> selectPage(PageQuery query);
}