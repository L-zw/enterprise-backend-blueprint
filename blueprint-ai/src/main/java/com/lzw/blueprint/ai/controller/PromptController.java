package com.lzw.blueprint.ai.controller;

import com.lzw.blueprint.ai.entity.PromptTemplate;
import com.lzw.blueprint.ai.service.prompt.PromptTemplateService;
import com.lzw.blueprint.common.Result;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/v1/ai/prompts")
public class PromptController {

    private final PromptTemplateService promptTemplateService;

    public PromptController(PromptTemplateService promptTemplateService) {
        this.promptTemplateService = promptTemplateService;
    }

    @GetMapping
    public Result<List<PromptTemplate>> list() {
        return Result.success(promptTemplateService.list());
    }

    @PostMapping
    public Result<Integer> create(@RequestBody PromptTemplate template) {
        return Result.success(promptTemplateService.create(template));
    }

    @PutMapping("/{id}")
    public Result<Integer> update(@PathVariable Long id, @RequestBody PromptTemplate template) {
        template.setId(id);
        return Result.success(promptTemplateService.update(template));
    }

    @DeleteMapping("/{id}")
    public Result<Integer> delete(@PathVariable Long id) {
        return Result.success(promptTemplateService.delete(id));
    }

    @PostMapping("/{id}/render")
    public Result<String> render(@PathVariable Long id, @RequestBody Map<String, String> variables) {
        return Result.success(promptTemplateService.render(id, variables));
    }
}
