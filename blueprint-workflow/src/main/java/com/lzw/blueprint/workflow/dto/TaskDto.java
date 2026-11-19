package com.lzw.blueprint.workflow.dto;

import lombok.Data;

import java.util.Date;

@Data
public class TaskDto {

    private String taskId;
    private String name;
    private String assignee;
    private String taskDefinitionKey;
    private String processInstanceId;
    private Date createTime;
    private Date dueDate;
}
