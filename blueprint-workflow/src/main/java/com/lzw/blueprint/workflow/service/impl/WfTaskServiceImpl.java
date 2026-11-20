package com.lzw.blueprint.workflow.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.lzw.blueprint.common.exception.BusinessException;
import com.lzw.blueprint.workflow.dto.TaskDto;
import com.lzw.blueprint.workflow.entity.WfInstanceExt;
import com.lzw.blueprint.workflow.mapper.WfInstanceExtMapper;
import com.lzw.blueprint.workflow.service.WfTaskService;
import org.flowable.engine.ProcessEngine;
import org.flowable.engine.RuntimeService;
import org.flowable.task.api.Task;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class WfTaskServiceImpl implements WfTaskService {

    private final ProcessEngine processEngine;
    private final WfInstanceExtMapper instanceExtMapper;

    public WfTaskServiceImpl(ProcessEngine processEngine, WfInstanceExtMapper instanceExtMapper) {
        this.processEngine = processEngine;
        this.instanceExtMapper = instanceExtMapper;
    }

    @Override
    public List<TaskDto> todoList(Long userId) {
        return taskQuery()
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
        Task task = resolveTask(taskId);
        return toTaskDto(task);
    }

    @Override
    public void approve(String taskId, Long operatorId, String comment) {
        Task task = resolveOperableTask(taskId, operatorId);
        String processInstanceId = task.getProcessInstanceId();
        if (comment != null && !comment.isBlank()) {
            processEngine.getTaskService().addComment(taskId, processInstanceId, comment);
        }
        processEngine.getTaskService().complete(taskId);
        if (instanceEnded(processInstanceId)) {
            markInstanceApproved(processInstanceId);
        }
    }

    @Override
    public void moveTo(String taskId, Long operatorId, String targetActivityId) {
        if (targetActivityId == null || targetActivityId.isBlank()) {
            throw new BusinessException(400, "目标节点不能为空");
        }
        Task task = resolveOperableTask(taskId, operatorId);
        runtimeService().createChangeActivityStateBuilder()
                .processInstanceId(task.getProcessInstanceId())
                .moveActivityIdTo(task.getTaskDefinitionKey(), targetActivityId)
                .changeState();
    }

    @Override
    public void transfer(String taskId, Long operatorId, Long targetUserId) {
        if (targetUserId == null) {
            throw new BusinessException(400, "转办人不能为空");
        }
        resolveOperableTask(taskId, operatorId);
        processEngine.getTaskService().setAssignee(taskId, String.valueOf(targetUserId));
    }

    @Override
    public void addSign(String taskId, Long operatorId, Long targetUserId) {
        if (targetUserId == null) {
            throw new BusinessException(400, "加签人不能为空");
        }
        resolveOperableTask(taskId, operatorId);
        processEngine.getTaskService().addCandidateUser(taskId, String.valueOf(targetUserId));
    }

    private Task resolveTask(String taskId) {
        Task task = taskQuery().taskId(taskId).singleResult();
        if (task == null) {
            throw new BusinessException(404, "任务不存在: " + taskId);
        }
        return task;
    }

    private Task resolveOperableTask(String taskId, Long operatorId) {
        Task task = resolveTask(taskId);
        if (operatorId == null || !isOperatorAllowed(task, operatorId)) {
            throw new BusinessException(403, "无权操作该任务");
        }
        return task;
    }

    private boolean isOperatorAllowed(Task task, Long operatorId) {
        String operator = String.valueOf(operatorId);
        if (operator.equals(task.getAssignee())) {
            return true;
        }
        return processEngine.getTaskService().getIdentityLinksForTask(task.getId()).stream()
                .anyMatch(link -> "candidate".equals(link.getType()) && operator.equals(link.getUserId()));
    }

    private boolean instanceEnded(String processInstanceId) {
        return runtimeService().createProcessInstanceQuery()
                .processInstanceId(processInstanceId)
                .singleResult() == null;
    }

    private void markInstanceApproved(String processInstanceId) {
        WfInstanceExt ext = instanceExtMapper.selectOne(
                new QueryWrapper<WfInstanceExt>().eq("instance_id", processInstanceId));
        if (ext != null) {
            ext.setStatus("approved");
            ext.setEndTime(LocalDateTime.now());
            instanceExtMapper.updateById(ext);
        }
    }

    private org.flowable.task.api.TaskQuery taskQuery() {
        return processEngine.getTaskService().createTaskQuery();
    }

    private RuntimeService runtimeService() {
        return processEngine.getRuntimeService();
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
