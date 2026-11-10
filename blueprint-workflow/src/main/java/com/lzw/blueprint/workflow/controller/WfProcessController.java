package com.lzw.blueprint.workflow.controller;

import com.lzw.blueprint.common.Result;
import com.lzw.blueprint.workflow.dto.ProcessDefinitionDto;
import com.lzw.blueprint.workflow.service.WfProcessService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "工作流流程定义", description = "BPMN 流程部署、版本与生命周期")
@RestController
@RequestMapping("/v1/workflow/definitions")
public class WfProcessController {

    private final WfProcessService processService;

    public WfProcessController(WfProcessService processService) {
        this.processService = processService;
    }

    @Operation(summary = "部署 BPMN XML")
    @PostMapping("/deploy")
    public Result<ProcessDefinitionDto> deploy(@RequestParam(required = false) String name,
                                               @RequestBody String xml) {
        return Result.success(processService.deploy(name, xml));
    }

    @Operation(summary = "已部署定义列表（每个 key 的最新版）")
    @GetMapping
    public Result<List<ProcessDefinitionDto>> list() {
        return Result.success(processService.list());
    }

    @Operation(summary = "查询某流程的版本列表")
    @GetMapping("/{key}/versions")
    public Result<List<ProcessDefinitionDto>> versions(@PathVariable String key) {
        return Result.success(processService.versions(key));
    }

    @Operation(summary = "激活指定版本（不传 version 默认最新）")
    @PostMapping("/{key}/active")
    public Result<Void> activate(@PathVariable String key,
                                 @RequestParam(required = false) Integer version) {
        processService.activate(key, version);
        return Result.success();
    }

    @Operation(summary = "暂停指定版本（不传 version 默认最新）")
    @PostMapping("/{key}/suspend")
    public Result<Void> suspend(@PathVariable String key,
                                @RequestParam(required = false) Integer version) {
        processService.suspend(key, version);
        return Result.success();
    }
}
