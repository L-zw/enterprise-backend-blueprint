package com.lzw.blueprint.workflow.dto;

import lombok.Data;

import java.util.List;

@Data
public class DiagramDto {

    private String bpmnXml;
    private List<String> activeActivityIds;
    private List<String> completedActivityIds;
}
