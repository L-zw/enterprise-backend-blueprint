package com.lzw.blueprint.workflow.service;

import com.lzw.blueprint.workflow.dto.TaskDto;

import java.util.List;

public interface WfTaskService {

    List<TaskDto> todoList(Long userId);

    TaskDto detail(String taskId);

    void approve(String taskId, Long operatorId, String comment);

    void moveTo(String taskId, Long operatorId, String targetActivityId);

    void transfer(String taskId, Long operatorId, Long targetUserId);

    void addSign(String taskId, Long operatorId, Long targetUserId);
}
