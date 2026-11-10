package com.lzw.blueprint.workflow.service.impl;

import com.lzw.blueprint.common.exception.BusinessException;
import com.lzw.blueprint.workflow.dto.ProcessDefinitionDto;
import com.lzw.blueprint.workflow.service.BpmnXmlValidator;
import com.lzw.blueprint.workflow.service.WfProcessService;
import org.flowable.bpmn.model.BpmnModel;
import org.flowable.engine.ProcessEngine;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.engine.repository.ProcessDefinitionQuery;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class WfProcessServiceImpl implements WfProcessService {

    private final ProcessEngine processEngine;
    private final BpmnXmlValidator bpmnXmlValidator;

    public WfProcessServiceImpl(ProcessEngine processEngine, BpmnXmlValidator bpmnXmlValidator) {
        this.processEngine = processEngine;
        this.bpmnXmlValidator = bpmnXmlValidator;
    }

    @Override
    public ProcessDefinitionDto deploy(String name, String xml) {
        BpmnModel model = bpmnXmlValidator.validate(xml);
        String key = model.getMainProcess().getId();
        String resourceName = key + ".bpmn20.xml";
        repositoryService().createDeployment()
                .name(name != null && !name.isBlank() ? name : key)
                .addString(resourceName, xml)
                .deploy();
        ProcessDefinition pd = repositoryService().createProcessDefinitionQuery()
                .processDefinitionKey(key)
                .latestVersion()
                .singleResult();
        return toDto(pd);
    }

    @Override
    public List<ProcessDefinitionDto> list() {
        return repositoryService().createProcessDefinitionQuery()
                .latestVersion()
                .orderByProcessDefinitionKey().asc()
                .list()
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ProcessDefinitionDto> versions(String key) {
        return repositoryService().createProcessDefinitionQuery()
                .processDefinitionKey(key)
                .orderByProcessDefinitionVersion().desc()
                .list()
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public void activate(String key, Integer version) {
        ProcessDefinition pd = resolveDefinition(key, version);
        repositoryService().activateProcessDefinitionById(pd.getId());
    }

    @Override
    public void suspend(String key, Integer version) {
        ProcessDefinition pd = resolveDefinition(key, version);
        repositoryService().suspendProcessDefinitionById(pd.getId());
    }

    private ProcessDefinition resolveDefinition(String key, Integer version) {
        ProcessDefinitionQuery query = repositoryService().createProcessDefinitionQuery()
                .processDefinitionKey(key);
        if (version != null) {
            query.processDefinitionVersion(version);
        } else {
            query.latestVersion();
        }
        ProcessDefinition pd = query.singleResult();
        if (pd == null) {
            throw new BusinessException(404, "流程定义不存在: " + key + (version != null ? ":" + version : ""));
        }
        return pd;
    }

    private RepositoryService repositoryService() {
        return processEngine.getRepositoryService();
    }

    private ProcessDefinitionDto toDto(ProcessDefinition pd) {
        if (pd == null) {
            return null;
        }
        ProcessDefinitionDto dto = new ProcessDefinitionDto();
        dto.setId(pd.getId());
        dto.setKey(pd.getKey());
        dto.setName(pd.getName());
        dto.setVersion(pd.getVersion());
        dto.setDeploymentId(pd.getDeploymentId());
        dto.setSuspended(pd.isSuspended());
        return dto;
    }
}
