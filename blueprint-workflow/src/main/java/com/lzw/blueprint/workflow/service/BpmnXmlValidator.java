package com.lzw.blueprint.workflow.service;

import com.lzw.blueprint.common.exception.BusinessException;
import org.flowable.bpmn.converter.BpmnXMLConverter;
import org.flowable.bpmn.model.BpmnModel;
import org.springframework.stereotype.Component;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;

@Component
public class BpmnXmlValidator {

    public BpmnModel validate(String xml) {
        if (xml == null || xml.isBlank()) {
            throw new BusinessException(400, "BPMN XML 不能为空");
        }
        try {
            XMLInputFactory factory = XMLInputFactory.newInstance();
            factory.setProperty(XMLInputFactory.SUPPORT_DTD, false);
            factory.setProperty(XMLInputFactory.IS_SUPPORTING_EXTERNAL_ENTITIES, false);
            XMLStreamReader reader = factory.createXMLStreamReader(
                    new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8)));
            BpmnModel model = new BpmnXMLConverter().convertToBpmnModel(reader);
            if (model.getMainProcess() == null) {
                throw new BusinessException(400, "BPMN XML 不包含流程定义");
            }
            return model;
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            throw new BusinessException(400, "BPMN XML 解析失败: " + e.getMessage());
        }
    }
}
