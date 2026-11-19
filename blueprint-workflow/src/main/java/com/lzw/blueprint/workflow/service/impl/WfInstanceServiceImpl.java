package com.lzw.blueprint.workflow.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.lzw.blueprint.common.exception.BusinessException;
import com.lzw.blueprint.workflow.dto.DiagramDto;
import com.lzw.blueprint.workflow.dto.InstanceDto;
import com.lzw.blueprint.workflow.dto.StartInstanceDto;
import com.lzw.blueprint.workflow.dto.TaskDto;
import com.lzw.blueprint.workflow.entity.WfInstanceExt;
import com.lzw.blueprint.workflow.mapper.WfInstanceExtMapper;
import com.lzw.blueprint.workflow.service.WfInstanceService;
import org.flowable.engine.ProcessEngine;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.history.HistoricActivityInstance;
import org.flowable.engine.history.HistoricProcessInstance;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.Task;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class WfInstanceServiceImpl implements WfInstanceService {

    private final ProcessEngine processEngine;
    private final WfInstanceExtMapper instanceExtMapper;

    public WfInstanceServiceImpl(ProcessEngine processEngine, WfInstanceExtMapper instanceExtMapper) {
        this.processEngine = processEngine;
        this.instanceExtMapper = instanceExtMapper;
    }

    @Override
    public InstanceDto start(StartInstanceDto request, Long initiator) {
        if (request.getBpmnKey() == null || request.getBpmnKey().isBlank()) {
            throw new BusinessException(400, "流程 Key 不能为空");
        }
        if (request.getTitle() == null || request.getTitle().isBlank()) {
            throw new BusinessException(400, "流程标题不能为空");
        }
        String instanceId;
        try {
            instanceId = runtimeService().startProcessInstanceByKey(request.getBpmnKey()).getId();
        } catch (Exception e) {
            throw new BusinessException(400, "流程启动失败: " + e.getMessage());
        }
        WfInstanceExt ext = new WfInstanceExt();
        ext.setInstanceId(instanceId);
        ext.setBpmnKey(request.getBpmnKey());
        ext.setInitiator(initiator);
        ext.setTitle(request.getTitle());
        ext.setFormData(request.getFormData());
        ext.setStatus("approving");
        ext.setStartTime(LocalDateTime.now());
        instanceExtMapper.insert(ext);
        return detail(instanceId);
    }

    @Override
    public InstanceDto detail(String instanceId) {
        InstanceDto dto = new InstanceDto();
        dto.setInstanceId(instanceId);

        ProcessInstance running = runtimeService().createProcessInstanceQuery()
                .processInstanceId(instanceId)
                .singleResult();
        if (running != null) {
            dto.setBpmnKey(running.getProcessDefinitionKey());
            dto.setStartTime(running.getStartTime());
            dto.setCurrentTasks(toTaskDtos(instanceId));
        } else {
            HistoricProcessInstance historic = processEngine.getHistoryService()
                    .createHistoricProcessInstanceQuery()
                    .processInstanceId(instanceId)
                    .singleResult();
            if (historic == null) {
                throw new BusinessException(404, "流程实例不存在: " + instanceId);
            }
            dto.setBpmnKey(historic.getProcessDefinitionKey());
            dto.setStartTime(historic.getStartTime());
            dto.setEndTime(historic.getEndTime());
        }
        WfInstanceExt ext = findByInstanceId(instanceId);
        if (ext != null) {
            dto.setTitle(ext.getTitle());
            dto.setFormData(ext.getFormData());
            dto.setStatus(ext.getStatus());
            dto.setInitiator(ext.getInitiator());
        }
        return dto;
    }

    @Override
    public void cancel(String instanceId, String reason) {
        ProcessInstance running = runtimeService().createProcessInstanceQuery()
                .processInstanceId(instanceId)
                .singleResult();
        if (running == null) {
            throw new BusinessException(400, "流程不存在或已结束: " + instanceId);
        }
        runtimeService().deleteProcessInstance(instanceId,
                reason != null && !reason.isBlank() ? reason : "用户取消");
        WfInstanceExt ext = findByInstanceId(instanceId);
        if (ext != null) {
            ext.setStatus("cancelled");
            ext.setEndTime(LocalDateTime.now());
            instanceExtMapper.updateById(ext);
        }
    }

    @Override
    public DiagramDto diagram(String instanceId) {
        String processDefinitionId;
        ProcessInstance running = runtimeService().createProcessInstanceQuery()
                .processInstanceId(instanceId)
                .singleResult();
        if (running != null) {
            processDefinitionId = running.getProcessDefinitionId();
        } else {
            HistoricProcessInstance historic = processEngine.getHistoryService()
                    .createHistoricProcessInstanceQuery()
                    .processInstanceId(instanceId)
                    .singleResult();
            if (historic == null) {
                throw new BusinessException(404, "流程实例不存在: " + instanceId);
            }
            processDefinitionId = historic.getProcessDefinitionId();
        }

        DiagramDto dto = new DiagramDto();
        dto.setActiveActivityIds(running != null
                ? runtimeService().getActiveActivityIds(instanceId)
                : List.of());
        dto.setCompletedActivityIds(processEngine.getHistoryService()
                .createHistoricActivityInstanceQuery()
                .processInstanceId(instanceId)
                .finished()
                .list()
                .stream()
                .map(HistoricActivityInstance::getActivityId)
                .distinct()
                .collect(Collectors.toList()));

        ProcessDefinition pd = processEngine.getRepositoryService().getProcessDefinition(processDefinitionId);
        try (InputStream in = processEngine.getRepositoryService()
                .getResourceAsStream(pd.getDeploymentId(), pd.getResourceName())) {
            dto.setBpmnXml(new String(in.readAllBytes(), StandardCharsets.UTF_8));
        } catch (IOException e) {
            throw new BusinessException(500, "读取流程图资源失败");
        }
        return dto;
    }

    private WfInstanceExt findByInstanceId(String instanceId) {
        return instanceExtMapper.selectOne(new QueryWrapper<WfInstanceExt>().eq("instance_id", instanceId));
    }

    private List<TaskDto> toTaskDtos(String instanceId) {
        return processEngine.getTaskService().createTaskQuery()
                .processInstanceId(instanceId)
                .active()
                .orderByTaskCreateTime().desc()
                .list()
                .stream()
                .map(this::toTaskDto)
                .collect(Collectors.toList());
    }

    private TaskDto toTaskDto(Task task) {
        TaskDto dto = new TaskDto();
        dto.setTaskId(task.getId());
        dto.setName(task.getName());
        dto.setAssignee(task.getAssignee());
        dto.setTaskDefinitionKey(task.getTaskDefinitionKey());
        dto.setProcessInstanceId(task.getProcessInstanceId());
        dto.setCreateTime(task.getCreateTime());
        dto.setDueDate(task.getDueDate());
        return dto;
    }

    private RuntimeService runtimeService() {
        return processEngine.getRuntimeService();
    }
}
