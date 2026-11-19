package com.lzw.blueprint.workflow.controller;

import com.lzw.blueprint.common.Result;
import com.lzw.blueprint.workflow.dto.DiagramDto;
import com.lzw.blueprint.workflow.dto.InstanceDto;
import com.lzw.blueprint.workflow.dto.StartInstanceDto;
import com.lzw.blueprint.workflow.service.WfInstanceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;

@Tag(name = "工作流实例", description = "流程实例启动/详情/取消")
@RestController
@RequestMapping("/v1/workflow/instances")
public class WfInstanceController {

    private final WfInstanceService instanceService;

    public WfInstanceController(WfInstanceService instanceService) {
        this.instanceService = instanceService;
    }

    @Operation(summary = "启动流程实例")
    @PostMapping
    public Result<InstanceDto> start(@RequestBody StartInstanceDto request,
                                     HttpServletRequest httpRequest) {
        Long userId = (Long) httpRequest.getAttribute("userId");
        return Result.success(instanceService.start(request, userId));
    }

    @Operation(summary = "实例详情")
    @GetMapping("/{id}")
    public Result<InstanceDto> detail(@PathVariable String id) {
        return Result.success(instanceService.detail(id));
    }

    @Operation(summary = "取消实例")
    @DeleteMapping("/{id}")
    public Result<Void> cancel(@PathVariable String id,
                               @RequestParam(required = false) String reason) {
        instanceService.cancel(id, reason);
        return Result.success();
    }

    @Operation(summary = "流程图（BPMN XML + 高亮节点）")
    @GetMapping("/{id}/diagram")
    public Result<DiagramDto> diagram(@PathVariable String id) {
        return Result.success(instanceService.diagram(id));
    }
}
