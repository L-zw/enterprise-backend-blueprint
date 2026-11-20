package com.lzw.blueprint.workflow.service.impl;

import com.lzw.blueprint.common.exception.BusinessException;
import com.lzw.blueprint.workflow.config.FlowableConfig;
import com.lzw.blueprint.workflow.dto.TaskDto;
import com.lzw.blueprint.workflow.entity.WfInstanceExt;
import com.lzw.blueprint.workflow.mapper.WfInstanceExtMapper;
import org.flowable.engine.ProcessEngine;
import org.h2.jdbcx.JdbcDataSource;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class WfTaskServiceImplTest {

    private static final String BPMN_XML =
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
            "<definitions xmlns=\"http://www.omg.org/spec/BPMN/20100524/MODEL\"\n" +
            "             xmlns:flowable=\"http://flowable.org/bpmn\"\n" +
            "             targetNamespace=\"http://example.com/test\">\n" +
            "  <process id=\"ut-leave\" name=\"请假流程\">\n" +
            "    <startEvent id=\"start\"/>\n" +
            "    <sequenceFlow id=\"f1\" sourceRef=\"start\" targetRef=\"manager\"/>\n" +
            "    <userTask id=\"manager\" name=\"经理审批\" flowable:candidateUsers=\"1\"/>\n" +
            "    <sequenceFlow id=\"f2\" sourceRef=\"manager\" targetRef=\"director\"/>\n" +
            "    <userTask id=\"director\" name=\"总监审批\" flowable:assignee=\"2\"/>\n" +
            "    <sequenceFlow id=\"f3\" sourceRef=\"director\" targetRef=\"end\"/>\n" +
            "    <endEvent id=\"end\"/>\n" +
            "  </process>\n" +
            "</definitions>";

    private ProcessEngine engine;
    private WfInstanceExtMapper instanceExtMapper;
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
        instanceExtMapper = mock(WfInstanceExtMapper.class);
        service = new WfTaskServiceImpl(engine, instanceExtMapper);
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

    private String activeTaskId(Long userId) {
        return service.todoList(userId).get(0).getTaskId();
    }

    @Test
    void todoListReturnsCandidateTask() {
        String instanceId = startInstance();

        List<TaskDto> todos = service.todoList(1L);

        assertEquals(1, todos.size());
        TaskDto dto = todos.get(0);
        assertEquals("经理审批", dto.getName());
        assertNull(dto.getAssignee());
        assertEquals("manager", dto.getTaskDefinitionKey());
        assertEquals(instanceId, dto.getProcessInstanceId());
        assertNotNull(dto.getTaskId());
    }

    @Test
    void todoListEmptyForOtherUser() {
        startInstance();

        assertTrue(service.todoList(999L).isEmpty());
    }

    @Test
    void detailReturnsTaskFields() {
        startInstance();
        String taskId = activeTaskId(1L);

        TaskDto dto = service.detail(taskId);

        assertEquals(taskId, dto.getTaskId());
        assertEquals("经理审批", dto.getName());
        assertEquals("manager", dto.getTaskDefinitionKey());
    }

    @Test
    void detailUnknownTaskThrows404() {
        BusinessException ex = assertThrows(BusinessException.class, () -> service.detail("no-such-task"));
        assertEquals(404, ex.getCode());
    }

    @Test
    void approveAdvancesToNextNode() {
        startInstance();
        String taskId = activeTaskId(1L);

        service.approve(taskId, 1L, "同意");

        assertTrue(service.todoList(1L).isEmpty());
        List<TaskDto> directorTodos = service.todoList(2L);
        assertEquals(1, directorTodos.size());
        assertEquals("director", directorTodos.get(0).getTaskDefinitionKey());
    }

    @Test
    void approveFinishingInstanceMarksExtApproved() {
        WfInstanceExt ext = new WfInstanceExt();
        ext.setId(1L);
        ext.setInstanceId("stubbed");
        ext.setStatus("approving");
        when(instanceExtMapper.selectOne(any())).thenReturn(ext);
        when(instanceExtMapper.updateById(any())).thenReturn(1);

        startInstance();
        service.approve(activeTaskId(1L), 1L, null);
        String directorTaskId = activeTaskId(2L);
        service.approve(directorTaskId, 2L, null);

        ArgumentCaptor<WfInstanceExt> captor = ArgumentCaptor.forClass(WfInstanceExt.class);
        verify(instanceExtMapper).updateById(captor.capture());
        assertEquals("approved", captor.getValue().getStatus());
        assertNotNull(captor.getValue().getEndTime());
    }

    @Test
    void rejectMovesBackToTargetNode() {
        startInstance();
        service.approve(activeTaskId(1L), 1L, null);
        String directorTaskId = activeTaskId(2L);

        service.moveTo(directorTaskId, 2L, "manager");

        assertTrue(service.todoList(2L).isEmpty());
        List<TaskDto> managerTodos = service.todoList(1L);
        assertEquals(1, managerTodos.size());
        assertEquals("manager", managerTodos.get(0).getTaskDefinitionKey());
    }

    @Test
    void transferChangesAssignee() {
        startInstance();
        String taskId = activeTaskId(1L);

        service.transfer(taskId, 1L, 2L);

        assertTrue(service.todoList(1L).isEmpty());
        List<TaskDto> todos = service.todoList(2L);
        assertEquals(1, todos.size());
        assertEquals("2", todos.get(0).getAssignee());
    }

    @Test
    void addSignAddsCandidateUser() {
        startInstance();
        String taskId = activeTaskId(1L);

        service.addSign(taskId, 1L, 2L);

        assertNull(service.detail(taskId).getAssignee());
        assertEquals(taskId, service.todoList(1L).get(0).getTaskId());
        assertEquals(taskId, service.todoList(2L).get(0).getTaskId());
    }

    @Test
    void approveByNonCandidateForbidden() {
        startInstance();
        String taskId = activeTaskId(1L);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> service.approve(taskId, 999L, null));
        assertEquals(403, ex.getCode());
    }

    @Test
    void approveUnknownTaskThrows404() {
        BusinessException ex = assertThrows(BusinessException.class,
                () -> service.approve("no-such-task", 1L, null));
        assertEquals(404, ex.getCode());
    }

    @Test
    void moveToRequiresTargetActivity() {
        startInstance();
        String taskId = activeTaskId(1L);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> service.moveTo(taskId, 1L, " "));
        assertEquals(400, ex.getCode());
    }

    @Test
    void finishedInstanceIsGoneFromRuntime() {
        WfInstanceExt ext = new WfInstanceExt();
        ext.setId(1L);
        ext.setStatus("approving");
        when(instanceExtMapper.selectOne(any())).thenReturn(ext);

        String instanceId = startInstance();
        service.approve(activeTaskId(1L), 1L, null);
        service.approve(activeTaskId(2L), 2L, null);

        assertNull(engine.getRuntimeService().createProcessInstanceQuery()
                .processInstanceId(instanceId).singleResult());
    }
}
