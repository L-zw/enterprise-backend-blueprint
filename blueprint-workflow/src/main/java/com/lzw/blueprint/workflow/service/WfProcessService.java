package com.lzw.blueprint.workflow.service;

import com.lzw.blueprint.workflow.dto.ProcessDefinitionDto;

import java.util.List;

public interface WfProcessService {

    ProcessDefinitionDto deploy(String name, String xml);

    List<ProcessDefinitionDto> list();

    List<ProcessDefinitionDto> versions(String key);

    void activate(String key, Integer version);

    void suspend(String key, Integer version);
}
