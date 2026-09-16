package com.lzw.blueprint.workflow.controller;

import com.lzw.blueprint.common.Result;
import com.lzw.blueprint.workflow.entity.WfCategory;
import com.lzw.blueprint.workflow.service.WfCategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "工作流分类", description = "流程分类 CRUD")
@RestController
@RequestMapping("/v1/workflow/categories")
public class WfCategoryController {

    private final WfCategoryService categoryService;

    public WfCategoryController(WfCategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @Operation(summary = "分类列表")
    @GetMapping
    public Result<List<WfCategory>> list() {
        return Result.success(categoryService.list());
    }

    @Operation(summary = "创建分类")
    @PostMapping
    public Result<Integer> create(@RequestBody WfCategory category) {
        return Result.success(categoryService.create(category));
    }

    @Operation(summary = "更新分类")
    @PutMapping("/{id}")
    public Result<Integer> update(@PathVariable Long id, @RequestBody WfCategory category) {
        category.setId(id);
        return Result.success(categoryService.update(category));
    }

    @Operation(summary = "删除分类")
    @DeleteMapping("/{id}")
    public Result<Integer> delete(@PathVariable Long id) {
        return Result.success(categoryService.delete(id));
    }
}
