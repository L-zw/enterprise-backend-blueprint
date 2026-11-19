package com.lzw.blueprint.workflow.dto;

import lombok.Data;

@Data
public class StartInstanceDto {

    private String bpmnKey;
    private String title;
    private String formData;
}
