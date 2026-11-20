package com.lzw.blueprint.workflow.dto;

import lombok.Data;

import java.util.List;

@Data
public class TaskActionDto {

    private String comment;
    private String targetActivityId;
    private Long userId;
    private List<Long> userIds;
}
