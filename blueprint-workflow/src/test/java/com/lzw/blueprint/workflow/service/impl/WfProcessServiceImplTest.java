package com.lzw.blueprint.workflow.service.impl;

import com.lzw.blueprint.common.exception.BusinessException;
import com.lzw.blueprint.workflow.config.FlowableConfig;
import com.lzw.blueprint.workflow.dto.ProcessDefinitionDto;
import com.lzw.blueprint.workflow.service.BpmnXmlValidator;
import org.flowable.engine.ProcessEngine;
import org.h2.jdbcx.JdbcDataSource;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class WfProcessServiceImplTest {

    private static final String BPMN_XML =
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
            "<definitions xmlns=\"http://www.omg.org/spec/BPMN/20100524/MODEL\"\n" +
            "             targetNamespace=\"http://example.com/test\">\n" +
            "  <process id=\"ut-leave\" name=\"请假流程\">\n" +
            "    <startEvent id=\"start\"/>\n" +
            "    <endEvent id=\"end\"/>\n" +
            "    <sequenceFlow id=\"flow1\" sourceRef=\"start\" targetRef=\"end\"/>\n" +
            "  </process>\n" +
            "</definitions>";

    private ProcessEngine engine;
    private WfProcessServiceImpl service;

    @BeforeEach
    void setUp() {
        JdbcDataSource dataSource = new JdbcDataSource();
        dataSource.setURL("jdbc:h2:mem:wf-process-test;DB_CLOSE_DELAY=-1");
        dataSource.setUser("sa");
        engine = new FlowableConfig().processEngine(dataSource, new DataSourceTransactionManager(dataSource));
        service = new WfProcessServiceImpl(engine, new BpmnXmlValidator());
    }

    @AfterEach
    void tearDown() {
        if (engine != null) {
            engine.getRepositoryService().createDeploymentQuery().list()
                    .forEach(d -> engine.getRepositoryService().deleteDeployment(d.getId(), true));
            engine.close();
        }
    }

    @Test
    void deployReturnsDefinitionAndListContainsIt() {
        ProcessDefinitionDto dto = service.deploy("请假", BPMN_XML);
        assertEquals("ut-leave", dto.getKey());
        assertEquals(1, dto.getVersion());
        assertFalse(dto.getSuspended());

        List<ProcessDefinitionDto> list = service.list();
        assertEquals(1, list.size());
        assertEquals("ut-leave", list.get(0).getKey());
    }

    @Test
    void redeployIncrementsVersionAndVersionsListDescends() {
        service.deploy("请假", BPMN_XML);
        ProcessDefinitionDto v2 = service.deploy("请假", BPMN_XML);
        assertEquals(2, v2.getVersion());

        List<ProcessDefinitionDto> versions = service.versions("ut-leave");
        assertEquals(2, versions.size());
        assertEquals(2, versions.get(0).getVersion());
        assertEquals(1, versions.get(1).getVersion());
    }

    @Test
    void suspendAndActivateLatestVersion() {
        service.deploy("请假", BPMN_XML);
        service.suspend("ut-leave", null);

        ProcessDefinitionDto latest = service.versions("ut-leave").get(0);
        assertTrue(latest.getSuspended());

        service.activate("ut-leave", null);
        latest = service.versions("ut-leave").get(0);
        assertFalse(latest.getSuspended());
    }

    @Test
    void suspendSpecificVersionLeavesOthersActive() {
        service.deploy("请假", BPMN_XML);
        service.deploy("请假", BPMN_XML);
        service.suspend("ut-leave", 1);

        List<ProcessDefinitionDto> versions = service.versions("ut-leave");
        ProcessDefinitionDto v1 = versions.stream().filter(v -> v.getVersion() == 1).findFirst().orElseThrow();
        ProcessDefinitionDto v2 = versions.stream().filter(v -> v.getVersion() == 2).findFirst().orElseThrow();
        assertTrue(v1.getSuspended());
        assertFalse(v2.getSuspended());
    }

    @Test
    void activateUnknownKeyThrows() {
        assertThrows(BusinessException.class, () -> service.activate("no-such-key", null));
    }

    @Test
    void deployInvalidXmlThrows400() {
        BusinessException ex = assertThrows(BusinessException.class, () -> service.deploy("bad", "not xml"));
        assertEquals(400, ex.getCode());
    }
}
