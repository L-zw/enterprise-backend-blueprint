package com.lzw.blueprint.workflow.service;

import com.lzw.blueprint.workflow.entity.WfCategory;

import java.util.List;

public interface WfCategoryService {

    List<WfCategory> list();

    WfCategory getById(Long id);

    int create(WfCategory category);

    int update(WfCategory category);

    int delete(Long id);
}
