package com.lzw.blueprint.core.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lzw.blueprint.common.PageQuery;
import com.lzw.blueprint.common.PageResult;
import com.lzw.blueprint.core.mapper.BaseMapper;
import com.lzw.blueprint.core.service.BaseService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * 通用 Service 实现
 * 使用 MyBatis-Plus 分页插件，子类继承并指定 Mapper 类型即可
 */
public abstract class BaseServiceImpl<M extends BaseMapper<T>, T> implements BaseService<T> {

    @Autowired
    protected M baseMapper;

    @Override
    public int insert(T entity) {
        return baseMapper.insert(entity);
    }

    @Override
    public int deleteById(Long id) {
        return baseMapper.deleteById(id);
    }

    @Override
    public int updateById(T entity) {
        return baseMapper.updateById(entity);
    }

    @Override
    public T selectById(Long id) {
        return baseMapper.selectById(id);
    }

    @Override
    public List<T> selectList() {
        return baseMapper.selectList(null);
    }

    @Override
    public PageResult<T> selectPage(PageQuery query) {
        Page<T> page = Page.of(query.getPage(), query.getSize());
        Page<T> result = baseMapper.selectPage(page, null);

        PageResult<T> pageResult = new PageResult<>();
        pageResult.setRecords(result.getRecords());
        pageResult.setTotal(result.getTotal());
        pageResult.setPage((int) result.getCurrent());
        pageResult.setSize((int) result.getSize());
        pageResult.setPages(result.getPages());
        return pageResult;
    }
}