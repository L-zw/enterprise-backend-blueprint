package com.lzw.blueprint.workflow.service.impl;

import com.lzw.blueprint.common.exception.BusinessException;
import com.lzw.blueprint.workflow.dto.TaskDto;
import com.lzw.blueprint.workflow.service.WfTaskService;
import org.flowable.engine.ProcessEngine;
import org.flowable.task.api.Task;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class WfTaskServiceImpl implements WfTaskService {

    private final ProcessEngine processEngine;

    public WfTaskServiceImpl(ProcessEngine processEngine) {
        this.processEngine = processEngine;
    }

    @Override
    public List<TaskDto> todoList(Long userId) {
        return processEngine.getTaskService().createTaskQuery()
                .taskCandidateOrAssigned(String.valueOf(userId))
                .active()
                .orderByTaskCreateTime().desc()
                .list()
                .stream()
                .map(this::toTaskDto)
                .collect(Collectors.toList());
    }

    @Override
    public TaskDto detail(String taskId) {
        Task task = processEngine.getTaskService().createTaskQuery()
                .taskId(taskId)
                .singleResult();
        if (task == null) {
            throw new BusinessException(404, "任务不存在: " + taskId);
        }
        return toTaskDto(task);
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
}
