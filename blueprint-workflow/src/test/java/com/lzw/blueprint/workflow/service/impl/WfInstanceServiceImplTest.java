package com.lzw.blueprint.workflow.service.impl;

import com.lzw.blueprint.common.exception.BusinessException;
import com.lzw.blueprint.workflow.config.FlowableConfig;
import com.lzw.blueprint.workflow.dto.InstanceDto;
import com.lzw.blueprint.workflow.dto.StartInstanceDto;
import com.lzw.blueprint.workflow.entity.WfInstanceExt;
import com.lzw.blueprint.workflow.mapper.WfInstanceExtMapper;
import org.flowable.engine.ProcessEngine;
import org.h2.jdbcx.JdbcDataSource;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class WfInstanceServiceImplTest {

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
    private WfInstanceExtMapper instanceExtMapper;
    private WfInstanceServiceImpl service;

    @BeforeEach
    void setUp() {
        JdbcDataSource dataSource = new JdbcDataSource();
        dataSource.setURL("jdbc:h2:mem:wf-instance-test;DB_CLOSE_DELAY=-1");
        dataSource.setUser("sa");
        engine = new FlowableConfig().processEngine(dataSource, new DataSourceTransactionManager(dataSource));
        engine.getRepositoryService().createDeployment()
                .addString("ut-leave.bpmn20.xml", BPMN_XML)
                .deploy();
        instanceExtMapper = mock(WfInstanceExtMapper.class);
        service = new WfInstanceServiceImpl(engine, instanceExtMapper);
    }

    @AfterEach
    void tearDown() {
        engine.getRepositoryService().createDeploymentQuery().list()
                .forEach(d -> engine.getRepositoryService().deleteDeployment(d.getId(), true));
        engine.close();
    }

    private StartInstanceDto request() {
        StartInstanceDto request = new StartInstanceDto();
        request.setBpmnKey("ut-leave");
        request.setTitle("张三的请假");
        request.setFormData("{\"days\":3}");
        return request;
    }

    private WfInstanceExt extRow() {
        WfInstanceExt ext = new WfInstanceExt();
        ext.setId(1L);
        ext.setInstanceId("stubbed");
        ext.setBpmnKey("ut-leave");
        ext.setInitiator(1L);
        ext.setTitle("张三的请假");
        ext.setFormData("{\"days\":3}");
        ext.setStatus("approving");
        return ext;
    }

    @Test
    void startCreatesInstanceAndWritesExt() {
        WfInstanceExt stored = extRow();
        when(instanceExtMapper.insert(any())).thenReturn(1);
        when(instanceExtMapper.selectOne(any())).thenReturn(stored);

        InstanceDto dto = service.start(request(), 1L);

        assertNotNull(dto.getInstanceId());
        assertEquals("ut-leave", dto.getBpmnKey());
        assertEquals(1, dto.getCurrentTasks().size());
        assertEquals("审批", dto.getCurrentTasks().get(0).getName());
        assertEquals(dto.getInstanceId(), dto.getCurrentTasks().get(0).getProcessInstanceId());

        ArgumentCaptor<WfInstanceExt> captor = ArgumentCaptor.forClass(WfInstanceExt.class);
        verify(instanceExtMapper).insert(captor.capture());
        assertEquals(dto.getInstanceId(), captor.getValue().getInstanceId());
        assertEquals("approving", captor.getValue().getStatus());
        assertEquals(1L, captor.getValue().getInitiator());
        assertEquals("{\"days\":3}", captor.getValue().getFormData());
    }

    @Test
    void detailMergesExtData() {
        when(instanceExtMapper.insert(any())).thenReturn(1);
        when(instanceExtMapper.selectOne(any())).thenReturn(extRow());

        InstanceDto dto = service.start(request(), 1L);

        assertEquals("张三的请假", dto.getTitle());
        assertEquals("approving", dto.getStatus());
        assertEquals(1L, dto.getInitiator());
        assertNotNull(dto.getStartTime());
        assertNull(dto.getEndTime());
    }

    @Test
    void detailUnknownInstanceThrows404() {
        BusinessException ex = assertThrows(BusinessException.class, () -> service.detail("no-such-instance"));
        assertEquals(404, ex.getCode());
    }

    @Test
    void cancelEndsInstanceAndUpdatesExt() {
        WfInstanceExt stored = extRow();
        when(instanceExtMapper.insert(any())).thenReturn(1);
        when(instanceExtMapper.selectOne(any())).thenReturn(stored);
        when(instanceExtMapper.updateById(any())).thenReturn(1);

        InstanceDto dto = service.start(request(), 1L);
        service.cancel(dto.getInstanceId(), "不批了");

        assertNull(engine.getRuntimeService().createProcessInstanceQuery()
                .processInstanceId(dto.getInstanceId()).singleResult());
        assertEquals("cancelled", stored.getStatus());
        assertNotNull(stored.getEndTime());
    }

    @Test
    void cancelUnknownInstanceThrows400() {
        BusinessException ex = assertThrows(BusinessException.class, () -> service.cancel("no-such-instance", null));
        assertEquals(400, ex.getCode());
    }

    @Test
    void startUnknownKeyThrows400() {
        StartInstanceDto request = request();
        request.setBpmnKey("no-such-key");
        BusinessException ex = assertThrows(BusinessException.class, () -> service.start(request, 1L));
        assertEquals(400, ex.getCode());
    }

    @Test
    void startRequiresKeyAndTitle() {
        StartInstanceDto noKey = request();
        noKey.setBpmnKey(" ");
        assertThrows(BusinessException.class, () -> service.start(noKey, 1L));

        StartInstanceDto noTitle = request();
        noTitle.setTitle(null);
        assertThrows(BusinessException.class, () -> service.start(noTitle, 1L));
    }
}
