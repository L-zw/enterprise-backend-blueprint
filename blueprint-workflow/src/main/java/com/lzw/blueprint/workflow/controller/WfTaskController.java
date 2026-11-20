package com.lzw.blueprint.workflow.controller;

import com.lzw.blueprint.common.Result;
import com.lzw.blueprint.workflow.dto.TaskActionDto;
import com.lzw.blueprint.workflow.dto.TaskDto;
import com.lzw.blueprint.workflow.service.WfTaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "工作流任务", description = "待办查询与审批操作")
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

    @Operation(summary = "批准")
    @PostMapping("/{taskId}/approve")
    public Result<Void> approve(@PathVariable String taskId,
                                @RequestBody(required = false) TaskActionDto body,
                                HttpServletRequest request) {
        taskService.approve(taskId, currentUserId(request),
                body != null ? body.getComment() : null);
        return Result.success();
    }

    @Operation(summary = "驳回（回退到指定节点）")
    @PostMapping("/{taskId}/reject")
    public Result<Void> reject(@PathVariable String taskId,
                               @RequestBody TaskActionDto body,
                               HttpServletRequest request) {
        taskService.moveTo(taskId, currentUserId(request),
                body != null ? body.getTargetActivityId() : null);
        return Result.success();
    }

    @Operation(summary = "退回到指定节点")
    @PostMapping("/{taskId}/goToActivity")
    public Result<Void> goToActivity(@PathVariable String taskId,
                                     @RequestBody TaskActionDto body,
                                     HttpServletRequest request) {
        taskService.moveTo(taskId, currentUserId(request),
                body != null ? body.getTargetActivityId() : null);
        return Result.success();
    }

    @Operation(summary = "转办")
    @PostMapping("/{taskId}/transfer")
    public Result<Void> transfer(@PathVariable String taskId,
                                @RequestBody TaskActionDto body,
                                HttpServletRequest request) {
        taskService.transfer(taskId, currentUserId(request),
                body != null ? body.getUserId() : null);
        return Result.success();
    }

    @Operation(summary = "加签")
    @PostMapping("/{taskId}/addSign")
    public Result<Void> addSign(@PathVariable String taskId,
                                @RequestBody TaskActionDto body,
                                HttpServletRequest request) {
        taskService.addSign(taskId, currentUserId(request),
                body != null ? body.getUserId() : null);
        return Result.success();
    }

    private Long currentUserId(HttpServletRequest request) {
        return (Long) request.getAttribute("userId");
    }
}
