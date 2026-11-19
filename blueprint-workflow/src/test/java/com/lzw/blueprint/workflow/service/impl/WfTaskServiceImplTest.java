package com.lzw.blueprint.workflow.service.impl;

import com.lzw.blueprint.common.exception.BusinessException;
import com.lzw.blueprint.workflow.config.FlowableConfig;
import com.lzw.blueprint.workflow.dto.TaskDto;
import org.flowable.engine.ProcessEngine;
import org.h2.jdbcx.JdbcDataSource;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class WfTaskServiceImplTest {

    private static final String BPMN_XML =
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
            "<definitions xmlns=\"http://www.omg.org/spec/BPMN/20100524/MODEL\"\n" +
            "             xmlns:flowable=\"http://flowable.org/bpmn\"\n" +
            "             targetNamespace=\"http://example.com/test\">\n" +
            "  <process id=\"ut-leave\" name=\"请假流程\">\n" +
            "    <startEvent id=\"start\"/>\n" +
            "    <sequenceFlow id=\"f1\" sourceRef=\"start\" targetRef=\"approve\"/>\n" +
            "    <userTask id=\"approve\" name=\"审批\" flowable:assignee=\"1\"/>\n" +
            "    <sequenceFlow id=\"f2\" sourceRef=\"approve\" targetRef=\"end\"/>\n" +
            "    <endEvent id=\"end\"/>\n" +
            "  </process>\n" +
            "</definitions>";

    private ProcessEngine engine;
    private WfTaskServiceImpl service;

    @BeforeEach
    void setUp() {
        JdbcDataSource dataSource = new JdbcDataSource();
        dataSource.setURL("jdbc:h2:mem:wf-task-test;DB_CLOSE_DELAY=-1");
        dataSource.setUser("sa");
        engine = new FlowableConfig().processEngine(dataSource, new DataSourceTransactionManager(dataSource));
        engine.getRepositoryService().createDeployment()
                .addString("ut-leave.bpmn20.xml", BPMN_XML)
                .deploy();
        service = new WfTaskServiceImpl(engine);
    }

    @AfterEach
    void tearDown() {
        engine.getRepositoryService().createDeploymentQuery().list()
                .forEach(d -> engine.getRepositoryService().deleteDeployment(d.getId(), true));
        engine.close();
    }

    private String startInstance() {
        return engine.getRuntimeService().startProcessInstanceByKey("ut-leave").getId();
    }

    @Test
    void todoListReturnsAssignedActiveTask() {
        String instanceId = startInstance();

        List<TaskDto> todos = service.todoList(1L);

        assertEquals(1, todos.size());
        TaskDto dto = todos.get(0);
        assertEquals("审批", dto.getName());
        assertEquals("1", dto.getAssignee());
        assertEquals("approve", dto.getTaskDefinitionKey());
        assertEquals(instanceId, dto.getProcessInstanceId());
        assertNotNull(dto.getTaskId());
        assertNotNull(dto.getCreateTime());
    }

    @Test
    void todoListEmptyForOtherUser() {
        startInstance();

        assertTrue(service.todoList(999L).isEmpty());
    }

    @Test
    void detailReturnsTaskFields() {
        startInstance();
        String taskId = service.todoList(1L).get(0).getTaskId();

        TaskDto dto = service.detail(taskId);

        assertEquals(taskId, dto.getTaskId());
        assertEquals("审批", dto.getName());
        assertEquals("approve", dto.getTaskDefinitionKey());
    }

    @Test
    void detailUnknownTaskThrows404() {
        BusinessException ex = assertThrows(BusinessException.class, () -> service.detail("no-such-task"));
        assertEquals(404, ex.getCode());
    }
}
