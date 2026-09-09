package com.lzw.blueprint.ai.controller;

import com.lzw.blueprint.ai.entity.PromptTemplate;
import com.lzw.blueprint.ai.service.prompt.PromptTemplateService;
import com.lzw.blueprint.common.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Tag(name = "AI Prompt 模板", description = "提示词模板 CRUD 与变量渲染")
@RestController
@RequestMapping("/v1/ai/prompts")
public class PromptController {

    private final PromptTemplateService promptTemplateService;

    public PromptController(PromptTemplateService promptTemplateService) {
        this.promptTemplateService = promptTemplateService;
    }

    @Operation(summary = "模板列表")
    @GetMapping
    public Result<List<PromptTemplate>> list() {
        return Result.success(promptTemplateService.list());
    }

    @Operation(summary = "创建模板")
    @PostMapping
    public Result<Integer> create(@RequestBody PromptTemplate template) {
        return Result.success(promptTemplateService.create(template));
    }

    @Operation(summary = "更新模板")
    @PutMapping("/{id}")
    public Result<Integer> update(@PathVariable Long id, @RequestBody PromptTemplate template) {
        template.setId(id);
        return Result.success(promptTemplateService.update(template));
    }

    @Operation(summary = "删除模板（内置不可删除）")
    @DeleteMapping("/{id}")
    public Result<Integer> delete(@PathVariable Long id) {
        return Result.success(promptTemplateService.delete(id));
    }

    @Operation(summary = "渲染模板")
    @PostMapping("/{id}/render")
    public Result<String> render(@PathVariable Long id, @RequestBody Map<String, String> variables) {
        return Result.success(promptTemplateService.render(id, variables));
    }
}
