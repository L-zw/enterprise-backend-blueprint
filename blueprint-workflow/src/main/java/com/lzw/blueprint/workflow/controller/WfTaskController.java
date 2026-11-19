package com.lzw.blueprint.workflow.controller;

import com.lzw.blueprint.common.Result;
import com.lzw.blueprint.workflow.dto.TaskDto;
import com.lzw.blueprint.workflow.service.WfTaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "工作流任务", description = "待办任务查询")
@RestController
@RequestMapping("/v1/workflow/tasks")
public class WfTaskController {

    private final WfTaskService taskService;

    public WfTaskController(WfTaskService taskService) {
        this.taskService = taskService;
    }

    @Operation(summary = "用户待办列表")
    @GetMapping("/group/{userId}")
    public Result<List<TaskDto>> todoList(@PathVariable Long userId) {
        return Result.success(taskService.todoList(userId));
    }

    @Operation(summary = "任务详情")
    @GetMapping("/{taskId}")
    public Result<TaskDto> detail(@PathVariable String taskId) {
        return Result.success(taskService.detail(taskId));
    }
}
