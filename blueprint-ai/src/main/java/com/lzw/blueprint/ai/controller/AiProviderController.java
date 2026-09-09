package com.lzw.blueprint.ai.controller;

import com.lzw.blueprint.ai.entity.AiProvider;
import com.lzw.blueprint.ai.service.AiProviderService;
import com.lzw.blueprint.common.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/**
 * 供应商管理 API（CRUD + 启用/禁用）。
 */
@Tag(name = "AI 供应商管理", description = "供应商 CRUD 与启用/禁用")
@RestController
@RequestMapping("/v1/ai/providers")
public class AiProviderController {

    @Autowired
    private AiProviderService aiProviderService;

    @Operation(summary = "供应商列表")
    @GetMapping
    public Result<List<AiProvider>> list() {
        return Result.success(aiProviderService.selectList());
    }

    @Operation(summary = "新增供应商")
    @PostMapping
    public Result<Integer> create(@RequestBody AiProvider provider) {
        return Result.success(aiProviderService.insert(provider));
    }

    @Operation(summary = "编辑供应商")
    @PutMapping("/{id}")
    public Result<Integer> update(@PathVariable Long id, @RequestBody AiProvider provider) {
        provider.setId(id);
        return Result.success(aiProviderService.updateById(provider));
    }

    @Operation(summary = "禁用供应商")
    @PatchMapping("/{id}/disable")
    public Result<Integer> disable(@PathVariable Long id) {
        AiProvider provider = aiProviderService.selectById(id);
        if (provider == null) {
            return Result.fail("Provider not found");
        }
        provider.setEnabled(false);
        return Result.success(aiProviderService.updateById(provider));
    }
}
