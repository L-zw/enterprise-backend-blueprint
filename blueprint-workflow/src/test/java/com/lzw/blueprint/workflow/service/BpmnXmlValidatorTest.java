package com.lzw.blueprint.workflow.service;

import com.lzw.blueprint.common.exception.BusinessException;
import org.flowable.bpmn.model.BpmnModel;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BpmnXmlValidatorTest {

    private final BpmnXmlValidator validator = new BpmnXmlValidator();

    private static final String VALID_BPMN =
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
            "<definitions xmlns=\"http://www.omg.org/spec/BPMN/20100524/MODEL\"\n" +
            "             targetNamespace=\"http://example.com/test\">\n" +
            "  <process id=\"ut-leave\" name=\"请假流程\">\n" +
            "    <startEvent id=\"start\"/>\n" +
            "    <endEvent id=\"end\"/>\n" +
            "    <sequenceFlow id=\"flow1\" sourceRef=\"start\" targetRef=\"end\"/>\n" +
            "  </process>\n" +
            "</definitions>";

    @Test
    void validXmlReturnsModelWithProcess() {
        BpmnModel model = validator.validate(VALID_BPMN);
        assertNotNull(model.getMainProcess());
        assertEquals("ut-leave", model.getMainProcess().getId());
    }

    @Test
    void nullOrBlankThrows() {
        assertThrows(BusinessException.class, () -> validator.validate(null));
        assertThrows(BusinessException.class, () -> validator.validate("   "));
    }

    @Test
    void garbageStringThrows400() {
        BusinessException ex = assertThrows(BusinessException.class, () -> validator.validate("not xml at all"));
        assertEquals(400, ex.getCode());
    }

    @Test
    void xmlWithoutProcessThrows() {
        String noProcess =
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                "<definitions xmlns=\"http://www.omg.org/spec/BPMN/20100524/MODEL\"></definitions>";
        BusinessException ex = assertThrows(BusinessException.class, () -> validator.validate(noProcess));
        assertTrue(ex.getMessage().contains("流程定义"));
    }
}
