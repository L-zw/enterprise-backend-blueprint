package com.lzw.blueprint.workflow.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.lzw.blueprint.common.exception.BusinessException;
import com.lzw.blueprint.workflow.entity.WfCategory;
import com.lzw.blueprint.workflow.mapper.WfCategoryMapper;
import com.lzw.blueprint.workflow.service.WfCategoryService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WfCategoryServiceImpl implements WfCategoryService {

    private final WfCategoryMapper categoryMapper;

    public WfCategoryServiceImpl(WfCategoryMapper categoryMapper) {
        this.categoryMapper = categoryMapper;
    }

    @Override
    public List<WfCategory> list() {
        return categoryMapper.selectList(new QueryWrapper<WfCategory>().orderByAsc("sort_order", "id"));
    }

    @Override
    public WfCategory getById(Long id) {
        return categoryMapper.selectById(id);
    }

    @Override
    public int create(WfCategory category) {
        if (existsByCode(category.getCode())) {
            throw new BusinessException(400, "分类编码已存在");
        }
        if (category.getSortOrder() == null) category.setSortOrder(0);
        if (category.getEnabled() == null) category.setEnabled(true);
        return categoryMapper.insert(category);
    }

    @Override
    public int update(WfCategory category) {
        return categoryMapper.updateById(category);
    }

    @Override
    public int delete(Long id) {
        return categoryMapper.deleteById(id);
    }

    private boolean existsByCode(String code) {
        return categoryMapper.selectCount(new QueryWrapper<WfCategory>().eq("code", code)) > 0;
    }
}
