package com.lzw.blueprint.workflow.service;

import com.lzw.blueprint.workflow.dto.TaskDto;

import java.util.List;

public interface WfTaskService {

    List<TaskDto> todoList(Long userId);

    TaskDto detail(String taskId);
}
