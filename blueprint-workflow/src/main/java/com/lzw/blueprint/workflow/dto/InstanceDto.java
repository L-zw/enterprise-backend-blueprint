package com.lzw.blueprint.workflow.dto;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class InstanceDto {

    private String instanceId;
    private String bpmnKey;
    private String title;
    private String formData;
    private String status;
    private Long initiator;
    private Date startTime;
    private Date endTime;
    private List<TaskDto> currentTasks;
}
