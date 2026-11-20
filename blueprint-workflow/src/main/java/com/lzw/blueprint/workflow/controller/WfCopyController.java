package com.lzw.blueprint.workflow.controller;

import com.lzw.blueprint.common.Result;
import com.lzw.blueprint.workflow.entity.WfCopy;
import com.lzw.blueprint.workflow.service.WfCopyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "工作流抄送", description = "抄送列表与已读")
@RestController
@RequestMapping("/v1/workflow/copies")
public class WfCopyController {

    private final WfCopyService copyService;

    public WfCopyController(WfCopyService copyService) {
        this.copyService = copyService;
    }

    @Operation(summary = "我的抄送列表")
    @GetMapping("/{userId}")
    public Result<List<WfCopy>> listByUser(@PathVariable Long userId) {
        return Result.success(copyService.listByUser(userId));
    }

    @Operation(summary = "标记已读")
    @PostMapping("/{id}/read")
    public Result<Integer> markRead(@PathVariable Long id) {
        return Result.success(copyService.markRead(id));
    }
}
